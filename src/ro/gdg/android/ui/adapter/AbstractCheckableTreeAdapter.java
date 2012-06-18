package ro.gdg.android.ui.adapter;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import ro.gdg.android.R;
import ro.gdg.android.ui.support.SimpleCursorTreeAdapter;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

public abstract class AbstractCheckableTreeAdapter extends
		SimpleCursorTreeAdapter implements OnClickListener {
	
	private static final String TAG = AbstractCheckableTreeAdapter.class.getSimpleName();

	String idColumnName;

	private Set<String> checkedIds = new HashSet<String>();
	// TODO persist this;
	/*
	 * store the number of selected children for each group position
	 */
	private SparseIntArray checkedCount = new SparseIntArray();

	private SelectionListener selectionListener;

	/**
	 * @return the selectionListener
	 */
	public SelectionListener getSelectionListener() {
		Log.d(TAG, "getSelectionListener");
		return selectionListener;
	}

	/**
	 * @param selectionListener
	 *            the selectionListener to set
	 */
	public void setSelectionListener(SelectionListener selectionListener) {
		this.selectionListener = selectionListener;
	}

	public AbstractCheckableTreeAdapter(Context context, Cursor cursor,
			int groupLayout, String[] groupFrom, int[] groupTo,
			int childLayout, String[] childFrom, int[] childTo,
			String idColumnName) {
		super(context, cursor, groupLayout, groupFrom, groupTo, childLayout,
				childFrom, childTo);
		this.idColumnName = idColumnName;
		Log.d(TAG, "constructor idColumnName=" + idColumnName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.CursorTreeAdapter#getGroupView(int, boolean,
	 * android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getGroupView(final int groupPosition, boolean isExpanded,
			View convertView, final ViewGroup parent) {
		View view = super.getGroupView(groupPosition, isExpanded, convertView,
				parent);
		Log.d(TAG, "getGroupView");
		int count = checkedCount.get(groupPosition, 0);
		Log.d(TAG, "getGroupView count=" + count);
		TextView selectCount = (TextView) view.findViewById(R.id.textCount);

		if (count > 0) {
			selectCount.setText(count + " selected");
		} else {
			selectCount.setText(null);
		}

		return view;
	}

	public void toggleChecked(View v, int groupPosition, int childPosition,
			long id) {
		Log.d(TAG, "toggleChecked");
		Cursor cursor = this.getChild(groupPosition, childPosition);
		String serverId = cursor.getString(cursor.getColumnIndex(idColumnName));
		Log.d(TAG, "toggleChecked serverid= " + serverId);
		boolean exist = checkedIds.remove(serverId);
		Log.d(TAG, "toggleChecked exist = " + exist);
		if (!exist) {
			checkedIds.add(serverId);
		}
		int count = checkedCount.get(groupPosition, 0);
		Log.d(TAG, "toggleChecked count = " + count);
		count += exist ? -1 : 1;
		checkedCount.put(groupPosition, count);

		checkOthersGroups(groupPosition, exist, serverId);

		notifySelectionListener();
		notifyDataSetChanged(false);
		// we need to also update the group view
		// ((CheckedTextView)v.findViewById(R.id.checkBox)).setChecked(!exist);
	}

	public String[] getCheckedIds() {
		Log.d(TAG, "getCheckedIds");
		return checkedIds.toArray(new String[checkedIds.size()]);
	}

	public void setCheckedIds(String[] ids) {
		Log.d(TAG, "setCheckedIds");
		checkedIds.clear();
		Collections.addAll(checkedIds, ids);

		// TODO do better store restore instead of going to all data
		fullCheckOfSelection();

		notifyDataSetChanged(false);
	}

	private void notifySelectionListener() {
		Log.d(TAG, "notifySelectionListener checkedIds size=" + checkedIds.size());
		if (selectionListener != null) {
			selectionListener.onChangeSelection(checkedIds.size());
		}
	}

	// handle select all/none
	public void onClick(View v) {
		Log.d(TAG, "onClick");
		// ??
	}

	protected void checkOthersGroups(int groupPosition, boolean check,
			String... array) {
		Log.d(TAG, "checkOthersGroups");
		// TODO Auto-generated method stub
	}

	boolean isCheked(String id) {
		Log.d(TAG, "isCheked id=" + id);
		return checkedIds.contains(id);
	}

	public void fullCheckOfSelection() {
		Log.d(TAG, "fullCheckOfSelection");
		checkedCount.clear();// clear everything
		if (checkedIds.size() == 0) {
			return;
		}

		int groupCount = this.getGroupCount();

		for (int groupPosition = 0; groupPosition < groupCount; groupPosition++) {
			int childrenCount = getChildrenCount(groupPosition);
			int countSelected = 0;
			for (int childPosition = 0; childPosition < childrenCount; childPosition++) {
				Cursor cursor = this.getChild(groupPosition, childPosition);
				String contatcServerId = cursor.getString(cursor
						.getColumnIndex(idColumnName));
				if (checkedIds.contains(contatcServerId)) {
					countSelected++;
				}
				if (countSelected > 0) {
					checkedCount.put(groupPosition, countSelected);
				} // else by default there is no selection
			}
		}
	}

	public int getSelectionCount() {
		Log.d(TAG, "getSelectionCount");
		return checkedIds.size();
	}

	static public interface SelectionListener {
		void onChangeSelection(int count);
	}
}
