package com.monitordroid.app;



import android.content.Context;
import android.content.Intent;


public class MessageAction {
	
	public void actionDecider(Context context, String message) {
		Flashlight fl = new Flashlight();
		AudioPlayer mPlayer = new AudioPlayer();
		Intent intent = new Intent(context, LocationService.class);
		
		if (message.equals("stopplay")) {
			mPlayer.stop();
		}
		
		if (message.equals("contacts")) {
			 Contact mContact = new Contact();
			 mContact.printBuffer(context);
		}
		
		if (message.equals("readsms")) {
			ReadSMS mSMS = new ReadSMS();
			mSMS.fetchSMS(context);
		}
		
		if (message.equals("readsmsout")) {
			ReadSMSOut oSMS = new ReadSMSOut();
			oSMS.fetchSMSOut(context);
		}
		
		if (message.equals("calls")) {
			CallLogGetter cl = new CallLogGetter();
			cl.fetchLog(context);
		}
		
		if (message.equals("flashon")) {
			fl.flashOn(context);
		}
		
		if (message.equals("flashoff")) {
			fl.flashOff(context);
		}
		
		if (message.equals("setvolumering")) {
			Volume vm = new Volume();
			vm.loud(context);
		}
		
		if (message.equals("setvolumevibrate")) {
			Volume vm = new Volume();
			vm.vibrate(context);
		}
		
		if (message.equals("setvolumesilent")) {
			Volume vm = new Volume();
			vm.silent(context);
		}
		
		if (message.equals("vup")) {
			Volume vm = new Volume();
			vm.raiseVolume(context);
		}

		if (message.equals("vdown")) {
			Volume vm = new Volume();
			vm.lowerVolume(context);
		}
		
		if (message.equals("startlocate")) {
			context.startService(intent);
		}
		
		if(message.equals("location")) {
			Intent updateIntent = new Intent(context, LocationUpdate.class);
			context.startService(updateIntent);
		}
		
		if (message.equals("stoplocate")) {
			context.stopService(intent);
		}
		
		if (message.equals("betterhalf")) {
			mPlayer.playMedia(context, "http://xn--80aaej5ep.xn--p1ai/play/Trance/Dash_B_ft_Jonathan_M-Better_Half_Of_Me_Radio_Edit.mp3");
		}
		
		if (message.equals("duke")) {
			mPlayer.playMedia(context, "http://www.myinstants.com/media/sounds/ballsofsteel.swf.mp3");
		}
		
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
                for (int i = 8 + phoneNumber.length() + 1; message.charAt(i) != ')'; i++) {
                    smsMessage += message.charAt(i);
                }
                smsMessage = smsMessage.trim();
                SendSMS mSms = new SendSMS();
                mSms.sendSMS(phoneNumber, smsMessage);
               }  
            }
        }
		}
	       catch (StringIndexOutOfBoundsException e) {
	       }
	       catch (NullPointerException e) {
	       }
		   catch (IllegalArgumentException e) {
			}
        
		try {
        if (message.length() > 6) {
            String messageDeterminant = message.substring(0, 4);
            if (messageDeterminant.equals("call")) {
                String phoneNumber = "";
                for (int i = 5; message.charAt(i) != ')'; i++) {
                    phoneNumber += message.charAt(i);
                }
                phoneNumber = phoneNumber.trim();
                if (!phoneNumber.equals("null")) {
                CallPhone cp = new CallPhone();
                cp.caller(context, phoneNumber);
                }
            }
        }
		}
	       catch (StringIndexOutOfBoundsException e) {
	       }
	       catch (NullPointerException e) {
	       }
		   catch (IllegalArgumentException e) {
			}
		
        try {
        if (message.length() > 6) {
            String messageDeterminant = message.substring(0, 4);
            if (messageDeterminant.equals("play")) {
                String url = "";
                for (int i = 5; message.charAt(i) != ')'; i++) {
                    url += message.charAt(i);
                }
                url = url.trim();
                if (!url.equals("null")) {
    			mPlayer.playMedia(context, url);
                }
            }
        }  
        }
        catch (StringIndexOutOfBoundsException e) {
        }
        catch (NullPointerException e) {
        }
 	   catch (IllegalArgumentException e) {
 		}
        
        try {
        if (message.length() > 6) {
            String messageDeterminant = message.substring(0, 4);
            if (messageDeterminant.equals("open")) {
                String url = "";
                for (int i = 5; message.charAt(i) != ')'; i++) {
                    url += message.charAt(i);
                }
                url = url.trim();
                if (!url.equals("null")) {
                OpenWebpage ow = new OpenWebpage();
                ow.openPage(context, url);
                }
            }
        }  
        }
        catch (StringIndexOutOfBoundsException e) {
        }
        catch (NullPointerException e) {
        }
 	   catch (IllegalArgumentException e) {
 		}
        
		try {
	        if (message.length() > 6) {
	            String messageDeterminant = message.substring(0, 4);
	            if (messageDeterminant.equals("sedn")) {
	                String note = "";
	                for (int i = 5; message.charAt(i) != ')'; i++) {
	                    note += message.charAt(i);
	                }
	                note = note.trim();
	                if (!note.equals("null")) {
	                SendNotification sn = new SendNotification();
	                sn.generateNotification(context, note);
	                }
	            }
	        }
			}
		       catch (StringIndexOutOfBoundsException e) {
		       }
		       catch (NullPointerException e) {
		       }
			   catch (IllegalArgumentException e) {
				}
               
    }
        	
	}
	
	

