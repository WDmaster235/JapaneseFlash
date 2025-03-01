package com.example.japaneseflash;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignUpFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private EditText emailInput, passwordInput, confirmPasswordInput;
    private Button loginButton, signUpButton;

    public SignUpFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the fragment layout
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize UI elements
        TextInputLayout emailLayout = view.findViewById(R.id.email_input);
        emailInput = emailLayout.getEditText();

        TextInputLayout passwordLayout = view.findViewById(R.id.password_input);
        passwordInput = passwordLayout.getEditText();

        TextInputLayout confirmPasswordLayout = view.findViewById(R.id.confirm_password_input);
        confirmPasswordInput = confirmPasswordLayout.getEditText();

        signUpButton = view.findViewById(R.id.signup_button);

        loginButton = view.findViewById(R.id.login_button);

        loginButton.setOnClickListener(v -> navigateToLogin());
        // Set up button listener
        signUpButton.setOnClickListener(v -> signUpUser());

        return view;
    }

    private void signUpUser() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String confirmPassword = confirmPasswordInput.getText().toString().trim();

        // Validate input
        if (TextUtils.isEmpty(email)) {
            emailInput.setError("Email is required");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            passwordInput.setError("Password is required");
            return;
        }
        if (!password.equals(confirmPassword)) {
            confirmPasswordInput.setError("Passwords do not match");
            return;
        }

        // Create a new user in Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Get the newly created user's ID
                        String userId = mAuth.getCurrentUser().getUid();

                        // Create a Firestore document for the new user
                        User user = new User(userId, email, "default_value");
                        db.collection("users").document(userId)
                                .set(user)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(getContext(), "Sign up successful!", Toast.LENGTH_SHORT).show();
                                    // Navigate to the LoginFragment
                                    getActivity().getSupportFragmentManager().beginTransaction()
                                            .replace(R.id.fragment_container, new LoginFragment())
                                            .commit();

                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(getContext(), "Failed to create user document.", Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        Toast.makeText(getContext(), "Sign up failed: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void navigateToLogin() {
        getActivity().getSupportFragmentManager().beginTransaction()
                                           .replace(R.id.fragment_container, new LoginFragment())
                                           .commit();
    }
}