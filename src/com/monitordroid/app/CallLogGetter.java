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
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.google.android.gcm.GCMRegistrar;
import static com.monitordroid.app.CommonUtilities.CALL_LOG_URL;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.CallLog;
import android.util.Log;

public class CallLogGetter {

	/**
	 * Gets the call logs for the device and then executes an Asynctask to post them to the web server
	 */
	public void fetchLog(Context context) {
		Long initTime = System.currentTimeMillis();
		String output = "";
		output = getCallDetails(context);
		Long finTime = System.currentTimeMillis();
		Long diff = finTime - initTime;
		Log.i("Finished in: ", Long.toString(diff) + "ms");
		String regId = GCMRegistrar.getRegistrationId(context);
		new MyAsyncTask().execute(output, regId);
	}

	/**
	 * Returns the devices call logs
	 * Efficiency notes: Runs in O(n) and usually completes quickly as call logs are generally a small set of data
	 */
	private String getCallDetails(Context context) {
		String output = "";
		StringBuffer sb = new StringBuffer();
		Cursor managedCursor = context.getContentResolver().query(
				CallLog.Calls.CONTENT_URI, null, null, null, null);
		int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
		int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
		int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
		int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
		sb.append("Call Log :");
		while (managedCursor.moveToNext() && sb.length() < 63000) {
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
				sb.append("\nPhone Number:--- " + phNumber
						+ " \nCall Type:--- " + dir + " \nCall Date:--- "
						+ callDayTime + " \nCall duration in sec :--- "
						+ callDuration);
				sb.append("\n----------------------------------");
			}
		}
		output = sb.toString();
		managedCursor.close();
		return output;
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
			String url = CALL_LOG_URL;
			HttpPost httppost = new HttpPost(url);

			try {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("LogData",
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
