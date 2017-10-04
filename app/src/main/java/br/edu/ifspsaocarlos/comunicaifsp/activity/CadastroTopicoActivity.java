package br.edu.ifspsaocarlos.comunicaifsp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import br.edu.ifspsaocarlos.comunicaifsp.CommonActivity;
import br.edu.ifspsaocarlos.comunicaifsp.R;
import br.edu.ifspsaocarlos.comunicaifsp.Topic;

/**
 * Created by MRissi on 15-Sep-17.
 */

public class CadastroTopicoActivity extends CommonActivity
        implements DatabaseReference.CompletionListener, View.OnClickListener{

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private Topic topic;
    private AutoCompleteTextView name;
    private AutoCompleteTextView description;
    private AutoCompleteTextView course;

    private Button btnCadastrarTopico;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topico_cadastro);
        btnCadastrarTopico = (Button) findViewById(R.id.btn_CadastrarTopico );

        initViews();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Novo Tópico");

        btnCadastrarTopico.setOnClickListener(this);
    }

    @Override
    protected void initViews() {
        name = (AutoCompleteTextView) findViewById(R.id.edt_Nome_Topico);
        description = (AutoCompleteTextView) findViewById(R.id.edt_Descricao_Topico);
        course = (AutoCompleteTextView) findViewById(R.id.edt_Curso);
        password = (AutoCompleteTextView) findViewById(R.id.edt_Senha_Topico);
    }

    @Override
    protected void initObject() {
        topic = new Topic();
        topic.setName(name.getText().toString());
        topic.setDescription(description.getText().toString());
        topic.setCourse(course.getText().toString());
    }

    @Override
    public void onClick(View v) {
        initObject();

        Boolean noError = true;

        if (name.getText().toString().isEmpty()) {
            name.setError("Nome do tópico não informado!");
            name.requestFocus();
            noError = false;
        }

        if (name.getText().toString().isEmpty()) {
            description.setError("Descrição não informada!");
            description.requestFocus();
            noError = false;
        }

        if (course.getText().toString().isEmpty()) {
            course.setError("Curso não informado!");
            course.requestFocus();
            noError = false;
        }

        if (password.getText().toString().isEmpty()) {
            password.setError("Nome não informado!");
            password.requestFocus();
            noError = false;
        }

        if (noError) {
            btnCadastrarTopico.setEnabled(false);
            topic.saveDB(CadastroTopicoActivity.this);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
        showToast("Tópico cadastrado com sucesso!");
        finish();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        showToast(connectionResult.getErrorMessage());
    }
}
