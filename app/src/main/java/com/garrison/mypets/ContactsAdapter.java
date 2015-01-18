package com.garrison.mypets;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by Garrison on 10/15/2014.
 */
public class ContactsAdapter extends CursorAdapter {

    private final String LOG_TAG = ContactsAdapter.class.getSimpleName();

    public ContactsFragment mContactFragment = null;
    public ListView mContactsListView = null;

    public ContactsAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);

    }

    @Override
    public View newView(final Context context, final Cursor cursor, ViewGroup viewGroup) {

        View view = LayoutInflater.from(context).inflate(R.layout.contacts_list_view, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        Button clearContactButton = viewHolder.clearContactButton;
        clearContactButton.setFocusable(false);

        Button shareContactButton = viewHolder.shareContactButton;
        shareContactButton.setFocusable(false);

        TextView contactName = viewHolder.contactNameView;
        contactName.setFocusable(false);

        contactName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = mContactsListView.getPositionForView((View) v.getParent());
                Cursor cursor = (Cursor)mContactsListView.getItemAtPosition(pos);
                ((Callback)mContactFragment).viewContact(pos);
            }
        });

        shareContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = mContactsListView.getPositionForView((View) v.getParent());
                Cursor cursor = (Cursor)mContactsListView.getItemAtPosition(pos);
                ((Callback)mContactFragment).shareContact(pos);
            }
        });

        clearContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = mContactsListView.getPositionForView((View) v.getParent());
                Cursor cursor = (Cursor)mContactsListView.getItemAtPosition(pos);
                ((Callback)mContactFragment).clearContact(pos);

            }
        });

        return view;
    }

    public void setFragment(ContactsFragment cf) {
        mContactFragment = cf;
    }
    public void setListView(ListView lv) {
        mContactsListView = lv;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        String contactPhotoString = cursor.getString(ContactsFragment.ADAPTER_BINDER_COL_EMER_CONTACT_PHOTO_URI);

        // Need to reset this to the default image for some reason
        Resources res = context.getResources();
        Drawable draw = res.getDrawable(R.drawable.mypets_launcher);

        viewHolder.contactPhotoImageView.setImageDrawable(draw);
        if (contactPhotoString != null) {
            // TODO: ????   Works fine for now, may need bitmap conversion for performance
            viewHolder.contactPhotoImageView.setImageURI(Uri.parse(contactPhotoString));
        }

        String contactName = cursor.getString(ContactsFragment.ADAPTER_BINDER_COL_EMER_CONTACT_PRIMARY);
        viewHolder.contactNameView.setText(contactName);
    }

    //  Helper to reduce mapping
    public static class ViewHolder {

        public final ImageView contactPhotoImageView;
        public final TextView contactNameView;
        public final Button clearContactButton;
        public final Button shareContactButton;

        public ViewHolder(View view) {
            contactPhotoImageView = (ImageView) view.findViewById(R.id.list_contacts_photo);
            contactNameView = (TextView) view.findViewById(R.id.list_contact_primary_name);
            shareContactButton = (Button) view.findViewById(R.id.share_contact_button);
            clearContactButton = (Button) view.findViewById(R.id.clear_contact_button);
        }
    }

    public interface Callback {
        public void viewContact(int pos);
        public void clearContact(int pos);
        public void shareContact(int pos);

    }
}

