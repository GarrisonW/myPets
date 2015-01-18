package com.garrison.mypets;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Garrison on 10/4/2014.
 */
public class MainPetsListAdapter extends CursorAdapter {

    private final String LOG_TAG = MainPetsListAdapter.class.getSimpleName();

    public MainPetsListAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    private Uri mAvatarUri = null;

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View view = LayoutInflater.from(context).inflate(R.layout.pet_list_view, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder)view.getTag();

        String avatarUri = cursor.getString(MainFragment.ADAPTER_BINDER_COL_AVATAR);
        if (avatarUri != null) {
            mAvatarUri = Uri.parse(avatarUri);
            Bitmap sizedBitmap = Utility.resizeImage(context, avatarUri, 25, 25);
            viewHolder.petAvatarImageView.setImageBitmap(sizedBitmap);
        }

        String petName = cursor.getString(MainFragment.ADAPTER_BINDER_COL_PET_NAME);
        viewHolder.petNameView.setText(petName);

        String speciesText = cursor.getString(MainFragment.ADAPTER_BINDER_COL_SPECIES_TEXT);
        viewHolder.petSpeciesView.setText(speciesText);
    }

    //  Helper to reduce mapping
    public static class ViewHolder {
        public final ImageView petAvatarImageView;
        public final TextView petNameView;
        public final TextView petSpeciesView;

        public ViewHolder(View view) {
            petAvatarImageView = (ImageView) view.findViewById(R.id.list_pet_avatar);
            petNameView = (TextView) view.findViewById(R.id.list_pet_name);
            petSpeciesView = (TextView) view.findViewById(R.id.list_pet_species);
        }
    }
}
