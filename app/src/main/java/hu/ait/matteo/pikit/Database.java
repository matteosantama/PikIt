package hu.ait.matteo.pikit;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import hu.ait.matteo.pikit.interfaces.GetDataListener;

/**
 * Created by matteosantamaria on 5/14/17.
 */

public class Database {

    public void readDataOnce(String emailString, final GetDataListener listener) {
        listener.onStart();
        FirebaseDatabase.getInstance().getReference("users").orderByChild("email").equalTo(emailString).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listener.onSuccess(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onFailed(databaseError);
            }
        });
    }
}
