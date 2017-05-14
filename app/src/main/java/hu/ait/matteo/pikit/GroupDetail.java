package hu.ait.matteo.pikit;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hu.ait.matteo.pikit.data.Group;
import hu.ait.matteo.pikit.data.User;
import hu.ait.matteo.pikit.interfaces.GetDataListener;

public class GroupDetail extends AppCompatActivity {

    @BindView(R.id.addUsersBtn)
    Button addUsersBtn;

    private FirebaseDatabase firebaseDatabase;
    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.8F);
    private String groupID;
    private Group group;
    private Toolbar toolbar;

    private User addedUser = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail);

        // Get groupID from previous activity
        Bundle bundle = getIntent().getExtras();
        groupID = bundle.getString("groupID");

        // Init Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar_top);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Bind ButterKnife
        ButterKnife.bind(this);

        // Connect to Firebase and get local Group instance
        firebaseDatabase = FirebaseDatabase.getInstance();
        groupFromFirebase();

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
                    Log.d("test", data.getValue().toString());
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
        // TODO
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
