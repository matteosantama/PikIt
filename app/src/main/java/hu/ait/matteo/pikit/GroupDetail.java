package hu.ait.matteo.pikit;

import android.content.DialogInterface;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hu.ait.matteo.pikit.data.Group;
import hu.ait.matteo.pikit.data.User;

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

        queryRef.addValueEventListener(new ValueEventListener() {
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
                // Attempt to find user in DB
                userFromDB(emailString);
                // if not null, add to group on firebase
//                Log.d("TEST", addedUser.email);
                if (addedUser != null) {
                    addedUser = null;
                    alert.dismiss();
                } else {
                    inputField.setError("Cannot find user");
                }
            }
        });

    }

    private void userFromDB(String emailString) {

        Query userQuery = firebaseDatabase.getReference("users").orderByChild("email").equalTo(emailString);

        userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("TEST", dataSnapshot.toString());
                if (dataSnapshot.getValue() != null) {
                    addedUser = dataSnapshot.getChildren().iterator().next().getValue(User.class);
                    Log.d("TEST", addedUser.email);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
