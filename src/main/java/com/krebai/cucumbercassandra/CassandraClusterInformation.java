
package com.krebai.cucumbercassandra;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.HostDistance;
import com.datastax.driver.core.PoolingOptions;

@Component
public class CassandraClusterInformation {

	private static final int DEFAULT_REPLICA_FACTOR = 2;

	@Value("#{T(java.util.Arrays).asList('${authorizationengine.cassandra.cluster.contact.point}')}")
	private List<String> contactPoints;

	@Value("${authorizationengine.cassandra.keyspace}")
	private String keySpace;

	@Value("${authorizationengine.cassandra.username}")
	private String user;

	@Value("${authorizationengine.cassandra.password}")
	private String password;

	private Map<String, Integer> dataCenter = new HashMap<>();

	private int replicaFactor = DEFAULT_REPLICA_FACTOR;

	@Value("${authorizationengine.cassandra.cluster.port}")
	private int port;

	@Value("${authorizationengine.cassandra.pooling.hearbeat.interval}")
	private int hearbeatIntervalInSeconds;

	@Value("${authorizationengine.cassandra.pooling.connection.local.core}")
	private int poolConnectionLocalCore;

	@Value("${authorizationengine.cassandra.pooling.connection.local.max}")
	private int poolConnectionLocalMax;

	@Value("${authorizationengine.cassandra.pooling.connection.remote.core}")
	private int poolConnectionRemoteCore;

	@Value("${authorizationengine.cassandra.pooling.connection.remote.max}")
	private int poolConnectionRemoteMax;

	@Value("${authorizationengine.cassandra.pooling.connection.idle.timeout}")
	private int poolConnectionIdleTimeoutInSeconds;

	/**
	 * new instance of CassandraClusterConfiguration.
	 *
	 * @return the new instance
	 */
	public static CassandraClusterInformation create() {
		return new CassandraClusterInformation();
	}

	public List<String> getContactPoints() {
		return contactPoints;
	}

	public void setContactPoints(List<String> contactPoints) {
		this.contactPoints = contactPoints;
	}

	public String getKeySpace() {
		return keySpace;
	}

	public void setKeySpace(String keySpace) {
		this.keySpace = keySpace;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getReplicaFactor() {
		return replicaFactor;
	}

	public void setReplicaFactor(int replicaFactor) {
		this.replicaFactor = replicaFactor;
	}

	public Map<String, Integer> getDataCenter() {
		return dataCenter;
	}

	public void setDataCenter(Map<String, Integer> dataCenter) {
		this.dataCenter = dataCenter;
	}

	/**
	 * set the port.
	 *
	 * @param port
	 *            the port
	 * @return this
	 */
	public CassandraClusterInformation withPort(int port) {
		this.port = port;
		return this;
	}

	/**
	 * informs the replicaFactor will be used when will generate keySpace
	 * Automatically.
	 *
	 * @param replicaFactor
	 *            the replicaFactor
	 * @return this
	 */
	public CassandraClusterInformation withReplicaFactor(int replicaFactor) {
		this.replicaFactor = replicaFactor;
		return this;
	}

	/**
	 * set user to authentication.
	 *
	 * @param user
	 *            the user
	 * @return this
	 */
	public CassandraClusterInformation withUser(String user) {
		this.user = user;
		return this;
	}

	/**
	 * set the password to authentication.
	 *
	 * @param password
	 *            the password
	 * @return this
	 */
	public CassandraClusterInformation withPassword(String password) {
		this.password = password;
		return this;
	}

	/**
	 * defines the keySpace.
	 *
	 * @param keySpace
	 *            the keySpace
	 * @return this
	 */
	public CassandraClusterInformation withKeySpace(String keySpace) {
		this.keySpace = keySpace;
		return this;
	}

	/**
	 * add member to cluster
	 * Best practice : add all clusters in this configuration.
	 *
	 * @param contactPoints
	 *            the hosts member of the cluster
	 * @return this
	 */
	public CassandraClusterInformation addHost(String... contactPoints) {
		if (contactPoints != null) {
			for (String contactPoint : contactPoints) {
				this.contactPoints.add(contactPoint);
			}
		}
		return this;
	}

	/**
	 * Inform a replica factor to a specific data center, you should use when define NETWORK_TOPOLOGY_STRATEGY
	 * as Replica Strategy.
	 *
	 * @param dataCenterName
	 *            the data center name
	 * @param factor
	 *            the replica factor to data center
	 * @return this.
	 */
	public CassandraClusterInformation addDataCenter(String dataCenterName, int factor) {
		this.dataCenter.put(dataCenterName, factor);
		return this;
	}

	protected Cluster build() {

		Cluster.Builder buildCluster = Cluster.builder().withPort(port)
		/* .addContactPoints(contactPoints.toArray(new String[0])) */

		.addContactPoint(contactPoints.get(0));

		if (!user.isEmpty() && !password.isEmpty()) {
			buildCluster.withCredentials(user, password);
		}

		// TODO Add Pooling Options, Reconnections, SSL,
		PoolingOptions opts = new PoolingOptions();
		opts.setConnectionsPerHost(HostDistance.LOCAL, poolConnectionLocalCore, poolConnectionLocalMax)
				.setConnectionsPerHost(HostDistance.REMOTE, poolConnectionRemoteCore, poolConnectionRemoteMax);
		buildCluster.withPoolingOptions(opts);

		// buildCluster.withProtocolVersion();
		// buildCluster.withReconnectionPolicy(DowngradingConsistencyRetryPolicy.INSTANCE);
		// buildCluster.withRetryPolicy();
		// buildCluster.withSSL();

		return buildCluster.build();
	}

}
