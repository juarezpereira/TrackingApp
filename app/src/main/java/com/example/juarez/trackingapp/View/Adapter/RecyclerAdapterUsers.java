package com.example.juarez.trackingapp.View.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.juarez.trackingapp.Model.User;
import com.example.juarez.trackingapp.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Juarez Pereira on 06/03/2017.
 */

public class RecyclerAdapterUsers extends RecyclerView.Adapter<RecyclerAdapterUsers.ViewHolder> {

    private Context c;
    private List<User> mList;
    private LayoutInflater mLayoutInflater;
    private OnClickItemRecycler onClickItemRecycler;

    public RecyclerAdapterUsers(Context c, List<User> lst){
        this.c = c;
        this.mList = lst;
        this.mLayoutInflater = LayoutInflater.from(c);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bindView(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void setOnClickItemRecycler(OnClickItemRecycler onClickItemRecycler) {
        this.onClickItemRecycler = onClickItemRecycler;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.tvName)
        TextView tvName;
        @BindView(R.id.tvStatus)
        TextView tvStatus;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(this);
        }

        void bindView(User user){
            tvName.setText(user.getName());

            if(user.isOnline()){
                tvStatus.setText("Online");
                tvStatus.setTextColor(c.getResources().getColor(R.color.colorPrimary));
                tvStatus.setBackground(c.getResources().getDrawable(R.drawable.status_background_online));
            }else{
                tvStatus.setText("Offline");
                tvStatus.setTextColor(c.getResources().getColor(R.color.colorAccent));
            }
        }

        @Override
        public void onClick(View v) {
            if(onClickItemRecycler != null){
                onClickItemRecycler.onClickListener(v, getAdapterPosition());
            }
        }

    }

    public interface OnClickItemRecycler{
        void onClickListener(View view, int position);
    }

}
