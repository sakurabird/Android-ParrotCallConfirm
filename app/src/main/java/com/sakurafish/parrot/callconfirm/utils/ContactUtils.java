package com.sakurafish.parrot.callconfirm.utils;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.ContactsContract;

import com.sakurafish.parrot.callconfirm.MyApplication;

import java.io.ByteArrayInputStream;

import static com.sakurafish.parrot.callconfirm.utils.RuntimePermissionsUtils.hasPermission;

public final class ContactUtils {


    public static ContactInfo getContactInfoByNumber(String number) {
        if (!hasPermission(MyApplication.getContext(), Manifest.permission.READ_CONTACTS)) {
            return null;
        }

        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));

        String projection[] = new String[]{BaseColumns._ID,
                ContactsContract.PhoneLookup.DISPLAY_NAME,
                ContactsContract.PhoneLookup.PHOTO_THUMBNAIL_URI
        };

        ContentResolver contentResolver = MyApplication.getContext().getContentResolver();
        Cursor contactLookup;
        try {
            contactLookup = contentResolver.query(uri, projection, null, null, null);
            if (contactLookup == null || contactLookup.getCount() <= 0) {
                return null;
            }
        } catch (NullPointerException e) {
            // SHARP AQUOSのAndroid5端末でNullPointerExceptionが発生する
            e.printStackTrace();
            return null;
        }

        ContactInfo info = new ContactInfo();
        try {
            contactLookup.moveToNext();
            info.number = number;
            info.id = contactLookup.getString(contactLookup.getColumnIndex(BaseColumns._ID));
            info.name = contactLookup.getString(contactLookup.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
            info.photoBitmap = openPhoto(Long.parseLong(info.id));
        } finally {
            contactLookup.close();
        }
        return info;
    }

    public static Bitmap openPhoto(long contactId) {
        Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId);
        Uri photoUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
        Cursor cursor = MyApplication.getContext().getContentResolver().query(photoUri,
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

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Bitmap getPhotoBitmap() {
            return photoBitmap;
        }

        public void setPhotoBitmap(Bitmap photoBitmap) {
            this.photoBitmap = photoBitmap;
        }
    }
}
