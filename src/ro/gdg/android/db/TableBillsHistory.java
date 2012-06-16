package ro.gdg.android.db;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import ro.gdg.android.TablesActivity;
import ro.gdg.android.domain.TBill;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Handler;
import android.os.Message;
import android.provider.BaseColumns;
import android.util.Log;

public class TableBillsHistory extends SQLiteOpenHelper {

	private static final String TAG = TableBillsHistory.class.getSimpleName();

	private static int DATABASE_VERSION = 1;

	WeakReference<Cursor> lastCursorRef = new WeakReference<Cursor>(null);

	SimpleDateFormat formatter = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			updateCursor();
		}
	};

	public static class TableBill implements BaseColumns {

		public static final String TABLE = "table_bill";

		public static final String WAITER_EMAIL = "waiter_email";
		public static final String TABLE_NUMBER = "table_number";
		public static final String DATE = "creation_time";
		public static final String STATUS = "status";

		public static final String SORT_DEFAULT = TableBill.DATE + " DESC";

		public long id;
		public String waiterEmail;
		public int tableNumber;
		public long date;
		public int status;

		private TableBill() {
		}

		public TableBill(long id, String waiterEmail, int tableNumber,
				long date, int status) {
			super();
			this.id = id;
			this.waiterEmail = waiterEmail;
			this.tableNumber = tableNumber;
			this.date = date;
			this.status = status;
		}

		public static TableBill readTableBill(Cursor cursor) {
			TableBill tableBill = new TableBill();
			tableBill.id = cursor.getLong(cursor.getColumnIndex(TableBill._ID));
			tableBill.waiterEmail = cursor.getString(cursor
					.getColumnIndex(TableBill.WAITER_EMAIL));
			tableBill.tableNumber = cursor.getInt(cursor
					.getColumnIndex(TableBill.TABLE_NUMBER));
			tableBill.date = cursor.getLong(cursor
					.getColumnIndex(TableBill.DATE));
			tableBill.status = cursor.getInt(cursor
					.getColumnIndex(TableBill.STATUS));
			return tableBill;

		}
	}

	public static class Category implements BaseColumns {

		public static final String TABLE = "category";

		public static final String NAME = "name";

		public static final String SORT_DEFAULT = Category.NAME + " ASC";

		public long id;
		public String name;

		private Category() {
		}

		public Category(long id, String name) {
			super();
			this.id = id;
			this.name = name;
		}

		public static Category readCategory(Cursor cursor) {
			Category category = new Category();
			category.id = cursor.getLong(cursor.getColumnIndex(Category._ID));
			category.name = cursor.getString(cursor
					.getColumnIndex(Category.NAME));
			return category;

		}
	}

	public static class Product implements BaseColumns {

		public static final String TABLE = "product";

		public static final String NAME = "name";
		public static final String CATEGORY_ID = "category_id";
		public static final String PRICE = "price";

		public static final String SORT_DEFAULT = Product.CATEGORY_ID + " ASC";

		public long id;
		public String name;
		public long categoryId;
		public int price;

		private Product() {
		}

		public Product(long id, String name, long categoryId, int price) {
			super();
			this.id = id;
			this.name = name;
			this.categoryId = categoryId;
			this.price = price;
		}

		public static Product readProduct(Cursor cursor) {
			Product product = new Product();
			product.id = cursor.getLong(cursor.getColumnIndex(Product._ID));
			product.name = cursor
					.getString(cursor.getColumnIndex(Product.NAME));
			product.categoryId = cursor.getLong(cursor
					.getColumnIndex(Product.CATEGORY_ID));
			product.price = cursor.getInt(cursor.getColumnIndex(Product.PRICE));
			return product;

		}
	}

	public static class ProductOrdered implements BaseColumns {

		public static final String TABLE = "product_ordered";

		public static final String TABLE_BILL_ID = "table_bill_id";
		public static final String PRODUCT_ID = "product_id";
		public static final String STATE_ID = "state_id";
		public static final String EXTRA_INFO = "extra_info";

		public static final String SORT_DEFAULT = ProductOrdered.TABLE_BILL_ID
				+ " DESC";

		public long id;
		public long tableBillId;
		public long productId;
		public long stateId;
		public String extraInfo;

		private ProductOrdered() {
		}

		public ProductOrdered(long id, long tableBillId, long productId,
				long stateId, String extraInfo) {
			super();
			this.id = id;
			this.tableBillId = tableBillId;
			this.productId = productId;
			this.stateId = stateId;
			this.extraInfo = extraInfo;
		}

		public static ProductOrdered readProductOrdered(Cursor cursor) {
			ProductOrdered productOrdered = new ProductOrdered();
			productOrdered.id = cursor.getLong(cursor
					.getColumnIndex(ProductOrdered._ID));
			productOrdered.tableBillId = cursor.getLong(cursor
					.getColumnIndex(ProductOrdered.TABLE_BILL_ID));
			productOrdered.productId = cursor.getLong(cursor
					.getColumnIndex(ProductOrdered.PRODUCT_ID));
			productOrdered.stateId = cursor.getLong(cursor
					.getColumnIndex(ProductOrdered.STATE_ID));
			productOrdered.extraInfo = cursor.getString(cursor
					.getColumnIndex(ProductOrdered.EXTRA_INFO));
			return productOrdered;

		}
	}

	public TableBillsHistory(Context context) {
		super(context, "restaurant.db", null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + Category.TABLE + " (" + Category._ID
				+ " INTEGER PRIMARY KEY," + Category.NAME + " STRING" + ");");

		db.execSQL("CREATE TABLE " + Product.TABLE + " (" + Product._ID
				+ " INTEGER PRIMARY KEY," + Product.NAME + " STRING,"
				+ Product.CATEGORY_ID + " LONG," + Product.PRICE + " INTEGER"
				+ ");");

		db.execSQL("CREATE TABLE " + TableBill.TABLE + " (" + TableBill._ID
				+ " INTEGER PRIMARY KEY," + TableBill.WAITER_EMAIL + " STRING,"
				+ TableBill.TABLE_NUMBER + " INTEGER," + TableBill.DATE
				+ " LONG," + TableBill.STATUS + " INTEGER" + ");");

		db.execSQL("CREATE TABLE " + ProductOrdered.TABLE + " ("
				+ ProductOrdered._ID + " INTEGER PRIMARY KEY,"
				+ ProductOrdered.TABLE_BILL_ID + " LONG,"
				+ ProductOrdered.PRODUCT_ID + " LONG,"
				+ ProductOrdered.STATE_ID + " LONG,"
				+ ProductOrdered.EXTRA_INFO + " TEXT" + ");");

		// TODO : is it necessary?
		// db.execSQL("CREATE UNIQUE INDEX VIN_INDEX ON " + TableBill.TABLE +
		// " ("
		// + TableBill.VIN + ")");

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
				+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + ProductOrdered.TABLE);
		db.execSQL("DROP TABLE IF EXISTS " + TableBill.TABLE);
		db.execSQL("DROP TABLE IF EXISTS " + Product.TABLE);
		db.execSQL("DROP TABLE IF EXISTS " + Category.TABLE);
		onCreate(db);
	}

	public Cursor getAllTableBills() {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db
				.query(TableBill.TABLE, null, null, null, null, null, null);

		lastCursorRef = new WeakReference<Cursor>(c);
		return c;
	}

	public int getNoOfTableBills() {
		int noOfTableBills;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TableBill.TABLE, null, null, null, null, null,
				null);
		noOfTableBills = cursor.getCount();
		cursor.close();
		return noOfTableBills;
	}

	public TableBill addTableBill(String waiter, int tableNumber, int status) {
		long creationDate = System.currentTimeMillis();

		SQLiteDatabase db = this.getWritableDatabase();
		TableBill bill = null;
		int count = 0;
		// new record
		ContentValues values = new ContentValues();
		values.put(TableBill.WAITER_EMAIL, waiter);
		values.put(TableBill.TABLE_NUMBER, tableNumber);
		values.put(TableBill.DATE, creationDate);
		values.put(TableBill.STATUS, status);
		long id = db.insert(TableBill.TABLE, null, values);
		if (id != -1) {
			Log.i(TAG, "addTableBill successful");
			bill = new TableBill(id, waiter, tableNumber, creationDate, status);
			count++;
		} // else failure

		notifyChanges(count);
		return bill;
	}

	public int deleteAllTableBills() {
		SQLiteDatabase db = this.getWritableDatabase();
		int count = db.delete(TableBill.TABLE, "1", null);
		Log.i(TAG, "deleteAllTableBills count:" + count);

		notifyChanges(count);
		return count;
	}

	public long replaceAllTableBills(TBill[] billsList) {
		Log.i(TAG, "deleteAllTableBills billsList length:" + billsList.length);
		SQLiteDatabase db = this.getWritableDatabase();
		int count = db.delete(TableBill.TABLE, "1", null);
		Log.i(TAG, "deleteAllTableBills count:" + count);

		long noOfRecords = 0;
		ContentValues values = new ContentValues();

		for (int i = 0; i < billsList.length-1; i++) {
			values.put(TableBill.WAITER_EMAIL, billsList[i].getWaiterEmail());
			values.put(TableBill.TABLE_NUMBER, billsList[i].getTableNumber());
			try {
				values.put(TableBill.DATE,
						formatter.parse(billsList[i].getDate().substring(0, (billsList[i].getDate().length()-2))).getTime());
			} catch (ParseException e) {
				Log.e("", "Parse exception: ", e);
			}
			values.put(TableBill.STATUS, billsList[i].getStatus());
			if (db.insert(TableBill.TABLE, null, values) != -1) {
				noOfRecords++;
			}
		}
		Log.i(TAG, "addAllRecords count:" + noOfRecords);

		notifyChanges(noOfRecords);
		return noOfRecords;
	}

	// public boolean isInHistory(String vin) {
	// boolean isInHistory;
	// SQLiteDatabase db = this.getReadableDatabase();
	// Cursor cursor = db.query(TableBill.TABLE, null, TableBill.VIN + "=?",
	// new String[] { vin }, null, null, null);
	//
	// isInHistory = cursor.moveToFirst();
	// cursor.close();
	// return isInHistory;
	// }

	public int getStateOfTable(String waiterEmail, int tableNo) {
		Log.d(TAG, "getStateOfTable no=" + tableNo);
		String tNumber = tableNo + "";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery("select " + TableBill.WAITER_EMAIL + ", "
				+ TableBill.STATUS + " from " + TableBill.TABLE + " where "
				+ TableBill.TABLE_NUMBER + "= ? order by " + TableBill.DATE
				+ " DESC", new String[] { tNumber });

		if (cursor.moveToFirst()) {
			Log.d(TAG, "getStateOfTable at least one bill with table " + tableNo);
			if (cursor.getInt(cursor.getColumnIndex(TableBill.STATUS)) == TBill.STATUS_OPEN) {
				if (waiterEmail.equalsIgnoreCase(cursor.getString(cursor
						.getColumnIndex(TableBill.WAITER_EMAIL)))) {
					Log.d(TAG, "getStateOfTable table " + tableNo + " is MINE and is occupied");
					return TablesActivity.TABLE_OCCUPIED_MINE;
				} else {
					Log.d(TAG, "getStateOfTable table " + tableNo + " is NOT MINE and is occupied");
					return TablesActivity.TABLE_OCCUPIED_OTHER;
				}
			}
		}
		cursor.close();
		Log.d(TAG, "getStateOfTable table " + tableNo + " is FREE");
		return TablesActivity.TABLE_FREE;
	}

	public boolean hasOpenTableBill(int tableNo) {
		String tNumber = tableNo + "";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery("select " + TableBill.WAITER_EMAIL + ", "
				+ TableBill.STATUS + " from " + TableBill.TABLE + " where "
				+ TableBill.TABLE_NUMBER + "= ? order by " + TableBill.DATE
				+ " DESC", new String[] { tNumber });

		if (cursor.moveToFirst()) {
			if (cursor.getInt(cursor.getColumnIndex(TableBill.STATUS)) == TBill.STATUS_OPEN) {
				return true;

			}
		}
		cursor.close();
		return false;
	}

	public void notifyChanges(long count) {
		if (!handler.hasMessages(0) && count > 0) {
			handler.sendEmptyMessage(0);
		}
	}

	public void updateCursor() {
		Log.i(TAG, "update cursor");
		Cursor c = lastCursorRef.get();
		if (c != null && !c.isClosed()) {
			c.requery();
		}
	}
}
