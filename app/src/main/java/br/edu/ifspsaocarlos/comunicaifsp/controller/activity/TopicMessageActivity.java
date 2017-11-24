package br.edu.ifspsaocarlos.comunicaifsp.controller.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import br.edu.ifspsaocarlos.comunicaifsp.model.entity.Message;
import br.edu.ifspsaocarlos.comunicaifsp.R;
import br.edu.ifspsaocarlos.comunicaifsp.model.entity.Topic;

/**
 * Created by MRissi on 25-Oct-17.
 */

public class TopicMessageActivity extends CommonActivity
        implements DatabaseReference.CompletionListener, View.OnClickListener {


    private NavigationView navigationView;
    private DrawerLayout container;
    private FloatingActionButton mFab;
    private Topic topic;
    private Message message;
    private EditText mMsgBox;
    private RecyclerView mRecyclerView;
    private LinearLayout mMessageContainer;
    private TextView mDefaultMsg;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);

        if (getIntent().getExtras() != null){
            topic = (Topic) getIntent().getExtras().get("topic");
        }

        initViews();
        createProgressDialog();
        boolean isSameUid = topic.getCreatorId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid());

        if(!isUserProfessor() || !isSameUid){
            mMessageContainer.setVisibility(View.GONE);
        }else{
            mMessageContainer.setVisibility(View.VISIBLE);
        }

        showProgressDialog("Carregando");

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        final FirebaseRecyclerAdapter<Message, MyOtherHolder> holder =  new FirebaseRecyclerAdapter<Message, MyOtherHolder>(Message.class, R.layout.cell_topic_messages,
                MyOtherHolder.class, databaseReference.child("topicos_mensagem").child(topic.getIdTopic())) {

            @Override
            protected void populateViewHolder(MyOtherHolder viewHolder, Message model, int position) {
				mDefaultMsg.setVisibility(View.GONE);
                viewHolder.txt_msg.setText(model.getMessage());
                viewHolder.txt_date.setText(model.getDate());
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

        dismissProgressDialog();

        mRecyclerView.setAdapter(holder);

        mRecyclerView.setLayoutManager(manager);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Mensagens");

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressDialog("Enviando");
                initObject();
                final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("topicos_mensagem").child(topic.getIdTopic());
                DatabaseReference refTopic = FirebaseDatabase.getInstance().getReference("generalTopic").child(topic.getIdTopic());
                final String key = ref.push().getKey();

                refTopic.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Topic topic = dataSnapshot.getValue(Topic.class);
                        message.setTitle(topic.getName());
                        ref.child(key).setValue(message);
                        mMsgBox.setText("");
                        dismissProgressDialog();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
    }


    @Override
    public void onClick(View v) {

    }

    @Override
    protected void initViews() {
        container = (DrawerLayout) findViewById(R.id.message_container);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        mMsgBox = (EditText) findViewById(R.id.shipper_field);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mFab = (FloatingActionButton) findViewById(R.id.fab_send_message);
        mMessageContainer = (LinearLayout) findViewById(R.id.message_sender_container);
        mDefaultMsg = (TextView) findViewById(R.id.default_msg);
    }

    @Override
    protected void initObject() {
        message = new Message();
        message.setMessage(mMsgBox.getText().toString());

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String formattedDate = df.format(calendar.getTime());
        message.setDate(formattedDate);
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

        if (id == android.R.id.home){
            Intent goToTopico = new Intent(TopicMessageActivity.this, MainActivity.class);
            startActivity(goToTopico);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void createProgressDialog(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
    }

    private void showProgressDialog (String text){
        progressDialog.setMessage(text);
        progressDialog.show();
    }

    private void dismissProgressDialog (){
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        dismissProgressDialog();
    }

    public static class MyOtherHolder extends RecyclerView.ViewHolder{
        TextView txt_msg;
        TextView txt_date;

        public MyOtherHolder(View itemView) {
            super(itemView);
            txt_msg = (TextView) itemView.findViewById(R.id.client_msg);
            txt_date = (TextView) itemView.findViewById(R.id.client_date);
        }
    }
}
