package com.superflace.core.phone;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.telephony.PhoneNumberUtils;
import android.util.Log;

public class PhoneUtils {
	
	private final static String PHONE_UTILS = "DrivingAssistant.PhoneUtils";
	
	private PhoneUtils() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Looks up the display name of a contact, startin7g from its phone number
	 * 
	 * @param context Context of the app, used to access contact information
	 * @param phoneNumber The phone number
	 * @return Displayed name or null in case of error
	 */
	public static String getDisplayNameForNumber(Context context, String phoneNumber){
		String contactName = null;
		
		if(context != null &&
				phoneNumber != null &&
				phoneNumber.length() > 0) {
			
			ContentResolver cr = context.getContentResolver();

			String[] reqPhoneColumns = { ContactsContract.CommonDataKinds.Phone.CONTACT_ID, ContactsContract.CommonDataKinds.Phone.NUMBER };
			Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, reqPhoneColumns, null, null, null);

			String id = null;
			while(pCur.moveToNext()){
				id = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
				String phone = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
				if(PhoneNumberUtils.compare(phone, phoneNumber) == true){
					Log.d(PHONE_UTILS, "matching id is: " + id + " phone nr: " + phone);
					break;
				}
				id = null;
			}
			pCur.close();
			if(id != null){
				String[] reqNameColumns = { ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME };
				Cursor nameCur = cr.query(ContactsContract.Contacts.CONTENT_URI, reqNameColumns, ContactsContract.Contacts._ID + " = ?", new String[] {id}, null);

				if(nameCur.moveToNext()){
					contactName = nameCur.getString(nameCur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
					Log.d(PHONE_UTILS, "matching contact name for phone nr " + phoneNumber + " is " + contactName);
				}
				else{
					Log.e(PHONE_UTILS, "no matching contact name for phone nr " + phoneNumber);
				}
				nameCur.close();
			}
			else{
				Log.e(PHONE_UTILS, "no contact id for phone nr " + phoneNumber);
			}
			
			cr = null;
		}
		else{
			Log.e(PHONE_UTILS, "getDisplayNameForNumber: invalid parameters");
		}
		
		return contactName;
	}
}
