package br.edu.ifspsaocarlos.comunicaifsp.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import br.edu.ifspsaocarlos.comunicaifsp.CommonActivity;
import br.edu.ifspsaocarlos.comunicaifsp.R;
import br.edu.ifspsaocarlos.comunicaifsp.Topic;

/**
 * Created by MRissi on 06-Oct-17.
 */

public class SignInTopicActivity extends CommonActivity
        implements DatabaseReference.CompletionListener, View.OnClickListener{

    private TextView name;
    private TextView course;
    private TextView description;
    Topic topic;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin_topic);

        initViews();

        topic = (Topic) getIntent().getSerializableExtra("topic");

        name.setText(topic.getName());
        course.setText(": " + topic.getCourse());
        description.setText(topic.getDescription());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Sign In");

    }
    @Override
    public void onClick(View v) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void initViews() {
        name = (TextView) findViewById(R.id.topic_name);
        course = (TextView) findViewById(R.id.topic_course);
        description = (TextView) findViewById(R.id.topic_description);
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
