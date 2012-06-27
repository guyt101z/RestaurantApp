package ro.gdg.android.ui.adapter;

import ro.gdg.android.R;
import ro.gdg.android.db.TableBillsHistory;
import ro.gdg.android.db.TableBillsHistory.ProductOrderedBC;
import ro.gdg.android.domain.OrderedProductExtended;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

public class OrderedProductsListAdapter extends ResourceCursorAdapter {

	private static final String TAG = OrderedProductsListAdapter.class
			.getSimpleName();

	private TableBillsHistory db;

	public OrderedProductsListAdapter(Context context, TableBillsHistory db,
			int layout, Cursor c, boolean autoRequery) {
		super(context, layout, c, autoRequery);
		this.db = db;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.CursorAdapter#bindView(android.view.View,
	 * android.content.Context, android.database.Cursor)
	 */
	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		OrderedProductExtended productOrdered = ProductOrderedBC
				.readProductOrderedExtended(cursor, db);
		Log.d(TAG, "bindView productOrdered = " + productOrdered.toString());
		((TextView) view.findViewById(R.id.orderedProductName))
				.setText(productOrdered.getProductName());
		((TextView) view.findViewById(R.id.extraText))
				.setText(productOrdered.getExtraInfo());
		((TextView) view.findViewById(R.id.status)).setText(productOrdered
				.getStatus());
	}

	@Override
	public void changeCursor(Cursor cursor) {
		Log.d(TAG, "changeCursor - count: " + cursor.getCount());
		super.changeCursor(cursor);
	}

}
