
package com.krebai.cucumbercassandra;

import org.cassandraunit.CQLDataLoader;
import org.cassandraunit.dataset.cql.ClassPathCQLDataSet;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;

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
