package ro.gdg.android.domain;

import java.util.Arrays;


public class TableBillsResponse  extends RestaurantResponse {

	private TBill [] tBills;

	public TBill [] getTBills() {
		return tBills;
	}

	public void setTBills(TBill [] tBills) {
		this.tBills = tBills;
	}

	@Override
	public String toString() {
		return "TableBillsResponse [tBills=" + Arrays.toString(tBills)
				+ "]";
	}
	
}
