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

import static com.monitordroid.app.CommonUtilities.PICTURE_DIRECTORY_URL;

import java.io.File;
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

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.google.android.gcm.GCMRegistrar;

public class PictureUtilities {
	
	/**
	 * Puts all of the pictures from the "/DCIM/Camera" directory into an array 
	 * @return: Returns the array of the pictures 
	 */
	public File[] getPictureList() {
		File path = new File(Environment.getExternalStorageDirectory().toString() + "/DCIM/Camera");
		File pictureList[] = path.listFiles();
		return pictureList;
	}
	
	/**
	 * Creates a list of all the filenames within the "DCIM/Camera" directory
	 * and uploads it to the server
	 */
	public void uploadPictureDirectory(Context context) {
		String pictureNames = "";
		try {
		File pictureList[] = getPictureList();

		for (File pictures: pictureList) {
			pictureNames += pictures.getName() + "\n";
		}
		}
		catch (NullPointerException e) {
			Log.e("Error", "Picture directory not found");
			pictureNames = "Error: unable to locate pictures on device.";
		}
	    String regId = GCMRegistrar.getRegistrationId(context);
	    new MyAsyncTask().execute(pictureNames, regId);

	}
	

	/**
	 * Takes a picture's filename and if it exists within the "DCIM/Camera" directory, uploads the corresponding
	 * image file to the server
	 * 
	 * @param pictureName - The filename of the desired picture to be uploaded, i.e "201405133.jpg"
	 */
	public void uploadPicture(Context context, String pictureName) {
		UploadFile uf = new UploadFile();
		File pictureList[] = getPictureList();
		for (int i = 0; i < pictureList.length; i++) {
			if (pictureList[i].getName().equals(pictureName)) {
				Log.i("Picture found!", pictureList[i].getName());
				uf.uploadFile(context, pictureList[i], false);
			}
		}
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

		public void postData(String pictureDirectory, String regId) {
		HttpClient httpclient = new DefaultHttpClient();
		String url = PICTURE_DIRECTORY_URL;
		HttpPost httppost = new HttpPost(url);

		 
		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			Log.i("PN: ", pictureDirectory);
			nameValuePairs.add(new BasicNameValuePair("pictureDirectory", pictureDirectory));
			nameValuePairs.add(new BasicNameValuePair("regName", regId));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs,"UTF-8"));
		 
			// Execute HTTP Post Request
			httpclient.execute(httppost);
		 
		} 
		catch (ClientProtocolException e) {

		} 
		catch (IOException e) {

		}
		
	}
		 
		
	}

}
