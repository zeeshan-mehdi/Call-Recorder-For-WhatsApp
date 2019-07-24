package com.shaikhutech.callrecorder.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AlertDialog;
import android.view.View;
import android.widget.Toast;

import com.shaikhutech.callrecorder.R;


/**
 * Created by Ashiq on 4/12/2017.
 */

public class AppUtils {

    private static long backPressed = 0;

    public static void tapToExit(Activity activity) {
        if (backPressed + 2500 > System.currentTimeMillis()) {
            activity.finish();
        } else {
            showToast(activity.getApplicationContext(), activity.getResources().getString(R.string.tapAgain));
        }
        backPressed = System.currentTimeMillis();
    }

    public static void showToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    private static String formatPhoneNumber(String previousPhoneNumber) {
        if (previousPhoneNumber != null) {
            previousPhoneNumber = previousPhoneNumber.replaceAll(" ", "");
            if (!previousPhoneNumber.startsWith("0") && !previousPhoneNumber.startsWith("+")) {
                return "0" + previousPhoneNumber;
            }
        }
        return previousPhoneNumber;
    }

/*    public static void makeLocalPhoneCall(Activity activity, String phoneNumber) {
        if (phoneNumber != null) {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            phoneNumber = formatPhoneNumber(phoneNumber);
            callIntent.setData(Uri.parse("tel:"+phoneNumber));
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                activity.startActivity(callIntent);
            }
        }
    }*/

    public static void noInternetWarning(View view, final Context context) {
        if (!isNetworkAvailable(context)) {
            Snackbar snackbar = Snackbar.make(view, context.getString(R.string.no_internet), Snackbar.LENGTH_LONG);
            snackbar.setAction(context.getString(R.string.connect), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(android.provider.Settings.ACTION_SETTINGS);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });
            snackbar.show();
        }
    }

    public static void shareApp(Activity activity) {
        try {
            final String appPackageName = activity.getPackageName();
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, activity.getResources().getString(R.string.share_text) + " https://play.google.com/store/apps/details?id=" + appPackageName);
            sendIntent.setType("text/plain");
            activity.startActivity(sendIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void rateThisApp(Activity activity) {
        try {
            activity.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + activity.getPackageName())));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void appClosePrompt(final Activity activity) {
        new AlertDialog.Builder(activity)
                .setTitle("Exit From App ")
                .setMessage("Are you sure you want to Exit ?")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        activity.finishAffinity();
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

    }

    public static void makePhoneCall(Activity activity, String phoneNumber) {
        if (phoneNumber != null) {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + phoneNumber));
            if (PermissionUtils.isPermissionGranted(activity, PermissionUtils.CALL_PERMISSIONS, PermissionUtils.REQUEST_CALL)) {
                activity.startActivity(callIntent);
            }
        }
    }

    public static void sendSMS(Activity activity, String phoneNumber, String text) {
        if (phoneNumber != null) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + phoneNumber));
            intent.putExtra("sms_body", text);
            try {
                activity.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void sendEmail(Activity activity, String email, String subject, String body) {
        if (email != null) {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:" + email)); // add more email like: , xyz@gmail.com
            intent.putExtra(Intent.EXTRA_SUBJECT, subject);
            intent.putExtra(Intent.EXTRA_TEXT, body);

            // another way
            /*Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
            intent.putExtra(Intent.EXTRA_SUBJECT, subject);
            intent.putExtra(Intent.EXTRA_TEXT, body);
            intent.setType("message/rfc822");*/
            try {
                activity.startActivity(Intent.createChooser(intent, "Send mail..."));
                //activity.startActivity(createEmailOnlyChooserIntent(activity.getApplicationContext(), intent, email, "Send mail..."));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void invokeMessengerBot(Activity activity) {
        try {
            if (AppUtils.isPackageInstalled(activity.getApplicationContext(), "com.facebook.orca")) {

                /**
                 * get id of your facebook page from here:
                 * https://findmyfbid.com/
                 *
                 * Suppose your facebook page url is: http://www.facebook.com/hiponcho
                 *
                 * Visit https://findmyfbid.com/ and put your url and click on "Find Numeric Id"
                 * You will get and ID like this: 788720331154519
                 *
                 * Append an extra 'l' (L in lower case) with the number and please bellow
                 * So, final ID: 788720331154519l
                 */
                String id="265623561053909";

                activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("fb://messaging/" +  id))); // replace id
            } else {
                AppUtils.showToast(activity.getApplicationContext(),
                        activity.getApplicationContext().getResources().getString(R.string.install_messenger));
                activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=com.facebook.orca")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isPackageInstalled(Context context, String packagename) {
        try {
            PackageManager pm = context.getPackageManager();
            pm.getPackageInfo(packagename, 0);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


}
