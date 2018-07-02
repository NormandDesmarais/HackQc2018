package com.example.payne.simpletestapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FirebaseAuthenticator extends AppCompatActivity {

    /**
     * [FIREBASE AUTH STARTS]
     * https://firebase.google.com/docs/auth/android/firebaseui
     * https://firebase.google.com/docs/auth/
     *
     * https://www.youtube.com/watch?v=-ywVw2O1pP8
     */
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // setting up the Firebase Authentification
        mAuth = FirebaseAuth.getInstance();
    }

    /**
     * Sert à vérifier que l'usager est en ligne ou non.
     */
    @Override
    public void onStart() {
        super.onStart();

        Log.w("!!!!!!!!!test FIREBASE", "méthode lancée!");

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);

        if(currentUser != null) {
            Log.w("!!!!!!!!!test FIREBASE", "ALLAH U AKBAR");
            Toast.makeText(this, "Currently logged in!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * TODO: Create a form and call this method when creating an account.
     *
     * @param email
     * @param password
     */
    public void createAccount(String email, String password) {

        // TODO: validate email and password.. ?

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("!!!firebase precode", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("!!!firebase precode", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(FirebaseAuthenticator.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }

    /**
     * TODO: Add a form to sign in users with their email and password and
     * call this new method when it is submitted.
     *
     * @param email
     * @param password
     */
    public void signIn(String email, String password) {

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("!!!firebase precode", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("!!!firebase precode", "signInWithEmail:failure", task.getException());
                            Toast.makeText(FirebaseAuthenticator.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }

    /*

    // If a user has signed in successfully you can get their
    // account data at any point with the getCurrentUser method.
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    if (user != null) {
        // Name, email address, and profile photo Url
        String name = user.getDisplayName();
        String email = user.getEmail();
        Uri photoUrl = user.getPhotoUrl();

        // Check if user's email is verified
        boolean emailVerified = user.isEmailVerified();

        // The user's ID, unique to the Firebase project. Do NOT use this value to
        // authenticate with your backend server, if you have one. Use
        // FirebaseUser.getToken() instead.
        String uid = user.getUid();
    }
     */
}
