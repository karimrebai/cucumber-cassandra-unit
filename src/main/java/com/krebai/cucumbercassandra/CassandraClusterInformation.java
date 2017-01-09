
package com.krebai.cucumbercassandra;

import com.datastax.driver.core.Cluster;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CassandraClusterInformation {

	@Value("#{T(java.util.Arrays).asList('${authorizationengine.cassandra.cluster.contact.point}')}")
	private List<String> contactPoints;

	@Value("${authorizationengine.cassandra.keyspace}")
	private String keySpace;

	@Value("${authorizationengine.cassandra.username}")
	private String user;

	@Value("${authorizationengine.cassandra.password}")
	private String password;

	@Value("${authorizationengine.cassandra.cluster.port}")
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
