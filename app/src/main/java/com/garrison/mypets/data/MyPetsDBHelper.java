package com.garrison.mypets.data;

/**
 * Created by Garrison on 10/15/2014.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.garrison.mypets.data.MyPetsContract.PetTable;
import com.garrison.mypets.data.MyPetsContract.PetsEmergencyContactsTable;
import com.garrison.mypets.data.MyPetsContract.VetsTable;

/**
 * Created by Garrison on 10/1/2014.
 */
public class MyPetsDBHelper extends SQLiteOpenHelper {

    private final String LOG_TAG = MyPetsDBHelper.class.getSimpleName();

    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "pets.db";

    public MyPetsDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_PET_TABLE = "CREATE TABLE " + PetTable.TABLE_NAME + " (" +
                PetTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PetTable.COLUMN_PET_NAME           + " TEXT NOT NULL, " +
                PetTable.COLUMN_SPECIES_POS        + " INTEGER NOT NULL, " +
                PetTable.COLUMN_SPECIES_TEXT       + " TEXT NOT NULL, " +
                PetTable.COLUMN_DIET               + " TEXT, " +
                PetTable.COLUMN_DIET_FREQUENCY_POS + " INTEGER, " +
                PetTable.COLUMN_MICROCHIP          + " TEXT, " +
                PetTable.COLUMN_AVATAR_URI         + " TEXT); ";

        final String SQL_CREATE_EMERGENCY_CONTACTS_TABLE = "CREATE TABLE " + PetsEmergencyContactsTable.TABLE_NAME + " (" +
                PetsEmergencyContactsTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PetsEmergencyContactsTable.COLUMN_EMER_CONTACT_LOOKUP     + " TEXT, " +
                PetsEmergencyContactsTable.COLUMN_EMER_CONTACT_PRIMARY    + " TEXT, " +
                PetsEmergencyContactsTable.COLUMN_EMER_CONTACT_PHOTO_URI  + " TEXT, " +
                PetsEmergencyContactsTable.COLUMN_EMER_CONTACT_EMAIL      + " TEXT, " +
                PetsEmergencyContactsTable.COLUMN_EMER_CONTACT_STATE      + " INTEGER); " ;  // 0 - blank contact; 1 - valid contact

        final String SQL_CREATE_VETS_TABLE = "CREATE TABLE " + VetsTable.TABLE_NAME + " (" +
                VetsTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                VetsTable.COLUMN_VET_NAME     + " TEXT, " +
                VetsTable.COLUMN_VET_ADDRESS    + " TEXT, " +
                VetsTable.COLUMN_VET_PHONE  + " TEXT, " +
                VetsTable.COLUMN_VET_OPEN      + " INTEGER, " +
                VetsTable.COLUMN_VET_LATITUDE      + " FLOAT, " +
                VetsTable.COLUMN_VET_LONGITUDE      + " FLOAT, " +
                VetsTable.COLUMN_VET_DISTANCE_VALUE + " FLOAT, " +
                VetsTable.COLUMN_VET_MY_VET     + " BOOLEAN); " ;

        // Set up the location column as a foreign key to location table.
        //" FOREIGN KEY (" + PetEntry.COLUMN_VET_KEY + ") REFERENCES " +        // Future
        //VetEntry.TABLE_NAME + " (" + VetEntry._ID + "), " +

        sqLiteDatabase.execSQL(SQL_CREATE_PET_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_EMERGENCY_CONTACTS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_VETS_TABLE);

        //  Preload Contacts Table
        ContentValues args = new ContentValues();
        String nullString = null;
        args.put(PetsEmergencyContactsTable.COLUMN_EMER_CONTACT_LOOKUP,    nullString);
        args.put(PetsEmergencyContactsTable.COLUMN_EMER_CONTACT_PHOTO_URI, nullString);
        args.put(PetsEmergencyContactsTable.COLUMN_EMER_CONTACT_PRIMARY,   PetsEmergencyContactsTable.DEFAULT_CONTACT_NAME);
        args.put(PetsEmergencyContactsTable.COLUMN_EMER_CONTACT_EMAIL,     nullString);
        args.put(PetsEmergencyContactsTable.COLUMN_EMER_CONTACT_STATE, 0);
        for (int i=0; i < 3; i++) {
            sqLiteDatabase.insert(PetsEmergencyContactsTable.TABLE_NAME,
                    null,
                    args);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {

    }
}