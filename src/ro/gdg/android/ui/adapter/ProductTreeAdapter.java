package ro.gdg.android.ui.adapter;

import ro.gdg.android.R;
import ro.gdg.android.RestaurantApplication;
import ro.gdg.android.api.RestaurantServiceClient;
import ro.gdg.android.ui.support.SimpleCursorTreeAdapter.ViewBinder;
import android.app.Activity;
import android.database.Cursor;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class ProductTreeAdapter extends AbstractCheckableTreeAdapter implements
		ViewBinder {

	static final String TAG = "ProductTreeAdapter";

	Activity activity;

	RestaurantServiceClient serviceClient;

	volatile CharSequence constraint;

	public ProductTreeAdapter(Activity activity) {
		super(activity, null, R.layout.menu_category,
				new String[] { "category_name" }, new int[] { R.id.textName },
				R.layout.menu_product_instance, new String[] { "product_name",
						"product_price" }, new int[] { R.id.productName,
						R.id.priceValue }, "product_id");

		Log.d(TAG, "onCreate");
		this.activity = activity;
		this.setViewBinder(this);
		serviceClient = ((RestaurantApplication) activity.getApplication())
				.getRestaurantServiceClient();

		// initialize using a filter so it's loading in background
		this.getFilter().filter(null);
	}

	@Override
	protected Cursor getChildrenCursor(Cursor groupCursor) {
		Log.d(TAG, "getChildrenCursor");
		String category = groupCursor.getString(0);

		Log.d(TAG, "getChildrenCursor(" + category + ") thread: "
				+ Thread.currentThread().getName());
		// TODO load cursor asynch
		Cursor cursor;

		if (constraint == null || constraint.toString().trim().length() == 0) {
			cursor = serviceClient.getTableBillsHistory()
					.getProductsByCategory(category);
		} else {
			cursor = serviceClient.getTableBillsHistory()
					.getProductsByCategoryFiltered(category,
							constraint.toString().trim());
		}

		if (Build.VERSION.SDK_INT < 11 || Build.VERSION.SDK_INT > 13) {
			activity.startManagingCursor(cursor);
		}
		return cursor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ro.gdg.android.ui.support.SimpleCursorTreeAdapter.ViewBinder#setViewValue
	 * (android.view.View, android.database.Cursor, int)
	 */
	public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
		Log.d(TAG, "setViewValue columnIndex=" + columnIndex);
		switch (view.getId()) {
		case R.id.productName: {
			((TextView) view).setText(cursor.getString(columnIndex));
			view.setVisibility(View.VISIBLE);
			return true;
		}
		case R.id.priceValue: {
			((TextView) view).setText(cursor.getString(columnIndex));
			view.setVisibility(View.VISIBLE);
			return true;
		}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.widget.CursorTreeAdapter#runQueryOnBackgroundThread(java.lang
	 * .CharSequence)
	 */
	@Override
	public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
		// // TODO Auto-generated method stub
		// return super.runQueryOnBackgroundThread(constraint);
		Log.d(TAG, "runQueryOnBackgroundThread(" + constraint + ") thread: "
				+ Thread.currentThread().getName());

		this.constraint = constraint;
		Cursor cursor = null;
		if (constraint == null || constraint.toString().trim().length() == 0) {
			cursor = serviceClient.getTableBillsHistory().getCategories();
		} else {
			cursor = serviceClient.getTableBillsHistory()
					.getCategoriesFiltered(constraint.toString().trim());
		}

		activity.startManagingCursor(cursor);

		return cursor;
	}

	@Override
	public Cursor getChild(int groupPosition, int childPosition) {
		Log.d(TAG, "getChild groupPosition=" + groupPosition + "  and childPosition=" + childPosition);
		return super.getChild(groupPosition, childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		Log.d(TAG, "getChildId groupPosition=" + groupPosition + "  and childPosition=" + childPosition);
		return super.getChildId(groupPosition, childPosition);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.widget.CursorTreeAdapter#changeCursor(android.database.Cursor)
	 */
	@Override
	public void changeCursor(Cursor cursor) {
		Log.d(TAG, "changeCursor");
		super.changeCursor(cursor);
		fullCheckOfSelection();
		notifyDataSetChanged(false);
	}
}