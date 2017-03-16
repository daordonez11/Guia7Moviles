package com.moviles.domiciliosmoviles.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ListViewCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.moviles.domiciliosmoviles.R;
import android.provider.ContactsContract;

public class ContactsActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        AdapterView.OnItemClickListener{
    private static final int LOADER_ID = 111;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 123;
    private ListView contactslv;
    /*
       * Defines an array that contains column names to move from
       * the Cursor to the ListView.
       */
    @SuppressLint("InlinedApi")
    private final static String[] FROM_COLUMNS = {
            Build.VERSION.SDK_INT
                    >= Build.VERSION_CODES.HONEYCOMB ?
                    ContactsContract.Contacts.DISPLAY_NAME_PRIMARY :
                    ContactsContract.Contacts.DISPLAY_NAME
    };
    @SuppressLint("InlinedApi")
    private static final String[] PROJECTION =
            {
                    ContactsContract.Contacts._ID,
                    ContactsContract.Contacts.LOOKUP_KEY,
                    ContactsContract.Contacts.HAS_PHONE_NUMBER,

                    Build.VERSION.SDK_INT
                            >= Build.VERSION_CODES.HONEYCOMB ?
                            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY :
                            ContactsContract.Contacts.DISPLAY_NAME

            };


    // Defines a variable for the search string
    private String mSearchString;
    // Defines the array to hold values that replace the ?
    // The column index for the _ID column
    private static final int CONTACT_ID_INDEX = 0;
    // The column index for the LOOKUP_KEY column
    private static final int LOOKUP_KEY_INDEX = 1;
    /*
     * Defines an array that contains resource ids for the layout views
     * that get the Cursor column contents. The id is pre-defined in
     * the Android framework, so it is prefaced with "android.R.id"
     */
    private final static int[] TO_IDS = {
            android.R.id.text1
    };
    // Define global mutable variables
    // Define a ListView object
    ListView mContactsList;
    // Define variables for the contact the user selects
    // The contact's _ID value
    long mContactId;
    // The contact's LOOKUP_KEY
    String mContactKey;
    // A content URI for the selected contact
    Uri mContactUri;
    // An adapter that binds the result Cursor to the ListView
    private SimpleCursorAdapter mCursorAdapter;
    private String name;
    private String price;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if(getIntent() != null && getIntent().getExtras().getString("name") != null){
            name = getIntent().getExtras().getString("name");
            price = getIntent().getExtras().getString("price");
        }
        // Initializes the loader
        getLoaderManager().initLoader(0, null, this);

        contactslv = (ListView)findViewById(R.id.contacts_listview);
        contactslv.setOnItemClickListener(this);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        }else{
            getLoaderManager().initLoader(
                    LOADER_ID,  // The identifier of the loader to initialize
                    null,       // Arguments for the loader (in this case, none)
                    this);      // The context of the activity

            // Creates a new cursor adapter to attach to the list view
            mCursorAdapter = new SimpleCursorAdapter(
                    this,
                    R.layout.detail_contact,
                    null,
                    FROM_COLUMNS, TO_IDS,
                    0);
            // Sets the ListView's backing adapter.
            contactslv.setAdapter(mCursorAdapter);

        }
        // Initializes the loader identified by LOADER_ID.

    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    getLoaderManager().initLoader(
                            LOADER_ID,  // The identifier of the loader to initialize
                            null,       // Arguments for the loader (in this case, none)
                            this);      // The context of the activity

                    // Creates a new cursor adapter to attach to the list view
                    mCursorAdapter = new SimpleCursorAdapter(
                            this,
                            R.layout.detail_contact,
                            null,
                            FROM_COLUMNS, TO_IDS,
                            0);
                    // Sets the ListView's backing adapter.

                    contactslv.setAdapter(mCursorAdapter);
                } else {
                     Snackbar.make(contactslv,"Sin permiso para contactos",Snackbar.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }


    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {

        // Starts the query
        return new CursorLoader(
                this,
                ContactsContract.Contacts.CONTENT_URI,
                PROJECTION,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Put the result Cursor in the adapter for the ListView
        mCursorAdapter.swapCursor(cursor);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Delete the reference to the existing Cursor
        mCursorAdapter.swapCursor(null);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        // Get the Cursor
        Cursor cursor =
                ((SimpleCursorAdapter)parent.getAdapter()).getCursor();
        // Move to the selected contact
        cursor.moveToPosition(position);
        // Get the _ID value
        mContactId = cursor.getLong(CONTACT_ID_INDEX);
        // Get the selected LOOKUP KEY
        mContactKey = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
        // Create the contact's content Uri
        mContactUri = ContactsContract.Contacts.getLookupUri(mContactId, mContactKey);
        /*
         * You can use mContactUri as the content URI for retrieving
         * the details for a contact.
         */
        if (Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0)
        {
            // Query phone here. Covered next
            Cursor phones = getContentResolver()
                    .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ id,null, null);
           String onePhone = "";
            while (phones.moveToNext()) {
                onePhone = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                Log.i("Number", onePhone);
            }
            //Send intent
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("smsto:"+onePhone));

            intent.putExtra("sms_body", name +" - "+price);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
            phones.close();
        }else{
            Log.d("Tag","no number");
        }
    }
}
