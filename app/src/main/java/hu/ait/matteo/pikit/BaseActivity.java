package hu.ait.matteo.pikit;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class BaseActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;

    public void showRegisterDialog() {
        if (progressDialog  == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Registering...");
        }

        progressDialog.show();
    }

    public void showLoginDialog() {
        if (progressDialog  == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Logging in...");
        }

        progressDialog.show();
    }

    public void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.hide();
        }
    }
}
