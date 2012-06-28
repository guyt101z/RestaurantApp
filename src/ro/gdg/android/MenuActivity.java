package ro.gdg.android;

import java.util.ArrayList;

import ro.gdg.android.api.RestaurantServiceClient;
import ro.gdg.android.domain.OrderedProduct;
import ro.gdg.android.ui.adapter.AbstractCheckableTreeAdapter.SelectionListener;
import ro.gdg.android.ui.adapter.ProductTreeAdapter;
import android.app.Activity;
import android.app.ExpandableListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.Filter.FilterListener;

public class MenuActivity extends ExpandableListActivity implements
		TextWatcher, SelectionListener, FilterListener {

	private static final String TAG = MenuActivity.class.getSimpleName();
	private static final int ADD_PRODUCT = 1;

	ProductTreeAdapter adapter;
	EditText filterText;
	private Button finishButton;
	ArrayList<OrderedProduct> orderedProducts;
	RestaurantServiceClient serviceClient;
	OrderedProduct orderedProduct;
	int tableNo;
	long billId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.menu);

		Intent intent = getIntent();
		tableNo = intent.getIntExtra(TablesActivity.TABLE_NO_EXTRA, 0);
		billId = intent.getLongExtra(TableBillActivity.EXTRA_TABLE_BILL_ID, -1);

		filterText = (EditText) this.findViewById(R.id.filterText);
		finishButton = (Button) this.findViewById(R.id.action_bar_finish);

		serviceClient = ((RestaurantApplication) getApplication())
				.getRestaurantServiceClient();
		adapter = new ProductTreeAdapter(this);
		orderedProducts = new ArrayList<OrderedProduct>();

		setListAdapter(adapter);
		filterText.addTextChangedListener(this);
		adapter.setSelectionListener(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.ExpandableListActivity#onChildClick(android.widget.
	 * ExpandableListView, android.view.View, int, int, long)
	 */
	@Override
	public boolean onChildClick(ExpandableListView parent, View v,
			int groupPosition, int childPosition, long id) {
		Log.d(TAG, "onChildClick id=" + id);
		orderedProduct = new OrderedProduct(id, OrderedProduct.STATUS_ORDERED);
		String name = serviceClient.getProductNameById(id);

		if (name != null) {
			Intent intent = new Intent(this, AddProductDialogActivity.class);
			intent.putExtra(AddProductDialogActivity.EXTRA_PRODUCT_NAME, name);
			startActivityForResult(intent, ADD_PRODUCT);
		} else {
			Log.e(TAG, "onChildClick of id=" + id + " the name is null!");
		}

		return true;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case ADD_PRODUCT:
			if (resultCode == Activity.RESULT_OK) {
				String extraInfo = data
						.getStringExtra(AddProductDialogActivity.EXTRA_INFO);
				Log.d(TAG, "extra info added: " + extraInfo);
				if (extraInfo != null) {
					orderedProduct.setExtraInfo(extraInfo);
				} else {
					orderedProduct.setExtraInfo("");
				}
				orderedProducts.add(orderedProduct);

				finishButton.setEnabled(true);
			}
			break;
		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onFilterComplete(int count) {
		ExpandableListView listView = getExpandableListView();
		if (filterText.getText().length() > 0) {
			for (int i = 0; i < count; i++) {
				listView.expandGroup(i);
			}
		} else {
			for (int i = 0; i < count; i++) {
				listView.collapseGroup(i);
			}
		}
	}

	@Override
	public void afterTextChanged(Editable s) {
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		adapter.getFilter().filter(s, this);
	}

	@Override
	public void onChangeSelection(int count) {
		Log.d(TAG, "onChangeSelection count=" + count);
	}

	public void finish(View view) {
		Log.d(TAG, "finish orderedProducts count = " + orderedProducts.size());

		if (billId == -1) { // new table bill
			// add to db
			long tableBillId = serviceClient.addTableBillToHistory(
					serviceClient.getUserLogin().getAccountEmail(), tableNo);

			addOrderedProducts(tableBillId);

			// go to bill
			Intent intent = new Intent(this, TableBillActivity.class);
			intent.putExtra(TableBillActivity.EXTRA_TABLE_BILL_ID, tableBillId);
			startActivity(intent);
		} else {
			addOrderedProducts(billId);

			// go back to bill
			Intent intent = new Intent();
			setResult(RESULT_OK, intent);
		}
		// remove from activity stack
		finish();

	}

	public void addOrderedProducts(long tableBillId) {
		for (OrderedProduct orProduct : orderedProducts) {
			orProduct.setTableBillId(tableBillId);
			serviceClient.addOrderedProductToDB(tableBillId,
					orProduct.getProductId(), orProduct.getStateId(),
					orProduct.getExtraInfo());
		}
	}
}
