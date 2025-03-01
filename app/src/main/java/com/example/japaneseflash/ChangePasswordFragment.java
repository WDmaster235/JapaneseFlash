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
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordFragment extends Fragment {

    private FirebaseAuth mAuth;
    private EditText currentPasswordInput, newPasswordInput, confirmNewPasswordInput;
    private Button changePasswordButton;

    public ChangePasswordFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_change_password, container, false);

        mAuth = FirebaseAuth.getInstance();

        // Retrieve the TextInputLayouts and get their EditTexts
        TextInputLayout currentPasswordLayout = view.findViewById(R.id.current_password_input);
        currentPasswordInput = currentPasswordLayout.getEditText();

        TextInputLayout newPasswordLayout = view.findViewById(R.id.new_password_input);
        newPasswordInput = newPasswordLayout.getEditText();

        TextInputLayout confirmNewPasswordLayout = view.findViewById(R.id.confirm_new_password_input);
        confirmNewPasswordInput = confirmNewPasswordLayout.getEditText();

        changePasswordButton = view.findViewById(R.id.change_password_button);
        changePasswordButton.setOnClickListener(v -> changePassword());

        return view;
    }

    private void changePassword() {
        String currentPassword = currentPasswordInput.getText().toString().trim();
        String newPassword = newPasswordInput.getText().toString().trim();
        String confirmNewPassword = confirmNewPasswordInput.getText().toString().trim();

        // Validate inputs
        if (TextUtils.isEmpty(currentPassword)) {
            currentPasswordInput.setError("Current password is required");
            return;
        }
        if (TextUtils.isEmpty(newPassword)) {
            newPasswordInput.setError("New password is required");
            return;
        }
        if (!newPassword.equals(confirmNewPassword)) {
            confirmNewPasswordInput.setError("Passwords do not match");
            return;
        }

        // Get the current user
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(getContext(), "No user logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        // Reauthenticate using the current password
        String email = user.getEmail();
        AuthCredential credential = EmailAuthProvider.getCredential(email, currentPassword);
        user.reauthenticate(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // If reauthentication is successful, update the password
                        user.updatePassword(newPassword)
                                .addOnCompleteListener(updateTask -> {
                                    if (updateTask.isSuccessful()) {
                                        Toast.makeText(getContext(), "Password changed successfully", Toast.LENGTH_SHORT).show();
                                        // Optionally, navigate back to the LoginFragment or another screen
                                        getActivity().getSupportFragmentManager().beginTransaction()
                                                .replace(R.id.fragment_container, new LoginFragment())
                                                .commit();
                                    } else {
                                        Toast.makeText(getContext(), "Password update failed: "
                                                + updateTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Toast.makeText(getContext(), "Reauthentication failed: "
                                + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
