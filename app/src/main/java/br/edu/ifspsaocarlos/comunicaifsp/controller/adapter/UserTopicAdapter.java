package br.edu.ifspsaocarlos.comunicaifsp.controller.adapter;

import android.app.ProgressDialog;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;

import br.edu.ifspsaocarlos.comunicaifsp.R;
import br.edu.ifspsaocarlos.comunicaifsp.controller.activity.ListUserTopicActivity;
import br.edu.ifspsaocarlos.comunicaifsp.model.entity.User;

/**
 * Created by estevao on 24/11/17.
 */

public class UserTopicAdapter extends FirebaseRecyclerAdapter<User, UserTopicAdapter.MyViewHolder> {

    private final ProgressDialog progressDialog;
    private final TextView mDefaultMsg;

    public UserTopicAdapter(Class<User> modelClass, int modelLayout, Class<MyViewHolder> viewHolderClass, Query ref, ProgressDialog progressDialog, TextView mDefaultMsg) {
        super(modelClass, modelLayout, viewHolderClass, ref);
        this.progressDialog = progressDialog;
        this.mDefaultMsg = mDefaultMsg;
    }

    @Override
    protected void populateViewHolder(MyViewHolder viewHolder, User model, int position) {
        mDefaultMsg.setVisibility(View.GONE);
        viewHolder.txt_user.setText(model.getName());
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView txt_user;

        public MyViewHolder(View itemView) {
            super(itemView);
            txt_user = (TextView) itemView.findViewById(R.id.user_name);
        }
    }
}
