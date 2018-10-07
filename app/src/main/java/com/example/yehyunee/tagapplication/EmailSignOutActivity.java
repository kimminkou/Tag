package com.example.yehyunee.tagapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.yehyunee.tagapplication.util.CommandUtil;
import com.example.yehyunee.tagapplication.util.PreshareUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EmailSignOutActivity extends Activity implements View.OnClickListener {

    private EditText mEmailText;
    private EditText mNameText;
    private EditText mPasswordText;
    private EditText mPasswordFinalText;

    private RelativeLayout mEmailSignOut;

    private String email = "";
    private String password = "";

    private boolean mPasswordNotConfirm = false;

    // 파이어베이스 인증 객체 생성
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_sign_out);

        mContext = this;

        // 파이어베이스 인증 객체 선언
        firebaseAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        mEmailText = (EditText) findViewById(R.id.email_edit_text);
        mNameText = (EditText) findViewById(R.id.name_edit_text);
        mPasswordText = (EditText) findViewById(R.id.password_edit_text);
        mPasswordFinalText = (EditText) findViewById(R.id.password_final_edit_text);

        Intent intent = getIntent();
        email = intent.getExtras().getString("email");
        mEmailText.setText(email);

        mPasswordText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasfocus) {
                if (hasfocus == true) {
                    // 포커스 잃었을 때
                    if (!mPasswordText.getText().toString().equals(mPasswordFinalText.getText().toString())) {
                        Toast.makeText(EmailSignOutActivity.this, "비밀번호를 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
                        mPasswordNotConfirm = true;
                    } else {
                        mPasswordNotConfirm = false;
                    }
                }
            }
        });


        mEmailSignOut = (RelativeLayout) findViewById(R.id.email_sign_out_layout);

        mEmailSignOut.setOnClickListener(this);
    }
    /**
     * 이메일 가입하기
     */
    public void singUp() {
        email = mEmailText.getText().toString();
        password = mPasswordText.getText().toString();

        if (CommandUtil.isValidEmail(mContext, email) && CommandUtil.isValidPasswd(mContext, password)) {
            createUser(password);
        }
    }

    /**
     * 이메일 가입하기 (파이어베이스 연동)
     */
    private void createUser(String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // 유효하지 않은 이메일
                            // 이메일인증 UI 나오면 추가 작업 예정!
//                            FirebaseUser user = firebaseAuth.getCurrentUser();
//                            if(user.isEmailVerified()){
//                                Toast.makeText(EmailSignOutActivity.this, "로그인 성공", Toast.LENGTH_SHORT).show();
//                            }else{
//                                Toast.makeText(EmailSignOutActivity.this, "이메일 인증해주세요.", Toast.LENGTH_SHORT).show();
//                                user.sendEmailVerification();
//                            }
                            Toast.makeText(EmailSignOutActivity.this, "가입 성공", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(EmailSignOutActivity.this, ProfileActivity.class);
                            intent.putExtra("email", email);
                            startActivity(intent);
                            overridePendingTransition(R.anim.ani_slide_in_right, R.anim.ani_slide_out_left);
                        } else {
                            // 유효한 이메일
                            Toast.makeText(EmailSignOutActivity.this, "이미 가입된 이메일입니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("firebase fail" , e.getMessage());
            }
        });
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.email_sign_out_layout:
                if (mEmailText.getText().toString().equals("")) {
                    Toast.makeText(EmailSignOutActivity.this, "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else if (mNameText.getText().toString().equals("")) {
                    Toast.makeText(EmailSignOutActivity.this, "이름을 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else if (mPasswordText.getText().toString().equals("")) {
                    Toast.makeText(EmailSignOutActivity.this, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else if (mPasswordFinalText.getText().toString().equals("") || mPasswordNotConfirm) {
                    Toast.makeText(EmailSignOutActivity.this, "비밀번호를 확인해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    singUp();
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.ani_slide_in_left, R.anim.ani_slide_out_right);
    }
}
