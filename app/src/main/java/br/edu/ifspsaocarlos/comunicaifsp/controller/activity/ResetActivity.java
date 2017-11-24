package br.edu.ifspsaocarlos.comunicaifsp.controller.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

import br.edu.ifspsaocarlos.comunicaifsp.R;
import br.edu.ifspsaocarlos.comunicaifsp.util.Validator;

/**
 * Created by MRissi on 08-Nov-17.
 */

public class ResetActivity extends CommonActivity implements View.OnClickListener{

    private AutoCompleteTextView email;
    private Button btnReset;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        initViews();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Resetar Senha");

        btnReset.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initViews();
    }

    protected void initViews() {
        email = (AutoCompleteTextView) findViewById(R.id.edt_Email_Reset);
        btnReset = (Button) findViewById(R.id.btn_Resetar_Email);
    }

    @Override
    public void onClick(View v) {
        final FirebaseAuth auth = FirebaseAuth.getInstance();
        Boolean noError = true;

        progressDialog.setMessage("Enviando");

        Boolean validateEmail = Validator.validateEmail(email.getText().toString());

        if (!validateEmail) {
            email.setError("Email inválido");
            email.setFocusable(true);
            email.requestFocus();
            noError = false;
        }

        if(noError) {
            progressDialog.show();

            auth.sendPasswordResetEmail(email.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        progressDialog.dismiss();
                        showToast("Password resetado com sucesso!");
                        Intent intent = new Intent(ResetActivity.this, MainActivity.class);
                        startActivity(intent);
                    } else{
                        progressDialog.dismiss();
                        if (task.getException() instanceof FirebaseAuthInvalidUserException){
                            showToast("Endereço de e-mail não cadastrado!");
                        }
                        else if (task.getException() instanceof FirebaseNetworkException){
                            showToast("Você precisa estar conectado a internet!");
                        }
                        else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            if (((FirebaseAuthInvalidCredentialsException) task.getException()).getErrorCode().equals("ERROR_INVALID_EMAIL")) {
                                showToast("O endereço de e-mail está inválido!");
                            }
                            else{
                                showToast("Não foi possível resetar a senha.");
                            }
                        }
                        else {
                            showToast("Não foi possível resetar a senha.");
                        }
                        return;
                    }
                }
            });
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }

    @Override
    protected void initObject() {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        showToast(connectionResult.getErrorMessage());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent goToLogin = new Intent(ResetActivity.this, LoginActivity.class);
                startActivity(goToLogin);
                finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
