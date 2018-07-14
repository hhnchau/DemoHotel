package com.appromobile.hotel.gcm;

import com.appromobile.hotel.HotelApplication;
import com.appromobile.hotel.utils.MyLog;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;


/**
 * Created by xuan on 11/7/2016.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    //Server Key

    //AAAAJ7r5t04:APA91bFOchRIwAawplzkQ7484oJ12Zrjb54H3kUtljNiHJaXsV2jyq6MS8tk5Pd_ZakDk_e47WCw8ioF105OhTHyy_slg5N2eWaQo06oh4AnCLKJBpV0sHKt5YtN9zb06UYfsnv6dGZAiZWtpygUlmBBv7M7LPfdWw

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        /*
        * Appflyer
        */

        // Get updated InstanceID token.
        FirebaseInstanceId instance = FirebaseInstanceId.getInstance();
        String refreshedToken;
        if (instance != null) {
            refreshedToken = instance.getToken();
            /*
            * Notify
            */
            FirebaseUtils.storeRegistrationId(this, refreshedToken);
            MyLog.writeLog("refreshedToken: " + refreshedToken);

            /*
            * Appflyer
            */
            //AppsFlyerLib.getInstance().updateServerUninstallToken(getApplicationContext(), refreshedToken); // ADD THIS LINE HERE
        }
    }
}
