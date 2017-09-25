package code.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * @author fxf
 * @create 2017-09-25 15:35
 **/

public class JDBCUtils {

	public static final String jdbcUrl = "jdbc:mysql://127.0.0.01:3306/ignite?user=znyuan&password=victop";

	public static boolean isMake = false;

	public static int count = 10;

	public static int sec = 5;


	public static Connection getConnection() throws SQLException {
		return DriverManager.getConnection(jdbcUrl);
	}

	public static void createData() {
		JDBCUtils jdbcUtils = new JDBCUtils();
		jdbcUtils.initData();
//		jdbcUtils.makeData();
	}

	/**
	 * 初始化数据库
	 */
	public void initData() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection(jdbcUrl);
			CustomerInitData(count, con);
			ProductInitData(count, con);
			OrderInitData(count, con);
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 每一秒插入数据
	 */
	public void makeData() {
		if (!isMake) {
			isMake = true;
			new Thread(new Runnable() {
				public void run() {
					long start = System.currentTimeMillis();
					System.out.println("生成开始...");
					while (true) {
						try {
							Class.forName("com.mysql.jdbc.Driver");
							Connection con = DriverManager.getConnection(jdbcUrl);
							OrderAddData(con);
							con.close();
							//System.out.println("生成一张订单！=。=");
						} catch (Exception e) {
							e.printStackTrace();
						}
						long end = System.currentTimeMillis();
						if (end - start > 4 * 60 * 1000l) {
							System.out.println("太多单据了～～～！T_T");
							return;
						}
						try {
							Thread.sleep(sec * 1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}).start();
		}
	}

	/**
	 * 顾客类initData
	 */
	private static void CustomerInitData(int count, Connection conn) {
		Statement st = null;
		try {
			st = conn.createStatement();
			st.execute("delete from Customer");
			for (int i = 1; i <= count; i++) {
				st.execute("insert into Customer(CustomerID,CustomerName)values"
						+ "('" + getCustomerID(i) + "','" + getCustomerName(i)
						+ "')");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				st.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 产品类initData
	 */
	private static void ProductInitData(int count, Connection conn) {
		Statement st = null;
		try {
			st = conn.createStatement();
			st.execute("delete from Product");
			for (int i = 1; i <= count; i++) {
				st.execute("insert into Product(ProductID,ProductName,price)values" + "('" + getProductID(i) + "','"
						+ getProductName(i) + "'," + i + ")");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				st.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 订单类initData
	 */
	private static void OrderInitData(int count, Connection conn) {
		Statement st = null;
		try {
			st = conn.createStatement();
			st.execute("delete from Order_D");
			st.execute("delete from Order_H");
			for (int i = 1; i <= count; i++) {
				OrderAddData(conn);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				st.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 销售单表头
	 * Order
	 * 【OrderID(订单号),CustomerID(客户编号),OrderDate(订单日期),OrderState(订单状态),
	 * SumMoney(总金额),UpdateDate(修改时间)】
	 * 销售单表体
	 * OrderInfo
	 * 【Guid,OrderID(订单号),ProductID(编号),Pcount(数量)，Price(价格),PMoney(金额)】
	 */
	private static void OrderAddData(Connection conn) {

		Date nowTime = new Date();
		SimpleDateFormat time = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		String OrderID = "Order" + time.format(nowTime);//订单id

		SimpleDateFormat datef = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateTime = datef.format(nowTime);//时间

		int CustomerID = (int) (Math.random() * count);
		int sumMoney = 0;
		int detailCount = (int) (Math.random() * 10);

		Statement st = null;
		try {
			st = conn.createStatement();
			for (int i = 0; i < detailCount; i++) {
				UUID uuid = UUID.randomUUID();
				String rowid = uuid.toString();
				int ProductID = (int) (Math.random() * (count - 1)) + 1;
				int Pcount = (int) (Math.random() * 20) + 1;
				int PMoney = Pcount * ProductID;

				st.execute("insert into OrderInfo(Guid,OrderID,ProductID,Pcount,Price,PMoney)values" + "('" + rowid + "','"
						+ OrderID + "','" + getProductID(ProductID) + "'," + Pcount + "," + ProductID + "," + PMoney
						+ ")");
				sumMoney = sumMoney + PMoney;
			}
			st.execute("insert into Order(OrderID,CustomerID,OrderDate,OrderState,SumMoney,UpdateDate)values" + "('"
					+ OrderID + "','" + getCustomerID(CustomerID) + "','" + dateTime + "',0," + sumMoney + ",'"
					+ dateTime + "')");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				st.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}


	private static String getCustomerID(int i) {
		return "ct" + i;
	}

	private static String getCustomerName(int i) {
		return "客户" + i;
	}

	private static String getProductID(int i) {
		return "pd" + i;
	}

	private static String getProductName(int i) {
		return "产品" + i;
	}

}