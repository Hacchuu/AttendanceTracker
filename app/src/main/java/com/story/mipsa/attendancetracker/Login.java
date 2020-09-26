package com.story.mipsa.attendancetracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {

    private TextView resetPassword;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private Button normalSignInButton;
    private SignInButton signInButton;
    private EditText email;
    private EditText password;
    private TextView signup;
    private GoogleSignInClient googleSignInClient;
    private int RC_SIGN_IN = 1;
    private FirebaseUser user;
    private FirebaseDatabase database;
    private DatabaseReference ref;
    private String minimumAttendance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

        this.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);getSupportActionBar().setCustomView(R.layout.custom_action_bar);
        View view=getSupportActionBar().getCustomView();
        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#556e5f"));
        ActionBar actionBar = getSupportActionBar();
        getSupportActionBar().setElevation(0);
        actionBar.setBackgroundDrawable(colorDrawable);
        TextView display = view.findViewById(R.id.name);
        display.setText("Student Pocket");

        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        ref = database.getReference().getRoot();
        firebaseAuth.signOut();

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
        //Building a google sign in
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
        progressDialog = new ProgressDialog(this);


        email = findViewById(R.id.email_sign_in);
        password = findViewById(R.id.password_sign_in);
        normalSignInButton = findViewById(R.id.SignIn);
        resetPassword = findViewById(R.id.resetPassword);

        normalSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userLogin();
            }
        });

        signup = findViewById(R.id.signUp);
        if (firebaseAuth.getCurrentUser() != null) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            startActivity(intent);
            finish();
        }
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SignUp.class);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                startActivity(intent);
            }
        });

        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PasswordReset.class);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                startActivity(intent);
            }
        });

    }

    //Sign in fucntion to call google sign in intent
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
                progressDialog.setMessage("Logging in.");
                progressDialog.isIndeterminate();
                progressDialog.show();
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Toast.makeText(getApplicationContext(),task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("google sign in error", "onActivityResult: "+e.getMessage());
            }
        }
    }


    public void userLogin() {
        String Email = email.getText().toString().trim();
        String Password = password.getText().toString().trim();
        boolean cancel = false;
        View focusView = null;
        progressDialog.setMessage("Logging in.");
        progressDialog.isIndeterminate();
        progressDialog.show();
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
            //firebase function that register the email and password
            firebaseAuth.signInWithEmailAndPassword(Email, Password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(Task<AuthResult> task) {

                    if (task.isSuccessful()) {
                        user = firebaseAuth.getCurrentUser();
                        if(user.isEmailVerified()){
                            target_callDB();
                            Toast.makeText(getApplicationContext(),"Succesful",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(),"Please verify your email address", Toast.LENGTH_SHORT).show();
                        }

                    }
                    else {
                        progressDialog.dismiss();
                        Toast.makeText(Login.this, "This account does not exist", Toast.LENGTH_LONG).show();
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

    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {

        //getting the auth credential
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

        //Now using firebase we are signing in the user here
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            user = firebaseAuth.getCurrentUser();
                            target_callDB();
                        } else {
                            // If sign in fails, display a message to the user.
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Authentication failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void target_callDB() {
        DatabaseReference checkRef = ref.child("Users").child(user.getUid());
        //Listener ofr Firebase DB
        checkRef.child("Target").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                minimumAttendance = (String) dataSnapshot.getValue();
                if(minimumAttendance != null){
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    progressDialog.dismiss();
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    startActivity(intent);
                    finish();
                }
                else{

                    Intent intent = new Intent(getApplicationContext(), AttendanceTarget.class);
                    progressDialog.dismiss();
                    intent.putExtra("initialTarget", "0");
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
