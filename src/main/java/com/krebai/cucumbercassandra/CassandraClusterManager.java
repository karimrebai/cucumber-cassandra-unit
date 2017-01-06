
package com.krebai.cucumbercassandra;

import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;

@Component
public class CassandraClusterManager  {

	private CassandraClusterInformation clusterInformation;

	private Cluster cluster;

	private Session session;

	public CassandraClusterManager(@Autowired CassandraClusterInformation clusterInformation) {
		this.clusterInformation = clusterInformation;
		this.initConnection();
	}

	protected Cluster getCluster() {
		return cluster;
	}

	public Session getSession() {
		return session;
	}

	@PreDestroy
	public void close() {
		try {
			if (!session.isClosed()) {
				session.close();
			}
			if (!cluster.isClosed()) {
				cluster.close();
			}
		} finally {
			// Noting to do
		}
	}

	private void initConnection() {
		this.cluster = clusterInformation.build();
		this.session = cluster.connect(clusterInformation.getKeySpace());
	}

}
