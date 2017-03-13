package com.example.juarez.trackingapp.View.Fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.juarez.trackingapp.R;
import com.example.juarez.trackingapp.TrackerApplication;

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

    private TrackerApplication application;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.application = (TrackerApplication) getActivity().getApplication();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @OnClick(R.id.btnRegister)
    public void onClick(){

        String name = edtName.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String pass = edtPass.getText().toString().trim();


        if(name.isEmpty() || email.isEmpty() || pass.isEmpty()){
            Toast.makeText(getActivity(), "Preencha os campos!", Toast.LENGTH_LONG).show();
        }else{
            application.createUserWithEmailAndPassword(name, email, pass);
        }

    }

}
