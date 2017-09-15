package br.edu.ifspsaocarlos.comunicaifsp;

import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MRissi on 15-Sep-17.
 */

public class TopicoAdapter extends RecyclerView.Adapter<TopicoAdapter.MyViewHolder> {

    //TODO criar uma ArrayList<Topico> do tipo tópico
    ArrayList<String> list;

    //TODO passar a ArrayList<Topico>
    public TopicoAdapter(ArrayList<String> list){
        this.list = list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_topico,parent,false);
        MyViewHolder vH = new MyViewHolder(view);
        return vH;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        //Tipo da lista (topico)
        String celula = list.get(position);
        //GetMsg do Topico
        holder.txt_msg.setText(celula);
        //GetName do Topico
        holder.txt_name.setText(celula);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView txt_name;
        TextView txt_msg;

        public MyViewHolder(View itemView) {
            super(itemView);
            txt_name = (TextView) itemView.findViewById(R.id.client_name);
            txt_msg = (TextView) itemView.findViewById(R.id.client_msg);
        }
    }

}
