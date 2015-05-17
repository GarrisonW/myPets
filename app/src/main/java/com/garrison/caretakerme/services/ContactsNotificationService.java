package com.garrison.caretakerme.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.garrison.caretakerme.MainActivity;
import com.garrison.caretakerme.R;
import com.garrison.caretakerme.data.CaretakerMeContract.PetsEmergencyContactsTable;

/**
 * Created by Garrison on 11/18/2014.
 */
public class ContactsNotificationService extends IntentService {

    private static final int DELETED_NOTIFICATION_ID = 1112;

    NotificationManager mNotificationManager = null;
    boolean fireNotify = false;
    private NotificationCompat.Builder mBuilder = null;
    private int numDeletes = 0;

    public ContactsNotificationService() {
        super("ContactsNotificationService");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        String lookupID;
        int _ID;

        Uri lookupUri = ContactsContract.Contacts.CONTENT_URI;
        String[] projection = { ContactsContract.Contacts.LOOKUP_KEY };
        String selection = ContactsContract.Contacts.LOOKUP_KEY + " = ?";
        Cursor contactsCursor;

        Uri petContactUri = PetsEmergencyContactsTable.buildEmergencyContactsUri();

        Cursor petContactsCursor = getContentResolver().query(petContactUri, null, null, null, null);
        if (petContactsCursor != null) {
            petContactsCursor.moveToFirst();
            while (!petContactsCursor.isAfterLast()) {

                lookupID = petContactsCursor.getString(petContactsCursor.getColumnIndex(PetsEmergencyContactsTable.COLUMN_EMER_CONTACT_LOOKUP));
                if (lookupID != null && lookupID.length() > 0) {
                    //  Check Contacts for existence
                    String[] values = { lookupID };
                    contactsCursor = getContentResolver().query(lookupUri, projection, selection, values, null);
                    // NOTE:   Not sure why, but NULL is not returned from query on Contacts when not found
                    // Instead, row count will be 0
                    if (contactsCursor.getCount() < 1) {
                        _ID = petContactsCursor.getInt(petContactsCursor.getColumnIndex(PetsEmergencyContactsTable._ID));

                        fireNotify = true;
                        setNotify();

                        // Clear the contact from the pets database
                        Uri clearUri = PetsEmergencyContactsTable.buildEmergencyContactUri(_ID);
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(PetsEmergencyContactsTable.COLUMN_EMER_CONTACT_LOOKUP, "");
                        contentValues.put(PetsEmergencyContactsTable.COLUMN_EMER_CONTACT_PRIMARY, PetsEmergencyContactsTable.DEFAULT_CONTACT_NAME);
                        contentValues.put(PetsEmergencyContactsTable.COLUMN_EMER_CONTACT_PHOTO_URI, (String)null);
                        contentValues.put(PetsEmergencyContactsTable.COLUMN_EMER_CONTACT_EMAIL, (String)null);
                        contentValues.put(PetsEmergencyContactsTable.COLUMN_EMER_CONTACT_STATE, 0);
                        String updateStmt = PetsEmergencyContactsTable._ID + " = " + _ID;
                        getContentResolver().update(clearUri, contentValues, updateStmt, null);
                    }
                    contactsCursor.close();
                }
                petContactsCursor.moveToNext();
                if (fireNotify)
                    doNotify();
            }
            petContactsCursor.close();
        }
    }

    public void setNotify() {

        String contentText;
        numDeletes++;
        if (numDeletes == 1)
            contentText = getResources().getString(R.string.notify_single);
        else
            contentText = numDeletes + " " + getResources().getString(R.string.notify_multiple);

        if (mBuilder == null) {
            mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.notify_icon)
                .setContentTitle(getString(R.string.notify_title));
        }
        mBuilder.setContentText(contentText);
    }

    public void doNotify() {
        if (mNotificationManager == null)
            mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Intent resultIntent = new Intent(this, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        mNotificationManager.notify(DELETED_NOTIFICATION_ID, mBuilder.build());
    }
}
