package com.example.voicesearch.helper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.example.voicesearch.app.App;

/**
 * Created by Naveen Verma on 9/3/21.
 * To The New
 * naveen.verma@tothenew.com
 */

public class Utility {

    public static int dpToPx(Context iContext, int dp) {
        try {
            if (iContext != null && iContext.getResources() != null && iContext.getResources().getDisplayMetrics() != null) {
                float density = iContext.getResources().getDisplayMetrics().density;
                return Math.round((float) dp * density);
            }
        } catch (NullPointerException ignored) {
        }
        return 0;
    }


    public static void showToast(Context context, String message) {
        try {
            if (context != null) {
                Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
                if (toast != null) {
                    toast.show();
                }
            }
        } catch (Exception e) {
            Log.e("", e.getMessage(), e);
        }
    }


    /**
     * To check device has internet
     *
     * @param context
     * @return boolean as per status
     */
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            return netInfo != null && netInfo.isConnected() && netInfo.isConnectedOrConnecting();
        }

        return false;
    }

    public static boolean isNetworkConnected() {
        return isNetworkConnected(App.Companion.getContext());
    }
}