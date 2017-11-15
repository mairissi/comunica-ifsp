package br.edu.ifspsaocarlos.comunicaifsp.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import br.edu.ifspsaocarlos.comunicaifsp.CommonActivity;
import br.edu.ifspsaocarlos.comunicaifsp.LibraryClass;
import br.edu.ifspsaocarlos.comunicaifsp.R;
import br.edu.ifspsaocarlos.comunicaifsp.Topic;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends CommonActivity {

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private DrawerLayout container;
    private NavigationView navigationView;
    private RecyclerView mRecycler;
    private TextView mDefaultMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();

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
                }
                else if (id == R.id.action_meuPerfil) {
                    Intent goToProfile = new Intent(MainActivity.this, PerfilActivity.class);
                    startActivity(goToProfile);
                }
                else if (id == R.id.action_home) {
                    container.closeDrawer(GravityCompat.START);
                }

                return false;
            }
        });

        getSupportActionBar().setTitle("Meus Tópicos");
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
                else {
                    if (!firebaseAuth.getCurrentUser().isEmailVerified()) {
                        firebaseAuth.signOut();
                    }
                }
            }
        };

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.addAuthStateListener(authStateListener);
        databaseReference = LibraryClass.getFirebase();
        databaseReference.getRef();


        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            getUserFlag();
            mRecycler.setAdapter(new FirebaseRecyclerAdapter<Topic, TopicoActivity.MyViewHolder>(Topic.class, R.layout.cell_topico,
                    TopicoActivity.MyViewHolder.class, databaseReference.child("usuario_topico").child(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                @Override
                protected void populateViewHolder(TopicoActivity.MyViewHolder viewHolder, Topic model, int position) {
                    mDefaultMsg.setVisibility(View.GONE);
                    final Topic modelFinal = model;
                    viewHolder.txt_name.setText("[" + model.getCourse().toUpperCase() + "] " + model.getName());
                    viewHolder.txt_msg.setText(model.getDescription());
                    viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //Pode usar o modelFinal aqui
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usuario_topico_id");
                            ref.orderByChild("id").equalTo(modelFinal.getIdTopic()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    boolean flag = false;
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                                        HashMap<String, String> map = (HashMap<String, String>) snapshot.getValue();
                                        flag =  map.containsValue(modelFinal.getIdTopic());
                                    }

                                    if(flag && ((HashMap<String, Object>) dataSnapshot.getValue()).containsKey(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                        Intent intent = new Intent(MainActivity.this, TopicMessageActivity.class);
                                        intent.putExtra("topic", modelFinal);
                                        startActivity(intent);
                                    } else {
                                        Intent intent = new Intent(MainActivity.this, SignInTopicActivity.class);
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
            });

            mRecycler.setLayoutManager(new LinearLayoutManager(this));
        }
    }

    public void configNavigationView(){
       FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
       View header =  navigationView.getHeaderView(0);
        CircleImageView profileImage = (CircleImageView) header.findViewById(R.id.nav_image_profile);TextView name = (TextView) header.findViewById(R.id.nav_name_label);
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

    @Override
    protected void initViews() {
        container = (DrawerLayout) findViewById(R.id.main_container);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        mRecycler = (RecyclerView) findViewById(R.id.meus_topicos);
		mDefaultMsg = (TextView) findViewById(R.id.default_msg);
    }

    @Override
    protected void initObject() {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
