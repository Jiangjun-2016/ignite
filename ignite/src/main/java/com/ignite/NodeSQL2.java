package com.ignite;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.zk.TcpDiscoveryZookeeperIpFinder;

public class NodeSQL2 {

	public static void main(String[] args) {
		TcpDiscoverySpi spi = new TcpDiscoverySpi();
		spi.setLocalPort(47900);
		Ignition.setClientMode(true);
		TcpDiscoveryZookeeperIpFinder ipFinder = new TcpDiscoveryZookeeperIpFinder();
		ipFinder.setZkConnectionString("192.168.0.180:2181");
		spi.setIpFinder(ipFinder);
		IgniteConfiguration cfg = new IgniteConfiguration();
		cfg.setDiscoverySpi(spi);
		cfg.setPeerClassLoadingEnabled(true);
		Ignite ignite = Ignition.start(cfg); // 启动ignite缓存

		CacheConfiguration<String, Object> cacheCfg = new CacheConfiguration<String, Object>();
		cacheCfg.setName("TestFXFCache");
		// cacheCfg.setIndexedTypes(String.class, DriverForSqlCache.class);
		IgniteCache<String, Object> driverCache = ignite
				.getOrCreateCache(cacheCfg);
		System.out.println(driverCache.size());
		driverCache.query(new SqlFieldsQuery("DELETE FROM DriverForSqlCache " +
				"WHERE ownCode >= ?").setArgs("123"));
		System.out.println(driverCache.size());
		driverCache.query(new SqlFieldsQuery(
				"INSERT INTO DriverForSqlCache(_key,ownCode, jobNumber, busTimeTable,runState,XM,CKRYIDO)"
						+ " VALUES(?,?, ?, ?, ?, ?, ?)").setArgs(
				"555", "555", "555", "555", "555", "555", "555"));
		System.out.println(driverCache.size());
	}
}