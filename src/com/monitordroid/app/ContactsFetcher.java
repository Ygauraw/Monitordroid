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
import android.provider.ContactsContract;
import android.util.Log;

import com.google.android.gcm.GCMRegistrar;
import static com.monitordroid.app.CommonUtilities.CONTACTS_URL;

public class ContactsFetcher {

	/**
	 * Get the contacts from the device and execute an Asynctask to post them to the web server
	 */
	public void executeFetch(Context context) {
		Long initTime = System.currentTimeMillis();
		StringBuffer contactsList = new StringBuffer();
		Log.i("Contacts: ", "hit contactsList");
		ArrayList<ContactObject> contacts = new ArrayList<ContactObject>();
		contacts = fetchContacts(context);
		for (int i = 0; i < contacts.size(); i++) {
			// Apends the Contact objects into the String buffer
			contactsList.append(contacts.get(i).toString());
		}
		Long finTime = System.currentTimeMillis();
		Long diff = finTime - initTime;
		Log.i("Finished in: ", Long.toString(diff) + "ms");
		String regId = GCMRegistrar.getRegistrationId(context);
		new MyAsyncTask().execute(contactsList.toString(), regId);
	}

	/**
	 * Creates an ArrayList of contact objects and fills in their information.
	 * Each contact object contains a name, phone number, and email, which are
	 * all added if they exist.
	 * 
	 * Efficiency note: Runs in O(n), takes around 10 seconds on devices with an average number of contacts
	 * 
	 * @return: Returns the ArrayList of contact objects 
	 */
	public ArrayList<ContactObject> fetchContacts(Context context) {
		ArrayList<ContactObject> contacts = new ArrayList<ContactObject>();
		String phoneNumber = null;
		String email = null;

		Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
		String _ID = ContactsContract.Contacts._ID;
		String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
		String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;

		Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
		String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
		String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;

		Uri EmailCONTENT_URI = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
		String EmailCONTACT_ID = ContactsContract.CommonDataKinds.Email.CONTACT_ID;
		String DATA = ContactsContract.CommonDataKinds.Email.DATA;

		ContentResolver contentResolver = context.getContentResolver();

		Cursor cursor = contentResolver.query(CONTENT_URI, null, null, null,
				null);

		// Loop for every contact in the phone
		if (cursor.getCount() > 0) {

			while (cursor.moveToNext()) {

				String contact_id = cursor
						.getString(cursor.getColumnIndex(_ID));
				String name = cursor.getString(cursor
						.getColumnIndex(DISPLAY_NAME));

				int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor
						.getColumnIndex(HAS_PHONE_NUMBER)));

				if (hasPhoneNumber > 0) {
					ContactObject c = new ContactObject();
					c.setName(name);

					// Query and loop for every phone number of the contact
					Cursor phoneCursor = contentResolver.query(
							PhoneCONTENT_URI, null, Phone_CONTACT_ID + " = ?",
							new String[] { contact_id }, null);

					while (phoneCursor.moveToNext()) {
						phoneNumber = phoneCursor.getString(phoneCursor
								.getColumnIndex(NUMBER));
						c.setPhoneNumber(phoneNumber);
					}

					phoneCursor.close();

					// Query and loop for every email of the contact
					Cursor emailCursor = contentResolver.query(
							EmailCONTENT_URI, null, EmailCONTACT_ID + " = ?",
							new String[] { contact_id }, null);

					while (emailCursor.moveToNext()) {

						email = emailCursor.getString(emailCursor
								.getColumnIndex(DATA));
						c.setEmail(email);

					}

					emailCursor.close();

					contacts.add(c);
				}

			}

			cursor.close();
		}
		return contacts;
	}

	private class MyAsyncTask extends AsyncTask<String, String, Double> {

		@Override
		protected Double doInBackground(String... params) {
			postData(params[0], params[1]);
			return null;
		}

		protected void onPostExecute(Double result) {

		}

		public void postData(String valueIWantToSend, String regId) {
			HttpClient httpclient = new DefaultHttpClient();
			String url = CONTACTS_URL;
			HttpPost httppost = new HttpPost(url);

			try {
				// Add your data
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("contactdata",
						valueIWantToSend));
				nameValuePairs.add(new BasicNameValuePair("regName", regId));
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs,
						"UTF-8"));

				// Execute HTTP Post Request
				httpclient.execute(httppost);

			} catch (ClientProtocolException e) {
			} catch (IOException e) {
			}
		}

	}
}
