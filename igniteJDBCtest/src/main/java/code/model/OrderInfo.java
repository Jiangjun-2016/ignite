package code.model;

import org.apache.ignite.cache.query.annotations.QuerySqlField;

import java.io.Serializable;

/**
 * 订单明细类
 *
 * @author fxf
 * @create 2017-09-25 14:28
 **/

public class OrderInfo implements Serializable {
	private static final long serialVersionUID = 1987363607742069690L;

	@QuerySqlField
	private String Guid;
	@QuerySqlField
	private String OrderID;
	@QuerySqlField
	private String ProductID;
	private int Pcount;
	private int Price;
	private int PMoney;

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public String getGuid() {
		return Guid;
	}

	public void setGuid(String guid) {
		Guid = guid;
	}

	public String getOrderID() {
		return OrderID;
	}

	public void setOrderID(String orderID) {
		OrderID = orderID;
	}

	public String getProductID() {
		return ProductID;
	}

	public void setProductID(String productID) {
		ProductID = productID;
	}

	public int getPcount() {
		return Pcount;
	}

	public void setPcount(int pcount) {
		Pcount = pcount;
	}

	public int getPrice() {
		return Price;
	}

	public void setPrice(int price) {
		Price = price;
	}

	public int getPMoney() {
		return PMoney;
	}

	public void setPMoney(int PMoney) {
		this.PMoney = PMoney;
	}
}