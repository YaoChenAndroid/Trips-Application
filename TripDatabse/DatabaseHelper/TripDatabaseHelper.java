package DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

import com.nyu.cs9033.eta.models.Trip;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class TripDatabaseHelper extends SQLiteOpenHelper {
	private final static String TAG = "TripDatabaseHelper";
	private static final int DATABASE_VERSION = 2;
	private static final String DATABASE_NAME = "trips";
	private static final String TABLE_TRIP = "trip";
	private static final String COL_TRIP_ID = "_id";
	private static final String COL_TRIP_DATE = "date";
	private static final String COL_TRIP_TIME = "time";
	private static final String COL_TRIP_LOCATION = "location";
	private static final String COL_TRIP_PARTICIPANTS = "participants";
	private static final String COL_TRIP_NAME = "name";
	//add the “webID” attribute to the trip table to store the trip ID from the web server
	private static final String COL_TRIP_WEBID = "webID";
	
	public TripDatabaseHelper(Context context)
	{

		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		Log.i(TAG, "successful!");
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		String sql = "create table " + TABLE_TRIP + "("
				+ COL_TRIP_ID + " integer primary key autoincrement, "
				+ COL_TRIP_NAME + " varchar(50), "
				+ COL_TRIP_TIME + " varchar(20), "
				+ COL_TRIP_DATE + " varchar(20), "
				+ COL_TRIP_LOCATION + " varchar(100), " 
				+ COL_TRIP_WEBID + " integer, "
				+ COL_TRIP_PARTICIPANTS + " text )";
		db.execSQL(sql);
	}
	public void clear()
	{
		   SQLiteDatabase database = getWritableDatabase();
		   database.execSQL("DROP TABLE IF EXISTS " + "trip");
		   onCreate(database);
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRIP);
		onCreate(db);
	}


	

	public long insertTrip(Trip trip)
	{
		ContentValues cv = new ContentValues();
		cv.put(COL_TRIP_NAME, trip.GetName());
		cv.put(COL_TRIP_DATE, trip.GetData());
		cv.put(COL_TRIP_WEBID, trip.GetWEBID());
		cv.put(COL_TRIP_TIME, trip.GetTime());
		cv.put(COL_TRIP_LOCATION, trip.GetAddress());
		cv.put(COL_TRIP_PARTICIPANTS, trip.GetPartici());

		Log.i(TAG, "CurrentView: " + String.valueOf(getWritableDatabase().getVersion()));
		return getWritableDatabase().insert(TABLE_TRIP, null, cv);
	}
	public List<Trip> getAllTrip()
	{
		List<Trip> tripList = new ArrayList<Trip>();
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cur = db.rawQuery("select * from " + TABLE_TRIP, null);
		cur.moveToFirst();
		int name = cur.getColumnIndex(COL_TRIP_NAME);
		int time = cur.getColumnIndex(COL_TRIP_TIME);
		int data = cur.getColumnIndex(COL_TRIP_DATE);
		int add = cur.getColumnIndex(COL_TRIP_LOCATION);
		int participants = cur.getColumnIndex(COL_TRIP_PARTICIPANTS);
		int id = cur.getColumnIndex(COL_TRIP_WEBID);
		for(; !cur.isAfterLast(); cur.moveToNext())
		{
			Trip trip = new Trip();
			
			trip.SetName(cur.getString(name));
			trip.SetTime(cur.getString(time));
			trip.SetData(cur.getString(data));
			trip.SetAddress(cur.getString(add));
			trip.SetParticipant(cur.getString(participants));
			trip.SetWebID(id);
			tripList.add(trip);
		}
		return tripList;
	}
}

