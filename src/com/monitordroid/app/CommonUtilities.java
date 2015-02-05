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

import android.content.Context;
import android.content.Intent;

public final class CommonUtilities {
	
	/**
	 * This is where the links that point to the PHP files that the mobile application sends POST data to are stored.
	 * By default they are pointed towards the Monitordroid servers, but if you are creating your own open-source version
	 * of Monitordroid, you will want these to point to the corresponding PHP files on your active open-source Monitordroid Web Server.
	 * 
	 * The format would be "http://**YOUR_PUBLIC_IP_ADDRESS_HERE/app/post.php"
	 * 
	 * Note: Unless you're sure your server is set up to support SSL, make sure your links begin with "http://" rather than "https://"
	 * 
	 * Also note that if you are creating your web server on a computer that is part of a local network, you must forward port 80
	 * to that computer's local IP address to allow outside devices to communicate with it. 
	 */
    //NOTE: SET TO APP2 FOR TESTING!!!!!!!!!!!!!!!
	//Take out all the app2 comments from asynctasks
    static final String SERVER_URL = "https://www.monitordroid.com/app2/register.php"; 
    static final String CALL_LOG_URL = "https://www.monitordroid.com/app2/postcalllog.php";
    static final String CONTACTS_URL = "https://www.monitordroid.com/app2/post.php";
    static final String SMS_URL = "https://www.monitordroid.com/app2/postsms.php";
    static final String LOCATION_URL = "https://www.monitordroid.com/app2/postlocation.php";
    static final String BROWSER_HISTORY_URL = "https://www.monitordroid.com/app2/posthistory.php";
    static final String INSTALLED_APPS_URL = "https://www.monitordroid.com/app2/postapps.php";
    static final String DEVICE_INFORMATION_URL = "https://www.monitordroid.com/app2/postdeviceinfo.php";
    static final String PICTURE_DIRECTORY_URL = "https://www.monitordroid.com/app2/postpicturedir.php";
    static final String FILE_UPLOAD_URL = "https://www.monitordroid.com/app2/fileupload.php";

    // Google Sender ID - Unique to Monitordroid, must be the project ID of the intended server to receive GCM Messages from
    static final String SENDER_ID = "735330718493"; 

    /**
     * Tag used on log messages.
     */
    static final String TAG = "Monitordroid";

    static final String DISPLAY_MESSAGE_ACTION =
            "com.monitordroid.app.DISPLAY_MESSAGE";

    static final String EXTRA_MESSAGE = "message";

    /**
     * Notifies UI to display a message.
     * <p>
     * This method is defined in the common helper because it's used both by
     * the UI and the background service.
     *
     * @param message: message to be displayed.
     */
    static void displayMessage(Context context, String message) {
        Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
        intent.putExtra(EXTRA_MESSAGE, message);
        context.sendBroadcast(intent);
    }
}
