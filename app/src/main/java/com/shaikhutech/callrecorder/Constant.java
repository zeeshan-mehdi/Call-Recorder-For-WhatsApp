package com.shaikhutech.callrecorder;

public final class Constant {

    public static final String TAG = "AbcCallRecorder";

    // notification topic to receive app wide push notifications
    public static final String TOPIC_GLOBAL = "notificationnn";

    // broadcast receiver intent filters
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";

    // id to handle the notification in the notification tray
    public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;

    public static final String SHARED_PREF = "ah_firebase";
    public static final String CALL_NUMBER ="+923476118810" ;
    public static final String SMS_TEXT = "Here goes your message ..";
    public static final String EMAIL_ADDRESS = "zeeshanmehdi18@gmail.com";
    public static final String EMAIL_SUBJECT = "Here goes your subject ..";
    public static final String EMAIL_BODY ="Here goes email body ... " ;
    public static String sSHARED_PREFERENCES = "sp";
    public static String sKEY_FOR_PROTECTED_MODE = "protectedMode";
    public static final String sKEY_FOR_BACKGROUND_STATUS = "background_status";
    public static final String sKEY_FOR_ONLY_SET_PIN = "only_set_pin";
    public static boolean sIS_FROM_ANOTHER_ACTIVITY = false;
    public static boolean sIS_FROM_BACKGROUND = false;
    public static boolean sIs_FROM_PIN_TO_PIN = false;
    public static boolean sFROM_MAIN_TO_ACTIVITY = false;
    public static boolean sFROM_FAV_TO_LISTEN = false;
    public static boolean sFROM_LISTEN_TO_MAIN = false;

}
