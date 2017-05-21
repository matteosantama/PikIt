package hu.ait.matteo.pikit.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import hu.ait.matteo.pikit.R;
import hu.ait.matteo.pikit.data.Photo;

/**
 * Created by matteosantamaria on 5/15/17.
 */

public class ImageRecyclerAdapter extends RecyclerView.Adapter<ImageRecyclerAdapter.ViewHolder> {

    private Context context;
    private Set handledIDs;
    private List<Photo> photos;
    private FirebaseStorage firebaseStorage;
    private String groupID;
    private String uID;

    public ImageRecyclerAdapter(Context context, String groupID, String uID) {
        this.context = context;
        handledIDs = new HashSet();
        photos = new ArrayList<Photo>();
        this.uID = uID;
        this.groupID = groupID;
        firebaseStorage = FirebaseStorage.getInstance();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.image_post_layout, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Photo currPhoto = photos.get(position);

        Glide.with(context).load(currPhoto.getUrl()).override(600,300).into(holder.imageView);

        // if the current user took the picture, pull the image to the right
        if (photos.get(position).getCreatorID().equals(uID)) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.imageView.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        }
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    public void addPhoto(Photo photo) {
        if (!handledIDs.contains(photo.getPhotoID())) {
            photos.add(photo);
            handledIDs.add(photo.getPhotoID());
            notifyDataSetChanged();
        }
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.post);
        }
    }
}
