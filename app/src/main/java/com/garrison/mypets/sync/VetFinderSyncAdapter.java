package com.garrison.mypets.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.garrison.mypets.R;
import com.garrison.mypets.data.MyPetsContract.VetsTable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;


/**
 * Created by Garrison on 9/21/2014.
 */
public class VetFinderSyncAdapter extends AbstractThreadedSyncAdapter {

    private final String LOG_TAG = VetFinderSyncAdapter.class.getSimpleName();

    public static String locationString = null;
    BufferedReader reader = null;

    public VetFinderSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    /*  Android 3.0 specification (for parallel syncs) when version 10 is no longer supported
    public VetFinderSyncAdapter(
            Context context,
            boolean autoInitialize,
            boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
    }
    */

    // Setter for location as "longitude,latitude" obtained from the user's device
    public static void setLocation(String loc) {
        locationString = loc;
    }

    @Override
    public void onPerformSync(Account account, Bundle bundle, String s, ContentProviderClient contentProviderClient, SyncResult syncResult) {
        Log.d(LOG_TAG, "Starting sync");

        HttpURLConnection urlConnection = null;
        String vetString = "";

        String android_API_Key = getContext().getString(R.string.android_api_key);  // Not used yet
        String browser_API_Key = getContext().getString(R.string.browser_api_key);
        String server_API_Key = getContext().getString(R.string.server_api_key);
        String radius = "10000";  //  In meters
        String types = "veterinary_care";

        Uri builder = Uri.parse("https://maps.googleapis.com/maps/api/place/nearbysearch/json?").buildUpon()
                //  Required paramters
                .appendQueryParameter("key", server_API_Key)
                .appendQueryParameter("location", locationString)
                .appendQueryParameter("radius", radius)
                //  Optional parameters
                .appendQueryParameter("types", types)
                .build();
Log.v(LOG_TAG, "GARRISON URL: " + builder.toString());
        URL vetsURL = null;
        try {
            vetsURL = new URL(builder.toString());
        }
        catch (MalformedURLException mue) {
            Log.e(LOG_TAG, "URL is Malformed: " + builder.toString());
            return;
        }

        try {
            // Open the connection
            urlConnection = (HttpURLConnection) vetsURL.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            InputStream inputStream = urlConnection.getInputStream();
            if (inputStream == null) {
                Log.v(LOG_TAG, "No data returned");// Nothing to do.
                return;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));

            StringBuffer buffer = new StringBuffer();
            // Read the input stream into a String
            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");

            }
            vetString = buffer.toString();

            Log.v(LOG_TAG, "VET STRING: " + vetString);
            if (buffer.length() == 0) {
                Log.e(LOG_TAG, "Empty String");
                return;
            }
        }
        catch (IOException ioe) {
            Log.e(LOG_TAG, "MYPETS IO ERROR: " + ioe.getMessage());
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        try {
            getJSONVetResults(vetString);

        } catch (JSONException je) {
            Log.e(LOG_TAG, "JSON error: ", je);
        }

        return;
    }

    /**
     * Helper method to have the sync adapter sync immediately
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */


        }
        return newAccount;
    }

    private void getJSONVetResults(String vetJSONString)throws JSONException {

        // These are the names of the JSON objects that need to be extracted.

        JSONArray vetsArray;

        final String VETS_RESULT_OBJECT = "results";
        final String VETS_NAME = "name";
        final String VETS_GEO_OBJECT = "geometry";
        final String VETS_LOCATION_OBJECT = "location";
        final String VETS_ADDRESS = "vicinity";
        final String VETS_LONGITUDE = "lng";
        final String VETS_LATITUDE = "lat";
        final String VETS_OPEN_HOURS_OBJECT = "opening_hours";
        final String VETS_OPEN = "open_now";

        JSONObject resultsObject = null;
        JSONObject vetsLocationObject = null;
        JSONObject vetsGeoObject = null;
        JSONObject vetsOpenHoursObject = null;

        String vetName = "";
        String vetAddress = "";
        String vetPhone = "";
        String vetOpen = "false";
        Double vetLatitude = 0.0;
        Double vetLongitude = 0.0;

        try {
            JSONObject vetJSON = new JSONObject(vetJSONString);
            vetsArray = vetJSON.getJSONArray(VETS_RESULT_OBJECT);

            Vector<ContentValues> loaderVector = new Vector<ContentValues>(vetsArray.length());

            for(int i = 0; i < vetsArray.length(); i++) {
                vetName = "";
                vetAddress = "";
                vetPhone = "";
                vetOpen = "";
                vetLatitude = 0.0;
                vetLongitude = 0.0;

                ContentValues vetDataValues = new ContentValues();
                resultsObject = vetsArray.getJSONObject(i);
                vetName = resultsObject.getString(VETS_NAME);
                vetAddress = resultsObject.getString(VETS_ADDRESS);
                // vetPhone = ???????
                vetsGeoObject = resultsObject.getJSONObject(VETS_GEO_OBJECT);
                vetsLocationObject = vetsGeoObject.getJSONObject(VETS_LOCATION_OBJECT);
                vetLatitude = vetsLocationObject.getDouble(VETS_LATITUDE);
                vetLongitude = vetsLocationObject.getDouble(VETS_LONGITUDE);
                if(resultsObject.has(VETS_OPEN_HOURS_OBJECT)) {
                    vetsOpenHoursObject = resultsObject.getJSONObject(VETS_OPEN_HOURS_OBJECT);
                    vetOpen = vetsOpenHoursObject.getString(VETS_OPEN);
                }

                vetDataValues.put(VetsTable.COLUMN_VET_NAME, vetName);
                vetDataValues.put(VetsTable.COLUMN_VET_ADDRESS, vetAddress);
                vetDataValues.put(VetsTable.COLUMN_VET_PHONE, vetPhone);
                if (vetOpen.equalsIgnoreCase("true"))
                    vetDataValues.put(VetsTable.COLUMN_VET_OPEN, true);
                else
                    vetDataValues.put(VetsTable.COLUMN_VET_OPEN, false);
                vetDataValues.put(VetsTable.COLUMN_VET_LATITUDE, vetLatitude);
                vetDataValues.put(VetsTable.COLUMN_VET_LONGITUDE, vetLongitude);
                boolean myVet = false;
                vetDataValues.put(VetsTable.COLUMN_VET_MY_VET, myVet);

                loaderVector.add(vetDataValues);
            }

            if (loaderVector.size() > 0) {
                ContentValues[] loaderArray = new ContentValues[loaderVector.size()];
                loaderVector.toArray(loaderArray);
                int rowsInserted = getContext().getContentResolver()
                        .bulkInsert(VetsTable.CONTENT_URI, loaderArray);
 Log.v(LOG_TAG, "inserted " + rowsInserted + " rows of vet data");
            }
        }
        catch (JSONException je) {
            Log.e(LOG_TAG, je.getLocalizedMessage());
        }

    }
}
