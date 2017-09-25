package code.model;

import java.io.Serializable;

/**
 * 顾客类
 *
 * @author fxf
 * @create 2017-09-25 14:18
 **/

public class Customer implements Serializable {

	private static final long serialVersionUID = 3887713948021974098L;

	private String CustomerID;
	private String CustomerName;

	public String getCustomerID() {
		return CustomerID;
	}

	public void setCustomerID(String customerID) {
		CustomerID = customerID;
	}

	public String getCustomerName() {
		return CustomerName;
	}

	public void setCustomerName(String customerName) {
		CustomerName = customerName;
	}


}