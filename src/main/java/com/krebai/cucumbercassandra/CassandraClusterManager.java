
package com.krebai.cucumbercassandra;

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

	public Session getSession() {
		return session;
	}

	private void initConnection() {
		this.cluster = clusterInformation.build();
		this.session = cluster.connect(clusterInformation.getKeySpace());
	}

}
