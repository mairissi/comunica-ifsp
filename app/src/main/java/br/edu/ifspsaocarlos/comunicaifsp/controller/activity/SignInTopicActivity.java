package br.edu.ifspsaocarlos.comunicaifsp.controller.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import br.edu.ifspsaocarlos.comunicaifsp.R;
import br.edu.ifspsaocarlos.comunicaifsp.model.entity.Topic;
import br.edu.ifspsaocarlos.comunicaifsp.model.entity.User;

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
    ProgressDialog progressDialog;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin_topic);

        initViews();

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Cadastrando");

        signUserInTopicBtn.setOnClickListener(this);

        topic = (Topic) getIntent().getSerializableExtra("topic");

        name.setText(topic.getName());
        course.setText(": " + topic.getCourse().toUpperCase());
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
            progressDialog.show();
            if (topicPassword.getText().toString().equals(topic.getPassword())) {
                final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                FirebaseDatabase.getInstance().getReference().child("usuario_topico").child(currentUser.getUid()).child(topic.getIdTopic()).setValue(topic);
                FirebaseDatabase.getInstance().getReference().child("users/"+currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User value = dataSnapshot.getValue(User.class);
                        FirebaseDatabase.getInstance().getReference().child("topico_e_usuario").child(topic.getIdTopic()).child(currentUser.getUid()).setValue(value);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                FirebaseDatabase.getInstance().getReference().child("usuario_topico_id").child(currentUser.getUid()).child(topic.getIdTopic()).setValue(topic.getIdTopic());

                FirebaseMessaging.getInstance().subscribeToTopic("/topics/" + topic.getIdTopic());
                progressDialog.dismiss();
                //Redirecionar para a tela de mensagens
                Intent intent = new Intent(SignInTopicActivity.this, TopicMessageActivity.class);
                intent.putExtra("topic", topic);
                startActivity(intent);
            } else {
                progressDialog.dismiss();
                topicPassword.setError("Senha Incorreta!");
                topicPassword.requestFocus();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                Intent goToTopico = new Intent(SignInTopicActivity.this, TopicoActivity.class);
                startActivity(goToTopico);
                finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (progressDialog.isShowing())
            progressDialog.dismiss();
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
