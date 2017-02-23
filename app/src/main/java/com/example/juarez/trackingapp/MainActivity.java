package com.example.juarez.trackingapp;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.juarez.trackingapp.Utils.Constants;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FragmentManager mFragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.mAuth = FirebaseAuth.getInstance();

        setupFragmentManager();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_login:
                showFragment(new LoginFragment());
                return true;
            case R.id.menu_register:
                showFragment(new RegisterFragment());
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mAuth.getCurrentUser() != null){
            Intent intent = new Intent(MainActivity.this, MapActivity.class);
            startActivityForResult(intent, Constants.ACTIVITY_MAP);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Constants.ACTIVITY_MAP && resultCode == Constants.CLOSE_APP){
            finish();
        }
    }

    public void setupFragmentManager(){
        this.mFragmentManager = getSupportFragmentManager();
        mFragmentManager.beginTransaction()
                .replace(R.id.Fragment_Root, new LoginFragment())
                .addToBackStack(null)
                .commit();
    }

    public void showFragment(Fragment fragment){
        this.mFragmentManager = getSupportFragmentManager();
        mFragmentManager.beginTransaction()
                .replace(R.id.Fragment_Root,fragment)
                .addToBackStack(null)
                .commit();
    }

}