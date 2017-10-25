package br.edu.ifspsaocarlos.comunicaifsp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import br.edu.ifspsaocarlos.comunicaifsp.CommonActivity;
import br.edu.ifspsaocarlos.comunicaifsp.R;

/**
 * Created by MRissi on 25-Oct-17.
 */

public class TopicMessageActivity extends CommonActivity
        implements DatabaseReference.CompletionListener, View.OnClickListener {


    private NavigationView navigationView;
    private DrawerLayout container;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);

        container = (DrawerLayout) findViewById(R.id.message_container);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int id = item.getItemId();

                if (id == R.id.action_home) {
                    Intent goToMain = new Intent(TopicMessageActivity.this, MainActivity.class);
                    startActivity(goToMain);
                }
                else if (id == R.id.action_logout){
                    FirebaseAuth.getInstance().signOut();
                }
                else if (id == R.id.action_meuPerfil){
                    Intent goToProfile = new Intent(TopicMessageActivity.this, PerfilActivity.class);
                    startActivity(goToProfile);
                }
                else if (id == R.id.action_meusTopicos) {
                    Intent goToTopico = new Intent(TopicMessageActivity.this, TopicoActivity.class);
                    startActivity(goToTopico);
                }

                return false;
            }
        });

        getSupportActionBar().setTitle("Mensagens");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
    }


    @Override
    public void onClick(View v) {

    }

    @Override
    protected void initViews() {

    }

    @Override
    protected void initObject() {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

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
