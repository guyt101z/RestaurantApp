package ro.gdg.android;

import ro.gdg.android.util.SelectionListener;
import android.app.ExpandableListActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Filter.FilterListener;

public class MenuActivity extends ExpandableListActivity implements
		TextWatcher, SelectionListener, FilterListener {

	@Override
	public void onFilterComplete(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onChangeSelection(int count) {
		// TODO Auto-generated method stub

	}

	public void finish(View view) {
		// TODO
	}

}
