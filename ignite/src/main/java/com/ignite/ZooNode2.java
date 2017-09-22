package com.ignite;


import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.zk.TcpDiscoveryZookeeperIpFinder;




public class ZooNode2 {

	 private static IgniteCache<String, String> cache = null;

	public static void main(String[] args) throws InterruptedException {

		TcpDiscoverySpi spi = new TcpDiscoverySpi();
		spi.setLocalPort(49300);
//		TcpDiscoveryVmIpFinder ipFinder = new TcpDiscoveryVmIpFinder();
//		// Set initial IP addresses.
//		// Note that you can optionally specify a port or a port range.
//		ipFinder.setAddresses(Arrays.asList("192.168.0.103","192.168.0.204:49300..49310"));
//
//		spi.setIpFinder(ipFinder);
//		IgniteConfiguration cfg = new IgniteConfiguration();
//		// Override default discovery SPI.
//		cfg.setDiscoverySpi(spi);

		TcpDiscoveryZookeeperIpFinder ipFinder = new TcpDiscoveryZookeeperIpFinder();
		ipFinder.setZkConnectionString("10.55.28.85:2181");
		spi.setIpFinder(ipFinder);
		IgniteConfiguration cfg = new IgniteConfiguration();
		cfg.setDiscoverySpi(spi);

		Ignite ignite = Ignition.start(cfg); //启动ignite缓存
		CacheConfiguration<Integer, String> cacheCfg = new CacheConfiguration<Integer, String>();
	    cacheCfg.setName("myCache");
	    IgniteCache<Integer, String> cache = ignite.getOrCreateCache(cacheCfg);

	    while(true){
	    	  System.out.println(cache.get(1));
	 	      System.out.println(cache.get(2));
	 	      Thread.sleep(5000);
	     }

//	    CacheConfiguration<String, String> cacheCfg = new CacheConfiguration<>();
//        cacheCfg.setName("cache");
////	        cacheCfg.setReadThrough(true);
////        cacheCfg.setWriteThrough(true);
//        cfg.setCacheConfiguration(cacheCfg);
//
//		// Start Ignite node.
//        Ignite ignite = Ignition.start(cfg); //启动ignite缓存
//
//		cache = ignite.getOrCreateCache(cacheCfg);
//
//
	}
}
