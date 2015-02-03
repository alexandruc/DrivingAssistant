package com.alexandruc.drivingassistant.core.phone;


import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.util.Log;

public class PhoneUtils {
    private Activity mContext;

    public PhoneUtils(Activity context){
        mContext = context;
    }

    public String getContactDisplayNameByNumber(String number) {
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        String name = "";

        ContentResolver contentResolver = mContext.getContentResolver();
        Cursor contactLookup = contentResolver.query(uri, new String[] {BaseColumns._ID, ContactsContract.PhoneLookup.DISPLAY_NAME }, null, null, null);

        try {
            if (contactLookup != null && contactLookup.getCount() > 0) {
                contactLookup.moveToNext();
                name = contactLookup.getString(contactLookup.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
            }
        } finally {
            if (contactLookup != null) {
                contactLookup.close();
            }
        }
        Log.d("PhoneUtils", "caller name is " + name);
        return name;
    }
}
