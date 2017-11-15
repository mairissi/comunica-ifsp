package br.edu.ifspsaocarlos.comunicaifsp.activity;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.os.Bundle;
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
import br.edu.ifspsaocarlos.comunicaifsp.CommonActivity;
import br.edu.ifspsaocarlos.comunicaifsp.Mask;
import br.edu.ifspsaocarlos.comunicaifsp.R;
import br.edu.ifspsaocarlos.comunicaifsp.User;
import br.edu.ifspsaocarlos.comunicaifsp.Validator;

public class CadastroActivity extends CommonActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private User user;
    private AutoCompleteTextView name;
    private AutoCompleteTextView cpf;
    private AutoCompleteTextView ra;
    private Button btnCadastrar;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);
        btnCadastrar = (Button) findViewById(R.id.btn_Cadastrar);

        initViews();

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Carregando");

        //Mask CPF
        cpf.addTextChangedListener(Mask.insert("###.###.###-##", cpf));

        //Mask R.A.
        ra.addTextChangedListener(Mask.insert("######-#", ra));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Cadastro Usuário");

        mAuth = FirebaseAuth.getInstance();

        btnCadastrar.setOnClickListener(this);
    }

    @Override
    protected void initViews() {
        name = (AutoCompleteTextView) findViewById(R.id.edt_Nome_Cadastro);
        cpf = (AutoCompleteTextView) findViewById(R.id.edt_CPF_Cadastro);
        ra = (AutoCompleteTextView) findViewById(R.id.edt_RA_Cadastro);
        email = (AutoCompleteTextView) findViewById(R.id.edt_Email_Cadastro);
        password = (AutoCompleteTextView) findViewById(R.id.edt_Senha_Cadastro);
    }

    @Override
    protected void initObject() {
        user = new User();
        user.setName(name.getText().toString());
        user.setCpf(cpf.getText().toString());
        user.setRa(ra.getText().toString());
        user.setEmail(email.getText().toString());
        user.setPassword(password.getText().toString());
    }

    @Override
    public void onClick(View v) {
        initObject();

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
                noError = false;
            }

            Boolean validateEmail = Validator.validateEmail(email.getText().toString());

            if (!validateEmail) {
                email.setError("Email inválido");
                email.setFocusable(true);
                email.requestFocus();
                noError = false;
            }

            if(noError) {
                btnCadastrar.setEnabled(false);

                //progressDialog.show();

                saveUsuario();
                finish();
            }
        }
    }

    private void saveUsuario() {

        mAuth.createUserWithEmailAndPassword(
                user.getEmail(),
                user.getPassword()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    FirebaseUser firebaseUser = mAuth.getCurrentUser();

                    if (!firebaseUser.isEmailVerified()){
                        firebaseUser.sendEmailVerification();
                        showToast("Por favor, verifique seu e-mail.");

                        //progressDialog.dismiss();
                    }

                    if (user.getIdUser() == null && isNameOk(user, firebaseUser)) {
                        user.setIdUser(firebaseUser.getUid());
                        user.setNameIfNull(firebaseUser.getDisplayName());
                        user.setEmailIfNull(firebaseUser.getEmail());
                        user.setProfessor(false);
                        user.saveDB();
                    }
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
                //progressDialog.dismiss();
            }
        });
    }

    private boolean isNameOk(User user, FirebaseUser firebaseUser) {
        return (user.getName() != null || firebaseUser.getDisplayName() != null);
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
