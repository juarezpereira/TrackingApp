package com.example.juarez.trackingapp;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.juarez.trackingapp.Utils.Constants;

import com.example.juarez.trackingapp.Utils.EventLogin;
import com.example.juarez.trackingapp.Utils.IntroManager;
import com.example.juarez.trackingapp.View.Fragment.LoginFragment;
import com.example.juarez.trackingapp.View.Fragment.MainFragment;
import com.example.juarez.trackingapp.View.MapActivity;
import com.example.juarez.trackingapp.View.Fragment.RegisterFragment;
import com.example.juarez.trackingapp.View.WelcomeActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.Toolbar)
    Toolbar mToolbar;

    private TrackerApplication application;
    private FragmentManager mFragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        IntroManager introManager = new IntroManager(this);
        if(introManager.isFirstLaunch()){
            startActivity(new Intent(MainActivity.this, WelcomeActivity.class));
        }

        this.application = (TrackerApplication) getApplication();
        application.setEventLogin(new EventLogin() {
            @Override
            public void onBeforeLogin() {
                showFragment(new LoginFragment());

                invalidateOptionsMenu();
            }

            @Override
            public void onAfterLogin(FirebaseUser firebaseUser) {
                showFragment(new MainFragment());

                invalidateOptionsMenu();
            }
        });

        setupFragmentManager();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_logout:
                application.logout();
                return true;
            case R.id.menu_register:
                showFragment(new RegisterFragment());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Fragment fragment = mFragmentManager.findFragmentById(R.id.Fragment_Root);

        if(fragment instanceof MainFragment){

            MenuItem itemMenuR = menu.findItem(R.id.menu_register);
            itemMenuR.setVisible(false);

            MenuItem itemMenuL = menu.findItem(R.id.menu_logout);
            itemMenuL.setVisible(true);

        }else{

            MenuItem itemMenuR = menu.findItem(R.id.menu_register);
            itemMenuR.setVisible(true);

            MenuItem itemMenuL = menu.findItem(R.id.menu_logout);
            itemMenuL.setVisible(false);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        super.onResume();
        application.init();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.ACTIVITY_MAP && resultCode == Constants.CLOSE_APP) {
            finish();
        }
    }

    public void setupFragmentManager() {
        this.mFragmentManager = getSupportFragmentManager();
        mFragmentManager.beginTransaction()
                .replace(R.id.Fragment_Root, new MainFragment())
                .addToBackStack(null)
                .commit();
    }

    public void showFragment(Fragment fragment) {
        this.mFragmentManager = getSupportFragmentManager();
        mFragmentManager.beginTransaction()
                .replace(R.id.Fragment_Root, fragment)
                .addToBackStack(null)
                .commit();
    }

}