package com.example.juarez.trackingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.juarez.trackingapp.Model.User;
import com.example.juarez.trackingapp.Utils.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Juarez on 23/02/2017.
 */

public class RegisterFragment extends Fragment {

    @BindView(R.id.edtName)
    EditText edtName;
    @BindView(R.id.edtEmail)
    EditText edtEmail;
    @BindView(R.id.edtPass)
    EditText edtPass;

    private FirebaseAuth mAuth;
    private DatabaseReference mRefUsers;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mAuth = FirebaseAuth.getInstance();
        this.mRefUsers = FirebaseDatabase.getInstance().getReference(Constants.USERS);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @OnClick(R.id.btnRegister)
    public void onClick(View v){

        final String name = edtName.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String pass = edtPass.getText().toString().trim();

        mAuth.createUserWithEmailAndPassword(email,pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            if(mAuth.getCurrentUser() == null)
                                return;

                            User user = new User();
                            user.setName(name);
                            user.setUID(mAuth.getCurrentUser().getUid());
                            user.setOnline(true);

                            Map<String, Boolean> trackings = new HashMap<>();
                            trackings.put("LcTfdaN3jtZTDu6drKqd0VwpNvL2", true);

                            user.setTracking(trackings);

                            mRefUsers.child(mAuth.getCurrentUser().getUid())
                                    .setValue(user)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                Intent intent = new Intent(getContext(), MapActivity.class);
                                                startActivityForResult(intent, Constants.ACTIVITY_MAP);
                                            }
                                        }
                                    });
                        }
                    }
                });

    }

}
