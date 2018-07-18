//package com.example.payne.simpletestapp.Authentification;
//
//import android.content.Intent;
//import android.support.annotation.NonNull;
//import android.support.design.widget.Snackbar;
//import android.util.Log;
//
//import com.example.payne.simpletestapp.R;
//import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.auth.AuthCredential;
//import com.google.firebase.auth.AuthResult;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.auth.GoogleAuthProvider;
//
//public interface GoogleAuth {
//
//    /**
//     * Called to begin the Google Authentification.
//     * Can be used to adapt the UI based on the response.
//     *
//     * The GoogleSignInAccount object contains information
//     * about the signed-in user, such as the user's name.
//     *
//     * You can also get the user's email address with getEmail, the user's Google ID
//     * (for client-side use) with getId, and an ID token for the user with getIdToken.
//     *
//     *   GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getActivity());
//     if (acct != null) {
//     String personName = acct.getDisplayName();
//     String personGivenName = acct.getGivenName();
//     String personFamilyName = acct.getFamilyName();
//     String personEmail = acct.getEmail();
//     String personId = acct.getId();
//     Uri personPhoto = acct.getPhotoUrl();
//     }
//     *
//     * @param acct the signed in account
//     */
//    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
//        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
//        showProgressDialog();
//
//        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
//        mAuth.signInWithCredential(credential)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            // Sign in success, update UI with the signed-in user's information
//                            Log.d(TAG, "signInWithCredential:success");
//                            FirebaseUser user = mAuth.getCurrentUser();
//                            updateUI(user);
//                        } else {
//                            // If sign in fails, display a message to the user.
//                            Log.w(TAG, "signInWithCredential:failure", task.getException());
//                            Snackbar.make(findViewById(R.id.auth_logo_acclimate), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
//                            updateUI(null);
//                        }
//
//                        hideProgressDialog();
//                    }
//                });
//    }
//
//    private void signInGoogle() {
//        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
//        startActivityForResult(signInIntent, RC_SIGN_IN);
//    }
//
//    private void signOutGoogle() {
//        // Firebase sign out
//        mAuth.signOut();
//
//        // Google sign out
//        mGoogleSignInClient.signOut().addOnCompleteListener(this,
//                new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        updateUI(null);
//                    }
//                });
//    }
//
//    /**
//     * Disconnect.
//     */
//    private void revokeAccessGoogle() {
//        // Firebase sign out
//        mAuth.signOut();
//
//        // Google revoke access
//        mGoogleSignInClient.revokeAccess().addOnCompleteListener(this,
//                new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        updateUI(null);
//                    }
//                });
//    }
//}
