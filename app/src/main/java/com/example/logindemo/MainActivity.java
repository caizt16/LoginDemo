package com.example.logindemo;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[a-zA-Z]).{4,}$");

    private static int LoginFailedCounter = 0;

    private static Map<String, String> userMap = new HashMap<>();

    private final class MainActivityComponent {
        public TextInputLayout EmailAddressLayout;
        public TextInputLayout PasswordLayout;
        public TextInputLayout PasswordConfirmLayout;
        public TextView SignUpText;
        public Button Login;
        public Button SignUp;

        public MainActivityComponent() {
            EmailAddressLayout = (TextInputLayout)findViewById(R.id.tilEmailAddress);
            PasswordLayout = (TextInputLayout)findViewById(R.id.tilPassword);
            PasswordConfirmLayout = (TextInputLayout)findViewById(R.id.tilPasswordConfirm);
            SignUpText = (TextView)findViewById(R.id.tvSignUp);
            Login = (Button)findViewById(R.id.btnLogin);
            SignUp = (Button)findViewById(R.id.btnSignUp);
        }
    }
    private MainActivityComponent Component;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userMap.put("admin@mail.com", "admin");

        Component = new MainActivityComponent();
        Component.Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearError();

                String userEmailAddress = Component.EmailAddressLayout.getEditText().getText().toString().trim();
                String userPassword = Component.PasswordLayout.getEditText().getText().toString();

                doLogin(userEmailAddress, userPassword);
                clearInput();
            }
        });

        Component.SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearError();

                String userEmailAddress = Component.EmailAddressLayout.getEditText().getText().toString().trim();
                String userPassword = Component.PasswordLayout.getEditText().getText().toString();
                String userPasswordConfirm = Component.PasswordConfirmLayout.getEditText().getText().toString();

                if (doSignUp(userEmailAddress, userPassword, userPasswordConfirm)) {
                    Component.PasswordConfirmLayout.setVisibility(View.INVISIBLE);
                    Component.SignUp.setVisibility(View.GONE);
                    Component.SignUpText.setVisibility(View.VISIBLE);
                }
                clearInput();
            }
        });

        Component.SignUpText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearError();

                Component.PasswordConfirmLayout.setVisibility(View.VISIBLE);
                Component.SignUp.setVisibility(View.VISIBLE);
                Component.SignUpText.setVisibility(View.GONE);

                clearInput();
            }
        });

    }

    private boolean doLogin(String userEmailAddress, String userPassword) {
        if (validateEmailAddress(userEmailAddress) && validatePassword(userPassword)) {
            if (userMap.get(userEmailAddress) != null &&
                userMap.get(userEmailAddress).equals(userPassword)) {
                showAlert("Login Successfully");
                return true;
            }
            showAlert("Wrong email or password");
        }
        LoginFailedCounter ++;
        checkLoginCounter();
        return false;
    }

    private boolean doSignUp(String userEmailAddress, String userPassword, String userPasswordConfirm) {
        if (validateEmailAddress(userEmailAddress) && validatePassword(userPassword)) {
            if (userMap.get(userEmailAddress) != null) {
                showAlert("User exists! Please login");
                return false;
            } else if (!validatePasswordConfirm(userPassword, userPasswordConfirm)) {
                return false;
            }
            userMap.put(userEmailAddress, userPassword);
            showAlert("Sign up successfully!");
            return true;
        }
        return false;
    }

    private boolean validateEmailAddress(String userEmailAddress) {
        if (userEmailAddress.isEmpty()) {
            Component.EmailAddressLayout.setError("Field can't be empty");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(userEmailAddress).matches()) {
            Component.EmailAddressLayout.setError("Please enter a valid email address");
            return false;
        } else {
            return true;
        }
    }

    private boolean validatePassword(String userPassword) {
        if (userPassword.isEmpty()) {
            Component.PasswordLayout.setError("Field can't be empty");
            return false;
        } else if (!PASSWORD_PATTERN.matcher(userPassword).matches()) {
            Component.PasswordLayout.setError("Password too weak, at least 4");
            return false;
        } else {
            return true;
        }
    }

    private boolean validatePasswordConfirm(String userPassword, String userPasswordConfirm) {
        if (userPasswordConfirm.isEmpty()) {
            Component.PasswordConfirmLayout.setError("Field can't be empty");
            return false;
        } else if (userPasswordConfirm.equals(userPassword)) {
            return true;
        } else {
            Component.PasswordConfirmLayout.setError("Doesn't match with password");
            return false;
        }
    }

    private void checkLoginCounter() {
        if (LoginFailedCounter >= 5) {
            Component.Login.setEnabled(false);
            Timer buttonTimer = new Timer();
            buttonTimer.schedule(new TimerTask() {

                @Override
                public void run() {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            Component.Login.setEnabled(true);
                        }
                    });
                }
            }, 5000);
            Toast.makeText(getApplicationContext(), "failed too many times, please wait for five seconds.", Toast.LENGTH_SHORT).show();
        } else if (LoginFailedCounter >= 1) {
            Component.SignUpText.setText(R.string.sign_up_hint);
        }
    }

    private void clearInput() {
        Component.PasswordLayout.getEditText().setText(null);
        Component.PasswordConfirmLayout.getEditText().setText(null);
    }

    private void clearError() {
        Component.EmailAddressLayout.setError(null);
        Component.PasswordLayout.setError(null);
        Component.PasswordConfirmLayout.setError(null);
    }

    private void showAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}