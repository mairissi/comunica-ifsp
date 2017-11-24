package br.edu.ifspsaocarlos.comunicaifsp.controller.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import br.edu.ifspsaocarlos.comunicaifsp.R;
import br.edu.ifspsaocarlos.comunicaifsp.controller.adapter.UserTopicAdapter;
import br.edu.ifspsaocarlos.comunicaifsp.model.entity.User;

/**
 * Created by MRissi on 24-Nov-17.
 */

public class ListUserTopicActivity extends CommonActivity{

    ProgressDialog progressDialog;
    private TextView user;
    private TextView mDefaultMsg;

    private RecyclerView mRecycler;
    String topicid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_user_topic);

        initViews();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            topicid = extras.getString("TOPICID");
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Carregando");

        getSupportActionBar().setTitle("Usuários");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRecycler = (RecyclerView) findViewById(R.id.recycler_view_users);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        mRecycler.setAdapter(new UserTopicAdapter(User.class, R.layout.cell_user_topic,
                UserTopicAdapter.MyViewHolder.class, databaseReference.child("topico_e_usuario/" + topicid), progressDialog, mDefaultMsg));

        final LinearLayoutManager manager = new LinearLayoutManager(this);
        mRecycler.setLayoutManager(manager);

        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

    }

    @Override
    protected void initViews() {
        user = (TextView) findViewById(R.id.user_name);
        mRecycler = (RecyclerView) findViewById(R.id.recycler_view_users);
        mDefaultMsg = (TextView) findViewById(R.id.default_msg_user);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                Intent goToTopico = new Intent(ListUserTopicActivity.this, TopicMessageActivity.class);
                startActivity(goToTopico);
                finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }

    @Override
    protected void initObject() {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


}
