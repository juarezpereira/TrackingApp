package com.example.juarez.trackingapp.View.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
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

public class LoginFragment extends Fragment {

    @BindView(R.id.edtEmail)
    EditText edtEmail;
    @BindView(R.id.edtPass)
    EditText edtPass;

    private TrackerApplication application;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.application = (TrackerApplication) getActivity().getApplication();
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
            this.application.doLoginEmailAndPass(email, pass);
        }

    }

}