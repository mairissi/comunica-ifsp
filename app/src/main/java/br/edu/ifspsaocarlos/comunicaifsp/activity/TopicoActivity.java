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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;

import br.edu.ifspsaocarlos.comunicaifsp.CommonActivity;
import br.edu.ifspsaocarlos.comunicaifsp.R;
import br.edu.ifspsaocarlos.comunicaifsp.Topic;
import br.edu.ifspsaocarlos.comunicaifsp.TopicoAdapter;
import br.edu.ifspsaocarlos.comunicaifsp.view.TopicPresenter;

/**
 * Created by MRissi on 15-Sep-17.
 */

public class TopicoActivity extends CommonActivity implements TopicPresenter {
    RecyclerView rV;
    private DrawerLayout container;
    private NavigationView navigationView;
    private EditText mSearch;
    private String meuTextoQueEuProcuro = "";
    private FloatingActionButton mSearchBtn;
    private boolean flagMigue = false;
    FirebaseRecyclerAdapter<Topic, MyViewHolder> firebaseRecyclerAdapter;
    FirebaseRecyclerAdapter<Topic, MyViewHolder> adapterMigue;

    private FloatingActionButton btnNovoTopico;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topico);

        btnNovoTopico = (FloatingActionButton) findViewById(R.id.btn_callNovoTopico);
        mSearchBtn = (FloatingActionButton) findViewById(R.id.fab_buscarTopicos);
        mSearch = (EditText) findViewById(R.id.search_topicos);
        container = (DrawerLayout) findViewById(R.id.topico_container);
        rV = (RecyclerView) findViewById(R.id.recycler_view);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);

        if(!isUserProfessor()){
            btnNovoTopico.setVisibility(View.GONE);
        }else{
            btnNovoTopico.setVisibility(View.VISIBLE);
        }

        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!flagMigue){
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                    adapterMigue = new FirebaseRecyclerAdapter<Topic, MyViewHolder>(Topic.class, R.layout.cell_topico,
                            MyViewHolder.class, databaseReference.child("generalTopic").orderByChild("name").equalTo(mSearch.getText().toString())) {
                        @Override
                        protected void populateViewHolder(MyViewHolder viewHolder, Topic model, int position) {
                            final Topic modelFinal = model;
                            viewHolder.txt_name.setText("[" + model.getCourse().toUpperCase()+ "] " + model.getName());
                            viewHolder.txt_msg.setText(model.getDescription());
                            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //Pode usar o modelFinal aqui
                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usuario_topico_id");
                                    ref.orderByChild("id").equalTo(modelFinal.getIdTopic()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if(dataSnapshot.exists()){
                                                Intent intent = new Intent(TopicoActivity.this, TopicMessageActivity.class);
                                                intent.putExtra("topic", modelFinal);
                                                startActivity(intent);
                                            }else{
                                                Intent intent = new Intent(TopicoActivity.this, SignInTopicActivity.class);
                                                intent.putExtra("topic", modelFinal);
                                                startActivity(intent);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                }
                            });
                        }
                    };
                    //TODO MUDA o ICONE DO FAB PRA x PRA FICAR BACANA
                    rV.setAdapter(adapterMigue);
                    flagMigue = true;
                }else{
                    //TODO POE O ICONE DA SETA DE VOLTA AQUI
                    mSearch.setText("");
                    flagMigue = false;
                    rV.setAdapter(firebaseRecyclerAdapter);
                }



            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int id = item.getItemId();

                if (id == R.id.action_home) {
                    Intent goToMain = new Intent(TopicoActivity.this, MainActivity.class);
                    startActivity(goToMain);
                }
                else if (id == R.id.action_meuPerfil) {
                    Intent goToTopico = new Intent(TopicoActivity.this, PerfilActivity.class);
                    startActivity(goToTopico);
                }
                else if (id == R.id.action_logout){
                    FirebaseAuth.getInstance().signOut();
                }
                else if (id == R.id.action_meusTopicos){
                    container.closeDrawer(GravityCompat.START);
                }

                return false;
            }
        });

//        ArrayList<Topic> list = new ArrayList<>();
//        Topic celula = new Topic(this);
//        celula.setName("2017-2-PRJT6-648");
//        celula.setDescription("ADS - Disciplina de PRJ");
//        list.add(celula);
//
//        TopicoAdapter adapter = new TopicoAdapter(list);

         DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Topic, MyViewHolder>(Topic.class, R.layout.cell_topico,
                MyViewHolder.class, databaseReference.child("generalTopic")) {
            @Override
            protected void populateViewHolder(MyViewHolder viewHolder, Topic model, int position) {
                final Topic modelFinal = model;
                viewHolder.txt_name.setText("[" + model.getCourse().toUpperCase()+ "] " + model.getName());
                viewHolder.txt_msg.setText(model.getDescription());
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Pode usar o modelFinal aqui
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usuario_topico_id");
                        ref.orderByChild("id").equalTo(modelFinal.getIdTopic()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){
                                    Intent intent = new Intent(TopicoActivity.this, TopicMessageActivity.class);
                                    intent.putExtra("topic", modelFinal);
                                    startActivity(intent);
                                }else{
                                    Intent intent = new Intent(TopicoActivity.this, SignInTopicActivity.class);
                                    intent.putExtra("topic", modelFinal);
                                    startActivity(intent);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                });
            }
        };

        rV.setAdapter(firebaseRecyclerAdapter);
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
            if(container.isDrawerOpen(GravityCompat.START)){
                container.closeDrawer(GravityCompat.START);
            }else{
                container.openDrawer(GravityCompat.START);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showMessageToast(String message) {
        Toast.makeText(this,
                message,
                Toast.LENGTH_LONG)
                .show();
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

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView txt_name;
        TextView txt_msg;

        public MyViewHolder(View itemView) {
            super(itemView);
            txt_name = (TextView) itemView.findViewById(R.id.client_name);
            txt_msg = (TextView) itemView.findViewById(R.id.client_msg);
        }
    }
}
