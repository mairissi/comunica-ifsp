package br.edu.ifspsaocarlos.comunicaifsp.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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

import br.edu.ifspsaocarlos.comunicaifsp.CommonActivity;
import br.edu.ifspsaocarlos.comunicaifsp.R;
import br.edu.ifspsaocarlos.comunicaifsp.User;

public class LoginActivity extends CommonActivity implements View.OnClickListener {

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private User user;

    private TextView register;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = getFirebaseAuthResultHandler();

        initViews();

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Login");

        btnLogin = (Button) findViewById(R.id.btn_Login);
        btnLogin.setOnClickListener(this);
    }

    protected void initViews() {
        email = (EditText) findViewById(R.id.edt_Email_Login);
        password = (EditText) findViewById(R.id.edt_Senha_Login);
        progressBar = (ProgressBar) findViewById(R.id.login_progress);
        register = (TextView) findViewById(R.id.txt_Register);
    }

    protected void initUser() {
        user = new User();
        user.setEmail(email.getText().toString());
        user.setPassword(password.getText().toString());
    }

    @Override
    public void onClick(View v) {

        initUser();

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
                progressBar.setFocusable(true);

                openProgressBar();
                verifyLogin();
            }
            else {
                closeProgressBar();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        verifyLogged();
    }

    private void verifyLogged() {

        if (firebaseAuth.getCurrentUser() != null) {
            callMainActivity();
        }
        else {
            firebaseAuth.addAuthStateListener(authStateListener);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (authStateListener != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }

    private FirebaseAuth.AuthStateListener getFirebaseAuthResultHandler() {
        FirebaseAuth.AuthStateListener callback = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser userFirebase = firebaseAuth.getCurrentUser();

                if (userFirebase == null) {
                    return;
                }

                if (user.getIdUser() == null && isNameOk(user, userFirebase)) {

                    user.setIdUser(userFirebase.getUid());
                    user.setNameIfNull(userFirebase.getDisplayName());
                    user.setEmailIfNull(userFirebase.getEmail());
                    user.saveDB();
                }

                callMainActivity();
            }
        };
        return (callback);
    }

    private void verifyLogin() {
        firebaseAuth.signInWithEmailAndPassword(
                user.getEmail(),
                user.getPassword())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (!task.isSuccessful()) {
                            closeProgressBar();

                            btnLogin.setEnabled(true);
                            register.setEnabled(true);
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
                    }
                });
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        showToast(connectionResult.getErrorMessage());
    }

    private boolean isNameOk(User user, FirebaseUser firebaseUser) {
        return (user.getName() != null || firebaseUser.getDisplayName() != null);
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
}
