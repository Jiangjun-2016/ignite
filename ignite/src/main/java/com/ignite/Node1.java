package com.ignite;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.zk.TcpDiscoveryZookeeperIpFinder;

public class Node1 {
	public static void main(String[] args) {
		TcpDiscoverySpi spi = new TcpDiscoverySpi();
		spi.setLocalPort(47500);
		// Ignition.setClientMode(true);
		TcpDiscoveryZookeeperIpFinder ipFinder = new TcpDiscoveryZookeeperIpFinder();
		ipFinder.setZkConnectionString("127.0.0.1:2181");
		spi.setIpFinder(ipFinder);
		IgniteConfiguration cfg = new IgniteConfiguration();
		cfg.setDiscoverySpi(spi);
		cfg.setPeerClassLoadingEnabled(true);
		Ignite ignite = Ignition.start(cfg); // 启动ignite缓存

		CacheConfiguration<String, Object> cacheCfg = new CacheConfiguration<String, Object>();
		cacheCfg.setName("TestFXFCache");
		cacheCfg.setCacheMode(CacheMode.PARTITIONED);
		cacheCfg.setIndexedTypes(String.class, DriverForSqlCache.class);
		IgniteCache<String, Object> driverCache = ignite
				.getOrCreateCache(cacheCfg);
		DriverForSqlCache driver = new DriverForSqlCache("123", "123", "123",
				"123", "123", "123");
		driverCache.put("1", driver);
	}
}
