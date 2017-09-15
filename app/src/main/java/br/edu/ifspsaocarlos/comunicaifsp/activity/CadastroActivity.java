package br.edu.ifspsaocarlos.comunicaifsp.activity;

import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;

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

    private Button btnCadastrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);
        btnCadastrar = (Button) findViewById(R.id.btn_Cadastrar);

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

        btnCadastrar.setOnClickListener(this);
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
            name.requestFocus();
            noError = false;
        }

        if(cpf.getText().toString().isEmpty()){
            cpf.setError("CPF não informado!");
            cpf.requestFocus();
            noError = false;
        }

        if(ra.getText().toString().isEmpty()){
            ra.setError("RA não informado!");
            ra.requestFocus();
            noError = false;
        }

        if(email.getText().toString().isEmpty()){
            email.setError("E-mail não informado!");
            email.requestFocus();
            noError = false;
        }

        if(password.getText().toString().isEmpty()){
            password.setError("Por favor digite uma senha!");
            password.requestFocus();
            noError = false;
        }

        if(noError){
            Boolean validateCPF = Validator.validateCPF(cpf.getText().toString());

            if (!validateCPF) {
                cpf.setError("CPF inválido");
                cpf.setFocusable(true);
                cpf.requestFocus();
                closeProgressBar();
                noError = false;
            }

            Boolean validateEmail = Validator.validateEmail(email.getText().toString());

            if (!validateEmail) {
                email.setError("Email inválido");
                email.setFocusable(true);
                email.requestFocus();
                closeProgressBar();
                noError = false;
            }

            if(noError) {
                btnCadastrar.setEnabled(false);
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
                if (e instanceof FirebaseAuthWeakPasswordException){
                    showToast("A senha deve ter no mínimo 6 caracteres!");
                }
                else if (e instanceof FirebaseAuthUserCollisionException){
                    showToast("Esse endereço de e-mail já foi cadastrado!");
                }
                else if (e instanceof FirebaseNetworkException){
                    showToast("Você precisa estar conectado a internet!");
                }
                else showToast("Não foi possível realizar o cadastro!");
                btnCadastrar.setEnabled(true);
            }
        });
    }

    @Override
    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
        mAuth.signOut();

        showToast("Conta criada com sucesso!");
        closeProgressBar();
        finish();
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
