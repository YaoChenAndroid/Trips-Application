package com.nyu.cs9033.eta.controllers;

import java.util.List;

import DatabaseHelper.TripDatabaseHelper;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.nyu.cs9033.eta.R;
import com.nyu.cs9033.eta.models.Trip;

//show all trips within the database
public class TripHistoryActivity extends Activity{
//func: click on the trip within the list, view the trip details withn the ViewTripActivity
//extra:show trips pasted, upcoming trips, trips occuring right now.
	private final static String TAG = "TripHistoryActivity";
	private  List<Trip> tripList;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		setContentView(R.layout.trip_list);

		ListView listV = (ListView)findViewById(R.id.listView1);
		Button back = (Button)findViewById(R.id.buttonBack);
		//this button is used to back the main activity
		back.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				returnToMain();
			}
		});
		//get all view from the database
		TripDatabaseHelper db = new TripDatabaseHelper(this);
		tripList = db.getAllTrip();
		int nLen = tripList.size();
		if(nLen != 0)
		{
			//use the trip information to set the List on the UI
			String[] resStrA = new String[nLen];
			String temp = null;
			for(int i = 0; i < nLen; i++)
			{
				temp = tripList.get(i).GetName();
				if(temp != null)
					resStrA[i] = temp;
			}
			listV.setAdapter(new ArrayAdapter<String>(this,
					android.R.layout.simple_list_item_1, resStrA));
			//add click event to each Item of List. Then we click one item in the list, the ViewTrip activity will start.
			listV.setOnItemClickListener(new OnItemClickListener(){

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
					ViewTrip(arg2);
					
				}


				
			});
			
		}
		else
		{
			//show the message box if there is no trip
			new AlertDialog.Builder(this)
			.setTitle(this.getString(R.string.alart_Title))
			.setMessage(R.string.alert_Message)
			.setPositiveButton(R.string.alert_Ok, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
					// TODO Auto-generated method stub
					finish();
					
				}
			})
			.show();

		}
		Log.i(TAG, "successful!");
	}
	//Start the ViewTripActivity to show the detail information of the selected trip
	private void ViewTrip(int arg) {
		// TODO Auto-generated method stub
		Intent temp = new Intent(this, ViewTripActivity.class);
		temp.putExtra("Trip", tripList.get(arg));
		startActivity(temp);
	}
	public void returnToMain()
	{
		finish();

	}
}
