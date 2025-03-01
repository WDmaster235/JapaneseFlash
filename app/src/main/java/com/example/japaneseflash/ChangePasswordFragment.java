package com.example.japaneseflash;

import android.text.TextUtils;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class ChangePasswordFragment extends Fragment {

    private FirebaseAuth mAuth;
    private EditText emailInput;
    private Button changePasswordButton;

    public ChangePasswordFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_password, container, false);
        mAuth = FirebaseAuth.getInstance();

        TextInputLayout emailLayout = view.findViewById(R.id.email_input);
        emailInput = emailLayout.getEditText();

        changePasswordButton = view.findViewById(R.id.change_password_button);
        changePasswordButton.setOnClickListener(v -> sendPasswordResetEmail());

        return view;
    }

    private void sendPasswordResetEmail() {
        String email = emailInput.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            emailInput.setError("Email is required");
            return;
        }

        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "Password reset email sent", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Failed to send reset email: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
