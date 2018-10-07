package com.example.yehyunee.tagapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.yehyunee.tagapplication.util.CommandUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EmailInitLoginActivity extends Activity implements View.OnClickListener {

    EditText mEmailEditText;
    EditText mPasswordEditText;

    RelativeLayout mTextWholeLayout;
    RelativeLayout mEmailLoginLayout;
    RelativeLayout mEmailSignOutLayout;


    private String email = "";
    private String password = "";

    // 파이어베이스 인증 객체 생성
    private FirebaseAuth firebaseAuth;


    Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_init_login);

        // 파이어베이스 인증 객체 선언
        firebaseAuth = FirebaseAuth.getInstance();

        mTextWholeLayout = (RelativeLayout) findViewById(R.id.text_whole_layout);
        mEmailEditText = (EditText) findViewById(R.id.main_email_edit_text);
        mPasswordEditText = (EditText) findViewById(R.id.main_password_edit_text);
        mEmailLoginLayout = (RelativeLayout) findViewById(R.id.main_email_login_layout);
        mEmailSignOutLayout = (RelativeLayout) findViewById(R.id.main_email_sign_out);

        mEmailSignOutLayout.setOnClickListener(this);
        mEmailLoginLayout.setOnClickListener(this);

        mContext = this;
    }

    /**
     * 이메일 체크
     */
    public void emailsignIn() {
        email = mEmailEditText.getText().toString();
        password = mPasswordEditText.getText().toString();

        if (CommandUtil.isValidEmail(mContext, email) && CommandUtil.isValidPasswd(mContext, password)) {
            loginUser(email, password);
        }
    }

    /**
     * 로그인 성공 (파이어베이스 연동)
     */
    private void loginUser(final String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // 유효한 이메일
//                            FirebaseUser user = firebaseAuth.getCurrentUser();
//                            if(user.isEmailVerified()){
//                                Toast.makeText(EmailInitLoginActivity.this, "로그인 성공", Toast.LENGTH_SHORT).show();
//                            }else{
//                                Toast.makeText(EmailInitLoginActivity.this, "이메일 인증해주세요.", Toast.LENGTH_SHORT).show();
//                                user.sendEmailVerification();
//                            }
                            //Toast.makeText(EmailInitLoginActivity.this, "로그인 성공", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(EmailInitLoginActivity.this, HomeActivity.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.ani_slide_in_right, R.anim.ani_slide_out_left);
                        } else {
                            // 유효하지 않은 이메일
                            Toast.makeText(EmailInitLoginActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("firebase fail", e.getMessage());
            }
        });
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.main_email_login_layout:
                // 로그인
                emailsignIn();
                break;
            case R.id.main_email_sign_out:
                // 가입하기
                Intent intentTwo = new Intent(EmailInitLoginActivity.this, EmailSignOutActivity.class);
                startActivity(intentTwo);
                overridePendingTransition(R.anim.ani_slide_in_right, R.anim.ani_slide_out_left);
                break;
        }
    }
}
