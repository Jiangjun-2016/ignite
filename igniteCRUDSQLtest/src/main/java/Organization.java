import org.apache.ignite.cache.query.annotations.QuerySqlField;

import java.sql.Timestamp;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author fxf
 * @create 2017-09-22 15:19
 **/
public class Organization {

	private static final AtomicLong ID_GEN = new AtomicLong();

	@QuerySqlField(index = true)
	private Long id;

	@QuerySqlField(index = true)
	private String name;

	private Address addr;

	private OrganizationType type;

	private Timestamp lastUpdated;

	public Organization() {
	}

	public Organization(String name) {
		id = ID_GEN.incrementAndGet();
		this.name = name;
	}

	public Organization(String name, Address addr, OrganizationType type, Timestamp lastUpdated) {
		id = ID_GEN.incrementAndGet();
		this.name = name;
		this.addr = addr;
		this.type = type;
		this.lastUpdated = lastUpdated;
	}

	public Long id() {
		return id;
	}

	public String name() {
		return name;
	}

	public Address address() {
		return addr;
	}

	public OrganizationType type() {
		return type;
	}

	public Timestamp lastUpdated() {
		return lastUpdated;
	}

	@Override
	public String toString() {
		return "Organization [id=" + id +
				", name=" + name +
				", address=" + addr +
				", type=" + type +
				", lastUpdated=" + lastUpdated + ']';
	}
}