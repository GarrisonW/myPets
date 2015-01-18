package com.garrison.mypets;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import com.garrison.mypets.data.MyPetsContract;

import static com.garrison.mypets.data.MyPetsContract.PetsEmergencyContactsTable;

/**
 * Created by Garrison on 10/18/2014.
 */
public class ContactsActivity extends ActionBarActivity implements ContactsFragment.Callback {

    private final String LOG_TAG = ContactsActivity.class.getSimpleName();

    private static final int CONTACTS_PICKER_RESULT = 100;

    private int mContactID = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.contacts_activity);
        ContactsFragment contentsFragment = (ContactsFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_contacts);
    }

    @Override
    public void onItemSelected(int _ID, int state, int pos, String lookupID) {
        mContactID = _ID;
        if (state == 0) {
            Intent contactPickerIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
            startActivityForResult(contactPickerIntent, CONTACTS_PICKER_RESULT);
        }
        else {

            Uri contactLookupUri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupID);
            Intent contentIntent = new Intent(Intent.ACTION_VIEW, contactLookupUri);
            startActivity(contentIntent);
        }

    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        int _ID = mContactID;

        if (resultCode == RESULT_OK) {
            Uri contactUri = data.getData();
            String[] projection = {ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
                    ContactsContract.Contacts.LOOKUP_KEY,
                    ContactsContract.Contacts.PHOTO_URI,
                    ContactsContract.Contacts._ID
            };

            Cursor cursor = getContentResolver()
                    .query(contactUri, projection, null, null, null);
            cursor.moveToFirst();
            String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY));
            String lookupKey = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
            String photoUri = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI));
            String contactID = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));

            String emailAddress = null;
            String[] emailProjection = {ContactsContract.CommonDataKinds.Email.DATA };
            String[] selValues = { contactID };
            Cursor emailCursor = getContentResolver()
                    .query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, emailProjection, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", selValues, null);

            if (emailCursor.getCount() > 0) {
                emailCursor.moveToFirst();
                emailAddress = emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
            }

            // Set contact data for update
            ContentValues contactValues = new ContentValues();
            contactValues.put(PetsEmergencyContactsTable.COLUMN_EMER_CONTACT_LOOKUP, lookupKey);
            contactValues.put(PetsEmergencyContactsTable.COLUMN_EMER_CONTACT_PRIMARY, contactName);
            contactValues.put(PetsEmergencyContactsTable.COLUMN_EMER_CONTACT_PHOTO_URI, photoUri);
            contactValues.put(PetsEmergencyContactsTable.COLUMN_EMER_CONTACT_EMAIL, emailAddress);
            contactValues.put(PetsEmergencyContactsTable.COLUMN_EMER_CONTACT_STATE, 1);
            if (_ID > 0) {
                String updateStmt = MyPetsContract.PetTable._ID + " = " + _ID;
                int numRows = getContentResolver().update(PetsEmergencyContactsTable.buildEmergencyContactUri(_ID), contactValues, updateStmt, null);
            }

        }
    }

}
