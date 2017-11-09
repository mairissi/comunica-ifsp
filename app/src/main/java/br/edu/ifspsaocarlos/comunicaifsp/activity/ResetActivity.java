package br.edu.ifspsaocarlos.comunicaifsp.activity;

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
import com.google.firebase.auth.FirebaseAuth;

import br.edu.ifspsaocarlos.comunicaifsp.CommonActivity;
import br.edu.ifspsaocarlos.comunicaifsp.R;

/**
 * Created by MRissi on 08-Nov-17.
 */

public class ResetActivity extends CommonActivity implements View.OnClickListener{

    private AutoCompleteTextView email;
    private FirebaseAuth mAuth;
    private Button btnReset;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Carregando");

        initViews();

        mAuth = FirebaseAuth.getInstance();
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

        progressDialog.show();

        auth.sendPasswordResetEmail(email.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    showToast("Password resetado com sucesso!");
                    Intent intent = new Intent(ResetActivity.this, MainActivity.class);
                    startActivity(intent);
                }
                progressDialog.dismiss();
            }
        });
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
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
