package ro.gdg.android.domain;

import java.util.Arrays;


public class TableBillsResponse  extends RestaurantResponse {

	private TBill [] tableBills;

	public TBill [] getTableBills() {
		return tableBills;
	}

	public void setTableBills(TBill [] tableBills) {
		this.tableBills = tableBills;
	}

	@Override
	public String toString() {
		return "TableBillsResponse [tBills=" + Arrays.toString(tableBills)
				+ "]";
	}
	
}
