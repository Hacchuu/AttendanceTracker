package com.story.mipsa.attendancetracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class Login extends AppCompatActivity {

    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private TextView resetPassword;
    private ProgressDialog progressDialog;
    private View mLoginFormView;
    private FirebaseAuth firebaseAuth;
    private Button button;
    SignInButton signInButton;
    private EditText email;
    private EditText password;
    private TextView signup;
    GoogleSignInClient googleSignInClient;
    int RC_SIGN_IN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();

        signInButton = findViewById(R.id.googleSignIn);
        signInButton = findViewById(R.id.googleSignIn);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.googleSignIn:
                        signIn();
                        break;
                }
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        email = findViewById(R.id.email_sign_in);
        password = findViewById(R.id.password_sign_in);
        button = findViewById(R.id.Signin);
        resetPassword = findViewById(R.id.resetPassword);


        progressDialog = new ProgressDialog(this);
        signup = findViewById(R.id.signUp);
        if (firebaseAuth.getCurrentUser() != null) {
            finish();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                finish();
                Intent intent = new Intent(getApplicationContext(), SignUp.class);
                startActivity(intent);
            }
        });


//        email.setOnClickListener(new View.OnClickListener());

        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                finish();
                Intent intent = new Intent(getApplicationContext(), passwordReset.class);
                startActivity(intent);
            }
        });

//        button.setOnClickListener(this);
    }

    private void signIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                //Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);

                //authenticating with firebase
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
//                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void userLogin(View view) {
        String Email = email.getText().toString().trim();
        String Password = password.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(Password) && !isPasswordValid(Password)) {
            password.setError(getString(R.string.error_invalid_password));
            focusView = password;
            cancel = true;
        }
        if (TextUtils.isEmpty(Password)) {
            password.setError("Enter password");
            focusView = password;
            cancel = true;
        }
        // Check for a valid email address.
        if (TextUtils.isEmpty(Email)) {
            email.setError(getString(R.string.error_field_required));
            focusView = email;
            cancel = true;
        } else if (!isEmailValid(Email)) {
            email.setError(getString(R.string.error_invalid_email));
            focusView = email;
            cancel = true;
        }
        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            //showProgress(true);
            firebaseAuth.signInWithEmailAndPassword(Email, Password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(Task<AuthResult> task) {
                    //progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        //finish();
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        progressDialog.setMessage("Logging in.");
                        progressDialog.show();
                        startActivity(intent);
                    } else {
                        Toast.makeText(Login.this, "Unable to Log In!", Toast.LENGTH_LONG).show();
                    }

                }
            });
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
//        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        //getting the auth credential
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

        //Now using firebase we are signing in the user here
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
//                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            Toast.makeText(getApplicationContext(), "User Signed In", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), FirstPage.class);
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
//                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

}
