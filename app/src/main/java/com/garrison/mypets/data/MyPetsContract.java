package com.garrison.mypets.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Garrison on 10/1/2014.
 */
public class MyPetsContract {

    // Content Definitions
    public static String CONTENT_AUTHORITY = "com.garrison.mypets";
    public static Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static String PATH_PET = "pets";
    public static String PATH_EMER_CONTACT_PET = "contacts";

    //  Future
    //public static String PATH_PET_EVENT = "petevents";
    //public static String PATH_PET_MEDS =  "meds";


    /* Inner class that defines the table contents of the weather table */
    public static final class PetTable implements BaseColumns {

        public static Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_PET).build();
        public static String CONTENT_TYPE = "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_PET;
        public static String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_PET;

        public static final String TABLE_NAME = "pets";

        //  Pet name
        public static final String COLUMN_PET_NAME = "petName";
        //  Position of species in spinner
        public static final String COLUMN_SPECIES_POS = "speciesPos";
        //  Species Text
        public static final String COLUMN_SPECIES_TEXT = "speciesText";
        //  Pet diet Text
        public static final String COLUMN_DIET = "diet";
        //  Pet diet frequency
        public static final String COLUMN_DIET_FREQUENCY_POS = "frequency";
        //  Radio ID tag number
        public static final String COLUMN_MICROCHIP = "microchip";
        //  Pet photo
        public static final String COLUMN_AVATAR_URI = "avatar";

        //   Setters and getters for Content Provider Uri - Pets Table
        public static Uri buildPetsUri() {
            return CONTENT_URI;
        }
        public static Uri buildPetUri(long _id) {
            return ContentUris.withAppendedId(CONTENT_URI, _id);
        }
    }

    public static final class PetsEmergencyContactsTable implements BaseColumns {

        public static Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_EMER_CONTACT_PET).build();
        public static String CONTENT_TYPE = "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_EMER_CONTACT_PET;
        public static String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_EMER_CONTACT_PET;

        public static final String TABLE_NAME = "emergency";

        public static final String DEFAULT_CONTACT_NAME = "Add Contact";
        // Contacts Lookup Id
        public static final String COLUMN_EMER_CONTACT_LOOKUP = "contact";
        // Primary Contact Name
        public static final String COLUMN_EMER_CONTACT_PRIMARY = "primaryName";
        // Contact Photo
        public static final String COLUMN_EMER_CONTACT_PHOTO_URI = "photo";
        // Contact email
        public static final String COLUMN_EMER_CONTACT_EMAIL = "email";
        // Contact Photo
        public static final String COLUMN_EMER_CONTACT_STATE = "state";

        //   Setters and getters for Content Provider Uri - Pets Table
        public static Uri buildEmergencyContactsUri() {
            return CONTENT_URI;
        }
        public static Uri buildEmergencyContactUri(long _id) {
            return ContentUris.withAppendedId(CONTENT_URI, _id);
        }
        public static Uri buildEmergencyContactUriByLookupID(String lookupID) {
            return CONTENT_URI.buildUpon().appendQueryParameter(COLUMN_EMER_CONTACT_LOOKUP, lookupID).build();
        }
    }

    /*  Future
    public static final class PetEventTable implements BaseColumns {

        public static final String TABLE_NAME = "events";

        public static final String COLUMN_EVENT_TYPE = "event_type";
        public static final String COLUMN_EVENT_NOTIFY = "event_notify";
        public static final String COLUMN_FREQUENCY = "event_frequency";

        // foreign key
        public static final String COLUMN_PET_KEY = "pet_id";

    }
    */

}
