package com.ignite;

import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.lang.IgniteCallable;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.zk.TcpDiscoveryZookeeperIpFinder;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Hello world!
 */
public class App {
	public static void main(String[] args) {
		sumString("hello word");
//		zooKepper();
	}

	public static void sumString(String info) {
		try (Ignite ignite = Ignition
				.start("D:/worksoft/apache-ignite-2.1.0-src/examples/config/example-ignite.xml")) {
			Collection<IgniteCallable<Integer>> calls = new ArrayList<>();
			for (final String word : info.split(" ")) {
				calls.add(new IgniteCallable<Integer>() {
					@Override
					public Integer call() throws Exception {
						return word.length();
					}
				});
			}
			Collection<Integer> res = ignite.compute().call(calls);
			int sum = 0;
			for (int len : res)
				sum += len;
			System.out.println(info+"字数: '" + sum + "'.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void zooKepper() {
		TcpDiscoverySpi spi = new TcpDiscoverySpi();
		TcpDiscoveryZookeeperIpFinder ipFinder = new TcpDiscoveryZookeeperIpFinder();
		ipFinder.setZkConnectionString("127.0.0.1:2181");
		spi.setIpFinder(ipFinder);
		IgniteConfiguration cfg = new IgniteConfiguration();
		cfg.setDiscoverySpi(spi);
		Ignition.start(cfg);
	}


}
