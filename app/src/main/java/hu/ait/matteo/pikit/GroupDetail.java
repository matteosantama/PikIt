package hu.ait.matteo.pikit;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hu.ait.matteo.pikit.adapters.GroupRecyclerAdapter;
import hu.ait.matteo.pikit.adapters.ImageRecyclerAdapter;
import hu.ait.matteo.pikit.data.Group;
import hu.ait.matteo.pikit.data.Photo;
import hu.ait.matteo.pikit.data.User;
import hu.ait.matteo.pikit.helpers.BaseActivity;
import hu.ait.matteo.pikit.helpers.Database;
import hu.ait.matteo.pikit.interfaces.GetDataListener;

public class GroupDetail extends BaseActivity {

    @BindView(R.id.addUsersBtn)
    Button addUsersBtn;

    private FirebaseDatabase firebaseDatabase;
    private StorageReference storageRef;
    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.8F);

    private String groupName;
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

        // get data from previous activity
        Bundle extras = getIntent().getExtras();
        groupID = extras.getString("groupID");
        groupName = extras.getString("groupName");
        uID = extras.getString("userID");

        // init Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar_top);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle(groupName);

        // bind ButterKnife
        ButterKnife.bind(this);

        // connect to Firebase and get set local Group object
        firebaseDatabase = FirebaseDatabase.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();
        instantiateGroup();

        // instantiate Recycler
        imageRecyclerAdapter = new ImageRecyclerAdapter(this, groupID, uID);
        RecyclerView recyclerViewGroups = (RecyclerView) findViewById(
                R.id.imageRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewGroups.setLayoutManager(layoutManager);
        recyclerViewGroups.setAdapter(imageRecyclerAdapter);

        // connect listeners
        connectListeners();
    }

    private void instantiateGroup() {
        Query queryRef = firebaseDatabase.getReference("groups/"+groupID);

        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                group = dataSnapshot.getValue(Group.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // pass
            }
        });

    }

    private void connectListeners() {
        final DatabaseReference photosRef = FirebaseDatabase.getInstance().getReference("groups").child(groupID).child("photos");
        photosRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Photo newPhoto = dataSnapshot.getValue(Photo.class);
                imageRecyclerAdapter.addPhoto(newPhoto);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
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

        builder.setTitle(R.string.email);
        builder.setMessage(R.string.email_instr);
        builder.setView(dialogView);

        builder.setPositiveButton(R.string.add,
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        // Do nothing here
                    }
                });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
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
                    field.setError(getString(R.string.cannot_find_user));
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


    // open camera activity
    @OnClick(R.id.go_to_cam)
    public void openCamera() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }

    // on camera result
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");

            final String photoID = UUID.randomUUID().toString();

            // translate bitmap to byte array
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] arr = baos.toByteArray();

            StorageReference childReference = storageRef.child(groupID+"/"+photoID);

            showUploadingDialog();

            // upload file and set task listener
            final UploadTask uploadTask = childReference.putBytes(arr);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    hideProgressDialog();
                    Toast.makeText(GroupDetail.this, R.string.upload_error, Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    hideProgressDialog();

                    @SuppressWarnings("VisibleForTests")
                    String downloadUrl = taskSnapshot.getDownloadUrl().toString();
                    uploadPhoto(photoID, downloadUrl);
                }
            });
        }
    }

    private void uploadPhoto(String photoID, String url) {
        Photo photoObj = new Photo(photoID,uID,url);
        group.addPhoto(photoObj);

        DatabaseReference dbRef = firebaseDatabase.getReference("groups").child(groupID);
        dbRef.setValue(group);

        imageRecyclerAdapter.addPhoto(photoObj);
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
