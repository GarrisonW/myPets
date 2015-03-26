package com.garrison.mypets.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.garrison.mypets.data.MyPetsContract.PetTable;
import com.garrison.mypets.data.MyPetsContract.PetsEmergencyContactsTable;
import com.garrison.mypets.data.MyPetsContract.VetsTable;

/**
 * Created by Garrison on 10/1/2014.
 */
public class MyPetsProvider extends ContentProvider {

    private final String LOG_TAG = MyPetsProvider.class.getSimpleName();

    private static final int PETS = 100;
    private static final int PET_BY_ID = 101;

    private static final int EMERGENCY_CONTACTS = 200;
    private static final int EMERGENCY_CONTACTS_BY_ID = 201;
    private static final int EMERGENCY_CONTACTS_BY_LOOKUP_ID = 202;

    private static final int VETS = 300;
    private static final int VET_BY_ID = 301;

    MyPetsDBHelper mDBHelper = null;
    UriMatcher sUriMatcher = buildUriMatcher();


    /*        FUTURE - Table Joins for Events
    static{
        sEventsByPetQueryBuilder = new SQLiteQueryBuilder();
        sEventsByPetQueryBuilder.setTables(
                PetTable.TABLE_NAME + " INNER JOIN " +
                        PetEventTable.TABLE_NAME + " ON " +
                        PetEventTable.TABLE_NAME + "." + PetEventTable.COLUMN_PET_KEY +
                        " = " +
                        PetTable.TABLE_NAME + "." + PetTable._ID);
    }

    */

    public MyPetsProvider() {
        super();
    }

    @Override
    public boolean onCreate() {

       //CLEAR DATABASE FOR TESTING:
getContext().deleteDatabase(mDBHelper.DATABASE_NAME);
        mDBHelper = new MyPetsDBHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String queryString, String[] selectionArgs, String sortOrder) {

        Cursor retCursor = null;
        SQLiteDatabase db = mDBHelper.getReadableDatabase();

        switch (sUriMatcher.match(uri)) {
            case PETS:
            {
                retCursor = db.query(
                    PetTable.TABLE_NAME,
                    projection,
                    queryString,
                    selectionArgs,
                    null,
                    null,
                    sortOrder
                );

                break;
            }
            case PET_BY_ID:
            {
                retCursor = db.query(
                        PetTable.TABLE_NAME,
                        projection,
                        queryString,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );

                break;
            }
            case EMERGENCY_CONTACTS:
            {
                retCursor = db.query(
                        PetsEmergencyContactsTable.TABLE_NAME,
                        projection,
                        queryString,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                //retCursor.close();

                break;
            }
            case VETS:
            {
                retCursor = db.query(
                        VetsTable.TABLE_NAME,
                        projection,
                        queryString,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                //retCursor.close();

                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);
        }
        //  This sets the notification listener on the uri
        //retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return retCursor;
    }

    @Override
    public String getType(Uri uri) {
        // Use the Uri Matcher to determine what kind of URI this is - item or list
        final int myMatch =  sUriMatcher.match(uri);

        switch (myMatch) {
            case PETS:
                return PetTable.CONTENT_TYPE;
            case PET_BY_ID:
                return PetTable.CONTENT_ITEM_TYPE;
            case EMERGENCY_CONTACTS:
                return PetsEmergencyContactsTable.CONTENT_TYPE;
            case EMERGENCY_CONTACTS_BY_ID:
                return PetsEmergencyContactsTable.CONTENT_ITEM_TYPE;
            case EMERGENCY_CONTACTS_BY_LOOKUP_ID:
                return PetsEmergencyContactsTable.CONTENT_ITEM_TYPE;
            case VETS:
                return VetsTable.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {

        SQLiteDatabase db = mDBHelper.getWritableDatabase();

        int myMatch = sUriMatcher.match(uri);
        Uri returnUri = null;
        long _id = 0;

        Cursor retCursor;

        switch (myMatch) {
            case PETS: {
                try{
                    _id = db.insert(PetTable.TABLE_NAME, null, contentValues);
                }
                catch (Exception dbEx){
                    Log.e(LOG_TAG, "We have a db error on insert: " + dbEx.getMessage());
                }
                finally{
                    db.close();
                }

                if ( _id > 0 )
                    returnUri = PetTable.buildPetUri(_id);
                else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            case VETS: {
                try{
                    _id = db.insert(VetsTable.TABLE_NAME, null, contentValues);
                }
                catch (Exception dbEx){
                    Log.e(LOG_TAG, "We have a db error on insert: " + dbEx.getMessage());
                }
                finally{
                    db.close();
                }

                if ( _id > 0 )
                    returnUri = VetsTable.builVetsUri(_id);
                else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);
        }

        //  This notifies any Content Observers (i.e. other apps) that the insert on the URI has changed
        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {

        final SQLiteDatabase db = mDBHelper.getWritableDatabase();

        final int myMatch = sUriMatcher.match(uri);
        int numRows = 0;

        switch (myMatch) {
            case PET_BY_ID: {
                try {
                    numRows = db.update(PetTable.TABLE_NAME,
                            contentValues,
                            selection,
                            selectionArgs);
                }
                catch (Exception dbEx){
                    Log.e(LOG_TAG, "We have a db error on update: " + dbEx.getMessage());
                }
                finally{
                    db.close();
                }

                break;
            }
            case EMERGENCY_CONTACTS_BY_ID: {
                try {
                    numRows = db.update(PetsEmergencyContactsTable.TABLE_NAME,
                            contentValues,
                            selection,
                            selectionArgs);
                }
                catch (Exception dbEx){
                    Log.e(LOG_TAG, "We have a db error on update - EMER CONTACTS: " + dbEx.getMessage());
                }
                finally{
                    db.close();
                }

                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);
        }
        if (numRows > 0)
            getContext().getContentResolver().notifyChange(uri, null);
        Log.v(LOG_TAG, " UPDATING DONE ");
        return numRows;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int numRows = 0;

        SQLiteDatabase db = mDBHelper.getWritableDatabase();

        final int myMatch = sUriMatcher.match(uri);
        switch (myMatch) {
            case PETS: {
                try {
                    numRows = db.delete(PetTable.TABLE_NAME,
                            selection,
                            null
                    );
                } catch (Exception dbEx) {
                    Log.e(LOG_TAG, "We have a db error on delete: " + dbEx.getMessage());
                } finally {
                    db.close();
                }
                break;
            }
            case VETS: {
                try {
                    numRows = db.delete(VetsTable.TABLE_NAME,
                            selection,
                            null
                    );
                } catch (Exception dbEx) {
                    Log.e(LOG_TAG, "We have a db error on delete: " + dbEx.getMessage());
                } finally {
                    db.close();
                }
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);

        }
        Log.v(LOG_TAG, " DELETEING DONE ");
        return numRows;
    }

    public static UriMatcher buildUriMatcher(){

        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        String authority = MyPetsContract.CONTENT_AUTHORITY;

        uriMatcher.addURI(authority, MyPetsContract.PATH_PET, PETS);
        uriMatcher.addURI(authority, MyPetsContract.PATH_PET + "/#", PET_BY_ID);
        uriMatcher.addURI(authority, MyPetsContract.PATH_EMERGENCY_CONTACT, EMERGENCY_CONTACTS);
        uriMatcher.addURI(authority, MyPetsContract.PATH_EMERGENCY_CONTACT + "/#", EMERGENCY_CONTACTS_BY_ID);
        uriMatcher.addURI(authority, MyPetsContract.PATH_EMERGENCY_CONTACT + "/*", EMERGENCY_CONTACTS_BY_LOOKUP_ID);
        uriMatcher.addURI(authority, MyPetsContract.PATH_VETS, VETS);
        uriMatcher.addURI(authority, MyPetsContract.PATH_VETS + "/#", VET_BY_ID);

        return uriMatcher;
    }
}
