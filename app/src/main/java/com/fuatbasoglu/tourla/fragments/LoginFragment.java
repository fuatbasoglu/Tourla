package com.fuatbasoglu.tourla.fragments;

import static android.content.ContentValues.TAG;
import static com.fuatbasoglu.tourla.fragments.CreateAccountFragment.EMAIL_REGEX;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.fuatbasoglu.tourla.FragmentReplacerActivity;
import com.fuatbasoglu.tourla.MainActivity;
import com.fuatbasoglu.tourla.R;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.protobuf.Api;

import java.util.HashMap;
import java.util.Map;

public class LoginFragment extends Fragment {

    private EditText emailEt, passwordEt;
    private TextView signUpTv, forgotPasswordTv;
    private Button loginBtn, googleSignInBtn;
    private ProgressBar progressBar;
    private FirebaseAuth auth;

    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 1 ;
    private static final String TAG = "GOOGLE_SIGN_IN_TAG";
    private GoogleSignInClient googleSignInClient;
    private FirebaseAuth mAuth;

    private ActivityResultLauncher<Intent> mGoogleSignInLauncher;

// ...
// Initialize Firebase Auth

    public LoginFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);

        clickListener();

    }

    private void init(View view){

        emailEt = view.findViewById(R.id.emailEt);
        passwordEt = view.findViewById(R.id.passwordEt);
        loginBtn = view.findViewById(R.id.loginTv);
        googleSignInBtn = view.findViewById(R.id.googleSignInBtn);
        signUpTv = view.findViewById(R.id.signUpTv);
        forgotPasswordTv = view.findViewById(R.id.forgotTV);
        progressBar = view.findViewById(R.id.progressBar);



        auth = FirebaseAuth.getInstance();

        // Google oturum açma isteği yapılandırın
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("950162370084-c9d0pru0ofk2i7dr4274gc6intka84fb.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
        mAuth = FirebaseAuth.getInstance();

    }

    private void clickListener(){

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = emailEt.getText().toString();
                String password = passwordEt.getText().toString();

                if (email.isEmpty() || email.matches(EMAIL_REGEX)) {

                    emailEt.setError("Input valid email");
                    return;
                }
                if (password.isEmpty() || password.length() < 6) {

                    passwordEt.setError("Input 6 digit valid password");
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()) {

                                    FirebaseUser user = auth.getCurrentUser();
                                    if (!user.isEmailVerified()) {

                                        Toast.makeText(getContext(), "Please verify your email", Toast.LENGTH_SHORT).show();

                                    }

                                    sendUserToMainActivity();

                                } else {

                                    String exception = "Error : " + task.getException().getMessage();
                                    Toast.makeText(getContext(), exception, Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                }

                            }
                        });

            }
        });

        googleSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();


            }
        });
        signUpTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((FragmentReplacerActivity) getActivity()).setFragment(new CreateAccountFragment());
            }
        });

    }
    private void sendUserToMainActivity(){

        if (getActivity() == null)
           return;

        progressBar.setVisibility(View.GONE);

        startActivity(new Intent(getActivity().getApplicationContext(), MainActivity.class));
        getActivity().finish();

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // GoogleSignInApi.getSignInIntent (...) 'den başlatılan niyetten dönen sonuç
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google oturum açma başarılı oldu, Firebase ile kimlik doğrulayın
                GoogleSignInAccount account = task.getResult(ApiException.class);
                assert account != null;
            } catch (ApiException e) {
                // Google oturum açma başarısız oldu, UI'yi uygun şekilde güncelleyin
                e.printStackTrace();
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(),  new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Oturum açma başarılı oldu, UI'yi oturum açmış kullanıcının bilgileriyle güncelleyin
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // Oturum açma başarısız oldu, kullanıcıya bir mesaj gösterin
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }


    private void updateUI(FirebaseUser user){

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getActivity());

        Map<String, Object> map = new HashMap<>();

        map.put("name", account.getDisplayName());
        map.put("email", account.getEmail());
        map.put("profileImage",String.valueOf(account.getPhotoUrl()));
        map.put("uid", user.getUid());


        FirebaseFirestore.getInstance().collection("Users").document(user.getUid())
                .set(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()){
                            assert getActivity() != null;

                            progressBar.setVisibility(View.GONE);
                            sendUserToMainActivity();

                        }else{
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getContext(), "Error : " +task.getException().getMessage()
                                    , Toast.LENGTH_SHORT).show();
                        }

                    }
                });



    }



}