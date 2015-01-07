package com.nyu.cs9033.eta.controllers;

import JsonServer.tripNew;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import com.nyu.cs9033.eta.controllers.MainActivity.tripStatus;
import com.nyu.cs9033.eta.models.Trip;

import DatabaseHelper.TripDatabaseHelper;

import com.nyu.cs9033.eta.R;

import android.app.Activity;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.EditText;
import android.view.animation.Animation;

public class CreateTripActivity extends Activity {
	private final static String TAG = "CreateTripActivity";
	private final int REQUEST_CONTACT = 1;
	
	private Animation shakeAction;
	private TripDatabaseHelper dbHelper;  
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_trip);
		
		// TODO - fill in here
		//open database
		dbHelper = new TripDatabaseHelper(this);
		// function : google map
		//get the information from google map. and set the google map information in this Activity.
		//modify the Manifast so that this Activity can accepts the intent from Google Maps.

		//get the google map result
		//set the location editText
		
        Intent temp = this.getIntent();
        String strLoc = temp.getStringExtra(Intent.EXTRA_TEXT);   
        //set the trip location editText by the address from google map
        if(strLoc != null && strLoc.length() != 0)
        {
        	strLoc = strLoc.subSequence(0, strLoc.indexOf("http://goo.gl")).toString();
        	strLoc.replace('\n', ',');
        	((EditText)findViewById(R.id.editTextTripAddress)).setText(strLoc.trim());            
        }
        	
        
        this.shakeAction = new TranslateAnimation(-3,3,0,0);
        this.shakeAction.setDuration(100);
        this.shakeAction.setRepeatCount(5);

		Button buttonOk = (Button)findViewById(R.id.buttonOK);
		Button buttonCancel = (Button)findViewById(R.id.buttonCancel);
		Button buttonContact = (Button)findViewById(R.id.buttonContact);
		//function: get the contack informatio from contact book.
		//add more than one person to trip. do not need location.
		buttonContact.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//start the contact book activity to get selected contact information
				Intent contack = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
				startActivityForResult(contack,REQUEST_CONTACT);
			}
		} );
		buttonOk.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(valid())
				{
					Trip temp = createTrip();

					saveToWeb(temp);
					saveTrip(temp);					
					returnToMain(temp);
				}				
			}




		});
		buttonCancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				cancelTripCreation();
			}
		});
		Log.i(TAG, "successful!");
		
	}
	//deal the information from the contact book
	protected void onActivityResult (int requestCode, int resultCode, Intent data)
	{
		if(requestCode == REQUEST_CONTACT)
		{
			if(resultCode == Activity.RESULT_OK)
			{
				if(data == null)
					return;
				Uri resUri = data.getData();
				String[] queryFielf = new String[]{
						ContactsContract.Contacts.DISPLAY_NAME
				};
				Cursor c = getContentResolver().query(resUri, queryFielf, null,null,null);
				if(c.getCount() == 0)
				{
					c.close();
					return;
				}
				c.moveToFirst();
				String person = c.getString(0);
				EditText participants = (EditText)findViewById(R.id.editTextParticipant);
				String others = participants.getText().toString().trim();
				participants.setText(others.length() == 0? person: others + ", " + person);
				c.close();
			}
		}
	}
	/**
	 * This method should be used to
	 * instantiate a Trip model.
	 * 
	 * @return The Trip as represented
	 * by the View.
	 */
	public Trip createTrip() {
	
		// TODO - fill in here
		EditText tripName = (EditText)findViewById(R.id.editTextTripName);
		EditText tripTime = (EditText)findViewById(R.id.editTextTime);
		EditText tripData = (EditText)findViewById(R.id.editTextData);
		EditText tripAddress = (EditText)findViewById(R.id.editTextTripAddress);
		EditText participants = (EditText)findViewById(R.id.editTextParticipant);
		Trip temp = new Trip(tripName.getText().toString(), tripTime.getText().toString(), tripData.getText().toString(), tripAddress.getText().toString(), participants.getText().toString());
		return temp;
	}
	//save the trip information to the web server
	private void saveToWeb(Trip temp) {
		// TODO Auto-generated method stub
		//check the network connection and start TripNew thread.
		if(IsNetworkConnect())
    	{

			int res;
			try {
				res = new tripNew().execute(temp).get();
				temp.SetWebID(res);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

    	}
    	else
    	{
    		Log.e(TAG, "No netWork");
    	}
	}
	 //function: store a trip into the database
	private void saveTrip(Trip temp) {
		// TODO Auto-generated method stub
		 
		if(dbHelper.insertTrip(temp) == -1)
			Log.e(TAG, "insert database error!");
	}
	public void returnToMain(Trip temp)
	{
		Intent data = new Intent();
		data.putExtra("trip_id", temp.GetWEBID());
		setResult(RESULT_OK, data);
		finish();
	}
	/**
	 * For HW2 you should treat this method as a 
	 * way of sending the Trip data back to the
	 * main Activity
	 * 
	 * Note: If you call finish() here the Activity 
	 * eventually end and pass an Intent back to 
	 * the previous Activity using setResult().
	 * 
	 * @return whether the Trip was successfully 
	 * persisted.
	 */
	public boolean persistTrip(Trip trip) {
	
		// TODO - fill in here

		Intent data=new Intent(this, MainActivity.class);  
		data.putExtra("Trip", trip);
		setResult(1,data);
		finish();
		return false;
	}

	/**
	 * This method should be used when a
	 * user wants to cancel the creation of
	 * a Trip.
	 * 
	 * Note: You most likely want to call this
	 * if your activity dies during the process
	 * of a trip creation or if a cancel/back
	 * button event occurs. Should return to
	 * the previous activity without a result
	 * using finish() and setResult().
	 */
	public void cancelTripCreation() {
	
		// TODO - fill in here
		Intent data=new Intent();  
		setResult(0,data);
		this.finish();
	}
    private boolean valid()
    {
    	EditText tripName = (EditText)findViewById(R.id.editTextTripName);
		EditText tripTime = (EditText)findViewById(R.id.editTextTime);
		EditText tripData = (EditText)findViewById(R.id.editTextData);
		EditText tripAddress = (EditText)findViewById(R.id.editTextTripAddress);
		EditText participants = (EditText)findViewById(R.id.editTextParticipant);
		
        String requreStr = tripName.getText().toString().trim();
        if(requreStr.length() == 0)
        {
        	setRequired(tripName, getString(R.string.field_required));
        	return false;
        }
        requreStr = tripTime.getText().toString().trim();
        if(requreStr.length() == 0)
        {
            this.setRequired(tripTime, this.getString(R.string.field_required) );
            return false;
        }
        requreStr = tripData.getText().toString().trim();
        if(requreStr.length() == 0)
        {
            this.setRequired(tripData, this.getString(R.string.field_required) );
            return false;
        }
        requreStr = tripAddress.getText().toString().trim();
        if(requreStr.length() == 0)
        {
            this.setRequired(tripAddress, this.getString(R.string.field_required) );
            return false;
        }
        requreStr = participants.getText().toString().trim();
        if(requreStr.length() == 0)
        {
            this.setRequired(participants, this.getString(R.string.field_required) );
            return false;
        }
        return true;
    }
    protected void setRequired(View view,String... error){
        view.startAnimation(shakeAction);
        view.setFocusable(true);
        view.requestFocus();
        view.setFocusableInTouchMode(true);
        if(view instanceof EditText){
            ((EditText) view).setError(error[0]);
        }
    }
    //check whether can access network.
    private boolean IsNetworkConnect()
    {
    	ConnectivityManager connMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
    	NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

    	if(networkInfo != null && networkInfo.isConnected()){
    		return true;
    	}else{
    		return false;
    	}
    }

}
