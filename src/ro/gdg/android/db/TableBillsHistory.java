package ro.gdg.android.db;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import ro.gdg.android.TablesActivity;
import ro.gdg.android.domain.Category;
import ro.gdg.android.domain.OrderedProduct;
import ro.gdg.android.domain.OrderedProductExtended;
import ro.gdg.android.domain.Product;
import ro.gdg.android.domain.TableBill;
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

	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			updateCursor();
		}
	};

	public static class CategoryBC implements BaseColumns {

		public static final String TABLE = "category";

		public static final String NAME = "name";

		public static final String SORT_DEFAULT = CategoryBC._ID + " ASC";

		public long id;
		public String name;

		private CategoryBC() {
		}

		public CategoryBC(long id, String name) {
			super();
			this.id = id;
			this.name = name;
		}

		public static CategoryBC readCategory(Cursor cursor) {
			CategoryBC category = new CategoryBC();
			category.id = cursor.getLong(cursor.getColumnIndex(CategoryBC._ID));
			category.name = cursor.getString(cursor
					.getColumnIndex(CategoryBC.NAME));
			return category;

		}
	}

	public static class ProductBC implements BaseColumns {

		public static final String TABLE = "product";

		public static final String NAME = "name";
		public static final String CATEGORY_ID = "category_id";
		public static final String PRICE = "price";

		public static final String SORT_DEFAULT = ProductBC._ID + " ASC";

		public long id;
		public String name;
		public long categoryId;
		public int price;

		private ProductBC() {
		}

		public ProductBC(long id, String name, long categoryId, int price) {
			super();
			this.id = id;
			this.name = name;
			this.categoryId = categoryId;
			this.price = price;
		}

		public static ProductBC readProduct(Cursor cursor) {
			ProductBC product = new ProductBC();
			product.id = cursor.getLong(cursor.getColumnIndex(ProductBC._ID));
			product.name = cursor.getString(cursor
					.getColumnIndex(ProductBC.NAME));
			product.categoryId = cursor.getLong(cursor
					.getColumnIndex(ProductBC.CATEGORY_ID));
			product.price = cursor.getInt(cursor
					.getColumnIndex(ProductBC.PRICE));
			return product;

		}
	}

	public static class TableBillBC implements BaseColumns {

		public static final String TABLE = "table_bill";

		public static final String WAITER_EMAIL = "waiter_email";
		public static final String TABLE_NUMBER = "table_number";
		public static final String DATE = "creation_time";
		public static final String STATUS = "status";

		public static final String SORT_DEFAULT = TableBillBC.DATE + " DESC";

		public long id;
		public String waiterEmail;
		public int tableNumber;
		public long date;
		public int status;

		private TableBillBC() {
		}

		public TableBillBC(long id, String waiterEmail, int tableNumber,
				long date, int status) {
			super();
			this.id = id;
			this.waiterEmail = waiterEmail;
			this.tableNumber = tableNumber;
			this.date = date;
			this.status = status;
		}

		public static TableBillBC readTableBill(Cursor cursor) {
			TableBillBC tableBill = new TableBillBC();
			tableBill.id = cursor.getLong(cursor
					.getColumnIndex(TableBillBC._ID));
			tableBill.waiterEmail = cursor.getString(cursor
					.getColumnIndex(TableBillBC.WAITER_EMAIL));
			tableBill.tableNumber = cursor.getInt(cursor
					.getColumnIndex(TableBillBC.TABLE_NUMBER));
			tableBill.date = cursor.getLong(cursor
					.getColumnIndex(TableBillBC.DATE));
			tableBill.status = cursor.getInt(cursor
					.getColumnIndex(TableBillBC.STATUS));
			return tableBill;

		}
	}

	public static class ProductOrderedBC implements BaseColumns {

		public static final String TABLE = "product_ordered";

		public static final String TABLE_BILL_ID = "table_bill_id";
		public static final String PRODUCT_ID = "product_id";
		public static final String STATE_ID = "state_id";
		public static final String EXTRA_INFO = "extra_info";

		public static final String SORT_DEFAULT = ProductOrderedBC.TABLE_BILL_ID
				+ " DESC";

		public long id;
		public long tableBillId;
		public long productId;
		public long stateId;
		public String extraInfo;

		private ProductOrderedBC() {
		}

		public ProductOrderedBC(long id, long tableBillId, long productId,
				long stateId, String extraInfo) {
			super();
			this.id = id;
			this.tableBillId = tableBillId;
			this.productId = productId;
			this.stateId = stateId;
			this.extraInfo = extraInfo;
		}

		public static ProductOrderedBC readProductOrdered(Cursor cursor) {
			ProductOrderedBC productOrdered = new ProductOrderedBC();
			productOrdered.id = cursor.getLong(cursor
					.getColumnIndex(ProductOrderedBC._ID));
			productOrdered.tableBillId = cursor.getLong(cursor
					.getColumnIndex(ProductOrderedBC.TABLE_BILL_ID));
			productOrdered.productId = cursor.getLong(cursor
					.getColumnIndex(ProductOrderedBC.PRODUCT_ID));
			productOrdered.stateId = cursor.getLong(cursor
					.getColumnIndex(ProductOrderedBC.STATE_ID));
			productOrdered.extraInfo = cursor.getString(cursor
					.getColumnIndex(ProductOrderedBC.EXTRA_INFO));
			return productOrdered;

		}

		public static OrderedProductExtended readProductOrderedExtended(
				Cursor cursor, TableBillsHistory db) {
			OrderedProductExtended orderedProductExtended = new OrderedProductExtended();
			orderedProductExtended
					.setProductName(db.getProductNameById(cursor.getLong(cursor
							.getColumnIndex(ProductOrderedBC.PRODUCT_ID))));
			orderedProductExtended
					.setStatus(cursor.getLong(cursor
							.getColumnIndex(ProductOrderedBC.STATE_ID)) == OrderedProduct.STATUS_ORDERED ? "ordered"
							: "ready");
			String extra = cursor.getString(cursor
					.getColumnIndex(ProductOrderedBC.EXTRA_INFO));
			Log.i(TAG, "readProductOrderedExtended extra=" + extra + "|");
			orderedProductExtended.setExtraInfo(extra == null ? "" : extra);
			return orderedProductExtended;

		}
	}

	public TableBillsHistory(Context context) {
		super(context, "restaurant.db", null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + CategoryBC.TABLE + " (" + CategoryBC._ID
				+ " INTEGER PRIMARY KEY," + CategoryBC.NAME + " STRING" + ");");

		db.execSQL("CREATE TABLE " + ProductBC.TABLE + " (" + ProductBC._ID
				+ " INTEGER PRIMARY KEY," + ProductBC.NAME + " STRING,"
				+ ProductBC.CATEGORY_ID + " LONG," + ProductBC.PRICE
				+ " INTEGER" + ");");

		db.execSQL("CREATE TABLE " + TableBillBC.TABLE + " (" + TableBillBC._ID
				+ " INTEGER PRIMARY KEY," + TableBillBC.WAITER_EMAIL
				+ " STRING," + TableBillBC.TABLE_NUMBER + " INTEGER,"
				+ TableBillBC.DATE + " LONG," + TableBillBC.STATUS + " INTEGER"
				+ ");");

		db.execSQL("CREATE TABLE " + ProductOrderedBC.TABLE + " ("
				+ ProductOrderedBC._ID + " INTEGER PRIMARY KEY,"
				+ ProductOrderedBC.TABLE_BILL_ID + " LONG,"
				+ ProductOrderedBC.PRODUCT_ID + " LONG,"
				+ ProductOrderedBC.STATE_ID + " LONG,"
				+ ProductOrderedBC.EXTRA_INFO + " TEXT" + ");");

		// TODO : is it necessary?
		// db.execSQL("CREATE UNIQUE INDEX VIN_INDEX ON " + TableBill.TABLE +
		// " ("
		// + TableBill.VIN + ")");

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
				+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + ProductOrderedBC.TABLE);
		db.execSQL("DROP TABLE IF EXISTS " + TableBillBC.TABLE);
		db.execSQL("DROP TABLE IF EXISTS " + ProductBC.TABLE);
		db.execSQL("DROP TABLE IF EXISTS " + CategoryBC.TABLE);
		onCreate(db);
	}

	// ===================== Table Bills ===================================

	public Cursor getAllTableBills() {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.query(TableBillBC.TABLE, null, null, null, null, null,
				null);

		lastCursorRef = new WeakReference<Cursor>(c);
		return c;
	}

	public int getNoOfTableBills() {
		int noOfTableBills;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TableBillBC.TABLE, null, null, null, null,
				null, null);
		noOfTableBills = cursor.getCount();
		cursor.close();
		return noOfTableBills;
	}

	public long addTableBill(String waiter, int tableNumber, int status) {
		long creationDate = System.currentTimeMillis();

		SQLiteDatabase db = this.getWritableDatabase();
		int count = 0;
		// new record
		ContentValues values = new ContentValues();
		values.put(TableBillBC.WAITER_EMAIL, waiter);
		values.put(TableBillBC.TABLE_NUMBER, tableNumber);
		values.put(TableBillBC.DATE, creationDate);
		values.put(TableBillBC.STATUS, status);
		long id = db.insert(TableBillBC.TABLE, null, values);
		if (id != -1) {
			Log.i(TAG, "addTableBill successful");
			count++;
		} // else failure

		notifyChanges(count);
		return id;
	}

	public int deleteAllTableBills() {
		SQLiteDatabase db = this.getWritableDatabase();
		int count = db.delete(TableBillBC.TABLE, "1", null);
		Log.i(TAG, "deleteAllTableBills count:" + count);
		int count2 = db.delete(ProductOrderedBC.TABLE, "1", null);
		Log.i(TAG, "deleteAllTableBills count:" + count2);

		notifyChanges(count + count2);
		return count;
	}

	public void deleteTableBill(long id) {
		Log.i(TAG, "deleteTableBill id:" + id);
		SQLiteDatabase db = this.getWritableDatabase();
		// delete first the ordered products
		int count = db
				.delete(ProductOrderedBC.TABLE, ProductOrderedBC.TABLE_BILL_ID
						+ "=?", new String[] { id + "" });
		Log.i(TAG, "deleteTableBill count deleted products:" + count);

		// delete the table bill
		count = db.delete(TableBillBC.TABLE, TableBillBC._ID + "=?",
				new String[] { id + "" });
		Log.i(TAG, "deleteTableBill count deleted bills:" + count);
	}

	public void closeTableBill(long id) {
		Log.i(TAG, "closeTableBill id:" + id);
		SQLiteDatabase db = this.getWritableDatabase();

		db.execSQL("update table_bill set status=1 where " + TableBillBC._ID
				+ "=?", new String[] { id + "" });
	}

	public long replaceAllTableBills(TableBill[] billsList) {
		Log.i(TAG, "replaceAllTableBills billsList length:" + billsList.length);
		SQLiteDatabase db = this.getWritableDatabase();
		int count = db.delete(TableBillBC.TABLE, "1", null);
		Log.i(TAG, "replaceAllTableBills count:" + count);
		int count2 = db.delete(ProductOrderedBC.TABLE, "1", null);
		Log.i(TAG, "replaceAllTableBills count2:" + count2);

		long noOfRecords = 0;
		ContentValues values = new ContentValues();

		for (int i = 0; i < billsList.length - 1; i++) {
			values.put(TableBillBC.WAITER_EMAIL, billsList[i].getWaiterEmail());
			values.put(TableBillBC.TABLE_NUMBER, billsList[i].getTableNumber());
			try {
				values.put(
						TableBillBC.DATE,
						formatter.parse(
								billsList[i].getDate().substring(0,
										(billsList[i].getDate().length() - 2)))
								.getTime());
			} catch (ParseException e) {
				Log.e("", "Parse exception: ", e);
			}
			values.put(TableBillBC.STATUS, billsList[i].getStatus());
			long tableBillId = db.insert(TableBillBC.TABLE, null, values);
			if (tableBillId != -1) {
				noOfRecords++;

				if (billsList[i].getProducts() != null) { // my table bill
					values = new ContentValues();
					for (OrderedProduct orderedProduct : billsList[i]
							.getProducts()) {
						values.put(ProductOrderedBC.TABLE_BILL_ID, tableBillId);
						values.put(ProductOrderedBC.PRODUCT_ID,
								orderedProduct.getProductId());
						values.put(ProductOrderedBC.STATE_ID,
								orderedProduct.getStateId());
						values.put(ProductOrderedBC.EXTRA_INFO,
								orderedProduct.getExtraInfo());
						db.insert(ProductOrderedBC.TABLE, null, values);
					}
				}
			}
		}
		Log.i(TAG, "replaceAllTableBills added bills count:" + noOfRecords);

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
		Cursor cursor = db.rawQuery("select " + TableBillBC.WAITER_EMAIL + ", "
				+ TableBillBC.STATUS + " from " + TableBillBC.TABLE + " where "
				+ TableBillBC.TABLE_NUMBER + "= ? order by " + TableBillBC.DATE
				+ " DESC", new String[] { tNumber });

		if (cursor.moveToFirst()) {
			Log.d(TAG, "getStateOfTable at least one bill with table "
					+ tableNo);
			if (cursor.getInt(cursor.getColumnIndex(TableBillBC.STATUS)) == TableBill.STATUS_OPEN) {
				if (waiterEmail.equalsIgnoreCase(cursor.getString(cursor
						.getColumnIndex(TableBillBC.WAITER_EMAIL)))) {
					Log.d(TAG, "getStateOfTable table " + tableNo
							+ " is MINE and is occupied");
					return TablesActivity.TABLE_OCCUPIED_MINE;
				} else {
					Log.d(TAG, "getStateOfTable table " + tableNo
							+ " is NOT MINE and is occupied");
					return TablesActivity.TABLE_OCCUPIED_OTHER;
				}
			}
		}
		cursor.close();
		Log.d(TAG, "getStateOfTable table " + tableNo + " is FREE");
		return TablesActivity.TABLE_FREE;
	}

	public boolean hasOpenBill(int tableNo) {
		String tNumber = tableNo + "";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery("select " + TableBillBC.WAITER_EMAIL + ", "
				+ TableBillBC.STATUS + " from " + TableBillBC.TABLE + " where "
				+ TableBillBC.TABLE_NUMBER + "= ? order by " + TableBillBC.DATE
				+ " DESC", new String[] { tNumber });

		if (cursor.moveToFirst()) {
			if (cursor.getInt(cursor.getColumnIndex(TableBillBC.STATUS)) == TableBill.STATUS_OPEN) {
				return true;

			}
		}
		cursor.close();
		return false;
	}

	public long tableBillIdAfterTable(int tableNo) {
		String tNumber = tableNo + "";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery("select " + TableBillBC._ID + ", "
				+ TableBillBC.STATUS + " from " + TableBillBC.TABLE + " where "
				+ TableBillBC.TABLE_NUMBER + "= ? order by " + TableBillBC.DATE
				+ " DESC", new String[] { tNumber });

		if (cursor.moveToFirst()) {
			if (cursor.getInt(cursor.getColumnIndex(TableBillBC.STATUS)) == TableBill.STATUS_OPEN) {
				return cursor.getLong(cursor.getColumnIndex(TableBillBC._ID));
			}
		}
		cursor.close();
		return -1;
	}

	// ======================= Menu =====================================

	public long replaceAllMenu(Category[] categories) {
		Log.i(TAG, "replaceAllMenu categories length:" + categories.length);
		SQLiteDatabase db = this.getWritableDatabase();
		int count = db.delete(ProductBC.TABLE, "1", null);
		Log.i(TAG, "replaceAllMenu deleted products count:" + count);
		count = db.delete(CategoryBC.TABLE, "1", null);
		Log.i(TAG, "replaceAllMenu deleted categories count:" + count);

		long noOfCategories = 0;
		long noOfProducts = 0;
		ContentValues values = new ContentValues();

		for (int i = 0; i < categories.length - 1; i++) {
			values.put(CategoryBC.NAME, categories[i].getName());
			long id = db.insert(CategoryBC.TABLE, null, values);
			Log.i(TAG, "replaceAllMenu inserted category id: " + id);
			if (id != -1) {
				noOfCategories++;
			}
			ContentValues values2 = new ContentValues();
			for (int j = 0; j < categories[i].getProducts().length; j++) {
				Log.i(TAG,
						"replaceAllMenu inserted product name: "
								+ categories[i].getProducts()[j].getName()
								+ " and categid=" + id + " and price="
								+ categories[i].getProducts()[j].getPrice());
				values2.put(ProductBC.NAME,
						categories[i].getProducts()[j].getName());
				values2.put(ProductBC.CATEGORY_ID, id);
				values2.put(ProductBC.PRICE,
						categories[i].getProducts()[j].getPrice());
				if (db.insert(ProductBC.TABLE, null, values2) != -1) {
					noOfProducts++;
				}
			}
		}
		Log.i(TAG, "replaceAllMenu added categories count:" + noOfCategories);
		Log.i(TAG, "replaceAllMenu added products count:" + noOfProducts);

		notifyChanges(noOfCategories + noOfProducts);
		return noOfCategories + noOfProducts;
	}

	public Cursor getAllCategories() {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.query(CategoryBC.TABLE, null, null, null, null, null,
				null);

		lastCursorRef = new WeakReference<Cursor>(c);
		return c;
	}

	public Cursor getProductsByCategory(String category) {
		Log.d(TAG, "getProductsByCategory name=" + category);
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db
				.rawQuery(
						"SELECT product._id AS _id, product._id AS product_id, product.name AS product_name, "
								+ "product.price AS product_price, category_id, category.name AS category_name "
								+ "FROM product INNER JOIN category ON (category_id = category._id) "
								+ "WHERE category.name = ? "
								+ "ORDER BY product._id",
						new String[] { category });

		lastCursorRef = new WeakReference<Cursor>(cursor);
		return cursor;
	}

	public Product getProductById(long id) {
		Log.d(TAG, "getProductById id=" + id);
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.rawQuery("SELECT * " + "FROM product "
				+ "WHERE product._id = ? ", new String[] { id + "" });

		if (cursor.moveToFirst()) {
			return new Product(cursor.getString(cursor
					.getColumnIndex(ProductBC.NAME)), cursor.getInt(cursor
					.getColumnIndex(ProductBC.CATEGORY_ID)),
					cursor.getInt(cursor.getColumnIndex(ProductBC.PRICE)));
		}
		return null;
	}

	public int getProductPriceById(long id) {
		Log.d(TAG, "getProductPriceById id=" + id);
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.rawQuery("SELECT * " + "FROM product "
				+ "WHERE product._id = ? ", new String[] { id + "" });

		if (cursor.moveToFirst()) {
			return cursor.getInt(cursor.getColumnIndex(ProductBC.PRICE));
		}
		return 0;
	}

	public String getProductNameById(long id) {
		Log.d(TAG, "getProductNameById id=" + id);
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.rawQuery("SELECT * " + "FROM product "
				+ "WHERE product._id = ? ", new String[] { id + "" });

		if (cursor.moveToFirst()) {
			return cursor.getString(cursor.getColumnIndex(ProductBC.NAME));
		}
		return null;
	}

	public Cursor getProductsByCategoryFiltered(String category, String filter) {
		Log.d(TAG, "getProductsByCategoryFiltered name=" + category
				+ " and filter=" + filter);
		SQLiteDatabase db = this.getReadableDatabase();

		filter = '%' + filter + '%';

		Cursor cursor = db
				.rawQuery(
						"SELECT product._id AS _id, product._id AS product_id, product.name AS product_name, "
								+ "product.price AS product_price, category_id, category.name AS category_name "
								+ "FROM product INNER JOIN category ON (category_id = category._id) "
								+ "WHERE category.name = ? and (category.name LIKE ? OR product.name LIKE ?) "
								+ "ORDER BY product._id", new String[] {
								category, filter, filter });

		lastCursorRef = new WeakReference<Cursor>(cursor);
		return cursor;
	}

	public int getCategoryIdByName(String categoryName) {
		Log.d(TAG, "getCategoryById name=" + categoryName);
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.rawQuery("select * from " + CategoryBC.TABLE
				+ " where " + CategoryBC.NAME + "= ? ",
				new String[] { categoryName });

		if (cursor.moveToFirst()) {
			return cursor.getInt(cursor.getColumnIndex(CategoryBC._ID));
		}

		return -1;
	}

	public Cursor getCategories() {
		Log.d(TAG, "getCategories");
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery("SELECT category.name AS category_name, "
				+ "MIN(product._id) as _id, COUNT(product._id) as count "
				+ "FROM category "
				+ "INNER JOIN product ON (product.category_id = category._id) "
				+ "GROUP BY category_name ORDER BY category._id", null);

		Log.d(TAG, "getCategories count=" + cursor.getCount());
		return cursor;
	}

	public Cursor getCategoriesFiltered(String filter) {
		Log.d(TAG, "getCategoriesFiltered filter=" + filter);
		SQLiteDatabase db = this.getReadableDatabase();
		filter = '%' + filter + '%';

		Cursor cursor = db.rawQuery("SELECT category.name AS category_name, "
				+ "MIN(product._id) as _id, COUNT(product._id) as count "
				+ "FROM category "
				+ "INNER JOIN product ON (product.category_id = category._id) "
				+ "WHERE product.name LIKE ? "
				+ "GROUP BY category_name  ORDER BY category._id",
				new String[] { filter });

		Log.d(TAG, "getCategoriesFiltered count=" + cursor.getCount());
		return cursor;
	}

	public long addOrderedProduct(long tableBillId, long productId,
			int stateId, String extraInfo) {
		SQLiteDatabase db = this.getWritableDatabase();
		int count = 0;
		// new record
		ContentValues values = new ContentValues();
		values.put(ProductOrderedBC.TABLE_BILL_ID, tableBillId);
		values.put(ProductOrderedBC.PRODUCT_ID, productId);
		values.put(ProductOrderedBC.STATE_ID, stateId);
		values.put(ProductOrderedBC.EXTRA_INFO, extraInfo);
		long id = db.insert(ProductOrderedBC.TABLE, null, values);
		if (id != -1) {
			Log.i(TAG, "addOrderedProduct successful");
			count++;
		} // else failure

		notifyChanges(count);
		return id;
	}

	public Cursor getOrderedProductsOfBill(long tableBillId) {
		Log.d(TAG, "getOrderedProductsOfBill tableBillId=" + tableBillId);
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery("SELECT * " + "FROM product_ordered "
				+ "WHERE table_bill_id=?", new String[] { tableBillId + "" });

		Log.d(TAG, "getOrderedProductsOfBill count=" + cursor.getCount());
		return cursor;
	}

	public int getOrderedProductsTotalOfBill(long tableBillId) {
		Log.d(TAG, "getOrderedProductsTotalOfBill tableBillId=" + tableBillId);
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery("SELECT * " + "FROM product_ordered "
				+ "WHERE table_bill_id=?", new String[] { tableBillId + "" });
		int total = 0;
		Log.d(TAG, "getOrderedProductsOfBill count=" + cursor.getCount());
		while (cursor.moveToNext()) {
			long productId = cursor.getInt(cursor
					.getColumnIndex(ProductOrderedBC.PRODUCT_ID));
			total += getProductPriceById(productId);
		}
		return total;
	}

	public void deleteOrderedProduct(long id) {
		Log.i(TAG, "deleteOrderedProduct id:" + id);
		SQLiteDatabase db = this.getWritableDatabase();
		int count = db.delete(ProductOrderedBC.TABLE, ProductOrderedBC._ID
				+ "=?", new String[] { id + "" });
		Log.i(TAG, "deleteOrderedProduct count deleted products:" + count);
		notifyChanges(count);
	}

	// ======================= Other =====================================

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
