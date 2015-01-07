package locationServer;

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

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;
import android.widget.Toast;
//set system alarm service to periodically upload the location information to the web server.
public class locationService extends IntentService{
	private static final String TAG = "locationService";
	private static final int UPDATE_INTERVAL = 1000*15;//15 seconds
	public locationService() {
		super(TAG);
		// TODO Auto-generated constructor stub
	}
	//Do the job when the locationService is received the system call
	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		Log.i(TAG, "Update location information");
		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		if ( locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
			//obtain the GPS location
			Location lastKnowLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			UpdateLocation(lastKnowLocation);
		}
		else
		{
			Log.e(TAG, "The GPS is not on, the user's location cannot update!");
		}

	}

	public static void setServiceAlarm(Context context,boolean isOn){
		Intent location = new Intent(context, locationService.class);
		PendingIntent pi = PendingIntent.getService(context, 0, location, 0);
		AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		if(isOn){
			//set alarmManager to repeat call locationService
			alarmManager.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), UPDATE_INTERVAL, pi);
		}else{
			alarmManager.cancel(pi);
			pi.cancel();
		}
	}
	private void UpdateLocation(Location lastKnowLocation) {
		// TODO Auto-generated method stub
		HttpURLConnection urlConnection = BuildConenction();
    	try
    	{
    	    //Create JSONObject here
    	    JSONObject jsonParam = new JSONObject();
    	    jsonParam.put("command", "UPDATE_LOCATION");
    	    jsonParam.put("latitude", lastKnowLocation.getLatitude());
    	    jsonParam.put("longitude", lastKnowLocation.getLongitude());
    	    jsonParam.put("datetime", System.currentTimeMillis());
    	    OutputStreamWriter out = new   OutputStreamWriter(urlConnection.getOutputStream());
    	    out.write(jsonParam.toString());
    	    out.close();  
    	    //get repsonse from web server
    	    int HttpResult =urlConnection.getResponseCode();  
    	    if(HttpResult ==HttpURLConnection.HTTP_OK){  
    	        BufferedReader br = new BufferedReader(new InputStreamReader(  
    	            urlConnection.getInputStream(),"utf-8"));  
    	    String line = null;      	    
            if((line = br.readLine()) != null)
            	{
            		br.close(); 
            		JSONObject obj = new JSONObject(line); 
            		if(obj.getInt("response_code") != 0)
            		{
            			Log.e(TAG, "Update current location is not successful!");
            		}
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
	}


	// build web serve connection and set the POST method
    private HttpURLConnection BuildConenction()
    {
    	String http = "http://cs9033-homework.appspot.com/";  

    	HttpURLConnection conn=null;  
    	try {  


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
}
