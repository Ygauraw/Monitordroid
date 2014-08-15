package com.monitordroid.app;

import android.content.Context;
import android.content.Intent;

public final class CommonUtilities {
	
    
    static final String SERVER_URL = "http://www.monitordroid.com/app/register.php"; 

    // Google Project ID - Unique to monitordroid.com, must be the project ID of the intended server to receive GCM Messages from
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
     * @param context application's context.
     * @param message message to be displayed.
     */
    static void displayMessage(Context context, String message) {
        Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
        intent.putExtra(EXTRA_MESSAGE, message);
        context.sendBroadcast(intent);
    }
}
