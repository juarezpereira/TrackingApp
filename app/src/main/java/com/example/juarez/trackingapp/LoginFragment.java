package com.example.juarez.trackingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.juarez.trackingapp.Model.User;
import com.example.juarez.trackingapp.Utils.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginFragment extends Fragment {

    @BindView(R.id.edtEmail)
    EditText edtEmail;
    @BindView(R.id.edtPass)
    EditText edtPass;

    private FirebaseAuth mAuth;
    private DatabaseReference mRefUsers;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @OnClick(R.id.btnStart)
    public void onClick(View v){

        String email = edtEmail.getText().toString().trim();
        String pass = edtPass.getText().toString().trim();

        if(TextUtils.isEmpty(email) || TextUtils.isEmpty(pass)){
            Toast.makeText(getContext(), "Preencha os campos", Toast.LENGTH_SHORT).show();
        }else{
            mAuth.signInWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){

                                mRefUsers = FirebaseDatabase.getInstance().getReference(Constants.USERS);
                                final FirebaseUser mUser = mAuth.getCurrentUser();

                                if(mUser != null){

                                    mRefUsers.child(mUser.getUid())
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    User user = dataSnapshot.getValue(User.class);

                                                    if(user == null)
                                                        return;

                                                    user.setOnline(true);

                                                    mRefUsers.child(mUser.getUid()).setValue(user);
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });

                                    Intent intent = new Intent(getContext(), MapActivity.class);
                                    startActivityForResult(intent, Constants.ACTIVITY_MAP);
                                }

                                Toast.makeText(getContext(), "Login sucess!", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(getContext(), "Login Failure", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

    }

}
