package ro.gdg.android.domain;

import java.util.Arrays;


public class TableBillsResponse  extends RestaurantResponse {

	private TableBill [] tBills;

	public TableBill [] getTBills() {
		return tBills;
	}

	public void setTBills(TableBill [] tBills) {
		this.tBills = tBills;
	}

	@Override
	public String toString() {
		return "TableBillsResponse [tBills=" + Arrays.toString(tBills)
				+ "]";
	}
	
}
