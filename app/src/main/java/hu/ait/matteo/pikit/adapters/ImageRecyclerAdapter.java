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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hu.ait.matteo.pikit.R;

/**
 * Created by matteosantamaria on 5/15/17.
 */

public class ImageRecyclerAdapter extends RecyclerView.Adapter<ImageRecyclerAdapter.ViewHolder> {

    private Map<String,String> idMap;
    private List<String> imageIDs;
    private FirebaseStorage firebaseStorage;
    private String groupID;
    private String uID;
    final long ONE_MEGABYTE = 1024 * 1024;

    public ImageRecyclerAdapter(String groupID, String uID) {
        idMap = new HashMap<String,String>();
        imageIDs = new ArrayList<String>();
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
        StorageReference storageReference = firebaseStorage.getReference(groupID+"/"+imageIDs.get(position));
        Log.d("TEST",groupID+"/"+imageIDs.get(position));

        storageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                // add byte array to imageview
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                holder.imageView.setImageBitmap(Bitmap.createBitmap(bmp));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("TEST", "download failed");
            }
        });
//         if this user took the picture, pull the image to the right
        if (idMap.get(imageIDs.get(position)).equals(uID)) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.imageView.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        }
    }

    @Override
    public int getItemCount() {
        return imageIDs.size();
    }

    public void addImage(String userID, String photoID) {
        imageIDs.add(photoID);
        idMap.put(photoID, userID);
        notifyDataSetChanged();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.post);
        }
    }
}
