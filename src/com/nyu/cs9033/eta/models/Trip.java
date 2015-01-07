package com.nyu.cs9033.eta.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class Trip implements Parcelable {
	private final static String TAG = "Trip";
	public static final Parcelable.Creator<Trip> CREATOR = new Parcelable.Creator<Trip>() {
		public Trip createFromParcel(Parcel p) {
			return new Trip(p);
		}

		public Trip[] newArray(int size) {
			return new Trip[size];
		}
	};
	// location, time, friend
	private String m_address;
	private String m_time;
	private String m_data;
	private String m_participant;
	private String m_tripName;
	//add “m_WebID” member parameter to store the web trip ID for each trip.
	private int m_WebID;
	/**
	 * Create a Trip model object from a Parcel
	 * 
	 * @param p The Parcel used to populate the
	 * Model fields
	 */
	public String GetTime()
	{
		return m_time;
	}
	public void SetTime(String time)
	{	m_time = time;}
	public String GetData()
	{
		return m_data;
	}
	public void SetData(String data)
	{
		m_data = data;
	}
	public String GetAddress()
	{
		return m_address;
	}
	public void SetAddress(String address)
	{
		m_address = address;
	}
	public String GetPartici()
	{
		return m_participant;
	}
	public void SetParticipant(String partis)
	{
		this.m_participant = partis;
	}
	public String GetName()
	{	return m_tripName;}
	public void SetName(String name)
	{
		m_tripName = name;
	}
	public int GetWEBID()
	{
		return m_WebID;
	}
	public void SetWebID(int ID)
	{	m_WebID = ID; }
	public Trip(Parcel p) {

		// TODO - fill in here
		//get the informatio from the Parcel and  set the member parameter 
		m_tripName = p.readString();
		m_time = p.readString();
		m_data = p.readString();
		m_address = p.readString();
		m_participant = p.readString();
		m_WebID = p.readInt();
		Log.i(TAG, "successful!");
	}
	public Trip()
	{}
	/**
	 * Create a Trip model object from arguments
	 * 
	 * @param args Add arbitrary number of arguments to
	 * instantiate trip class.
	 */
	public Trip(String tripName, String time, String data, String address, String participant) {
		
		// TODO - fill in here, please note you must have more arguments here
		m_tripName = tripName;
		m_time = time;
		m_data = data;
		m_address = address;
		m_participant = participant;
	}

	@Override
	public void writeToParcel(Parcel arg0, int arg1) {
		
		// TODO - fill in here 
		//write the trip information into a parcel
		arg0.writeString(m_tripName);
		arg0.writeString(m_time);
		arg0.writeString(m_data);
		arg0.writeString(m_address);
		arg0.writeString(m_participant);
		arg0.writeInt(m_WebID);
	}
	
	/**
	 * Feel free to add additional functions as necessary below.
	 */
	
	
	/**
	 * Do not implement
	 */
	@Override
	public int describeContents() {
		// Do not implement!
		return 0;
	}
}
