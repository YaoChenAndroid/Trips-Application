package JsonServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import com.nyu.cs9033.eta.models.Trip;

import android.os.AsyncTask;
import android.util.Log;
// It is used to upload the new trip to the web server and get ID of the new trip
public class tripNew extends AsyncTask<Trip, Void, Integer>{
	private final static String TAG = "tripNew";
	@Override
	protected Integer doInBackground(Trip... arg0) {
		// TODO Auto-generated method stub
		
		return UpdateTrip(arg0[0]);
	}
	//upload trip to the web server
	private Integer UpdateTrip(Trip trip) {
		// TODO Auto-generated method stub
		HttpURLConnection urlConnection = BuildConenction();
    	try
    	{
    	    //Create JSONObject here
    	    JSONObject jsonParam = new JSONObject();
    	    jsonParam.put("command", "CREATE_TRIP");
    	    jsonParam.put("location", trip.GetAddress());
    	    String time = trip.GetData() + trip.GetTime();
    	    jsonParam.put("datetime", time);
    	    jsonParam.put("people", trip.GetPartici());
    	    OutputStreamWriter out = new   OutputStreamWriter(urlConnection.getOutputStream());
    	    out.write(jsonParam.toString());
    	    out.close();  
    	    //get the web server response and extract trip ID information
    	    int HttpResult =urlConnection.getResponseCode();  
    	    if(HttpResult ==HttpURLConnection.HTTP_OK){  
    	        BufferedReader br = new BufferedReader(new InputStreamReader(  
    	            urlConnection.getInputStream(),"utf-8"));  
    	    String line = null;      	    
            if((line = br.readLine()) != null)
            	{
            		br.close(); 
            		JSONObject obj = new JSONObject(line); 
            		int res = obj.getInt("trip_id");
            		return res;
            	}

    	    }else{  
    	    	Log.i(TAG, urlConnection.getResponseMessage());
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
		return -1;
	}


	//build the connection to the web server
    private HttpURLConnection BuildConenction()
    {
    	String http = "http://cs9033-homework.appspot.com/";  
    	//String http = "http://www.baidu.com/";

    	HttpURLConnection conn=null;  
    	try {  


    	    URL url = new URL(http);  
    	    conn = (HttpURLConnection) url.openConnection();
    	    
    	    conn.setReadTimeout( 10000 /*milliseconds*/ );
    	    conn.setConnectTimeout( 15000 /* milliseconds */ );
    	    conn.setRequestMethod("POST");
    	    conn.setDoInput(true);
    	    conn.setDoOutput(true);
    	    //conn.setFixedLengthStreamingMode(message.getBytes().length);

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

}
