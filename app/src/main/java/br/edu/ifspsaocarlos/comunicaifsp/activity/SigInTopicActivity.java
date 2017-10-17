package br.edu.ifspsaocarlos.comunicaifsp.activity;

import android.os.Bundle;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import br.edu.ifspsaocarlos.comunicaifsp.CommonActivity;
import br.edu.ifspsaocarlos.comunicaifsp.R;

/**
 * Created by MRissi on 06-Oct-17.
 */

public class SigInTopicActivity extends CommonActivity
        implements DatabaseReference.CompletionListener, View.OnClickListener{

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sigin_topic);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Sign In");

    }
    @Override
    public void onClick(View v) {

    }

    @Override
    protected void initViews() {

    }

    @Override
    protected void initObject() {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

    }
}
