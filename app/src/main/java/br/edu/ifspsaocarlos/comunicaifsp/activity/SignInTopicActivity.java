package br.edu.ifspsaocarlos.comunicaifsp.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import br.edu.ifspsaocarlos.comunicaifsp.CommonActivity;
import br.edu.ifspsaocarlos.comunicaifsp.R;
import br.edu.ifspsaocarlos.comunicaifsp.Topic;

/**
 * Created by MRissi on 06-Oct-17.
 */

public class SignInTopicActivity extends CommonActivity
        implements DatabaseReference.CompletionListener, View.OnClickListener {

    private TextView name;
    private TextView course;
    private TextView description;
    private EditText topicPassword;
    private Button signUserInTopicBtn;
    private Topic topic;
    private FirebaseAuth mAuth;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin_topic);

        initViews();
        signUserInTopicBtn.setOnClickListener(this);

        topic = (Topic) getIntent().getSerializableExtra("topic");

        name.setText(topic.getName());
        course.setText(": " + topic.getCourse());
        description.setText(topic.getDescription());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Sign In");

    }

    @Override
    public void onClick(View v) {

        if (topicPassword.toString().isEmpty()) {
            topicPassword.setError("Por favor digite uma senha!");
            topicPassword.requestFocus();
        } else {
            if (topicPassword.getText().toString().equals(topic.getPassword())) {
                //TODO get o current user do app, salva o current topic nele (na lista), binda a passagem pra proxima tela (topic messages) -  DONE;
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                reference.child("usuario_topico").child(userId).child(topic.getIdTopic()).setValue(topic);
                reference.child("topico_e_usuario").child(topic.getIdTopic()).child(userId).setValue(topic);
                //TODO chamar a intent para a tela de topico

            } else {
                topicPassword.setError("Senha Incorreta!");
                topicPassword.requestFocus();
            }
        }
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
        topicPassword = (EditText) findViewById(R.id.edt_Senha_Signin_Topico);
        signUserInTopicBtn = (Button) findViewById(R.id.btn_Cadastrar_Signin_Topico);
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
