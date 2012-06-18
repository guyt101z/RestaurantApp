package ro.gdg.android;

import ro.gdg.android.ui.adapter.AbstractCheckableTreeAdapter.SelectionListener;
import ro.gdg.android.ui.adapter.ProductTreeAdapter;
import android.app.ExpandableListActivity;
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

	ProductTreeAdapter adapter;
	EditText filterText;
	private Button finishButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.menu);

		filterText = (EditText) this.findViewById(R.id.filterText);
		finishButton = (Button) this.findViewById(R.id.action_bar_finish);

		adapter = new ProductTreeAdapter(this);

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
		Log.d(TAG, "onChildClick");
		// TODO add to bill
		return true;
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
		if (count == 0) {
			finishButton.setEnabled(false);
		} else {
			finishButton.setEnabled(true);
		}
	}

	public void finish(View view) {
		Log.d(TAG, "finish");
		// TODO go to bill
	}
}
