package hu.ait.matteo.pikit;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by matteosantamaria on 5/13/17.
 */

public class SignUpActivity extends AppCompatActivity {

    @BindView(R.id.input_name) EditText nameInput;
    @BindView(R.id.input_email) EditText emailInput;
    @BindView(R.id.input_password) EditText passwordInput;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.activity_signup);

        // Bind butterknife
        ButterKnife.bind(this);
    }

    @OnClick(R.id.link_signup)
    public void creatAccount(String username, String password) {
        // TODO
        // create account with firebase
        // and if success, go back to login screen
    }
}
