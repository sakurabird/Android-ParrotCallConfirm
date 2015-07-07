package com.sakurafish.parrot.callconfirm.activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.sakurafish.common.lib.pref.Pref;
import com.sakurafish.parrot.callconfirm.Config;
import com.sakurafish.parrot.callconfirm.MyApplication;
import com.sakurafish.parrot.callconfirm.R;

import java.io.ByteArrayInputStream;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 確認ダイアログ表示
 * Created by sakura on 2015/01/23.
 */
public class ConfirmActivity extends Activity {
    private Context mContext;
    private String mPhoneNumber;

    public static Intent createIntent(@NonNull final Context context, @NonNull final Class clazz, @NonNull final String phoneNumber) {
        Intent intent = new Intent(context, clazz);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle bundle = new Bundle();
        bundle.putString(Config.INTENT_EXTRAS_PHONENUMBER, phoneNumber);
        intent.putExtras(bundle);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_confirm);
        ButterKnife.bind(this);

        mContext = this;
        mPhoneNumber = getIntent().getStringExtra(Config.INTENT_EXTRAS_PHONENUMBER);
        if (mPhoneNumber == null) {
            throw new IllegalStateException();
        }

        initLayout();
    }

    private void initLayout() {
        if (Pref.getPrefBool(mContext, getString(R.string.PREF_VIBRATE), true)) {
            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(1000);
        }
        if (Pref.getPrefBool(mContext, getString(R.string.PREF_SOUND_ON), true)) {
            MyApplication.getSoundManager().play(MyApplication.getSoundIds()[0]);
        }

        ((TextView) findViewById(R.id.textView_telno)).setText(mPhoneNumber);
        ContactInfo info = getContactInfoByNumber(mPhoneNumber);
        if (info != null) {
            ((TextView) findViewById(R.id.textView_name)).setText(info.name);
            if (info.photoBitmap != null)
                ((ImageView) findViewById(R.id.imageView_callto)).setImageBitmap(info.photoBitmap);
        }
    }

    @OnClick(R.id.button_ok)
    void call() {
        Pref.setPref(mContext, Config.PREF_AFTER_CONFIRM, true);
        String number = "tel:" + mPhoneNumber.trim();
        startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(number)));
        ConfirmActivity.this.finish();
    }

    @OnClick(R.id.button_no)
    void cancel() {
        Pref.setPref(mContext, Config.PREF_AFTER_CONFIRM, false);
        ConfirmActivity.this.finish();
    }

    @OnClick(R.id.imageView_setting)
    void setting() {
        Pref.setPref(mContext, Config.PREF_AFTER_CONFIRM, false);
        startActivity(MainActivity.createIntent(mContext, MainActivity.class));
        ConfirmActivity.this.finish();
    }

    @Override
    public void onBackPressed() {
        Pref.setPref(mContext, Config.PREF_AFTER_CONFIRM, false);
        ConfirmActivity.this.finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mContext = null;
        mPhoneNumber = null;
    }

    public ContactInfo getContactInfoByNumber(String number) {
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));

        String projection[] = new String[]{BaseColumns._ID,
                ContactsContract.PhoneLookup.DISPLAY_NAME,
                ContactsContract.PhoneLookup.PHOTO_THUMBNAIL_URI
        };

        ContentResolver contentResolver = getContentResolver();
        Cursor contactLookup = contentResolver.query(uri, projection, null, null, null);
        if (contactLookup == null || contactLookup.getCount() <= 0) {
            return null;
        }

        ContactInfo info = new ContactInfo();
        try {
            contactLookup.moveToNext();
            info.number = mPhoneNumber;
            info.id = contactLookup.getString(contactLookup.getColumnIndex(BaseColumns._ID));
            info.name = contactLookup.getString(contactLookup.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
            info.photoBitmap = openPhoto(Long.parseLong(info.id));
        } finally {
            contactLookup.close();
        }
        return info;
    }

    public Bitmap openPhoto(long contactId) {
        Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId);
        Uri photoUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
        Cursor cursor = getContentResolver().query(photoUri,
                new String[]{ContactsContract.Contacts.Photo.PHOTO}, null, null, null);
        if (cursor == null) {
            return null;
        }
        try {
            if (cursor.moveToFirst()) {
                byte[] data = cursor.getBlob(0);
                if (data != null) {
                    return BitmapFactory.decodeStream(new ByteArrayInputStream(data));
                }
            }
        } finally {
            cursor.close();
        }
        return null;
    }

    public static class ContactInfo {
        String id;
        String number;
        String name;
        Bitmap photoBitmap;
    }
}
