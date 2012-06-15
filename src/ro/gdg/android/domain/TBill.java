package ro.gdg.android.domain;

public class TBill {
	public static final int STATUS_OPEN = 0;
	public static final int STATUS_CLOSED = 1;

	private String waiterEmail;
	private int tableNumber;
	private String date;
	private int status;

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

	@Override
	public String toString() {
		return "TBill [waiterEmail=" + waiterEmail + ", tableNumber="
				+ tableNumber + ", date=" + date + ", status=" + status + "]";
	}

}
