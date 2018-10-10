package com.example.yehyunee.tagapplication;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Handler;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yehyunee.tagapplication.util.CommandUtil;
import com.example.yehyunee.tagapplication.util.PreshareUtil;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.KakaoSDK;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.LoginButton;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;

public class MainActivity extends FragmentActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {


    RelativeLayout mImageWholeLayout;
    RelativeLayout mSnsLoginWholeLayout;
    RelativeLayout mGoogleLoginLayout;
    RelativeLayout mFacebookLoginLayout;
    RelativeLayout mKakaoLoginLayout;

    LoginButton mKakaoLogin;

    TextView mEmailLogin;

    //GoogleApiClient mGoogleApiClient;
    GoogleSignInClient mGoogleSignInClient;
    FirebaseAuth mAuth;

    private CallbackManager callbackManager;
    private SessionCallback sessionCallback;


    Handler mhandler;

    int RC_SIGN_IN = 1000;
    String TAG = "TAG";

    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mImageWholeLayout = (RelativeLayout) findViewById(R.id.image_whole_layout);
        mSnsLoginWholeLayout = (RelativeLayout) findViewById(R.id.sns_login_whole_layout);
        mGoogleLoginLayout = (RelativeLayout) findViewById(R.id.google_login_btn);
        mFacebookLoginLayout = (RelativeLayout) findViewById(R.id.facebook_login_btn);
        mKakaoLoginLayout = (RelativeLayout) findViewById(R.id.kakao_login_layout);
        mKakaoLogin = (LoginButton) findViewById(R.id.btnKakaoLogin);


        mEmailLogin = (TextView) findViewById(R.id.email_login_layout);

        mGoogleLoginLayout.setClickable(false);
        mFacebookLoginLayout.setClickable(false);
        mKakaoLoginLayout.setClickable(false);
        mEmailLogin.setClickable(false);

        mKakaoLoginLayout.setOnClickListener(this);
        mEmailLogin.setOnClickListener(this);
        mGoogleLoginLayout.setOnClickListener(this);
        mFacebookLoginLayout.setOnClickListener(this);

        mContext = this;

        mhandler = new Handler();
        mhandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mImageWholeLayout.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.ani_img));
                mSnsLoginWholeLayout.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.ani_text));
                mSnsLoginWholeLayout.setVisibility(View.VISIBLE);
            }
        }, 1000);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
//                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
//                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);

        mAuth = FirebaseAuth.getInstance();


        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(android.util.Base64.encode(md.digest(), 0));
                Log.d("Hash key", something);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.e("name not found", e.toString());
        }

    }

    /**
     * 구글 로그인
     */
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    /**
     * 페이스북 로그인
     */
    public void facebookLoginOnClick() {
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().logInWithReadPermissions(MainActivity.this,
                Arrays.asList("public_profile", "email"));
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(final LoginResult result) {

                GraphRequest request;
                request = GraphRequest.newMeRequest(result.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {

                    @Override
                    public void onCompleted(JSONObject user, GraphResponse response) {
                        if (response.getError() != null) {

                        } else {
                            Log.i("TAG", "user: " + user.toString());
                            Log.i("TAG", "AccessToken: " + result.getAccessToken().getToken());
                            setResult(RESULT_OK);

                        }
                    }
                });
                Toast.makeText(MainActivity.this, "페이스북 로그인 성공", Toast.LENGTH_SHORT).show();
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender,birthday");
                request.setParameters(parameters);
                request.executeAsync();

                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.ani_slide_in_right, R.anim.ani_slide_out_left);
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(MainActivity.this, "페이스북 로그인 실패", Toast.LENGTH_SHORT).show();
                Log.e("test", "Error: " + error);
                //finish();
            }

            @Override
            public void onCancel() {
                //finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                e.printStackTrace();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(),null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            //db 연결 후 기존유저인지 신규유저인지 판별
                            // /names/$uid:"name"
                            // uid를 이용해서 신규인지 기존유저인지 확인
                            // FirebaseUser user = mAuth.getcurrentUser()
                            // user.getUid()
                            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.ani_slide_in_right, R.anim.ani_slide_out_left);
                        }else {
                            Toast.makeText(MainActivity.this, "구글 로그인 실패", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getApplicationContext(), "" + connectionResult, Toast.LENGTH_SHORT).show();
    }

    public void request() {
        UserManagement.getInstance().requestMe(new MeResponseCallback() {
            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                Log.d("error", "Session Closed Error is " + errorResult.toString());
                Toast.makeText(MainActivity.this, "카카오로그인 실패", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNotSignedUp() {

            }

            @Override
            public void onSuccess(UserProfile result) {
                Toast.makeText(MainActivity.this, "카카오로그인 성공" , Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.ani_slide_in_right, R.anim.ani_slide_out_left);
            }
        });
    }

    private class SessionCallback implements ISessionCallback {

        @Override
        public void onSessionOpened() {
            request();
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            Log.d("error", "Session Fail Error is " + exception.getMessage().toString());
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.google_login_btn:
                signIn();
                break;
            case R.id.facebook_login_btn:
                facebookLoginOnClick();
                break;
            case R.id.email_login_layout:
                Intent intent = new Intent(MainActivity.this, EmailInitLoginActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.ani_slide_in_right, R.anim.ani_slide_out_left);
                break;
            case R.id.kakao_login_layout:
                sessionCallback = new SessionCallback();
                Session.getCurrentSession().addCallback(sessionCallback);
                Session.getCurrentSession().checkAndImplicitOpen();

                mKakaoLogin.performClick();
                break;
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Session.getCurrentSession().clearCallbacks();
        Session.getCurrentSession().close();
    }
}
