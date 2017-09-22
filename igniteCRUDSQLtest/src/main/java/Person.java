import org.apache.ignite.cache.affinity.AffinityKey;
import org.apache.ignite.cache.query.annotations.QuerySqlField;
import org.apache.ignite.cache.query.annotations.QueryTextField;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author fxf
 * @create 2017-09-22 15:04
 **/

public class Person implements Serializable {

	private static final long serialVersionUID = -3244871772518664441L;
	private static final AtomicLong ID_GEN = new AtomicLong();

	@QuerySqlField(index = true)
	public Long id;

	@QuerySqlField(index = true)
	public Long orgId;

	@QuerySqlField
	public String firstName;

	@QuerySqlField
	public String lastName;

	@QueryTextField
	public String resume;

	@QuerySqlField(index = true)
	public double salary;

	private transient AffinityKey<Long> key;

	public Person() {
	}

	public Person(Organization org, String firstName, String lastName, double salary, String resume) {
		id = ID_GEN.incrementAndGet();
		orgId = org.id();
		this.firstName = firstName;
		this.lastName = lastName;
		this.salary = salary;
		this.resume = resume;
	}

	public Person(Long id, Long orgId, String firstName, String lastName, double salary, String resume) {
		this.id = id;
		this.orgId = orgId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.salary = salary;
		this.resume = resume;
	}

	public Person(Long id, String firstName, String lastName) {
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
	}

	public AffinityKey<Long> key() {
		if (key == null)
			key = new AffinityKey<>(id, orgId);

		return key;
	}

	@Override
	public String toString() {
		return "Person [id=" + id +
				", orgId=" + orgId +
				", lastName=" + lastName +
				", firstName=" + firstName +
				", salary=" + salary +
				", resume=" + resume + ']';
	}
}