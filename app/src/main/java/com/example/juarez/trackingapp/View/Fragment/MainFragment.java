package com.example.juarez.trackingapp.View.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.juarez.trackingapp.Model.User;
import com.example.juarez.trackingapp.R;
import com.example.juarez.trackingapp.TrackerApplication;
import com.example.juarez.trackingapp.Utils.Constants;
import com.example.juarez.trackingapp.Utils.DividerItemDecorator;
import com.example.juarez.trackingapp.View.Adapter.RecyclerAdapterUsers;
import com.example.juarez.trackingapp.View.MapActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Juarez Pereira on 06/03/2017.
 */

public class MainFragment extends Fragment implements RecyclerAdapterUsers.OnClickItemRecycler{

    @BindView(R.id.SwipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.RecyclerView)
    RecyclerView mRecyclerView;

    private RecyclerAdapterUsers mRecyclerAdapter;
    private List<User> mList;

    private DatabaseReference mRefDatabaseUsers;

    private TrackerApplication application;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.application = (TrackerApplication) getActivity().getApplication();

        this.mRefDatabaseUsers = FirebaseDatabase.getInstance().getReference().child(Constants.USERS);

        this.mList = new ArrayList<>();
        this.mRecyclerAdapter = new RecyclerAdapterUsers(getActivity(),mList);
        this.mRecyclerAdapter.setOnClickItemRecycler(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);

        this.mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        this.mRecyclerView.setHasFixedSize(true);
        this.mRecyclerView.setAdapter(mRecyclerAdapter);
        this.mRecyclerView.addItemDecoration(new DividerItemDecorator(getActivity()));

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.mRefDatabaseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (mList.size() > 0) mList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);
                    mList.add(user);
                }

                mRecyclerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onClickListener(View view, int position) {
        application.setTracking(mList.get(position).getUID());
        startActivity(new Intent(getActivity(), MapActivity.class));
    }

}