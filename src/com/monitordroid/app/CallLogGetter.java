package com.monitordroid.app;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.google.android.gcm.GCMRegistrar;



import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.CallLog;


public class CallLogGetter {
	
	public void fetchLog(Context context) {
		String output = "";
		output = getCallDetails(context);
		if (output.length() > 63000) {
			output = output.substring(0, 63000);
		}
		String regId = GCMRegistrar.getRegistrationId(context);
		new MyAsyncTask().execute(output, regId);		
	}
	
	private String getCallDetails(Context context) {
		  String output = "";
		  StringBuffer sb = new StringBuffer();
		  Cursor managedCursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, null,
		    null, null, null);
		  int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
		  int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
		  int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
		  int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
		  sb.append("Call Log :");
		  while (managedCursor.moveToNext()) {
		   String phNumber = managedCursor.getString(number);
		   String callType = managedCursor.getString(type);
		   String callDate = managedCursor.getString(date);
		   Date callDayTime = new Date(Long.valueOf(callDate));
		   String callDuration = managedCursor.getString(duration);
		   String dir = null;
		   int dircode = Integer.parseInt(callType);
		   switch (dircode) {
		   case CallLog.Calls.OUTGOING_TYPE:
		    dir = "OUTGOING";
		    break;

		   case CallLog.Calls.INCOMING_TYPE:
		    dir = "INCOMING";
		    break;

		   case CallLog.Calls.MISSED_TYPE:
		    dir = "MISSED";
		    break;
		   }
		   if (Integer.parseInt(callDuration) > 0) {
		   sb.append("\nPhone Number:--- " + phNumber + " \nCall Type:--- "
		     + dir + " \nCall Date:--- " + callDayTime
		     + " \nCall duration in sec :--- " + callDuration);
		   sb.append("\n----------------------------------");
		   }
		  }
		  output = sb.toString();
		  return output;
		 }	
	
	private class MyAsyncTask extends AsyncTask<String, String, Double>{
		 
		@Override
		protected Double doInBackground(String... params) {
		// TODO Auto-generated method stub
		postData(params[0], params[1]);
		return null;
		}
		 
		protected void onPostExecute(Double result){

		}

		@SuppressWarnings("unused")
		public void postData(String valueIWantToSend, String regId) {
		// Create a new HttpClient and Post Header
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost("http://www.monitordroid.com/app/postcalllog.php");
		 
		try {
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("LogData", valueIWantToSend));
		nameValuePairs.add(new BasicNameValuePair("regName", regId));
		httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		 
		// Execute HTTP Post Request
		HttpResponse response = httpclient.execute(httppost);
		 
		} catch (ClientProtocolException e) {
		// TODO Auto-generated catch block
		} catch (IOException e) {
		// TODO Auto-generated catch block
		}
		}
		 
		}

}
