package hu.ait.matteo.pikit;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.Date;

import butterknife.ButterKnife;
import butterknife.OnClick;
import hu.ait.matteo.pikit.adapters.GroupRecyclerAdapter;
import hu.ait.matteo.pikit.data.Group;

public class GroupsActivity extends AppCompatActivity {

    // permission constants
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private FirebaseAuth firebaseAuth;
    private DatabaseReference firebaseDatabase;
    private GroupRecyclerAdapter groupRecyclerAdapter;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);

        // set up toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // give app read/write storage permissions
        verifyStoragePermissions(this);

        // bind ButterKnife
        ButterKnife.bind(this);

        // get Firebase references
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance().getReference();

        // get current userID
        userID = firebaseAuth.getCurrentUser().getUid();

        // instantiate recycler
        groupRecyclerAdapter = new GroupRecyclerAdapter(getApplicationContext(), userID);
        RecyclerView recyclerViewGroups = (RecyclerView) findViewById(R.id.recyclerViewGroups);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewGroups.setLayoutManager(layoutManager);
        recyclerViewGroups.setAdapter(groupRecyclerAdapter);

        // attach listeners to Firebase DB events
        connectFirebaseListeners();
    }

    public void connectFirebaseListeners() {
        Query groupsRef = FirebaseDatabase.getInstance().getReference("groups");
        groupsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                for (DataSnapshot data: dataSnapshot.child("members").getChildren()) {
                    if (data.getValue().toString().equals(userID)) {
                        Group group = dataSnapshot.getValue(Group.class);
                        groupRecyclerAdapter.addGroup(group);
                    }
                }
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

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("TEST", "on resume");
        groupRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        firebaseAuth.signOut();
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    // Called when "logout" button is clicked
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.logout_icon) {
            firebaseAuth.signOut();
            finish();
        }

        return true;
    }

    // open create group dialog
    @OnClick(R.id.fab)
    public void openCreateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.add_dialog, null);

        builder.setTitle(R.string.create_group);
        builder.setMessage(R.string.group_instr);
        builder.setView(dialogView);

        final EditText groupNameET = (EditText) dialogView.findViewById(R.id.group_name);

        builder.setPositiveButton(R.string.create, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                addGroupToFireBase(groupNameET.getText().toString());
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void addGroupToFireBase(String groupName) {
        Group newGroup = new Group(groupName, userID);
        newGroup.setDate(new Date());

        // add the full group to the "group" section of FireBase
        firebaseDatabase.child("groups").child(newGroup.getUniqueID()).setValue(newGroup);
        // add the groupID to the "user"
        firebaseDatabase.child("users").child(userID).child("groupIDs").child(newGroup.getName())
                .setValue(newGroup.getUniqueID());
    }

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

}
