package br.edu.ifspsaocarlos.comunicaifsp;

import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by MRissi on 13-Sep-17.
 */

abstract public class CommonActivity extends AppCompatActivity {

    protected EditText email;
    protected EditText password;
    protected ProgressBar progressBar;
    private static String MY_FLAG_REF = "flag_is_professor";

    //Shared Preference

    protected  void getUserFlag(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
        Query query = ref.orderByChild("idUser").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                        User user =  childSnapshot.getValue(User.class);
                        SharedPreferences.Editor editor = getSharedPreferences(MY_FLAG_REF, MODE_PRIVATE).edit();
                        editor.putBoolean("flag", user.getIsProfessor());
                        editor.apply();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    protected boolean isUserProfessor(){
        SharedPreferences prefs = getSharedPreferences(MY_FLAG_REF, MODE_PRIVATE);
        return prefs.getBoolean("flag", false);
    }

    protected void showSnackbar( String message ){
        Snackbar.make(progressBar,
                message,
                Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    protected void showToast( String message ){
        Toast.makeText(this,
                message,
                Toast.LENGTH_LONG)
                .show();
    }

    protected void openProgressBar(){
        progressBar.setVisibility( View.VISIBLE );
    }

    protected void closeProgressBar(){
        progressBar.setVisibility( View.GONE );
    }

    abstract protected void initViews();

    abstract protected void initObject();

    public abstract void onConnectionFailed(ConnectionResult connectionResult);
}
