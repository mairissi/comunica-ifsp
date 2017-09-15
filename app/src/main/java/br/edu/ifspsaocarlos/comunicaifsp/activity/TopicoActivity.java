package br.edu.ifspsaocarlos.comunicaifsp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import br.edu.ifspsaocarlos.comunicaifsp.R;
import br.edu.ifspsaocarlos.comunicaifsp.TopicoAdapter;

/**
 * Created by MRissi on 15-Sep-17.
 */

public class TopicoActivity extends AppCompatActivity{
    RecyclerView rV;
    private DrawerLayout container;
    private NavigationView navigationView;

    private FloatingActionButton btnNovoTopico;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topico);

        btnNovoTopico = (FloatingActionButton) findViewById(R.id.btn_callNovoTopico);
        container = (DrawerLayout) findViewById(R.id.topico_container);
        rV = (RecyclerView) findViewById(R.id.recycler_view);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int id = item.getItemId();

                if (id == R.id.action_home) {
                    Intent goToMain = new Intent(TopicoActivity.this, MainActivity.class);
                    startActivity(goToMain);
                }
                else if (id == R.id.action_logout){
                    FirebaseAuth.getInstance().signOut();
                }

                return false;
            }
        });

        ArrayList<String> list = new ArrayList<>();
        list.add("TOPICO 1");
        list.add("TOPICO 2");

        TopicoAdapter adapter = new TopicoAdapter(list);

        rV.setAdapter(adapter);
        rV.setLayoutManager(new LinearLayoutManager(this));

        getSupportActionBar().setTitle("Tópicos");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);

        btnNovoTopico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TopicoActivity.this , CadastroTopicoActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut();
            finish();
        }
        else if (id == android.R.id.home){
            container.openDrawer(GravityCompat.START);
        }

        return super.onOptionsItemSelected(item);
    }


}
