package br.edu.ifspsaocarlos.comunicaifsp.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import br.edu.ifspsaocarlos.comunicaifsp.CommonActivity;
import br.edu.ifspsaocarlos.comunicaifsp.R;
import br.edu.ifspsaocarlos.comunicaifsp.Topic;
import br.edu.ifspsaocarlos.comunicaifsp.User;
import br.edu.ifspsaocarlos.comunicaifsp.view.TopicPresenter;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by MRissi on 15-Sep-17.
 */

public class TopicoActivity extends CommonActivity implements TopicPresenter {
    RecyclerView rV;
    private DrawerLayout container;
    private NavigationView navigationView;
    private TextView mDefaultMsg;
    private EditText mSearch;
    private FloatingActionButton mSearchBtn;
    private boolean flagMigue = false;
    FirebaseRecyclerAdapter<Topic, MyViewHolder> firebaseRecyclerAdapter;
    FirebaseRecyclerAdapter<Topic, MyViewHolder> adapterMigue;
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    private FloatingActionButton btnNovoTopico;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topico);

        initViews();

        pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        editor = pref.edit();

        configNavigationView();

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
                                    ref.orderByChild(modelFinal.getIdTopic()).equalTo(modelFinal.getIdTopic()).addListenerForSingleValueEvent(new ValueEventListener() {
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
                    //Troca a imagem para X após clicar para buscar
                    mSearchBtn.setImageResource(R.drawable.ic_clear);
                    rV.setAdapter(adapterMigue);
                    flagMigue = true;
                }else{
                    //Volta o ícone para a seta para realizar nova busca
                    mSearchBtn.setImageResource(R.drawable.ic_action_go);
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
                    editor.clear();
                    editor.commit();
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
            protected void populateViewHolder(MyViewHolder viewHolder, final Topic model, int position) {
                mDefaultMsg.setVisibility(View.GONE);
                final Topic modelFinal = model;
                viewHolder.txt_name.setText("[" + model.getCourse().toUpperCase()+ "] " + model.getName());
                viewHolder.txt_msg.setText(model.getDescription());
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Pode usar o modelFinal aqui
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usuario_topico_id");
                        ref.orderByChild(modelFinal.getIdTopic()).equalTo(modelFinal.getIdTopic()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                boolean flag = false;
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                                       HashMap<String, String> map = (HashMap<String, String>) snapshot.getValue();
                                       flag =  map.containsValue(modelFinal.getIdTopic());
                                }

                                if(flag && ((HashMap<String, Object>) dataSnapshot.getValue()).containsKey(FirebaseAuth.getInstance().getCurrentUser().getUid())){
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
            editor.clear();
            editor.commit();
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

    public void configNavigationView(){
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        View header =  navigationView.getHeaderView(0);
        final CircleImageView profileImage = (CircleImageView) header.findViewById(R.id.nav_image_profile);
        final TextView name = (TextView) header.findViewById(R.id.nav_name_label);
        TextView email = (TextView) header.findViewById(R.id.nav_email_label);

        if (user != null){
            if (user.getUid() != null) {
                DatabaseReference refUser = FirebaseDatabase.getInstance().getReference("users");
                refUser.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User currentUser = dataSnapshot.getValue(User.class);

                        editor.putString("name", currentUser.getName());
                        editor.commit();

                        name.setText(pref.getString("name", null));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
            email.setText(user.getEmail());

            StorageReference ref = FirebaseStorage.getInstance().getReference();
            ref.child(FirebaseAuth.getInstance().getCurrentUser().getUid()+"/photo1.jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.with(TopicoActivity.this).setLoggingEnabled(true);
                    Picasso.with(TopicoActivity.this).load(uri).placeholder(getResources().getDrawable(R.mipmap.ic_launcher_round)).into(profileImage);
                }
            });
        }

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
        btnNovoTopico = (FloatingActionButton) findViewById(R.id.btn_callNovoTopico);
        container = (DrawerLayout) findViewById(R.id.topico_container);
        rV = (RecyclerView) findViewById(R.id.recycler_view);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        mDefaultMsg = (TextView) findViewById(R.id.default_msg);
		mSearchBtn = (FloatingActionButton) findViewById(R.id.fab_buscarTopicos);
        mSearch = (EditText) findViewById(R.id.search_topicos);
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
