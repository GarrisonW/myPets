package com.garrison.caretakerme.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Garrison on 10/1/2014.
 */
public class CaretakerMeContract {

    // Content Definitions
    public static String CONTENT_AUTHORITY = "com.garrison.caretakerme";
    public static Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static String PATH_PET = "pets";
    public static String PATH_EMERGENCY_CONTACT = "contacts";
    public static String PATH_VETS = "vets";

    /* Inner class that defines the table contents of the weather table */
    public static final class PetTable implements BaseColumns {

        public static Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_PET).build();
        public static String CONTENT_TYPE = "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_PET;
        public static String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_PET;

        public static final String TABLE_NAME = "pets";

        //  Pet name
        public static final String COLUMN_PET_NAME = "petName";
        //  Pet photo
        public static final String COLUMN_AVATAR_URI = "avatar";
        //  Position of species in spinner
        public static final String COLUMN_SPECIES_POS = "speciesPos";
        //  Species Text
        public static final String COLUMN_SPECIES_TEXT = "speciesText";
        //  Pet breed
        public static final String COLUMN_BREED = "breed";
        //  Pet appearance
        public static final String COLUMN_COLOR_POS = "colorPos";
        //  Pet appearance
        public static final String COLUMN_COLOR_TEXT = "colorText";
        //  Pet diet Text
        public static final String COLUMN_DIET = "diet";
        //  Pet diet frequency position
        public static final String COLUMN_DIET_FREQUENCY_POS = "frequencyPos";
        //  Pet diet frequency position
        public static final String COLUMN_DIET_FREQUENCY_TEXT = "frequencyText";
        //  Pet meds table id (FUTURE)
        public static final String COLUMN_MEDS_ID = "medsID";
        //  Pet medications free text
        public static final String COLUMN_MEDS_FREE_TEXT = "medsFreeText";
        //  Radio ID tag number
        public static final String COLUMN_MICROCHIP = "microchip";
        //  Other Free Text
        public static final String COLUMN_OTHER_FREE_TEXT = "otherInfo";

        //   Setters and getters for Content Provider Uri - Pets Table
        public static Uri buildPetsUri() {
            return CONTENT_URI;
        }
        public static Uri buildPetUri(long _id) {
            return ContentUris.withAppendedId(CONTENT_URI, _id);
        }
    }

    public static final class PetsEmergencyContactsTable implements BaseColumns {

        public static Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_EMERGENCY_CONTACT).build();
        public static String CONTENT_TYPE = "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_EMERGENCY_CONTACT;
        public static String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_EMERGENCY_CONTACT;

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

    public static final class VetsTable implements BaseColumns {

        public static Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_VETS).build();
        public static String CONTENT_TYPE = "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_VETS;
        public static String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_VETS;

        public static final String TABLE_NAME = "vets";

        // Vet Google place id
        public static final String COLUMN_VET_PLACE_ID = "vetPlaceID";
        // Vet name
        public static final String COLUMN_VET_NAME = "vetName";
        // Vet address
        public static final String COLUMN_VET_ADDRESS = "address";
        // Vet phone
        public static final String COLUMN_VET_PHONE = "phone";
        // Vet business status
        public static final String COLUMN_VET_OPEN = "open";
        // Vet business status
        public static final String COLUMN_VET_LATITUDE = "latitude";
        // Vet business status
        public static final String COLUMN_VET_LONGITUDE = "longitude";
        // Vet business status
        public static final String COLUMN_VET_DISTANCE_VALUE = "distanceValue";
        // Vet business status
        public static final String COLUMN_VET_MY_VET = "myVet";

        //   Setters and getters for Content Provider Uri - Vets Table
        public static Uri buildVetsUri() {
            return CONTENT_URI;
        }
        public static Uri buildVetsUri(long _id) {
            return ContentUris.withAppendedId(CONTENT_URI, _id);
        }
    }
}
