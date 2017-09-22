import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.binary.BinaryObject;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.cache.affinity.AffinityKey;
import org.apache.ignite.cache.query.*;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.lang.IgniteBiPredicate;

import javax.cache.Cache;
import java.util.List;

/**
 * @author fxf
 * @create 2017-09-22 15:22
 **/
public class CacheCRUDtest {

	private static final String ORG_CACHE = CacheCRUDtest.class
			.getSimpleName() + "Organizations";

	private static final String COLLOCATED_PERSON_CACHE = CacheCRUDtest.class
			.getSimpleName() + "CollocatedPersons";

	private static final String PERSON_CACHE = CacheCRUDtest.class
			.getSimpleName() + "Persons";

	public static void main(String[] args) throws Exception {
		try (Ignite ignite = Ignition
				.start("examples/config/example-ignite.xml")) {
			System.out.println("ignite开启");

			//Organization配置类
			CacheConfiguration<Long, Organization> orgCacheCfg = new CacheConfiguration<>(
					ORG_CACHE);
			orgCacheCfg.setCacheMode(CacheMode.PARTITIONED);
			orgCacheCfg.setIndexedTypes(Long.class, Organization.class);

			//Person配置类不序列化
			CacheConfiguration<AffinityKey<Long>, Person> colPersonCacheCfg = new CacheConfiguration<>(
					COLLOCATED_PERSON_CACHE);
			colPersonCacheCfg.setCacheMode(CacheMode.PARTITIONED);
			colPersonCacheCfg.setIndexedTypes(AffinityKey.class, Person.class);

			////Person配置类序列化
			CacheConfiguration<AffinityKey<Long>, Person> personCacheCfg = new CacheConfiguration<>(
					PERSON_CACHE);
			personCacheCfg.setCacheMode(CacheMode.PARTITIONED);
			personCacheCfg.setIndexedTypes(Long.class, Person.class);

			//根据不同的配置启动不同的ignite缓存
			try (IgniteCache<Long, Organization> orgCache = ignite
					.getOrCreateCache(orgCacheCfg);
				 IgniteCache<AffinityKey<Long>, Person> colPersonCache = ignite
						 .getOrCreateCache(colPersonCacheCfg);
				 IgniteCache<AffinityKey<Long>, Person> personCache = ignite
						 .getOrCreateCache(personCacheCfg)) {

				//三个ignite缓存启动后，各种清空并入值
				initialize();

				//ScanQuery范围查询
				scanQuery();

				//sql范围查询
				sqlQuery();

				//组织结构缓存 关联 人员缓存 查询
				sqlQueryWithJoin();

				//组织结构缓存 关联 人员缓存 查询
				sqlQueryWithDistributedJoin();

				//具体字段中的具体(包含)信息查询
				textQuery();

				//带sql函数的查询
				sqlQueryWithAggregation();

				//sql concat函数拼接查询
				sqlFieldsQuery();

				//sql 拼接，关联缓存查询
				sqlFieldsQueryWithJoin();

			} finally {//销毁缓存
				ignite.destroyCache(COLLOCATED_PERSON_CACHE);
				ignite.destroyCache(PERSON_CACHE);
				ignite.destroyCache(ORG_CACHE);
			}
			print("ignite服务结束");
		}
	}

	//ScanQuery范围查询
	private static void scanQuery() {
		IgniteCache<BinaryObject, BinaryObject> cache = Ignition.ignite()
				.cache(COLLOCATED_PERSON_CACHE).withKeepBinary();
		ScanQuery<BinaryObject, BinaryObject> scan = new ScanQuery<>(
				new IgniteBiPredicate<BinaryObject, BinaryObject>() {
					@Override
					public boolean apply(BinaryObject key, BinaryObject person) {
						return person.<Double>field("salary") <= 1000;
					}
				});
		print("工资在 0 and 1000 之间(scanQuery()方法) ",
				cache.query(scan).getAll());
	}

	//sql范围查询
	private static void sqlQuery() {
		IgniteCache<AffinityKey<Long>, Person> cache = Ignition.ignite().cache(
				PERSON_CACHE);
		String sql = "salary > ? and salary <= ?";
		print("工资在 0 and 1000 之间(sqlQuery()方法): ",
				cache.query(
						new SqlQuery<AffinityKey<Long>, Person>(Person.class,
								sql).setArgs(0, 1000)).getAll());

		print("工资在 1000 and 2000 之间(sqlQuery()方法):",
				cache.query(
						new SqlQuery<AffinityKey<Long>, Person>(Person.class,
								sql).setArgs(1000, 2000)).getAll());
	}

	//组织结构缓存 关联 人员缓存 查询
	private static void sqlQueryWithJoin() {
		IgniteCache<AffinityKey<Long>, Person> cache = Ignition.ignite().cache(
				COLLOCATED_PERSON_CACHE);
		String joinSql = "from Person, \"" + ORG_CACHE
				+ "\".Organization as org " + "where Person.orgId = org.id "
				+ "and lower(org.name) = lower(?)";
		print("Alibaba员工： ",
				cache.query(
						new SqlQuery<AffinityKey<Long>, Person>(Person.class,
								joinSql).setArgs("Alibaba")).getAll());
		print("Baidu员工: ",
				cache.query(
						new SqlQuery<AffinityKey<Long>, Person>(Person.class,
								joinSql).setArgs("Baidu")).getAll());
	}

	//组织结构缓存 关联 人员缓存 查询
	private static void sqlQueryWithDistributedJoin() {
		IgniteCache<AffinityKey<Long>, Person> cache = Ignition.ignite().cache(
				PERSON_CACHE);
		String joinSql = "from Person, \"" + ORG_CACHE
				+ "\".Organization as org " + "where Person.orgId = org.id "
				+ "and lower(org.name) = lower(?)";
		SqlQuery qry = new SqlQuery<AffinityKey<Long>, Person>(Person.class,
				joinSql).setArgs("Alibaba");
		qry.setDistributedJoins(true);
		print("Alibaba (distributed join): ",
				cache.query(qry).getAll());
		qry.setArgs("Baidu");
		print("Baidu (distributed join): ",
				cache.query(qry).getAll());
	}

	//具体字段中的具体(包含)信息查询
	private static void textQuery() {
		IgniteCache<AffinityKey<Long>, Person> cache = Ignition.ignite().cache(
				PERSON_CACHE);
		QueryCursor<Cache.Entry<AffinityKey<Long>, Person>> masters = cache
				.query(new TextQuery<AffinityKey<Long>, Person>(Person.class,
						"Master"));
		QueryCursor<Cache.Entry<AffinityKey<Long>, Person>> bachelors = cache
				.query(new TextQuery<AffinityKey<Long>, Person>(Person.class,
						"Bachelor"));
		print("查询包含Master关键字人员: ",
				masters.getAll());
		print("查询包含Bachelor关键字人员 ",
				bachelors.getAll());
	}

	//带sql函数的查询
	private static void sqlQueryWithAggregation() {
		IgniteCache<AffinityKey<Long>, Person> cache = Ignition.ignite().cache(
				COLLOCATED_PERSON_CACHE);
		String sql = "select avg(salary) " + "from Person, \"" + ORG_CACHE
				+ "\".Organization as org " + "where Person.orgId = org.id "
				+ "and lower(org.name) = lower(?)";
		QueryCursor<List<?>> cursor = cache.query(new SqlFieldsQuery(sql)
				.setArgs("Alibaba"));
		print("Alibaba平均工资: ", cursor.getAll());
	}

	//sql concat函数拼接查询
	private static void sqlFieldsQuery() {
		IgniteCache<AffinityKey<Long>, Person> cache = Ignition.ignite().cache(
				PERSON_CACHE);
		QueryCursor<List<?>> cursor = cache.query(new SqlFieldsQuery(
				"select concat(firstName, ' ', lastName) from Person"));
		List<List<?>> res = cursor.getAll();
		print("员工名字:", res);
	}

	//sql 拼接，关联缓存查询
	private static void sqlFieldsQueryWithJoin() {
		IgniteCache<AffinityKey<Long>, Person> cache = Ignition.ignite().cache(
				COLLOCATED_PERSON_CACHE);
		String sql = "select concat(firstName, ' ', lastName), org.name "
				+ "from Person, \"" + ORG_CACHE + "\".Organization as org "
				+ "where Person.orgId = org.id";
		QueryCursor<List<?>> cursor = cache.query(new SqlFieldsQuery(sql));
		List<List<?>> res = cursor.getAll();
		print("公司，员工名字: ", res);
	}

	//装配基础数据
	private static void initialize() {
		//ignite组织机构缓存
		IgniteCache<Long, Organization> orgCache = Ignition.ignite().cache(
				ORG_CACHE);
		orgCache.clear();
		Organization org1 = new Organization("Alibaba");
		Organization org2 = new Organization("Baidu");
		//ignite组织机构缓存入值
		orgCache.put(org1.id(), org1);
		orgCache.put(org2.id(), org2);
		//ignite人员缓存
		IgniteCache<AffinityKey<Long>, Person> colPersonCache = Ignition
				.ignite().cache(COLLOCATED_PERSON_CACHE);
		//ignite人员缓存
		IgniteCache<Long, Person> personCache = Ignition.ignite().cache(
				PERSON_CACHE);
		colPersonCache.clear();
		personCache.clear();
		Person p1 = new Person(org1, "John", "Doe", 2000,
				"John Doe has Master Degree.");
		Person p2 = new Person(org1, "Jane", "Doe", 1000,
				"Jane Doe has Bachelor Degree.");
		Person p3 = new Person(org2, "John", "Smith", 1000,
				"John Smith has Bachelor Degree.");
		Person p4 = new Person(org2, "Jane", "Smith", 2000,
				"Jane Smith has Master Degree.");
		//ignite人员缓存入值
		colPersonCache.put(p1.key(), p1);
		colPersonCache.put(p2.key(), p2);
		colPersonCache.put(p3.key(), p3);
		colPersonCache.put(p4.key(), p4);
		//ignite人员缓存入值
		personCache.put(p1.id, p1);
		personCache.put(p2.id, p2);
		personCache.put(p3.id, p3);
		personCache.put(p4.id, p4);
	}

	private static void print(String msg, Iterable<?> col) {
		print(msg);
		print(col);
	}

	private static void print(String msg) {
		System.out.println();
		System.out.println("查询条件:" + msg);
	}

	private static void print(Iterable<?> col) {
		for (Object next : col) {
			System.out.println("人员具体信息: " + next);
		}
	}
}