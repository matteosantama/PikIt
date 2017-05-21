package hu.ait.matteo.pikit;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hu.ait.matteo.pikit.data.User;
import hu.ait.matteo.pikit.helpers.BaseActivity;

public class LoginActivity extends BaseActivity {

    // UI handles
    @BindView(R.id.input_email)
    EditText emailInput;

    @BindView(R.id.input_password)
    EditText passwordInput;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Bind butterknife
        ButterKnife.bind(this);

        // Connect to FireBase
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance().getReference();

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
                    startActivity(new Intent(LoginActivity.this, GroupsActivity.class));
                } else {
                    Toast.makeText(LoginActivity.this, getString(R.string.failed)+task.getException().getLocalizedMessage(),
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

                } else {
                    final Snackbar snackbar = Snackbar.make(findViewById(R.id.root_view), R.string.login_failed, Snackbar.LENGTH_LONG);
                    snackbar.setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            snackbar.dismiss();
                        }
                    }).setActionTextColor(getResources().getColor(android.R.color.holo_red_light ));
                    snackbar.show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                hideProgressDialog();
                final Snackbar snackbar = Snackbar.make(findViewById(R.id.root_view), R.string.unable_to_complete, Snackbar.LENGTH_LONG);
                snackbar.setAction(R.string.ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        snackbar.dismiss();
                    }
                }).setActionTextColor(getResources().getColor(android.R.color.holo_red_light ));
                snackbar.show();
            }
        });
    }

    private void addUserToFireBaseDB(String email, String username, String userId) {
        User user = new User(email,username,userId);
        firebaseDatabase.child("users").child(userId).setValue(user);
    }

    private boolean isFormValid() {
        if (TextUtils.isEmpty(emailInput.getText().toString())) {
            emailInput.setError(getString(R.string.cannot_be_empty));
            return false;
        }

        if (TextUtils.isEmpty(passwordInput.getText().toString())) {
            passwordInput.setError(getString(R.string.cannot_be_empty));
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
