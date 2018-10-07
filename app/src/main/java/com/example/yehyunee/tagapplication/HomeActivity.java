package com.example.yehyunee.tagapplication;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.yehyunee.tagapplication.fragment.FollowFragment;
import com.example.yehyunee.tagapplication.fragment.HOTFragment;
import com.example.yehyunee.tagapplication.fragment.HomeFragment;
import com.example.yehyunee.tagapplication.fragment.MyPageFragment;
import com.example.yehyunee.tagapplication.fragment.UploadFragment;
import com.example.yehyunee.tagapplication.util.BottomNavigationViewHelper;
import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    private FirebaseAuth auth;

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        auth = FirebaseAuth.getInstance();

       /* Button logout_button = (Button)findViewById(R.id.logout_button);
        logout_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                LoginManager.getInstance().logOut();
                finish();
                Intent intent = new Intent(HomeActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });*/

        bottomNavigationView = (BottomNavigationView)findViewById(R.id.bottom_navigationView);

        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        loadFragment(new HomeFragment());

        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
    }

    private boolean loadFragment(Fragment fragment) {
        if(fragment != null) {

            getSupportFragmentManager().beginTransaction().replace(R.id.fragement_container, fragment).commit();

            return true;
        }
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        switch(item.getItemId()){
            case R.id.action_home:
                transaction.replace(R.id.fragement_container, new HomeFragment()).commit();
                return true;
            case R.id.action_hot:
                transaction.replace(R.id.fragement_container, new HOTFragment()).commit();
                return true;
            case R.id.action_upload:
                transaction.replace(R.id.fragement_container, new UploadFragment()).commit();
                return true;
            case R.id.action_follow:
                transaction.replace(R.id.fragement_container, new FollowFragment()).commit();
                return true;
            case R.id.action_mypage:
                transaction.replace(R.id.fragement_container, new MyPageFragment()).commit();
                return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
