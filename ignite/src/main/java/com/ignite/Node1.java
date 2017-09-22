package com.ignite;


import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;

import java.util.Arrays;




public class Node1 {
	
	 private static IgniteCache<String, String> cache = null;
	
	public static void main(String[] args) throws InterruptedException {
		
		TcpDiscoverySpi spi = new TcpDiscoverySpi();
		spi.setLocalPort(49300);
		TcpDiscoveryVmIpFinder ipFinder = new TcpDiscoveryVmIpFinder();
		// Set initial IP addresses.
		// Note that you can optionally specify a port or a port range.
		ipFinder.setAddresses(Arrays.asList("192.168.0.180"));
		
		spi.setIpFinder(ipFinder);
		IgniteConfiguration cfg = new IgniteConfiguration();
		// Override default discovery SPI.
		cfg.setDiscoverySpi(spi);
		
//		TcpCommunicationSpi commSpi = new TcpCommunicationSpi();
//		// Override local port.
//		commSpi.setLocalPort(4321);
//		// Override default communication SPI.
//		cfg.setCommunicationSpi(commSpi);
		
		
		Ignite ignite = Ignition.start(cfg); //启动ignite缓存
		 CacheConfiguration<Integer, String> cacheCfg = new CacheConfiguration<Integer, String>();
	     cacheCfg.setName("myCache");
	     IgniteCache<Integer, String> cache = ignite.getOrCreateCache(cacheCfg);
	     
	     while(true){
				cache.put(1, System.currentTimeMillis()+"");
				Thread.sleep(5000);
				System.out.println("***");
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
