package ro.gdg.android.domain;

import java.util.Arrays;

public class TableBill {
	public static final int STATUS_OPEN = 0;
	public static final int STATUS_CLOSED = 1;

	private String waiterEmail;
	private int tableNumber;
	private String date;
	private int status;
	private OrderedProduct[] products;

	public TableBill(String waiterEmail, int tableNumber, String date,
			int status) {
		super();
		this.waiterEmail = waiterEmail;
		this.tableNumber = tableNumber;
		this.date = date;
		this.status = status;
	}

	public String getWaiterEmail() {
		return waiterEmail;
	}

	public void setWaiterEmail(String waiterEmail) {
		this.waiterEmail = waiterEmail;
	}

	public int getTableNumber() {
		return tableNumber;
	}

	public void setTableNumber(int tableNumber) {
		this.tableNumber = tableNumber;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public OrderedProduct[] getProducts() {
		return products;
	}

	public void setProducts(OrderedProduct[] products) {
		this.products = products;
	}

	@Override
	public String toString() {
		return "TableBill [waiterEmail=" + waiterEmail + ", tableNumber="
				+ tableNumber + ", date=" + date + ", status=" + status
				+ ", products=" + Arrays.toString(products) + "]";
	}

}
