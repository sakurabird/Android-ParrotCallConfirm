package com.sakurafish.parrot.callconfirm.config;

import com.sakurafish.parrot.callconfirm.R;

public class Config {

    // preference
    public static final String PREF_SHOW_FIRST_TUTORIAL = "PREF_SHOW_FIRST_TUTORIAL";
    public static final String PREF_LAUNCH_COUNT = "PREF_LAUNCH_COUNT";
    public static final String PREF_ASK_REVIEW = "PREF_ASK_REVIEW";
    public static final String PREF_VIBRATE_PATTERN = "PREF_VIBRATE_PATTERN";
    public static final String PREF_SOUND_ON = "PREF_SOUND_ON";
    public static final String PREF_SOUND = "PREF_SOUND";
    public static final String PREF_SOUND_PLAY_SWITCH = "PREF_SOUND_PLAY_SWITCH";
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

    // Sounds
    public static final int SOUND_INCO1 = R.raw.inco1;
    public static final int SOUND_INCO2 = R.raw.inco2;
    public static final int SOUND_INCO3 = R.raw.inco3;
    public static final int SOUND_INCO4 = R.raw.inco4;
    public static final int SOUND_INCO5 = R.raw.inco5;
    public static final int SOUND_INCO6 = R.raw.inco6;
    public static final int SOUND_INCO7 = R.raw.inco7;
    public static final int SOUND_INCO8 = R.raw.inco8;
    public static final int SOUND_INCO9 = R.raw.inco9;
    public static final int SOUND_INCO10 = R.raw.inco10;
    public static final int SOUND_INCO11 = R.raw.inco11;
    public static final int SOUND_INCO12 = R.raw.inco12;
    public static final int SOUND_RING1 = R.raw.ring1;
    public static final int SOUND_RING2 = R.raw.ring2;
    public static final int SOUND_WARBLER = R.raw.warbler;
    public static final int SOUND_CUCKOO = R.raw.cuckoo;
    public static final int SOUND_CHICKEN1 = R.raw.chicken1;
    public static final int SOUND_CHICKEN2 = R.raw.chicken2;
    public static final int SOUND_CANARY = R.raw.canary;
    public static final int SOUND_BLUE_AND_WHITE_FLYCATCHER = R.raw.blue_and_white_flycatcher;
    public static final int SOUND_CAT = R.raw.cat1;
    public static final int SOUND_DOG = R.raw.dog1;
    public static final int SOUND_HORSE = R.raw.horse1;
    public static final int SOUND_ELEPHANT = R.raw.elephant;

    public static final int[] SOUND_IDS = {
            SOUND_INCO1, SOUND_INCO2, SOUND_INCO3, SOUND_INCO4, SOUND_INCO5,
            SOUND_INCO6, SOUND_INCO7, SOUND_INCO8, SOUND_INCO9, SOUND_INCO10,
            SOUND_INCO11, SOUND_INCO12,
            SOUND_RING1, SOUND_RING2,
            SOUND_WARBLER, SOUND_CUCKOO, SOUND_CHICKEN1, SOUND_CHICKEN2,
            SOUND_CANARY, SOUND_BLUE_AND_WHITE_FLYCATCHER,
            SOUND_CAT, SOUND_DOG, SOUND_HORSE, SOUND_ELEPHANT
    };

    private Config() {
    }
}
