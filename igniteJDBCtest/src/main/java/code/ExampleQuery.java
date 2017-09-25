package code;

import code.utils.IgniteConfigurationTool;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.configuration.IgniteConfiguration;

import java.util.List;

/**
 * @author fxf
 * @create 2017-09-25 16:08
 **/

public class ExampleQuery {
	public static void main(String[] args) {
		IgniteConfiguration icf = IgniteConfigurationTool.getInstance().getIgniteConfiguration(false);
		try (Ignite ignite = Ignition.start(icf)) {
			IgniteCache<Object, Object> cache = ignite.getOrCreateCache(ExampleInit.PARTITIONED_cache);
			SqlFieldsQuery sfq = new SqlFieldsQuery("select top 1 * from Order "
					+ "join OrderInfo on Order.orderid=OrderInfo.orderid  order by Order.UpdateDate ");
			while (true) {
				QueryCursor<List<?>> cursor = cache.query(sfq);
				for (List<?> row : cursor) {
					StringBuffer sb = new StringBuffer();
					for (Object p : row) {
						sb.append(p);
						sb.append(",");
					}
					System.out.println(sb.toString());
				}
				try {
					Thread.sleep(10 * 1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}