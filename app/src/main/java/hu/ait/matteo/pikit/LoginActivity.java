package hu.ait.matteo.pikit;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

    private int SIGNUPCODE = 100;

    // UI handles
    @BindView(R.id.input_email) EditText emailInput;
    @BindView(R.id.input_password) EditText passwordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Bind butterknife
        ButterKnife.bind(this);
    }

    @OnClick(R.id.link_signup)
    public void goToSignUp() {
        Intent intent = new Intent(this, SignUpActivity.class);

        startActivityForResult(intent, SIGNUPCODE);
    }
}
