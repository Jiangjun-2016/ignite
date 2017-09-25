package code;

import code.model.Customer;
import code.model.Order;
import code.model.OrderInfo;
import code.model.Product;
import code.utils.IgniteConfigurationTool;
import code.utils.JDBCUtils;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.cache.affinity.AffinityKey;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author fxf
 * @create 2017-09-25 14:18
 **/

public class ExampleInit {

	public static Ignite ignite;
	public static String REPLICATED_cache = "REPLICATED";//复制模式缓存
	public static String PARTITIONED_cache = "PARTITIONED";//分片模式缓存
	public static String nowDate = "1999-09-09"; // 检测时间

	public static void main(String[] args) {
		IgniteConfiguration icf = IgniteConfigurationTool.getInstance().getIgniteConfiguration(false);
		createCache(icf);
		ignite = Ignition.start(icf);
		System.out.println("ExampleInit ignite服务 started.");
		JDBCUtils.createData();
		try {
			loadCache();//手动加载缓存
		} catch (SQLException e) {
			e.printStackTrace();
		}
		checkData();//手动检测数据
	}

	/**
	 * 初始化缓存
	 */
	public static void createCache(IgniteConfiguration icf) {
		//复制模式
		CacheConfiguration<String, Object> repCacheCfg = new CacheConfiguration<>();
		repCacheCfg.setName(REPLICATED_cache);
		repCacheCfg.setCacheMode(CacheMode.REPLICATED);
		repCacheCfg.setIndexedTypes(String.class, Customer.class, String.class, Product.class);
		//分片模式
		CacheConfiguration<Object, Object> factCacheCfg = new CacheConfiguration<>();
		factCacheCfg.setName(PARTITIONED_cache);
		factCacheCfg.setCacheMode(CacheMode.PARTITIONED);
		factCacheCfg.setIndexedTypes(Object.class, Order.class, Object.class, OrderInfo.class);

		icf.setCacheConfiguration(repCacheCfg, factCacheCfg);
	}

	/**
	 * 手动加载缓存
	 *
	 * @throws SQLException
	 */
	private static void loadCache() throws SQLException {
		Connection conn = JDBCUtils.getConnection();
		IgniteCache<String, Object> REPcache = ignite.getOrCreateCache(REPLICATED_cache);

		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery("select * from Customer");
		while (rs.next()) {
			Customer cr = new Customer();
			cr.setCustomerID(rs.getString("CustomerID"));
			cr.setCustomerName(rs.getString("customerName"));
			REPcache.put(rs.getString("CustomerID"), cr);
		}
		rs.close();
		rs = st.executeQuery("select * from Product");
		while (rs.next()) {
			Product cr = new Product();
			cr.setProductID(rs.getString("ProductID"));
			cr.setProductName(rs.getString("ProductName"));
			cr.setPrice(rs.getInt("Price"));
			REPcache.put(rs.getString("ProductID"), cr);
		}
		rs.close();

		IgniteCache<Object, Object> PARTcache = ignite.getOrCreateCache(PARTITIONED_cache);
		rs = st.executeQuery("select * from Order order by updateDate");
		while (rs.next()) {
			String orderID = rs.getString("orderID");
			Order cr = new Order();
			cr.setCustomerID(rs.getString("customerID"));
			cr.setOrderID(orderID);
			cr.setOrderState(rs.getInt("orderState"));
			cr.setSumMoney(rs.getInt("sumMoney"));
			cr.setOrderDate(rs.getString("orderDate"));
			cr.setUpdateDate(rs.getString("updateDate"));
			nowDate = rs.getString("updateDate");
			PARTcache.put(orderID, cr);
			Statement std = conn.createStatement();
			ResultSet rsd = std.executeQuery("select * from Order_D where orderid='" + orderID + "'");
			while (rsd.next()) {
				Object personKey1 = new AffinityKey(rsd.getString("guid"), orderID);
				OrderInfo od = new OrderInfo();
				od.setGuid(rsd.getString("guid"));
				od.setOrderID(rsd.getString("orderid"));
				od.setPcount(rsd.getInt("pcount"));
				od.setPMoney(rsd.getInt("pMoney"));
				od.setPrice(rsd.getInt("price"));
				od.setProductID(rsd.getString("productID"));
				PARTcache.put(personKey1, od);
			}
			rsd.close();
		}
		rs.close();
		st.close();


	}

	/**
	 * 手动检测数据
	 */
	public static void checkData() {
		new Thread(new Runnable() {
			public void run() {
				System.out.println("检查开始...");
				while (true) {
					try {
						Connection conn = JDBCUtils.getConnection();
						int count = 0;
						Statement st = conn.createStatement();
						ResultSet rs = st.executeQuery(
								"select * from Order_H where updateDate>'" + nowDate + "' order by updateDate");
						IgniteCache<Object, Object> PARTcache = ignite.getOrCreateCache(PARTITIONED_cache);
						while (rs.next()) {
							count++;
							Order cr = new Order();
							cr.setCustomerID(rs.getString("customerID"));
							cr.setOrderID(rs.getString("orderID"));
							cr.setOrderState(rs.getInt("orderState"));
							cr.setSumMoney(rs.getInt("sumMoney"));
							cr.setOrderDate(rs.getString("orderDate"));
							cr.setUpdateDate(rs.getString("updateDate"));
							nowDate = rs.getString("updateDate");
							PARTcache.put(rs.getString("orderID"), cr);
							Statement std = conn.createStatement();
							ResultSet rsd = std.executeQuery(
									"select * from Order_D where orderid='" + rs.getString("orderID") + "'");
							while (rsd.next()) {
								Object personKey1 = new AffinityKey(rsd.getString("guid"), rs.getString("orderID"));
								OrderInfo od = new OrderInfo();
								od.setGuid(rsd.getString("guid"));
								od.setOrderID(rsd.getString("orderid"));
								od.setPcount(rsd.getInt("pcount"));
								od.setPMoney(rsd.getInt("pMoney"));
								od.setPrice(rsd.getInt("price"));
								od.setProductID(rsd.getString("productID"));
								PARTcache.put(personKey1, od);
							}
							rsd.close();
						}
						rs.close();
						st.close();
						conn.close();
						//System.out.println("检查" + count + "张订单大于时间," + nowDate + "！>_<");
						count = 0;
					} catch (Exception e) {
						e.printStackTrace();
					}
					try {
						Thread.sleep(10 * 1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}
}