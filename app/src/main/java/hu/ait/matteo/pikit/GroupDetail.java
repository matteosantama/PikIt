package hu.ait.matteo.pikit;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hu.ait.matteo.pikit.adapters.ImageRecyclerAdapter;
import hu.ait.matteo.pikit.data.Group;
import hu.ait.matteo.pikit.data.User;
import hu.ait.matteo.pikit.helpers.Database;
import hu.ait.matteo.pikit.interfaces.GetDataListener;

public class GroupDetail extends AppCompatActivity {

    @BindView(R.id.addUsersBtn)
    Button addUsersBtn;

    private FirebaseDatabase firebaseDatabase;
    private StorageReference storageRef;
    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.8F);
    private String groupID;
    private String uID;
    private Group group;
    private Toolbar toolbar;
    private ImageRecyclerAdapter imageRecyclerAdapter;

    private User addedUser = null;

    private static final int CAMERA_REQUEST = 1888;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail);

        // Get groupID from previous activity
        Bundle extras = getIntent().getExtras();
        groupID = extras.getString("groupID");
        uID = extras.getString("userID");

        // Init Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar_top);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Bind ButterKnife
        ButterKnife.bind(this);

        // Connect to Firebase and get local Group instance
        firebaseDatabase = FirebaseDatabase.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();
        groupFromFirebase();

        // Instantiate Recycler
        imageRecyclerAdapter = new ImageRecyclerAdapter(groupID, uID);
        RecyclerView recyclerViewGroups = (RecyclerView) findViewById(
                R.id.imageRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewGroups.setLayoutManager(layoutManager);
        recyclerViewGroups.setAdapter(imageRecyclerAdapter);

        // Connect listeners
        connectListeners();
    }

    private void groupFromFirebase() {
        Query queryRef = firebaseDatabase.getReference("groups/"+groupID);

        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                group = dataSnapshot.getValue(Group.class);
                toolbar.setTitle(group.getName());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // pass
            }
        });

    }

    private void connectListeners() {
        Query imagesRef = FirebaseDatabase.getInstance().getReference("images").child(groupID);
        imagesRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    String key = dataSnapshot.getKey();
                    String value = data.getValue().toString();
//                    Log.d("TAG", "adding: ("+key+", "+value+")");
                    imageRecyclerAdapter.addImage(key, value);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    String key = dataSnapshot.getKey();
                    String value = data.getValue().toString();
//                    Log.d("TAG", "adding: ("+key+", "+value+")");
                    imageRecyclerAdapter.addImage(key, value);
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @OnClick(R.id.addUsersBtn)
    public void addUsers() {
        addUsersBtn.startAnimation(buttonClick);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.user_add_dialog, null);

        builder.setTitle("Enter Email");
        builder.setMessage("Enter the email address of a user you would like to add");
        builder.setView(dialogView);

        builder.setPositiveButton("Add",
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        // Do nothing here
                    }
                });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        final AlertDialog alert = builder.create();
        alert.show();

        alert.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText inputField = (EditText) dialogView.findViewById(R.id.user_to_add);
                String emailString = inputField.getText().toString();

                // call async task and get user object
                userFromDB(emailString, inputField, alert);
            }
        });

    }

    private void userFromDB(String emailString, final EditText field, final AlertDialog alert) {
        new Database().readDataOnce(emailString, new GetDataListener() {
            @Override
            public void onStart() {
                //DO SOME THING WHEN START GET DATA HERE
            }

            @Override
            public void onSuccess(DataSnapshot data) {
                // On success, check value of returned data
                if (data.getValue() != null) {
//                    addedUser = data.getChildren().iterator().next().getValue(User.class);
                    String Uid = data.getChildren().iterator().next().child("Uid").getValue(String.class);
                    String email = data.getChildren().iterator().next().child("email").getValue(String.class);
                    String username = data.getChildren().iterator().next().child("username").getValue(String.class);
                    DataSnapshot groups = data.getChildren().iterator().next().child("groupIDs");
                    Iterable<DataSnapshot> it = groups.getChildren();
                    List<String> groupIdList = new ArrayList<String>();

                    for (DataSnapshot child : it) {
                        groupIdList.add(child.getValue(String.class));
                    }

                    addedUser = new User(email,username,Uid,groupIdList);

                    // add user to group object and group to user object
                    group.addUser(addedUser.Uid);
                    addedUser.addGroup(groupID);
                    //update Firebase
                    updateFirebase(group, addedUser);
                    //set user to null again for next addition
                    addedUser = null;
                    // dismiss alert
                    alert.dismiss();
                } else {
                    // otherwise set error message
                    field.setError("Cannot find user");
                }
            }

            @Override
            public void onFailed(DatabaseError databaseError) {
                //DO SOME THING WHEN GET DATA FAILED HERE
            }
        });

    }

    private void updateFirebase(Group group, User user) {

        // add the full group to the "group" section of FireBase
        firebaseDatabase.getReference().child("groups").child(group.getUniqueID()).setValue(group);
        // add the groupID to the "user"
        firebaseDatabase.getReference().child("users").child(user.Uid)
                .child("groupIDs").child(group.getName()).setValue(group.getUniqueID());
    }

    @OnClick(R.id.go_to_cam)
    public void openCamera() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");

            String photoID = UUID.randomUUID().toString();

            // add photo to storage
            StorageReference childReference = storageRef.child(groupID+"/"+photoID);
            Uri uri = getImageUri(photo);
            childReference.putFile(uri);

            // add photoID to DB
            DatabaseReference dbRef = firebaseDatabase.getReference("images").child(groupID).child(uID).push();
            dbRef.setValue(photoID);
        }
    }

    public Uri getImageUri(Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(this.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
