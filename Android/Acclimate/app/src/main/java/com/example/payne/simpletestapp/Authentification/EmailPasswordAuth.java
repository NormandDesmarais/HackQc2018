//package com.example.payne.simpletestapp.Authentification;
//
//import android.support.annotation.NonNull;
//import android.text.TextUtils;
//import android.util.Log;
//import android.widget.Toast;
//
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.auth.AuthResult;
//import com.google.firebase.auth.FirebaseUser;
//
//public interface EmailPasswordAuth {
//
//    /**
//     * Pour créer un compte via la méthode Email/Password
//     *
//     * @param email email
//     * @param password password
//     */
//    private void createAccount(String email, String password) {
//        Log.d(TAG, "createAccount:" + email);
//        if (!validateForm()) {
//            return;
//        }
//
//        showProgressDialog();
//
//        mAuth.createUserWithEmailAndPassword(email, password)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            // Sign in success, update UI with the signed-in user's information
//                            Log.d(TAG, "createUserWithEmail:success");
//                            FirebaseUser user = mAuth.getCurrentUser();
//                            // Send verification email
//                            user.sendEmailVerification();
//                            updateUI(user);
//                        } else {
//                            // If sign in fails, display a message to the user.
//                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
//                            Toast.makeText(AuthUIActivity.this, "Authentication failed.",
//                                    Toast.LENGTH_SHORT).show();
//                            updateUI(null);
//                        }
//
//                        hideProgressDialog();
//                    }
//                });
//    }
//
//    /**
//     * Pour Sign In avec la méthode "Email/Password"
//     *
//     * @param email email
//     * @param password password
//     */
//    private void signInEmail(String email, String password) {
//        Log.d(TAG, "signInEmail:" + email);
//        if (!validateForm()) {
//            return;
//        }
//
//        showProgressDialog();
//
//        mAuth.signInWithEmailAndPassword(email, password)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            // Sign in success, update UI with the signed-in user's information
//                            Log.d(TAG, "signInWithEmail:success");
//                            FirebaseUser user = mAuth.getCurrentUser();
//                            updateUI(user);
//                        } else {
//                            // If sign in fails, display a message to the user.
//                            Log.w(TAG, "signInWithEmail:failure", task.getException());
//                            Toast.makeText(AuthUIActivity.this, "Authentication failed.",
//                                    Toast.LENGTH_SHORT).show();
//                            updateUI(null);
//                        }
//
//                        hideProgressDialog();
//                    }
//                });
//    }
//
//    /**
//     * Pour valider l'entrée de User/Passw.
//     *
//     * @return booléen désignant si la validation est correcte
//     */
//    private boolean validateForm() {
//        boolean valid = true;
//
//        String email = mEmailField.getText().toString();
//        if (TextUtils.isEmpty(email)) {
//            mEmailField.setError("Required.");
//            valid = false;
//        } else {
//            mEmailField.setError(null);
//        }
//
//        String password = mPasswordField.getText().toString();
//        if (TextUtils.isEmpty(password)) {
//            mPasswordField.setError("Required.");
//            valid = false;
//        } else {
//            mPasswordField.setError(null);
//        }
//
//        return valid;
//    }
//
//
//    /**
//     * Pour envoyer le email de vérification
//     */
//    private void sendEmailVerification() {
//        // Disable button
//        verify_email_btn.setEnabled(false);
//
//        // Send verification email
//        final FirebaseUser user = mAuth.getCurrentUser();
//        user.sendEmailVerification()
//                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        // Re-enable button
//                        verify_email_btn.setEnabled(true);
//
//                        if (task.isSuccessful()) {
//                            Toast.makeText(AuthUIActivity.this,
//                                    "Verification email sent to " + user.getEmail(),
//                                    Toast.LENGTH_SHORT).show();
//                        } else {
//                            Log.e(TAG, "sendEmailVerification", task.getException());
//                            Toast.makeText(AuthUIActivity.this,
//                                    "Failed to send verification email.",
//                                    Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//    }
//}
