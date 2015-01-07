package com.nyu.cs9033.eta.controllers;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import DatabaseHelper.TripDatabaseHelper; 
import locationServer.locationService;

import com.nyu.cs9033.eta.R;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class MainActivity extends Activity {
	private final static String TAG = "MainActivity";
	private List<Map<String, String>> data;
	private String curTripID;
	private final int createTripCode = 100;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// TODO - fill in here	
		Button buttonCreate = (Button) findViewById(R.id.buttonCreat);
		Button buttonView = (Button)findViewById(R.id.buttonView);
		Button buttonAlarm = (Button)findViewById(R.id.buttonAlarm);
		buttonAlarm.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//stop the system alarm service  manually by user.
				locationService.setServiceAlarm(v.getContext(), false);
			}
		});
		buttonCreate.setOnClickListener(new View.OnClickListener() {			
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startTripCreator();
			}
		}) ;
		buttonView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub;
				startTripViewer();
			}
		});
		TripDatabaseHelper dbHelper = new TripDatabaseHelper(this);
		dbHelper.clear();
		//add alarm to update current location
		locationService.setServiceAlarm(this, true);

		//intial the list view with current trip information
		curTripID = "3645686546";
		
		data = new ArrayList<Map<String, String>>();
		//Modify UI to show current trip
		ListView listV = (ListView)findViewById(R.id.listViewCurTrip);	
		SimpleAdapter adapter = new SimpleAdapter(this, data,
                android.R.layout.simple_list_item_2,
                new String[] {"title", "date"},
                new int[] {android.R.id.text1,
                           android.R.id.text2});
		listV.setAdapter(adapter);
		//start the tripStatus thread
		UpdateCurrentTrip();

	}
	private void UpdateCurrentTrip()
	{
    	if(this.IsNetworkConnect())
    	{
    		
    		new tripStatus().execute(curTripID);					
    	}
    	else
    	{
    		Log.e(TAG, "No netWork");
    	}
	}

	/**
	 * Receive result from CreateTripActivity here.
	 * Can be used to save instance of Trip object
	 * which can be viewed in the ViewTripActivity.
	 */
	

	/**
	 * This method should start the
	 * Activity responsible for creating
	 * a Trip.
	 */
	//generate the "CreateTripActivity"
	public void startTripCreator() {
		
		// TODO - fill in here
		Intent intent = new Intent(this, CreateTripActivity.class);

		startActivityForResult(intent,createTripCode);  
	}
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		switch(requestCode){
		case createTripCode:
			if(resultCode == RESULT_OK){
				curTripID = String.valueOf(data.getIntExtra("trip_id", 0));
				UpdateCurrentTrip();
			}
			break;
		}
	}
	/**
	 * This method should start the
	 * Activity responsible for viewing
	 * a Trip.
	 */
	//generate the "TripHistoryActivity"
	public void startTripViewer() {
		
		// TODO - fill in here
		Intent intent = new Intent(this, TripHistoryActivity.class);
		
		startActivity(intent);
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
    //Its' function is get trip information from web server and update UI
    public class tripStatus extends AsyncTask<String, Void, String>{

    	private final static String TAG = "jsonData";

        private HttpURLConnection BuildConenction()
        {
        	String http = "http://cs9033-homework.appspot.com/";  
        	//String http = "http://www.baidu.com/";

        	HttpURLConnection conn=null;  
        	try {  

        		//connect to the web server
        	    URL url = new URL(http);  
        	    conn = (HttpURLConnection) url.openConnection();
        	    
        	    conn.setReadTimeout( 10000 /*milliseconds*/ );
        	    conn.setConnectTimeout( 15000 /* milliseconds */ );
        	    conn.setRequestMethod("POST");
        	    conn.setDoInput(true);
        	    conn.setDoOutput(true);

        	    //make some HTTP header nicety
        	    conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
        	    conn.setRequestProperty("X-Requested-With", "XMLHttpRequest");

        	    //open
        	    Log.i(TAG, "open connection_yao");
        	    conn.connect();      	    

        	} 
        	catch (MalformedURLException e) {  

                Log.e(TAG, e.getMessage());
        	}  
        	catch (IOException e) {  
    	
        		Log.e(TAG, e.getMessage());  
    	       } 
        	catch(Exception e)
        	{
        		Log.e(TAG, "connect error in " + TAG);
        		Log.e(TAG, e.getMessage());

        	}

    		return conn;
        }
        private String ReadTripInfo(HttpURLConnection urlConnection,String tripID)
        {
        	try
        	{
        	    //Create JSONObject here
        	    JSONObject jsonParam = new JSONObject();
        	    jsonParam.put("command", "TRIP_STATUS");
        	    jsonParam.put("trip_id", tripID);
        	    //send json object to server
        	    OutputStreamWriter out = new   OutputStreamWriter(urlConnection.getOutputStream());
        	    out.write(jsonParam.toString());
        	    out.close();  

        	    int HttpResult =urlConnection.getResponseCode();  
        	    if(HttpResult ==HttpURLConnection.HTTP_OK){  
        	    //get response from web server
        	    BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(),"utf-8"));  
        	    String line = null;  
        	    
                if((line = br.readLine()) != null)
                	{

                		br.close(); 
                		return line;
                	}

        	    }else{  
        	    	Log.i(TAG, urlConnection.getResponseMessage());
        	    	return "";
        	    }  
        	}    	
        	catch (JSONException e) {
     	       // TODO Auto-generated catch block
        		Log.e(TAG, e.getMessage());
         	} catch (IOException e) {
    			// TODO Auto-generated catch block
        		Log.e(TAG, e.getMessage());
        		
    		}
        	finally{  
     	       if(urlConnection!=null)  
     	    	   urlConnection.disconnect();  
     	   }
        	return "";
        }



    	@Override
    	protected String doInBackground(String... urls) {
    		return download(urls[0]);

    	}
    	private String download(String tripID) {
    		// TODO Auto-generated method stub
    		HttpURLConnection urlConnection = BuildConenction();
    		return ReadTripInfo(urlConnection,tripID);

    	}
    	//Deal with the response data and update UI
    	protected void onPostExecute (String res)
    	{
    		String temp;
        	JSONObject obj;
			try {
				//convert the response information to a Json Object
				obj = new JSONObject(res);
				JSONArray friends = (JSONArray) obj.get("people");
				JSONArray distance = (JSONArray) obj.get("distance_left");
				JSONArray time = (JSONArray) obj.get("time_left");
	        	int nLen = time.length();
	        	data.clear();
	    		for(int i = 0; i < nLen; i++) {
	    		    Map<String, String> datum = new HashMap<String, String>(2);
	    		    datum.put("title", friends.get(i).toString());
	    		    temp = "distance_left: " + distance.get(i).toString() + "\ntime_left: " + time.get(i).toString() + "\n";
	    		    datum.put("date", temp);
	    		    data.add(datum);
	    		}
	    		SimpleAdapter adapter = (SimpleAdapter) ((ListView)findViewById(R.id.listViewCurTrip)).getAdapter();
	    		adapter.notifyDataSetChanged();
	    		TextView currentTrip = (TextView)findViewById(R.id.textView2);
	    		currentTrip.setText(getResources().getString(R.string.cur_trip) + curTripID);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

    	}
    	
    	private String[] getResArray(String source) {
    		// TODO Auto-generated method stub
    		String temp = source.substring(2,source.indexOf("]"));
    		return temp.split(",");
    	}
    }
}
