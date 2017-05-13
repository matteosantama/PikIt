package hu.ait.matteo.pikit;

import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hu.ait.matteo.pikit.data.Group;
import hu.ait.matteo.pikit.data.User;

public class GroupsActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);

        // Set up Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Bind ButterKnife
        ButterKnife.bind(this);

        // Connect to FireBase
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance().getReference();
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
            FirebaseAuth.getInstance().signOut();
            finish();
        }

        return true;
    }

    @OnClick(R.id.fab)
    public void openCreateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.add_dialog, null);

        builder.setTitle("Create a Group");
        builder.setMessage("Enter the name of a group you would like to create");
        builder.setView(dialogView);

        final EditText groupNameET = (EditText) dialogView.findViewById(R.id.group_name);

        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // TODO go to group view
                addGroupToFireBase(groupNameET.getText().toString());
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public void addGroupToFireBase(String groupName) {
        String creatorID = firebaseAuth.getCurrentUser().getUid();

        Group newGroup = new Group(groupName, creatorID);

        // add the full group to the "group" section of FireBase
        firebaseDatabase.child("groups").child(newGroup.getUniqueID()).setValue(newGroup);
        // add the groupID to the "user"
        firebaseDatabase.child("users").child(creatorID).child("groups").child(newGroup.getName()).setValue(newGroup.getUniqueID());

    }

}
