package code.model;

import org.apache.ignite.cache.query.annotations.QuerySqlField;

import java.io.Serializable;

/**
 * 订单类
 * @author fxf
 * @create 2017-09-25 14:26
 **/

public class Order implements Serializable {
	private static final long serialVersionUID = 1296488678585580707L;

	@QuerySqlField
	private String OrderID;
	@QuerySqlField
	private String CustomerID;
	private String OrderDate;
	private int OrderState;
	@QuerySqlField
	private int SumMoney;
	@QuerySqlField
	private String UpdateDate;

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public String getOrderID() {
		return OrderID;
	}

	public void setOrderID(String orderID) {
		OrderID = orderID;
	}

	public String getCustomerID() {
		return CustomerID;
	}

	public void setCustomerID(String customerID) {
		CustomerID = customerID;
	}

	public String getOrderDate() {
		return OrderDate;
	}

	public void setOrderDate(String orderDate) {
		OrderDate = orderDate;
	}

	public int getOrderState() {
		return OrderState;
	}

	public void setOrderState(int orderState) {
		OrderState = orderState;
	}

	public int getSumMoney() {
		return SumMoney;
	}

	public void setSumMoney(int sumMoney) {
		SumMoney = sumMoney;
	}

	public String getUpdateDate() {
		return UpdateDate;
	}

	public void setUpdateDate(String updateDate) {
		UpdateDate = updateDate;
	}
}