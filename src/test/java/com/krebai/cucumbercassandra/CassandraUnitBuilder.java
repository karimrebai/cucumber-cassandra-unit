package com.krebai.cucumbercassandra;

import java.io.IOException;
import java.util.List;

import org.apache.thrift.transport.TTransportException;
import org.cassandraunit.CQLDataLoader;
import org.cassandraunit.dataset.cql.ClassPathCQLDataSet;
import org.cassandraunit.utils.EmbeddedCassandraServerHelper;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Session;

public class CassandraUnitBuilder {

	final static String LOCALHOST = "127.0.0.1";

	final static int DEFAULT_PORT = 9142;

	static final String KEY_SPACE = "d_authz_payment";

	private final static String INSERT_INTO_ACCOUNT_BALANCE =  "INSERT INTO ACCOUNT_BALANCE (account, balance) VALUES (?, ?);";

	private Cluster cluster;

	public void startLocalCassandraInstance(String localhost, int defaultPort, String dataSetLocation,
			String keySpace) throws TTransportException, IOException, InterruptedException {
		EmbeddedCassandraServerHelper.startEmbeddedCassandra();
		Cluster c = createCluster(localhost, defaultPort);
		createKeySpace(dataSetLocation, keySpace, c);
		cluster = c;
	}

	private void createKeySpace(String dataSetLocation, String keySpace, Cluster c) {
		Session session = c.connect();
		CQLDataLoader dataLoader = new CQLDataLoader(session);
		dataLoader.load(new ClassPathCQLDataSet(dataSetLocation, true, keySpace));
		session.close();
	}

	private Cluster createCluster(String localhost, int defaultPort) {
		return new Cluster.Builder().addContactPoints(localhost).withPort(defaultPort).build();
	}

	public void saveFixture(List<Account> accounts) {
		try (Session session = cluster.connect(KEY_SPACE)) {
			PreparedStatement statement = session.prepare(INSERT_INTO_ACCOUNT_BALANCE);

			for (Account account : accounts) {
				session.execute(statement.bind(account.getAccountNumber(), account.getBalance()));
			}
		}
	}

	public void close() {
		cluster.close();
	}
}
