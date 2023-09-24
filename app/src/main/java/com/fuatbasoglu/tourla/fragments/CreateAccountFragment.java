package com.fuatbasoglu.tourla.fragments;
//OFYF
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EdgeEffect;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.fuatbasoglu.tourla.FragmentReplacerActivity;
import com.fuatbasoglu.tourla.MainActivity;
import com.fuatbasoglu.tourla.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class CreateAccountFragment extends Fragment {

    private EditText nameEt, emailEt, passwordEt, confirmPasswordEt;
    private ProgressBar progressBar;
    private TextView loginTv;
    private Button signUpBtn;
    private FirebaseAuth auth;
    public static final String EMAIL_REGEX ="^(.+)@(.+)$";
    public CreateAccountFragment() {

        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init(view);

        auth = FirebaseAuth.getInstance();


        clickListener();

    }
    private void init(View view){


        nameEt= view.findViewById(R.id.nameEt);
        emailEt= view.findViewById(R.id.emailEt);
        passwordEt= view.findViewById(R.id.passwordEt);
        confirmPasswordEt= view.findViewById(R.id.confirmPassEt);
        loginTv= view.findViewById(R.id.loginTv);
        signUpBtn= view.findViewById(R.id.signUpBtn);
        progressBar = view.findViewById(R.id.progressBar);

        auth = FirebaseAuth.getInstance();

}
    private void clickListener(){
        loginTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((FragmentReplacerActivity) getActivity()).setFragment(new LoginFragment());

            }
        });

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameEt.getText().toString();
                String email = emailEt.getText().toString();
                String password = passwordEt.getText().toString();
                String confirmPassword = confirmPasswordEt.getText().toString();

                if (name.isEmpty() || name.equals("")){
                    nameEt.setError("Please input valid name");
                    return;
                }
                if (email.isEmpty() || !email.matches(EMAIL_REGEX)) {
                    nameEt.setError("Please input valid email");
                    return;
                }
                if (!password.equals(confirmPassword) ){
                    nameEt.setError("Password not match");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);


                createAccount(name, email, password);


            }
        });

    }

    private void createAccount(String name, String email, String password){

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()){

                            FirebaseUser user = auth.getCurrentUser();
                            user.sendEmailVerification()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                if (task.isSuccessful()){
                                                    Toast.makeText(getContext(),"Email verification link send", Toast.LENGTH_SHORT).show();

                                                }


                                                }
                                            });
                            uploadUser(user,name,email);

                        }else {
                            String exception = task.getException().getMessage();
                            Toast.makeText(getContext(), "Error : " +exception, Toast.LENGTH_SHORT).show();

                        }

                    }
                });

    }
    private void uploadUser(FirebaseUser user, String name, String email){
        Map<String, Object> map = new HashMap<>();

        map.put("name", name);
        map.put("email", email);
        map.put("profileImaeg"," ");
        map.put("uid", user.getUid());


        FirebaseFirestore.getInstance().collection("Users").document(user.getUid())
                .set(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()){
                            assert getActivity() != null;

                            progressBar.setVisibility(View.GONE);

                            startActivity(new Intent(getContext().getApplicationContext(), MainActivity.class));

                            getActivity().finish();
                        }else{
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getContext(), "Error : " +task.getException().getMessage()
                                    , Toast.LENGTH_SHORT).show();
                        }

                    }
                });

    }

}