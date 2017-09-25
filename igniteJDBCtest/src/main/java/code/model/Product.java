package code.model;

import java.io.Serializable;

/**
 * 产品类
 *
 * @author fxf
 * @create 2017-09-25 14:22
 **/

public class Product implements Serializable {
	private static final long serialVersionUID = -2901938992094930543L;

	private String ProductID;
	private String ProductName;
	private int Price;

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public String getProductID() {
		return ProductID;
	}

	public void setProductID(String productID) {
		ProductID = productID;
	}

	public String getProductName() {
		return ProductName;
	}

	public void setProductName(String productName) {
		ProductName = productName;
	}

	public int getPrice() {
		return Price;
	}

	public void setPrice(int price) {
		Price = price;
	}
}