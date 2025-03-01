package com.example.japaneseflash;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginFragment extends Fragment {

    private FirebaseAuth mAuth;
    private EditText emailInput, passwordInput;
    private Button loginButton, signupButton, forgotPasswordButton;
    private TextView signUpText;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the fragment layout (ensure you have a fragment_login.xml layout file)
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        mAuth = FirebaseAuth.getInstance();

        // Retrieve views from the layout
        TextInputLayout emailLayout = view.findViewById(R.id.email_input);
        emailInput = emailLayout.getEditText();

        TextInputLayout passwordLayout = view.findViewById(R.id.password_input);
        passwordInput = passwordLayout.getEditText();

        loginButton = view.findViewById(R.id.login_button);
        signupButton = view.findViewById(R.id.signup_button);
        signUpText = view.findViewById(R.id.signup_text);
        forgotPasswordButton = view.findViewById(R.id.forgot_password_button);

        // Set click listeners
        loginButton.setOnClickListener(v -> loginUser());
        signupButton.setOnClickListener(v -> navigateToSignUp());
        forgotPasswordButton.setOnClickListener(v -> navigateToChangePassword());

        setupSignUpText();

        return view;
    }

    private void setupSignUpText() {
        String text = signUpText.getText().toString();
        SpannableString spannableString = new SpannableString(text);

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                navigateToSignUp();
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.parseColor("#FFA500"));
                ds.setUnderlineText(false);
            }
        };

        int start = text.indexOf("Sign Up!");
        if (start != -1) {
            int end = start + "Sign Up!".length();
            spannableString.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        signUpText.setText(spannableString);
        signUpText.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void loginUser() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            emailInput.setError("Email is required");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            passwordInput.setError("Password is required");
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(getContext(), "Login successful", Toast.LENGTH_SHORT).show();
                        navigateToHome();
                    } else {
                        Toast.makeText(getContext(), "Login failed: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void navigateToSignUp() {
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new SignUpFragment())
                .addToBackStack(null)
                .commit();
    }

    private void navigateToHome() {
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new HomeFragment())
                .commit();
    }

    private void navigateToChangePassword() {
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new ChangePasswordFragment())
                .commit();
    }

}
