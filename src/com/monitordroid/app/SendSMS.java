package com.monitordroid.app;

import android.telephony.SmsManager;



public class SendSMS {
	
	public void sendSMS(String phoneNumber, String SMSMessage) {
	    SmsManager smsManager = SmsManager.getDefault();
	    smsManager.sendTextMessage(phoneNumber, null, SMSMessage, null, null);
	}

}
