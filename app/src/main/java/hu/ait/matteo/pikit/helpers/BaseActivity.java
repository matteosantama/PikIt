package hu.ait.matteo.pikit.helpers;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;

import hu.ait.matteo.pikit.R;

public class BaseActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;

    public void showRegisterDialog() {
        if (progressDialog  == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage(getString(R.string.registering));
        }

        progressDialog.show();
    }

    public void showLoginDialog() {
        if (progressDialog  == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage(getString(R.string.logging_in));
        }

        progressDialog.show();
    }

    public void showUploadingDialog() {
        if (progressDialog  == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage(getString(R.string.uploading));
        }

        progressDialog.show();
    }

    public void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        progressDialog = null;
    }
}
