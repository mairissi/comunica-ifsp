package br.edu.ifspsaocarlos.comunicaifsp.controller.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

import br.edu.ifspsaocarlos.comunicaifsp.R;
import br.edu.ifspsaocarlos.comunicaifsp.model.entity.User;

public class LoginActivity extends CommonActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private User user;
    private TextView register;
    private TextView reset;
    private Button btnLogin;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Carregando");

        initViews();

        getSupportActionBar().setTitle("Login");

        btnLogin = (Button) findViewById(R.id.btn_Login);
        btnLogin.setOnClickListener(this);
    }

    private void verifyLogged(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null){
            user.reload().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(mAuth.getCurrentUser().isEmailVerified()){
                        callMainActivity();
                    }
                }
            });
        }
    }

    protected void initViews() {
        email = (EditText) findViewById(R.id.edt_Email_Login);
        password = (EditText) findViewById(R.id.edt_Senha_Login);
        register = (TextView) findViewById(R.id.txt_Register);
        reset = (TextView) findViewById(R.id.txt_EsqueceuSenha);

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callResetActivity();
            }
        });
    }

    protected void initObject() {
        user = new User();
        user.setEmail(email.getText().toString());
        user.setPassword(password.getText().toString());
    }

    @Override
    public void onClick(View v) {

        initObject();

        int id = v.getId();
        if (id == R.id.btn_Login) {

            String emailString = email.getText().toString();
            String passwordString = password.getText().toString();

            boolean ok = true;

            if (emailString.isEmpty()) {
                email.setError("E-mail não informado!");
                email.requestFocus();
                ok = false;
            }

            if (passwordString.isEmpty()) {
                password.setError("Por favor digite uma senha!");
                password.requestFocus();
                ok = false;
            }

            if (ok) {
                btnLogin.setEnabled(false);
                register.setEnabled(false);
                reset.setEnabled(false);

                progressDialog.show();

                verifyLogin();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        verifyLogged();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }

    private void verifyLogin() {
        mAuth.signInWithEmailAndPassword(
                user.getEmail(),
                user.getPassword())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (!task.isSuccessful()) {
                            progressDialog.dismiss();

                            btnLogin.setEnabled(true);
                            register.setEnabled(true);
                            reset.setEnabled(true);

                            if (task.getException() instanceof FirebaseAuthInvalidUserException){
                                showToast("Usuário não cadastrado!");
                            }
                            else if (task.getException() instanceof FirebaseNetworkException){
                                showToast("Você precisa estar conectado a internet!");
                            }
                            else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException){
                                if (((FirebaseAuthInvalidCredentialsException) task.getException()).getErrorCode().equals("ERROR_INVALID_EMAIL")){
                                    showToast("O endereço de e-mail está inválido!");
                                }
                                else if (((FirebaseAuthInvalidCredentialsException) task.getException()).getErrorCode().equals("ERROR_WRONG_PASSWORD")){
                                    showToast("A senha está incorreta!");
                                }
                                else showToast("Os dados estão incorretos!");
                            }
                            else showToast("Não foi possível realizar o login!");

                            return;
                        }
                        else {
                            // Conseguiu Logar
                            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                            if (firebaseUser.isEmailVerified()) {
                                // Email confirmado. Permitir Login e cadastrar user no banco
                                progressDialog.dismiss();
                                callMainActivity();
                            }
                            else {
                                showToast("Foi enviado um novo e-mail para confirmar o seu cadastro.");
                                firebaseUser.sendEmailVerification();

                                btnLogin.setEnabled(true);
                                register.setEnabled(true);
                                reset.setEnabled(true);
                                progressDialog.dismiss();
                                return;
                            }
                        }
                    }
                });
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        showToast(connectionResult.getErrorMessage());
    }

    private void callMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void callRegister(View view) {
        Intent intent = new Intent(this, CadastroActivity.class);
        startActivity(intent);
    }

    public void callResetActivity() {
        Intent intent = new Intent(LoginActivity.this, ResetActivity.class);
        startActivity(intent);
        finish();
    }
}
