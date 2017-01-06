# Cucumber with Cassandra Unit

<p>L'objectif de cet article est de voir un exemple d'utilisation de Cucumber avec une base Cassandra "in memomry".</p>

<p>Pour cela, nous nous baserons sur une story simple dont le but est de, pour un client avec un numéro de compte donné, récupérer le montant présent sur son compte.</p>

<p>Avant de commencer, voici les dépendances Maven dont nous aurons besoin :</p>
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

On crée un fichier "account-balance.feature" dans le dossier "src/test/resources" qui décrit notre story avec la syntaxe [gherkin] (https://github.com/cucumber/cucumber/wiki/Gherkin) :

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
Là on créé la classe de test que l'on devra exécuter pour lancer les steps Cucumber :
```java
@RunWith(Cucumber.class)
@CucumberOptions(format = { "pretty", "html:target/cucumber" }, features = "src/test/resources/")
public class AccountBalanceTest {

}
```
Les options :
- format : "pretty" permet d'afficher les scénarios dans la console et "html:target/cucumber" permet de générer un rapport html dans le dossier target.
- features : permet de lier le runner au fichier feature que l'on a créé précédemment.

A ce stade, lorsqu'on exécute cette classe, Cucumber nous génère le squelette du code des steps :
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