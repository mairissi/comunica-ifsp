package br.edu.ifspsaocarlos.comunicaifsp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;

import br.edu.ifspsaocarlos.comunicaifsp.CommonActivity;
import br.edu.ifspsaocarlos.comunicaifsp.R;

/**
 * Created by MRissi on 15-Sep-17.
 */

public class CadastroTopicoActivity extends CommonActivity implements View.OnClickListener{


    private AutoCompleteTextView name;
    private AutoCompleteTextView description;
    private AutoCompleteTextView curso;

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
        curso = (AutoCompleteTextView) findViewById(R.id.edt_Curso);
        password = (AutoCompleteTextView) findViewById(R.id.edt_Senha_Topico);
    }

    @Override
    protected void initUser() {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onClick(View v) {

        Boolean noError = true;

        if(name.getText().toString().isEmpty()){
            name.setError("Nome do tópico não informado!");
            name.requestFocus();
            noError = false;
        }

        if(name.getText().toString().isEmpty()){
            description.setError("Descrição não informada!");
            description.requestFocus();
            noError = false;
        }

        if(curso.getText().toString().isEmpty()){
            curso.setError("Curso não informado!");
            curso.requestFocus();
            noError = false;
        }

        if(password.getText().toString().isEmpty()){
            password.setError("Nome não informado!");
            password.requestFocus();
            noError = false;
        }

        if(noError){
            btnCadastrarTopico.setEnabled(false);
            showToast("Tópico cadastrado com sucesso!");
            saveTopic();
        }

    }

    private void saveTopic() {
        Intent intent = new Intent(this, TopicoActivity.class);
        startActivity(intent);
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
}
