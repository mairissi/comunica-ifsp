package br.edu.ifspsaocarlos.comunicaifsp.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;

import br.edu.ifspsaocarlos.comunicaifsp.CommonActivity;
import br.edu.ifspsaocarlos.comunicaifsp.R;
import br.edu.ifspsaocarlos.comunicaifsp.Topic;

/**
 * Created by MRissi on 15-Sep-17.
 */

public class CadastroTopicoActivity extends CommonActivity
        implements DatabaseReference.CompletionListener, View.OnClickListener{

    private Topic topic;
    private AutoCompleteTextView name;
    private AutoCompleteTextView description;
    private AutoCompleteTextView course;

    private Button btnCadastrarTopico;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topico_cadastro);
        btnCadastrarTopico = (Button) findViewById(R.id.btn_CadastrarTopico );

        initViews();

        createProgressDialog();

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
        topic.setPassword(password.getText().toString());
        topic.setCreatorId(FirebaseAuth.getInstance().getCurrentUser().getUid().toString());
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
            password.setError("Senha não informada!");
            password.requestFocus();
            noError = false;
        }

        if (noError) {
            btnCadastrarTopico.setEnabled(false);
            topic.setIdTopic(FirebaseAuth.getInstance().getCurrentUser().getUid());
            showProgressDialog("Criando");
            topic.saveDB(CadastroTopicoActivity.this);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        dismissProgressDialog();
    }

    private void createProgressDialog(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
    }

    private void showProgressDialog (String text){
        progressDialog.setMessage(text);
        progressDialog.show();
    }

    private void dismissProgressDialog (){
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
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
