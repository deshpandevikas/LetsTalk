package com.example.venkatesh.hw07;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Bundle;
import android.telephony.CellIdentityCdma;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{
    EditText email;
    EditText password;
    Button loginButton;
    Button createAccountButton;
    FirebaseAuth refAuth;
    FirebaseDatabase refDatabse;

    LoginButton fblogin;
    CallbackManager callbackManager;
    Button signup, cancel;
    private GoogleApiClient googleApiClient;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private static int rc_sign_in=0;
    private static String TAG="MAIN_ACTIVITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);

        loginButton = (Button) findViewById(R.id.button_login);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                final String userEmail = email.getText().toString().trim();
                String userPassword = password.getText().toString().trim();

                if(userEmail.length()<1 || userPassword.length()<1)
                {
                    Toast.makeText(getApplicationContext(),"Enter proper details", Toast.LENGTH_LONG).show();
                }
                else {
                    refAuth.signInWithEmailAndPassword(userEmail,userPassword).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {

                            Toast.makeText(getApplicationContext(),"Login Successfull", Toast.LENGTH_LONG).show();
                            final Intent intent = new Intent(LoginActivity.this, CreateProfile.class);
                            intent.putExtra("useremail", userEmail);

                            finish();
                            startActivity(intent);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {


                            Toast.makeText(getApplicationContext(),"Invalid Credentials",Toast.LENGTH_LONG).show();
                        }
                    });
                }

            }
        });

        refAuth = FirebaseAuth.getInstance();
        mAuth= FirebaseAuth.getInstance();
        refDatabse = FirebaseDatabase.getInstance();
        mAuthListener= new FirebaseAuth.AuthStateListener()
        {

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user1= firebaseAuth.getCurrentUser();
                if(user1!=null)
                {
                    Log.d("user",user1+" logged in" + user1.getEmail());
                    Intent i = new Intent(getApplicationContext(),CreateProfile.class);
                    finish();
                    startActivity(i);
                }
            }
        };

        GoogleSignInOptions gso= new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail().build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this).addApi(Auth.GOOGLE_SIGN_IN_API,gso).build();

        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signinintent= Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(signinintent,rc_sign_in);
            }
        });




        fblogin = (LoginButton) findViewById(R.id.fblogin);
        callbackManager=CallbackManager.Factory.create();


        fblogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult)
            {
                Toast.makeText(getApplicationContext(),"Logged in successfully "+loginResult.getAccessToken().getUserId(),Toast.LENGTH_LONG).show();
                Log.d("check","Logged in successfullyyy");

            }

            @Override
            public void onCancel()
            {
                Toast.makeText(getApplicationContext(),"Log In Cancelled by the user",Toast.LENGTH_LONG).show();
                Log.d("check","Logged in cancelled");
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(getApplicationContext(),"Something went wrong",Toast.LENGTH_LONG).show();
                Log.d("check","Something went wrong");
            }
        });




        email = (EditText) findViewById(R.id.editText_email);
        password = (EditText) findViewById(R.id.editText_password);
        loginButton = (Button) findViewById(R.id.button_login);
        createAccountButton = (Button) findViewById(R.id.button_createNewAccount);

        //FirebaseUser user =  FirebaseAuth.getInstance().getCurrentUser();

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                finish();
                startActivity(intent);

            }
        });

//        FirebaseAuth auth = FirebaseAuth.getInstance();
//        startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().
//                setProviders(AuthUI.FACEBOOK_PROVIDER,AuthUI.GOOGLE_PROVIDER).build(),1);




    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==rc_sign_in)
        {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            if(result.isSuccess())
            {
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseauthwithgoogle(account);
            }
            else
            {
                Log.d("failed","Google login failed");
            }
        }
        //callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    @Override
    protected void onStop() {
        super.onStop();
        if(mAuthListener!=null)
        {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }



    private void firebaseauthwithgoogle(GoogleSignInAccount account)
    {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d("auth","Sign in with credentials successfull"+ task.isSuccessful());
                Toast.makeText(getApplicationContext(),"Login Successfull", Toast.LENGTH_LONG).show();
                Intent i = new Intent(getApplicationContext(),CreateProfile.class);
                finish();
                startActivity(i);
            }
        });


    }

}
