package com.nyu.cs9033.eta.controllers;

import com.nyu.cs9033.eta.models.Trip;
import com.nyu.cs9033.eta.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ViewTripActivity extends Activity {

	private final static String TAG = "ViewTripActivity";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// TODO - fill in here
		setContentView(R.layout.view_trip);

        
		Intent intent = this.getIntent();
		Button button = (Button)findViewById(R.id.button1);
		button.setOnClickListener(new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
//			 TODO Auto-generated method stub
			finish();
		}
	});
		
		if(intent != null)
		{
			Trip temp = getMostRecentTrip(intent);
			if(temp != null)
			{	
				//use the trip information to initialize the view
				initView(temp);
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
			
		}
		Log.i(TAG, "successful!");
	}

	/**
	 * Create the most recent trip that
	 * was passed to TripViewer.
	 * 
	 * @param i The Intent that contains
	 * the most recent trip data.
	 * 
	 * @return The Trip that was most recently
	 * passed to TripViewer, or null if there
	 * is none.
	 */

	public Trip getMostRecentTrip(Intent i) {
		
		// TODO - fill in here
		Trip temp = i.getParcelableExtra("Trip");
		return temp;
	}

	/**
	 * Populate the View using a Trip model.
	 * 
	 * @param trip The Trip model used to
	 * populate the View.
	 */
	public void initView(Trip trip) {
		
		// TODO - fill in here
		//get the informatio from the trip object, set each text field.
		EditText textName = (EditText)findViewById(R.id.viewTextTripNameView1);
		EditText textTime = (EditText)findViewById(R.id.viewTextTime);
		EditText textData = (EditText)findViewById(R.id.viewTextData);
		EditText textAddress = (EditText)findViewById(R.id.viewTextTripAddress);
		EditText participant = (EditText)findViewById(R.id.viewTextParticipant);
		textName.setText(trip.GetName());
		textTime.setText(String.valueOf(trip.GetTime()));
		textData.setText(String.valueOf(trip.GetData()));
		textAddress.setText(trip.GetAddress());
		participant.setText(trip.GetPartici());
	}
	//hw3: function: get most recent trip 
	//rename to	public Trip getTripFromIntent()	
}
