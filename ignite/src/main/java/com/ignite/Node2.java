package com.ignite;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.zk.TcpDiscoveryZookeeperIpFinder;

import java.util.List;

public class Node2 {

	public static void main(String[] args) {
		TcpDiscoverySpi spi = new TcpDiscoverySpi();
		spi.setLocalPort(47900);
		Ignition.setClientMode(true);
		TcpDiscoveryZookeeperIpFinder ipFinder = new TcpDiscoveryZookeeperIpFinder();
		ipFinder.setZkConnectionString("127.0.0.1:2181");
		spi.setIpFinder(ipFinder);
		IgniteConfiguration cfg = new IgniteConfiguration();
		cfg.setDiscoverySpi(spi);
		cfg.setPeerClassLoadingEnabled(true);
		Ignite ignite = Ignition.start(cfg); // 启动ignite缓存

		CacheConfiguration<String, Object> cacheCfg = new CacheConfiguration<String, Object>();
		cacheCfg.setName("TestFXFCache");
//		cacheCfg.setIndexedTypes(String.class, DriverForSqlCache.class);
		IgniteCache<String, Object> driverCache = ignite
				.getOrCreateCache(cacheCfg);
		System.out.println(driverCache.size());
		SqlFieldsQuery sql = new SqlFieldsQuery(
				"select ownCode from DriverForSqlCache ");
		QueryCursor<List<?>> cursor = driverCache.query(sql);
		for (List<?> row : cursor) {
			System.out.println("info：" + row.get(0));
		}
	}
}