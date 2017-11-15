package br.edu.ifspsaocarlos.comunicaifsp.activity;

import android.content.Intent;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import br.edu.ifspsaocarlos.comunicaifsp.CommonActivity;
import br.edu.ifspsaocarlos.comunicaifsp.R;
import br.edu.ifspsaocarlos.comunicaifsp.Topic;

/**
 * Created by MRissi on 25-Oct-17.
 */

public class TopicMessageActivity extends CommonActivity
        implements DatabaseReference.CompletionListener, View.OnClickListener {


    private NavigationView navigationView;
    private DrawerLayout container;
    private FloatingActionButton mFab;
    private Topic topic;
    private EditText mMsgBox;
    private RecyclerView mRecyclerView;
    private LinearLayout mMessageContainer;
    private TextView mDefaultMsg;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);


        if (getIntent().getExtras() != null){
            topic = (Topic) getIntent().getExtras().get("topic");
        }

        container = (DrawerLayout) findViewById(R.id.message_container);
        mDefaultMsg = (TextView) findViewById(R.id.default_msg);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        mMsgBox = (EditText) findViewById(R.id.shipper_field);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mFab = (FloatingActionButton) findViewById(R.id.fab_send_message);
        mMessageContainer = (LinearLayout) findViewById(R.id.message_sender_container);

        if(!isUserProfessor()){
            mMessageContainer.setVisibility(View.GONE);
        }else{
            mMessageContainer.setVisibility(View.VISIBLE);
        }

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

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        final FirebaseRecyclerAdapter<String, MyOtherHolder> holder =  new FirebaseRecyclerAdapter<String, MyOtherHolder>(String.class, R.layout.cell_topic_messages,
                MyOtherHolder.class, databaseReference.child("topicos_mensagem").child(topic.getIdTopic())) {

            @Override
            protected void populateViewHolder(MyOtherHolder viewHolder, String model, int position) {
                mDefaultMsg.setVisibility(View.GONE);
                viewHolder.txt_msg.setText(model);

            }
        };
        final LinearLayoutManager manager = new LinearLayoutManager(this);

        holder.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = holder.getItemCount();
                int lastVisiblePosition =
                        manager.findLastCompletelyVisibleItemPosition();
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    manager.scrollToPosition(positionStart);
                }
            }
        });

        mRecyclerView.setAdapter(holder);

        mRecyclerView.setLayoutManager(manager);

        getSupportActionBar().setTitle("Mensagens");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               DatabaseReference ref = FirebaseDatabase.getInstance().getReference("topicos_mensagem");
                String key = ref.push().getKey();
                ref.child(topic.getIdTopic()).child(key).setValue(mMsgBox.getText().toString());
                mMsgBox.setText("");
            }
        });
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


    public static class MyOtherHolder extends RecyclerView.ViewHolder{
        //TextView txt_name;
        TextView txt_msg;

        public MyOtherHolder(View itemView) {
            super(itemView);
            //txt_name = (TextView) itemView.findViewById(R.id.client_name);
            txt_msg = (TextView) itemView.findViewById(R.id.client_msg);
        }
    }
}
