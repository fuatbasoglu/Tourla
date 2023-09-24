package com.fuatbasoglu.tourla;

import static android.provider.Settings.System.getString;

import android.content.Context;
import android.content.Intent;

import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class GoogleSignInContract extends ActivityResultContract<Void, GoogleSignInAccount> {


    @Override
    public Intent createIntent(@NonNull Context context, Void input) {
        // Create an intent for the Google sign-in activity
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("950162370084-c9d0pru0ofk2i7dr4274gc6intka84fb.apps.googleusercontent.com")
                .requestEmail()
                .build();
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(context, gso);
        return ((GoogleSignInClient) mGoogleSignInClient).getSignInIntent();
    }

    @Override
    public GoogleSignInAccount parseResult(int resultCode, @Nullable Intent intent) {
        // Parse the result from the Google sign-in activity
        try {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(intent);
            return task.getResult(ApiException.class);
        } catch (ApiException e) {
            e.printStackTrace();
            return null;
        }
    }
}
