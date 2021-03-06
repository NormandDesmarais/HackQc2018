package com.example.payne.simpletestapp.Authentification;

import com.example.payne.simpletestapp.R;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.GoogleAuthProvider;

import com.squareup.picasso.Picasso;

/*
    TODO: For verification on backend server!
    https://developers.google.com/identity/sign-in/android/backend-auth

    TODO: TESTS
        - Test "Auth" when disconnecting from Internet
        - "onActivityResult" and "signOutGoogle" are necessarily related to Google ?
    - Add a "don't remember me" option for Sign In experience

https://stackoverflow.com/questions/40838154/retrieve-google-access-token-after-authenticated-using-firebase-authentication
 */


/*
FirebaseUser.getIdToken(bool) : use to authenticate on backend server
is a JWT (JSON Web Token)     : https://jwt.io/


FirebaseUser.getUid()         : unique ID in the app


To set up on server: https://firebase.google.com/docs/auth/admin/
Token life-time = 1 hour

----------------------------------------------------------------------------------------------------

Verify ID Tokens :
If your Firebase client app communicates with a custom backend server, you might need to identify
the currently signed-in user on that server. To do so securely, after a successful sign-in, send
the user's ID token to your server using HTTPS. Then, on the server, verify the integrity and
authenticity of the ID token and retrieve the uid from it. You can use the uid transmitted in this
way to securely identify the currently signed-in user on your server.

----------------------------------------------------------------------------------------------------

Firebase ID token and FCM token are two completely different things: the first is an authentication
token, needed to authenticate a request on your backend server, the latter is to identify uniquely
an instance of an app installation, to know to whom send the proper message. Consider also the
Firebase user authentication ID - which is thread safe and unique id for a certain user account.

----------------------------------------------------------------------------------------------------

Firebase ID tokens : Created by Firebase when a user signs in to a Firebase app.

These tokens are signed JWTs that securely identify a user in a Firebase project.
These tokens contain basic profile information for a user, including the user's ID string,
which is unique to the Firebase project. Because the integrity of ID tokens can be verified,
you can send them to a backend server to identify the currently signed-in user.

----------------------------------------------------------------------------------------------------

Identity provider tokens : Created by federated identity providers, such as Google and Facebook.

These tokens can have different formats, but are often OAuth 2.0 access tokens.
Firebase apps use these tokens to verify that users have successfully authenticated with the
identity provider, and then convert them into credentials usable by Firebase services.
 */


/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class AuthUIActivity extends BaseActivity implements View.OnClickListener {
    // TODO: Implement "EmailPasswordAuth" and "GoogleAuth" as INTERFACES ?

    private static final String TAG = "GoogleActivity"; // TODO: Remove once tests are done
    private static final int RC_SIGN_IN = 9001; // arbitrary number

    public static FirebaseAuth mAuth;
    public static FirebaseAuth.AuthStateListener mAuthListener;

    // Variables globales utilisées pour la sélection de méthode d'authentification
    private static boolean hasSelectedAuthMethod = false;
    private static AuthMethod authMethod;
    private enum AuthMethod { GOOGLE, EMAIL }

    // Pour Google mode
    private GoogleSignInClient mGoogleSignInClient;
    // Pour User/Password mode
    private AutoCompleteTextView mEmailField;
    private EditText mPasswordField;
    private Button verify_email_btn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase);


        // Auth Mode Selectors listeners
        findViewById(R.id.email_selector).setOnClickListener(this);
        findViewById(R.id.google_selector).setOnClickListener(this);
        findViewById(R.id.back_to_mode_selection).setOnClickListener(this);

        // Email/Password buttons listeners
        findViewById(R.id.email_sign_in_button).setOnClickListener(this);
        findViewById(R.id.email_register_button).setOnClickListener(this);
        findViewById(R.id.email_sign_out_button).setOnClickListener(this);
        verify_email_btn = findViewById(R.id.email_verify_button);
        verify_email_btn.setOnClickListener(this);
        // Input fields
        mEmailField = findViewById(R.id.email);
        mPasswordField = findViewById(R.id.password);

        // Set up"Google Sign In" buttons listeners
        SignInButton signInButton = findViewById(R.id.google_sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setOnClickListener(this);
        findViewById(R.id.google_sign_out_button).setOnClickListener(this);
        findViewById(R.id.google_disconnect_button).setOnClickListener(this); // TODO: remove this option ?
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) // TODO: absent from tutorial.. ?
                .requestEmail()
                .build();
        // TODO? : Its important that you request for token Id
        // The string passed to requestIdToken, default_web_client_id,
        // can be obtained from credentials page (https://console.developers.google.com/apis/credentials).
        // There mentioned Web application type client ID is this string.

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    /**
     * Utilisé pour s'assurer de bien initialiser la connection avec Firebase.
     * @return l'utilisateur (null si pas authentifié!)
     */
    public static FirebaseUser setUpFirebaseAuth() {

        mAuth = FirebaseAuth.getInstance();

        /*
            @Override
            public void onStop() {
                super.onStop();
                if (mAuthListener != null) {
                    mAuth.removeAuthStateListener(mAuthListener);
                }
            }
         */ // TODO: Necessary? If so, add "mAuth.removeAuthListener()" in "onStop"
        if(mAuthListener == null) {
            mAuthListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (user != null) { // User is signed in
                        Log.w("AuthUI Activity", "onAuthStateChanged:signed_in:uid=" + user.getUid());
                    } else { // User is signed out
                        Log.w("AuthUI Activity", "onAuthStateChanged:signed_out");
                    }
                }
            };
            mAuth.addAuthStateListener(mAuthListener);
        }

        return mAuth.getCurrentUser(); // retourne "null" si usager pas authentifié
    }

    /**
     * Vérifie si l'utilisateur était déjà connecté ou non.
     */
    @Override
    public void onStart() {
        super.onStart();
        // Initialisation de l'authentification
        updateUI(setUpFirebaseAuth());
    }

    /**
     * Contrôleur de tous les boutons.
     *
     * @param v View
     */
    @Override
    public void onClick(View v) {
        int i = v.getId();

        // Google
        if        (i == R.id.google_sign_in_button) {
            signInGoogle();
        } else if (i == R.id.google_sign_out_button) {
            hasSelectedAuthMethod = false;
            signOutGoogle();
        } else if (i == R.id.google_disconnect_button) {
            hasSelectedAuthMethod = false;
            revokeAccessGoogle();

        // Email and Password
        } else if (i == R.id.email_verify_button) {
            sendEmailVerification();
        } else if (i == R.id.email_register_button) {
            createAccount(mEmailField.getText().toString(), mPasswordField.getText().toString());
        } else if (i == R.id.email_sign_in_button) {
            signInEmail(mEmailField.getText().toString(), mPasswordField.getText().toString());
        } else if (i == R.id.email_sign_out_button) {
            hasSelectedAuthMethod = false;
            signOutGoogle(); // TODO: Test if necessary to separate

        // Auth Method Selectors
        } else if (i == R.id.google_selector) {
            authMethod = AuthMethod.GOOGLE;
            hasSelectedAuthMethod = true;
            updateUI(mAuth.getCurrentUser());
        } else if (i == R.id.email_selector) {
            authMethod = AuthMethod.EMAIL;
            hasSelectedAuthMethod = true;
            updateUI(mAuth.getCurrentUser());
        } else if (i == R.id.back_to_mode_selection) {
            authMethod = null;
            hasSelectedAuthMethod = false;
            updateUI(mAuth.getCurrentUser());
        }
    }

    /**
     * Pour updater le UI en correspondance avec les réponses d'actions.
     *
     * @param user le user
     */
    private void updateUI(FirebaseUser user) {
        hideProgressDialog();

        Log.w("Firebase Activity", "updateUI called");

        // Pour afficher le bon mode
        findViewById(R.id.Selected_Method).setVisibility(View.GONE);
        findViewById(R.id.Mode_Selection).setVisibility(View.GONE);
        findViewById(R.id.back_to_mode_selection).setVisibility(View.GONE);


        if(user != null) { // IS signed in
            hasSelectedAuthMethod = true;
            findViewById(R.id.Selected_Method).setVisibility(View.VISIBLE);
            findViewById(R.id.Email_Method).setVisibility(View.GONE);
            findViewById(R.id.Google_Method).setVisibility(View.GONE);
            findViewById(R.id.back_to_mode_selection).setVisibility(View.GONE);

            /*
            TODO: Intégrer la synchronisation des MonitoredZones entre la mémoire interne et le backend
             */

            // Pour obtenir le ProviderID (type de l'authenticateur fédérale)
            switch(user.getProviders().get(0)) {
                case "google.com":
                    authMethod = AuthMethod.GOOGLE;
                    break;
                case "password":
                    authMethod = AuthMethod.EMAIL;
                    break;
                default:
                    Toast.makeText(this, "unknown", Toast.LENGTH_SHORT).show();
                    break;
            }


        //  [ TODO: Ce sont des tests pour les Tokens. Devraient être enlevés éventuellement. ]
            String tokenTest1 = user.getUid();
            Toast.makeText(this, "token1: " + tokenTest1, Toast.LENGTH_SHORT).show();
            Log.w("Token test", "token1: " + tokenTest1);
            user.getIdToken(false) // boolean = refresh token
                    .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                        public void onComplete(@NonNull Task<GetTokenResult> task) {
                            if (task.isSuccessful()) {
                                String idToken = task.getResult().getToken();
                                Log.w("Token test", "token2: " + idToken);
                                // Send token to your backend via HTTPS
                                // ...
                            } else {
                                // Handle error -> task.getException();
                                Log.w("Token test", "token failed");
                                task.getException().printStackTrace();
                            }
                        }
                    });
        // [ FIN DU TEST ]


            // Google
            if(authMethod == AuthMethod.GOOGLE) {
                findViewById(R.id.google_sign_in_button).setVisibility(View.GONE);
                findViewById(R.id.google_sign_out_fields).setVisibility(View.VISIBLE);
                findViewById(R.id.Google_Method).setVisibility(View.VISIBLE);

                // Show user info
                ((TextView) findViewById(R.id.google_DisplayName)).setText(user.getDisplayName());
                ((TextView) findViewById(R.id.google_Email)).setText(user.getEmail());
                Picasso.get().load(user.getPhotoUrl())
                        .placeholder(R.drawable.test) // TODO: Design a 'Placeholder' image 250x250
                        .resize(250, 250)
                        .centerCrop()
                        .into((ImageView) findViewById(R.id.google_Photo));
            }

            // Email/password
            if(authMethod == AuthMethod.EMAIL) {
                findViewById(R.id.google_sign_out_fields).setVisibility(View.GONE);
                findViewById(R.id.email_not_signed_in_fields).setVisibility(View.GONE);
                findViewById(R.id.email_not_signed_in_buttons).setVisibility(View.GONE);
                findViewById(R.id.email_sign_out_fields).setVisibility(View.VISIBLE);
                findViewById(R.id.Email_Method).setVisibility(View.VISIBLE);

                // Show email
                ((TextView) findViewById(R.id.email_show)).setText(user.getEmail());

                // Email verificator
                if(user.isEmailVerified()) {
                    findViewById(R.id.email_verify_button).setVisibility(View.GONE);
                } else {
                    Snackbar.make(findViewById(R.id.auth_logo_acclimate),
                            "You have not yet been verified by email!", Snackbar.LENGTH_SHORT).show();
                    findViewById(R.id.email_verify_button).setVisibility(View.VISIBLE);
                }
            }


        } else { // NOT signed in

            if(hasSelectedAuthMethod) { // Mode sélectionné
                findViewById(R.id.Selected_Method).setVisibility(View.VISIBLE);
                findViewById(R.id.back_to_mode_selection).setVisibility(View.VISIBLE);
                findViewById(R.id.Google_Method).setVisibility(View.GONE);
                findViewById(R.id.Email_Method).setVisibility(View.GONE);

                switch(authMethod) {
                    case EMAIL:
                        findViewById(R.id.Email_Method).setVisibility(View.VISIBLE);
                        findViewById(R.id.email_not_signed_in_buttons).setVisibility(View.VISIBLE);
                        findViewById(R.id.email_not_signed_in_fields).setVisibility(View.VISIBLE);
                        findViewById(R.id.email_sign_out_fields).setVisibility(View.GONE);
                        break;

                    case GOOGLE:
                        findViewById(R.id.Google_Method).setVisibility(View.VISIBLE);
                        findViewById(R.id.google_sign_in_button).setVisibility(View.VISIBLE);
                        findViewById(R.id.google_sign_out_fields).setVisibility(View.GONE);
                        break;

                    default:
                        break;
                }


            } else { // Pas encore sélectionné son AuthMode
                findViewById(R.id.Mode_Selection).setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * TODO: Figure out if this is necessarily related to Google.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach a listener
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                updateUI(null);
            }
        }
    }


    /*
    GOOGLE AUTHENTIFICATION !!!
     */

    /**
     * Called to begin the Google Authentification.
     * Can be used to adapt the UI based on the response.
     *
     * @param acct the signed in account
     */
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        showProgressDialog();

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            updateUI(mAuth.getCurrentUser());
                        } else {
                            // If sign in fails, display a message to the user.
                            Snackbar.make(findViewById(R.id.auth_logo_acclimate),
                                    "Authentication Failed.",
                                    Snackbar.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        hideProgressDialog();
                    }
                });
    }

    private void signInGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signOutGoogle() {
        mAuth.signOut();

        // Google sign out
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateUI(null);
                    }
                });
    }

    /**
     * Disconnect.
     */
    private void revokeAccessGoogle() {
        mAuth.signOut();

        // Google revoke access
        mGoogleSignInClient.revokeAccess().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateUI(null);
                    }
                });
    }


    /*
    USER/PASSWORD AUTH !!! (No google)
     */

    /**
     * Pour créer un compte via la méthode Email/Password
     *
     * @param email email
     * @param password password
     */
    private void createAccount(String email, String password) {
        if (!validateForm()) return;

        showProgressDialog();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            user.sendEmailVerification(); // Send verification email
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Snackbar.make(findViewById(R.id.auth_logo_acclimate),
                                    "Authentication Failed.",
                                    Snackbar.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        hideProgressDialog();
                    }
                });
    }

    /**
     * Pour Sign In avec la méthode "Email/Password"
     *
     * @param email email
     * @param password password
     */
    private void signInEmail(String email, String password) {
        if (!validateForm()) return;

        showProgressDialog();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            updateUI(mAuth.getCurrentUser());
                        } else {
                            // If sign in fails, display a message to the user.
                            Snackbar.make(findViewById(R.id.auth_logo_acclimate),
                                    "Authentication Failed.",
                                    Snackbar.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        hideProgressDialog();
                    }
                });
    }

    /**
     * Pour valider l'entrée de User/Passw.
     *
     * @return booléen désignant si la validation est correcte
     */
    private boolean validateForm() {
        boolean valid = true;

        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Required.");
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("Required.");
            valid = false;
        } else {
            mPasswordField.setError(null);
        }

        return valid;
    }


    /**
     * Pour envoyer le email de vérification
     */
    private void sendEmailVerification() {
        verify_email_btn.setEnabled(false); // Disable button

        // Send verification email
        final FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        verify_email_btn.setEnabled(true); // Re-enable button

                        if (task.isSuccessful()) {
                            Snackbar.make(findViewById(R.id.auth_logo_acclimate),
                                    "Verification email sent to " + user.getEmail(),
                                    Snackbar.LENGTH_SHORT).show();
                        } else {
                            Snackbar.make(findViewById(R.id.auth_logo_acclimate),
                                    "Failed to send verification email.",
                                    Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}