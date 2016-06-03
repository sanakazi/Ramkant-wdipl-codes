package com.wdipl.json;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

@SuppressLint("SdCardPath")
public class Database extends SQLiteOpenHelper {
	private Context myContext;
	int i = 0;
	private final String DB_PATH = "/data/data/com.wdipl.bloodbank/databases/";
	private static String DB_NAME = "bloodbank";
	public SQLiteDatabase myDatabase;
	Resources resources;

	public Database(Context context) throws IOException {
		super(context, DB_NAME, null, 1);
		this.myContext = context;
		resources = context.getResources();
		boolean dbExist = checkDatabase();
		i++;
		if (dbExist) {
			System.out.println("Database exists- opens " + i + " times.");
			openDatabase();

			System.out.println("search engine table created..........");

		} else {
			System.out.println("Database doesn't exist");
			createDatabase();
			System.out.println("Database created");
			openDatabase();

			System.out.println("Registered.");

		}

	}

	public void createDatabase() throws IOException {
		boolean dbExist = checkDatabase();
		if (dbExist) {
			System.out.println(" Database exists.");
		} else {
			this.getReadableDatabase();
			try {
				copyDatabase();
			} catch (IOException e) {

			}
		}
	}

	private boolean checkDatabase() {

		boolean checkdb = false;
		try {
			String myPath = DB_PATH + DB_NAME;
			File dbfile = new File(myPath);

			checkdb = dbfile.exists();
		} catch (SQLiteException e) {
			System.out.println("Database doesn't exist");
		}

		return checkdb;
	}

	private void copyDatabase() throws IOException {

		// Open your local db as the input stream

		InputStream myInput = myContext.getAssets().open(DB_NAME);

		// InputStream myInput =resources.openRawResource(R.raw.database);

		// Path to the just created empty db
		// String outFilename = DB_PATH + DB_NAME;

		// Open the empty db as the output stream
		OutputStream myOutput = new FileOutputStream(DB_PATH + DB_NAME);

		// transfer byte to inputfile to outputfile
		byte[] buffer = new byte[1024];
		int length;
		while ((length = myInput.read(buffer)) > 0) {
			myOutput.write(buffer, 0, length);
		}

		// Close the streams
		myOutput.flush();
		myOutput.close();
		myInput.close();

	}

	public void openDatabase() throws SQLException {
		// Open the database
		String mypath = DB_PATH + DB_NAME;
		myDatabase = SQLiteDatabase.openDatabase(mypath, null, SQLiteDatabase.OPEN_READWRITE);

	}

	public synchronized void close() {
		i--;
		if (myDatabase != null) {
			myDatabase.close();
			System.out.println("Database closed " + i + " times.");
		}
		super.close();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

	public boolean isFavouriteQuoteAdded(String id) {
		String QUERY = "SELECT DonorId FROM DonarList where DonorId=" + id;
		Cursor cursor = myDatabase.rawQuery(QUERY, null);
		int count = cursor.getCount();
		if (count == 0) {
			return false;
		} else {
			return true;
		}

	}

	public void addToFavourite(DonorDetail item) {
		if (!isFavouriteQuoteAdded(item.getId())) {
			ContentValues dataToInsert = new ContentValues();
			dataToInsert.put("DonorId", item.getId());
			dataToInsert.put("DonorName", item.getDonarName());
			dataToInsert.put("ContactNo", item.getContactNo());
			dataToInsert.put("BloodGroup", item.getBloodGroup());
			dataToInsert.put("Address", item.getAddress());

			long flag = myDatabase.insert("DonarList", null, dataToInsert);
			System.out.println("flag-" + flag);
		}
	}

	public void removeFromFavourite(String quoteId) {
		myDatabase.delete("", "quote_id=?", new String[] { quoteId + "" });
	}

	public DonorDetail getFavouriteQuote(int position) {
		DonorDetail item = null;
		String QUERY = "SELECT DonorId,DonorName,ContactNo,BloodGroup,Address FROM DonarList";
		Cursor cursor = myDatabase.rawQuery(QUERY, null);

		if (cursor.getCount() > 0) {
			item = new DonorDetail();
			if (cursor.moveToPosition(position)) {
				item.setId(cursor.getString(0));
				item.setDonarName((cursor.getString(1)));
				item.setContactNo(cursor.getString(2));
				item.setBloodGroup(cursor.getString(3));
				item.setAddress(cursor.getString(4));
				return item;
			}

		}
		return item;
	}

	public int getFavouriteQuoteCount() {
		String QUERY = "SELECT count(*) FROM DonarList";
		Cursor cursor = myDatabase.rawQuery(QUERY, null);
		cursor.moveToFirst();
		int count = cursor.getInt(0);
		return count;
	}

	public void addNotification(ContentValues item) {
		long flag = myDatabase.insert("notifications", null, item);
		System.out.println("flag-" + flag);

	}

	public ArrayList<DonorDetail> getNotifications() {
		ArrayList<DonorDetail> list=new ArrayList<DonorDetail>();
		
		DonorDetail item = null;
		String QUERY = "SELECT id,name,phone_no,blood_group,email_id,sent_time FROM notifications";
		Cursor cursor = myDatabase.rawQuery(QUERY, null);

		if (cursor.getCount() > 0) {
			for (int i = 0; i < cursor.getCount(); i++) {
				cursor.moveToPosition(i);
				item = new DonorDetail();
				item.setId(cursor.getString(0));
				item.setDonarName((cursor.getString(1)));
				item.setContactNo(cursor.getString(2));
				item.setBloodGroup(cursor.getString(3));
				item.setAddress(cursor.getString(5));
				list.add(item);
			}
		}
		return list;
	}
	
	public void removeFromNotification(String id) {
		myDatabase.delete("notifications", "id=?", new String[] { id + "" });
	}

}