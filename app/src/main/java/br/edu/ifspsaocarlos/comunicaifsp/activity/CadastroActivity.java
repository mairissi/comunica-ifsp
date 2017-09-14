package br.edu.ifspsaocarlos.comunicaifsp.activity;

import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.math.BigDecimal;

import br.edu.ifspsaocarlos.comunicaifsp.CommonActivity;
import br.edu.ifspsaocarlos.comunicaifsp.Mask;
import br.edu.ifspsaocarlos.comunicaifsp.R;
import br.edu.ifspsaocarlos.comunicaifsp.User;
import br.edu.ifspsaocarlos.comunicaifsp.Validator;

public class CadastroActivity extends CommonActivity
        implements DatabaseReference.CompletionListener, View.OnClickListener {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private User user;
    private AutoCompleteTextView name;
    private AutoCompleteTextView cpf;
    private AutoCompleteTextView ra;

    private FloatingActionButton FabCadastrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);
        FabCadastrar = (FloatingActionButton) findViewById(R.id.fab_enviarDados_Cadastro);

        initViews();

        //Mask CPF
        cpf.addTextChangedListener(Mask.insert("###.###.###-##", cpf));

        //Mask R.A.
        ra.addTextChangedListener(Mask.insert("######-#", ra));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Cadastro");

        mAuth = FirebaseAuth.getInstance();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                if (firebaseUser == null || user.getIdUser() != null) {
                    return;
                }

                user.setIdUser(firebaseUser.getUid());
                user.saveDB(CadastroActivity.this);
            }
        };

        FabCadastrar.setOnClickListener(this);
    }

    protected void initViews() {
        name = (AutoCompleteTextView) findViewById(R.id.edt_Nome_Cadastro);
        cpf = (AutoCompleteTextView) findViewById(R.id.edt_CPF_Cadastro);
        ra = (AutoCompleteTextView) findViewById(R.id.edt_RA_Cadastro);
        email = (AutoCompleteTextView) findViewById(R.id.edt_Email_Cadastro);
        password = (AutoCompleteTextView) findViewById(R.id.edt_Senha_Cadastro);
        progressBar = (ProgressBar) findViewById(R.id.sign_up_progress);
    }

    protected void initUser() {
        user = new User();
        user.setName(name.getText().toString());
        user.setCpf(cpf.getText().toString());
        user.setRa(ra.getText().toString());
        user.setEmail(email.getText().toString());
        user.setPassword(password.getText().toString());
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthStateListener != null) {
            mAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    @Override
    public void onClick(View v) {
        initUser();

        Boolean noError = true;

        if(name.getText().toString().isEmpty()){
            name.setError("Nome não informado!");
            noError = false;
        }

        if(cpf.getText().toString().isEmpty()){
            cpf.setError("CPF não informado!");
            noError = false;
        }

        if(ra.getText().toString().isEmpty()){
            ra.setError("RA não informado!");
            noError = false;
        }

        if(email.getText().toString().isEmpty()){
            email.setError("E-mail não informado!");
            noError = false;
        }

        if(password.getText().toString().isEmpty()){
            password.setError("Por favor digite uma senha!");
            noError = false;
        }

        if(noError){
            noError = verifyCPFAndEmail();
            if(noError) {
                FabCadastrar.setEnabled(false);
                progressBar.setFocusable(true);

                openProgressBar();
                saveUsuario();
            }
            else {
                closeProgressBar();
            }
        }
        else{
            closeProgressBar();
        }
    }

    private boolean verifyCPFAndEmail (){
        Boolean noError = Validator.validateCPF(cpf.getText().toString());

        if (!noError) {
            closeProgressBar();
            cpf.setError("CPF inválido");
        }

        noError = Validator.validateEmail(email.getText().toString());

        if (!noError) {
            closeProgressBar();
            email.setError("Email inválido");
        }

        return noError;
    }

    private void saveUsuario() {

        mAuth.createUserWithEmailAndPassword(
                user.getEmail(),
                user.getPassword()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (!task.isSuccessful()) {
                    closeProgressBar();
                }
            }
        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showSnackbar(e.getMessage());
                FabCadastrar.setEnabled(true);
            }
        });
    }

    @Override
    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
        mAuth.signOut();

        showSnackbar("Conta criada com sucesso!");
        closeProgressBar();
        finish();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        showSnackbar(connectionResult.getErrorMessage());
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
