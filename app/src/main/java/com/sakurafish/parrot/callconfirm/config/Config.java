package com.sakurafish.parrot.callconfirm.config;

public class Config {

    // preference
    public static final String PREF_SHOW_FIRST_TUTORIAL = "PREF_SHOW_FIRST_TUTORIAL";
    public static final String PREF_LAUNCH_COUNT = "PREF_LAUNCH_COUNT";
    public static final String PREF_ASK_REVIEW = "PREF_ASK_REVIEW";
    public static final String PREF_VIBRATE_PATTERN = "PREF_VIBRATE_PATTERN";
    public static final String PREF_SOUND_ON = "PREF_SOUND_ON";
    public static final String PREF_SOUND = "PREF_SOUND";
    public static final String PREF_CREDIT = "PREF_CREDIT";
    public static final String PREF_MAIL_TO_DEV = "PREF_MAIL_TO_DEV";
    public static final String PREF_AFTER_CONFIRM = "PREF_AFTER_CONFIRM";
    public static final String PREF_APP_MESSAGE_NO = "PREF_APP_MESSAGE_NO";
    public static final String PREF_STATE_INVALID_TELNO = "PREF_STATE_INVALID_TELNO";

    // Intent
    public static final String INTENT_DB_UPDTE_ACTION = "INTENT_DB_UPDTE_ACTION";
    public static final String INTENT_EXTRAS_PHONENUMBER = "INTENT_EXTRAS_PHONENUMBER";

    //    Tag string
    public static final String TAG_ERROR = "TAG_ERROR";

    // Vibrator patterns
    public static final long[] VIBRATOR_PATTERN0 = {0, 1000};
    public static final long[] VIBRATOR_PATTERN1 = {0, 500};
    public static final long[] VIBRATOR_PATTERN2 = {0, 100, 50, 100, 50, 100};
    public static final long[] VIBRATOR_PATTERN3 = {0, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100};
    public static final long[] VIBRATOR_PATTERN4 = {0, 1000, 100, 1000, 100, 1000};
    public static final int VIBRATOR_NOT_REPEAT = -1;

    private Config() {
    }
}
