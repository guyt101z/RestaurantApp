package ro.gdg.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

public class AddProductDialogActivity extends Activity {

	public static final String EXTRA_INFO = "extra_info";
	public static final String EXTRA_PRODUCT_NAME = "extra_product_name";

	private TextView productName;
	private EditText extraInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.add_product_dialog);

		Intent intent = getIntent();
		String name = intent.getStringExtra(EXTRA_PRODUCT_NAME);

		productName = (TextView) findViewById(R.id.productName);
		productName.setText(name);

		extraInfo = (EditText) findViewById(R.id.extraInfoText);
	}

	public void add(View view) {
		Intent intent = new Intent();
		if (extraInfo.getText().toString().trim().length() != 0) {
			intent.putExtra(EXTRA_INFO, extraInfo.getText().toString());
		}
		setResult(RESULT_OK, intent);
		finish();
	}

	public void cancel(View view) {
		Intent intent = new Intent();
		if (extraInfo.getText().toString().trim().length() != 0) {
			intent.putExtra(EXTRA_INFO, extraInfo.getText().toString());
		}
		setResult(RESULT_CANCELED, intent);
		finish();
	}
}
