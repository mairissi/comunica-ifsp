package br.edu.ifspsaocarlos.comunicaifsp.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;

import br.edu.ifspsaocarlos.comunicaifsp.LibraryClass;
import br.edu.ifspsaocarlos.comunicaifsp.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private DrawerLayout container;
    private NavigationView navigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        container = (DrawerLayout) findViewById(R.id.main_container);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        configNavigationView();
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int id = item.getItemId();

                if (id == R.id.action_meusTopicos) {
                    Intent goToTopico = new Intent(MainActivity.this, TopicoActivity.class);
                    startActivity(goToTopico);
                }
                else if (id == R.id.action_logout){
                    firebaseAuth.signOut();
                } else if (id == R.id.action_meuPerfil){
                    Intent goToProfile = new Intent(MainActivity.this, PerfilActivity.class);
                    startActivity(goToProfile);
                }

                return false;
            }
        });

        getSupportActionBar().setTitle("Comunica IFSP");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if (firebaseAuth.getCurrentUser() == null) {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.addAuthStateListener(authStateListener);
        databaseReference = LibraryClass.getFirebase();
        databaseReference.getRef();
    }


    public void configNavigationView(){
       FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
       View header =  navigationView.getHeaderView(0);
        CircleImageView profileImage = (CircleImageView) header.findViewById(R.id.nav_image_profile);
        TextView name = (TextView) header.findViewById(R.id.nav_name_label);
        TextView email = (TextView) header.findViewById(R.id.nav_email_label);

        if (user != null){
            Picasso.with(this).load(user.getPhotoUrl()).placeholder(getResources().getDrawable(R.mipmap.ic_launcher_round)).into(profileImage);
            name.setText(user.getDisplayName());
            email.setText(user.getEmail());
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (authStateListener != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut();
            finish();
        }
        else if (id == android.R.id.home){
            if(container.isDrawerOpen(GravityCompat.START)){
                container.closeDrawer(GravityCompat.START);
            }else{
                container.openDrawer(GravityCompat.START);
            }
        }

        return super.onOptionsItemSelected(item);
    }
}
