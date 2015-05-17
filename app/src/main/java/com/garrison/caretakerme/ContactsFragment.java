package com.garrison.caretakerme;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import static com.garrison.caretakerme.data.CaretakerMeContract.PetsEmergencyContactsTable;


/**
 * Created by Garrison on 10/18/2014.
 */
public class ContactsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int CONTACTS_LOADER = 4;

    private ListView mContactsListView = null;
    private ContactsAdapter mContactsAdapter = null;

   // static Context sContext;

    // For the pets view - columns to be displayed
    private static final String[] EMER_CONTACTS_TABLE_COLUMNS = {
            // ID is fully qualified because of later joins
            PetsEmergencyContactsTable.TABLE_NAME + "." + PetsEmergencyContactsTable._ID,
            PetsEmergencyContactsTable.COLUMN_EMER_CONTACT_LOOKUP,
            PetsEmergencyContactsTable.COLUMN_EMER_CONTACT_PRIMARY,
            PetsEmergencyContactsTable.COLUMN_EMER_CONTACT_PHOTO_URI,
            PetsEmergencyContactsTable.COLUMN_EMER_CONTACT_EMAIL,
            PetsEmergencyContactsTable.COLUMN_EMER_CONTACT_STATE
    };

    // These indices are to be bound (in the adapter) to PET_COLUMNS above.
    public static final int ADAPTER_BINDER_COL_EMER_CONTACT_ID = 0;
    public static final int ADAPTER_BINDER_COL_EMER_CONTACT_LOOKUP = 1;
    public static final int ADAPTER_BINDER_COL_EMER_CONTACT_PRIMARY = 2;
    public static final int ADAPTER_BINDER_COL_EMER_CONTACT_PHOTO_URI = 3;
    public static final int ADAPTER_BINDER_COL_EMER_CONTACT_EMAIL = 4;
    public static final int ADAPTER_BINDER_COL_EMER_CONTACT_STATE = 5;

    public ContactsFragment() {
        super();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(CONTACTS_LOADER, null, this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.contacts_fragment, container, false);

        // Get adapter
        mContactsAdapter = new ContactsAdapter(getActivity(), null, 0);
        mContactsAdapter.setFragment(this);

        mContactsListView = (ListView) rootView.findViewById(R.id.listview_contacts);
        mContactsListView.setAdapter(mContactsAdapter);
        mContactsAdapter.setListView(mContactsListView);

        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(CONTACTS_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        Uri contactsUri = PetsEmergencyContactsTable.buildEmergencyContactsUri();

        // Loads Cursor for list
        return new CursorLoader(
                getActivity(),
                contactsUri,
                EMER_CONTACTS_TABLE_COLUMNS,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor data) {
        mContactsAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mContactsAdapter.swapCursor(null);
    }

    public void viewContact(int pos) {
        Cursor cursor = (Cursor) mContactsListView.getItemAtPosition(pos);
        ((Callback)getActivity()).onItemSelected(
                cursor.getInt(ADAPTER_BINDER_COL_EMER_CONTACT_ID),
                cursor.getInt(ADAPTER_BINDER_COL_EMER_CONTACT_STATE),
                pos,
                cursor.getString(ADAPTER_BINDER_COL_EMER_CONTACT_LOOKUP));
    }

    public void shareContact(int pos) {

        Cursor cursor = (Cursor) mContactsListView.getItemAtPosition(pos);

        Intent shareIntent = new Intent(Intent.ACTION_SEND);

        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("message/rfc822");

        String emailAddress = cursor.getString(ADAPTER_BINDER_COL_EMER_CONTACT_EMAIL);
        shareIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{ emailAddress });
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, getText(R.string.email_subject_contacts));
        shareIntent.putExtra(Intent.EXTRA_TEXT, getText(R.string.share_message_contact));
        startActivity(Intent.createChooser(shareIntent, getText(R.string.share_contacts_header)));
    }

    public void clearContact(int pos) {
        Cursor cursor = (Cursor) mContactsListView.getItemAtPosition(pos);
        int _ID = cursor.getInt(ADAPTER_BINDER_COL_EMER_CONTACT_ID);
        Uri clearUri = PetsEmergencyContactsTable.buildEmergencyContactUri(_ID);

        ContentValues contentValues = new ContentValues();
        contentValues.put(PetsEmergencyContactsTable.COLUMN_EMER_CONTACT_LOOKUP, "");
        contentValues.put(PetsEmergencyContactsTable.COLUMN_EMER_CONTACT_PRIMARY, PetsEmergencyContactsTable.DEFAULT_CONTACT_NAME);
        contentValues.put(PetsEmergencyContactsTable.COLUMN_EMER_CONTACT_PHOTO_URI, (String)null);
        contentValues.put(PetsEmergencyContactsTable.COLUMN_EMER_CONTACT_EMAIL, (String)null);
        contentValues.put(PetsEmergencyContactsTable.COLUMN_EMER_CONTACT_STATE, 0);

        String updateStmt = PetsEmergencyContactsTable._ID + " = " + _ID;

        getActivity().getContentResolver().update(
                clearUri,
                contentValues,
                updateStmt,
                null
        );
        Toast.makeText(getActivity(), "Content has been cleared", Toast.LENGTH_SHORT).show();
        getLoaderManager().restartLoader(CONTACTS_LOADER, null, this);
    }

    public interface Callback {
        public void onItemSelected(int _ID, int state, int pos, String lookID);
    }

}

