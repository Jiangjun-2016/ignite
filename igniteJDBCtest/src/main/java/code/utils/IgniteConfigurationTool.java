package code.utils;

import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;

import java.util.Arrays;

/**
 * @author fxf
 * @create 2017-09-25 14:58
 **/

public class IgniteConfigurationTool {

	private static IgniteConfigurationTool igniteConfigurationTool = new IgniteConfigurationTool();

	public static IgniteConfigurationTool getInstance() {
		return igniteConfigurationTool;
	}

	/**
	 * 通过类配置Ignite节点
	 */
	public IgniteConfiguration getIgniteConfiguration(Boolean isClient) {
		IgniteConfiguration cfg = new IgniteConfiguration();
		TcpDiscoverySpi discoSpi = new TcpDiscoverySpi();
		TcpDiscoveryVmIpFinder IP_FINDER = new TcpDiscoveryVmIpFinder(true);
		IP_FINDER.setAddresses(Arrays.asList("127.0.0.1:47500..47509"));
		discoSpi.setIpFinder(IP_FINDER);
		cfg.setDiscoverySpi(discoSpi);
		// 是否客户端模式
		cfg.setClientMode(isClient);
		// 网格名称
		cfg.setGridName("client");
		// 自动传递闭包类
		cfg.setPeerClassLoadingEnabled(true);
		return cfg;
	}


}