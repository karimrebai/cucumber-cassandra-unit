# Cucumber with Cassandra Unit

L'objectif de cepoc est de voir un exemple d'utilisation de Cucumber avec une base Cassandra "in memory".
Pour cela, nous nous baserons sur une story simple dont le but est de, pour un client avec un numéro de compte donné, récupérer le montant présent sur son compte.
Avant de commencer, voici les dépendences Maven dont nous aurons besoin :
```xml
<dependencies>
    <dependency>
        <groupId>junit</groupId>
        <artifactId>junit</artifactId>
        <version>${junit.version}</version>
        <scope>test</scope>
    </dependency>

    <dependency>
        <groupId>info.cukes</groupId>
        <artifactId>cucumber-java</artifactId>
        <version>${cucumber.version}</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>info.cukes</groupId>
        <artifactId>cucumber-junit</artifactId>
        <version>${cucumber.version}</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```

## Cucumber

Pour la partie Cucumber, on suivra 3 étapes :
<ol>
<li>Création du fichier feature</li>
<li>Création du runner</li>
<li>Création des steps</li>
</ol>

### Fichier feature

Création d'un fichier "account-balance.feature" dans le dossier "src/test/resources" qui décrit notre story avec la syntaxe [gherkin](https://github.com/cucumber/cucumber/wiki/Gherkin) :

```gherkin
Feature: Get the account balance given an account

  Scenario: Account found
    Given A client with the account number 40000001939
    When I ask for the available balance
    Then I get the corresponding balance 95.38

  Scenario: Account not found
    Given A client with an invalid account number 00000000000
    When I ask for the available balance
    Then I get the error Account not found
```

### Runner
Classe de test permettant d'exécuter les steps Cucumber:
```java
@RunWith(Cucumber.class)
@CucumberOptions(format = { "pretty", "html:target/cucumber" }, features = "src/test/resources/")
public class AccountBalanceTest {

}
```
Les options :
- format : "pretty" permet d'afficher les scénarios dans la console et "html:target/cucumber" permet de générer un rapport html dans le dossier target.
- features : permet de lier le runner au fichier feature que l'on a créé précédemment.

A ce stade, lorsqu'on exécute cette classe, Cucumber génère le squelette du code des steps :
```
...
You can implement missing steps with the snippets below:

@Given("^A client with the account number (\\d+)$")
public void a_client_with_the_account_number(int arg1) throws Throwable {
    // Write code here that turns the phrase above into concrete actions
    throw new PendingException();
}
...
```

### Steps
```java
public class AccountBalanceSteps {

	private String accountNumber;

	private BigDecimal balance;

	private boolean isAccountNotFoundExceptionThrown;

	@Given("^A client with the account number (\\d+)$")
	public void a_client_with_the_account_number(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	@When("^I ask for the available balance$")
	public void i_ask_for_the_available_balance() {
		try {
			this.balance = getAccountBalance(accountNumber);
		} catch (AccountNotFoundException e) {
			this.isAccountNotFoundExceptionThrown = true;
		}
	}

	@Then("^I get the corresponding balance (\\d+)$")
	public void i_get_the_corresponding_balance(String balance) {
		assertThat(this.balance).isEqualTo(new BigDecimal(balance));
	}

	@Given("^A client with an invalid account number (\\d+)$")
	public void a_client_with_an_invalid_account_number(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	@Then("^I get the error Account not found$")
	public void i_get_the_error_Account_not_found() {
		assertThat(this.isAccountNotFoundExceptionThrown).isTrue();
	}

	private BigDecimal getAccountBalance(String accountNumber) throws AccountNotFoundException {
		if ("40000001939".equals(accountNumber)) {
			return new BigDecimal("1200");
		}
		throw new AccountNotFoundException();
	}
}
```

## Code de prod

### Conf Cassandra
Créarion d'une classe qui portera la configuration de l'accès à la base Cassandra :

```java
@Component
public class CassandraClusterInformation {

	@Value("#{T(java.util.Arrays).asList('${cassandra.cluster.contact.point}')}")
	private List<String> contactPoints;

	@Value("${cassandra.keyspace}")
	private String keySpace;

	@Value("${cassandra.username}")
	private String user;

	@Value("${cassandra.password}")
	private String password;

	@Value("${cassandra.cluster.port}")
	private int port;

	public String getKeySpace() {
		return keySpace;
	}

	protected Cluster build() {

		Cluster.Builder buildCluster = Cluster.builder().withPort(port)
		.addContactPoint(contactPoints.get(0));

		if (!user.isEmpty() && !password.isEmpty()) {
			buildCluster.withCredentials(user, password);
		}
		return buildCluster.build();
	}

}
```
Et voici la classe qui permet de gérer la connexion au cluster Cassandra :
```java
@Component
public class CassandraClusterManager  {

	private CassandraClusterInformation clusterInformation;

	private Cluster cluster;

	private Session session;

	public CassandraClusterManager(@Autowired CassandraClusterInformation clusterInformation) {
		this.clusterInformation = clusterInformation;
		this.initConnection();
	}

	public Session getSession() {
		return session;
	}

	private void initConnection() {
		this.cluster = clusterInformation.build();
		this.session = cluster.connect(clusterInformation.getKeySpace());
	}

}
```

### DAO Casandra
Maintenant on crée une classe de DAO qui exposera une méthode permettant de récupérer, dans la base Cassandra, la balance d'un compte en fonction d'un numéro de compte :
```java
@Repository
public class DefaultAccountDao implements AccountDao {

	private static final String SELECT_FROM_ACCOUNT_BALANCE = "SELECT * FROM ACCOUNT_BALANCE WHERE account = ?";

	private static final String ACCOUNT_COLUMN = "account";

	private static final String BALANCE_COLUMN = "balance";

	private final Session session;

	private final PreparedStatement accountBalancePreparedStatement;

	public DefaultAccountDao(@Autowired CassandraClusterManager clusterManager) {
		this.session = clusterManager.getSession();

		accountBalancePreparedStatement = this.session.prepare(SELECT_FROM_ACCOUNT_BALANCE);
	}

	@Override
	public BigDecimal getAccountBalance(String accountNumber) throws AccountNotFoundException {
		ResultSet resultSet = session.execute(accountBalancePreparedStatement.bind(accountNumber));
		Row row = resultSet.one();

		if (row == null) {
			throw new AccountNotFoundException();
		}
		return new Account(row.getString(ACCOUNT_COLUMN), row.getDecimal(BALANCE_COLUMN)).getBalance();
	}
}
```

### Configuration Spring

Et pour finir, une petite classe pour la conf Spring où l'on définit notamment le fichier qui contient les propriétés de connexion à la base Cassandra:

```java
@org.springframework.context.annotation.Configuration
@PropertySource(value = "classpath:system.properties")
@ComponentScan("com.krebai.cucumbercassandra")
public class Configuration {

}
```

## Cassandra Unit

Finalement il ne reste plus qu'à intégrer Cassandra Unit dans nos steps.

Etant donné que nous ne voulons démarrer la base Cassandra Unit qu'une seule fois pour tous les tests, l'appel à la méthode startEmbeddedCassandra de Cassandra Unit a été positionné dans un bloc static.
Le chargement des données de test, ainsi que l'initialisation du contexte Spring peuvent aussi être effectués à ce niveau.

```java
	static {
		try {
			EmbeddedCassandraServerHelper.startEmbeddedCassandra();
			new CassandraUnitDataLoader(LOCALHOST, DEFAULT_PORT).load(THE_KEY_SPACE, "account_balance_dataset.cql");
			applicationContext = new AnnotationConfigApplicationContext(Configuration.class);
		} catch (Exception e) {
			Assert.fail("Fail to initialize test context: " + e.getMessage());
		}
	}
```

Le code de chargement du dataset a été délégué à la classe suivante :

```java
public class CassandraUnitDataLoader {

	private Cluster cluster;

	private Session session;

	public CassandraUnitDataLoader(String host, int port) {
		this.cluster = new Cluster.Builder().addContactPoints(host).withPort(port).build();
		this.session = cluster.connect();
	}

	public void load(String keySpace, String dataSetLocation) {
		CQLDataLoader dataLoader = new CQLDataLoader(session);
		dataLoader.load(new ClassPathCQLDataSet(dataSetLocation, true, keySpace));
		session.close();
	}
}
```
