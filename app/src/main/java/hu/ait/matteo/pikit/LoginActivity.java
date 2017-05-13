package hu.ait.matteo.pikit;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hu.ait.matteo.pikit.data.Group;
import hu.ait.matteo.pikit.data.User;

public class LoginActivity extends BaseActivity {

    // UI handles
    @BindView(R.id.input_email)
    EditText emailInput;

    @BindView(R.id.input_password)
    EditText passwordInput;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference firebaseDatabase;
    private GroupRecyclerAdapter groupRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Bind butterknife
        ButterKnife.bind(this);

        // Connect to FireBase
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance().getReference();

        // Instantiate Recycler
        groupRecyclerAdapter = new GroupRecyclerAdapter(getApplicationContext(), FirebaseAuth.getInstance().getCurrentUser().getUid());
        RecyclerView recyclerViewGroups = (RecyclerView) findViewById(
                R.id.recyclerViewGroups);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewGroups.setLayoutManager(layoutManager);
        recyclerViewGroups.setAdapter(groupRecyclerAdapter);

        initFireBaseListener();
    }

    public void initFireBaseListener() {
        DatabaseReference groupsRef = FirebaseDatabase.getInstance().getReference("");
        postsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Post newPost = dataSnapshot.getValue(Post.class);
                postsAdapter.addPost(newPost, dataSnapshot.getKey());
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

    @OnClick(R.id.btn_login)
    public void login() {
        if (!isFormValid()) {
            return;
        }

        showLoginDialog();

        firebaseAuth.signInWithEmailAndPassword(
                emailInput.getText().toString(),
                passwordInput.getText().toString()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                hideProgressDialog();

                if (task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "Login ok", Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(LoginActivity.this, GroupsActivity.class));
                } else {
                    Toast.makeText(LoginActivity.this, "Failed: "+task.getException().getLocalizedMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @OnClick(R.id.btn_register)
    public void register() {
        if (!isFormValid()) {
            return;
        }

        showRegisterDialog();

        final String emailString = emailInput.getText().toString();
        final String usernameString = userNameFromEmail(emailString);

        firebaseAuth.createUserWithEmailAndPassword(
                emailString, passwordInput.getText().toString()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                hideProgressDialog();

                if (task.isSuccessful()) {
                    FirebaseUser firebaseUser = task.getResult().getUser();
                    firebaseUser.updateProfile(
                            new UserProfileChangeRequest.Builder().
                                    setDisplayName(usernameString).build()
                    );

                    // add current user to FireBase DB
                    addUserToFireBaseDB(emailString, usernameString, firebaseAuth.getCurrentUser().getUid());

                    Toast.makeText(LoginActivity.this, "REG OK",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LoginActivity.this, "Failed: "+
                                    task.getException().getLocalizedMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                hideProgressDialog();
                Toast.makeText(LoginActivity.this,
                        "error: "+e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addUserToFireBaseDB(String email, String username, String userId) {
        List<String> list = new ArrayList<>();
        User user = new User(email,username,userId,list);
        firebaseDatabase.child("users").child(userId).setValue(user);
    }

    private boolean isFormValid() {
        if (TextUtils.isEmpty(emailInput.getText().toString())) {
            emailInput.setError("Cannot be empty");
            return false;
        }

        if (TextUtils.isEmpty(passwordInput.getText().toString())) {
            passwordInput.setError("Cannot be empty");
            return false;
        }

        return true;
    }

    private String userNameFromEmail(String email) {
        if (email.contains("@")) {
            return email.split("@")[0];
        } else {
            return email;
        }
    }
}
