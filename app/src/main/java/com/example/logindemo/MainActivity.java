package com.example.logindemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.telephony.ClosedSubscriberGroupInfo;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.util.Timer;
import java.util.TimerTask;

final class AdminAccount {
    public static String EmailAddress = "Admin@mail.com";
    public static String Password = "admin";
}

enum LoginState {
    AdminSuccess, UserSuccess, WrongEmailPassword
}

public class MainActivity extends AppCompatActivity {

    private int LoginFailedCounter = 0;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final MainActivityComponent Component = new MainActivityComponent();
        Component.Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userEmailAddress = Component.EmailAddressLayout.getEditText().getText().toString().trim();
                String userPassword = Component.PasswordLayout.getEditText().getText().toString();

                LoginState ValidatedState = validate(userEmailAddress, userPassword, Component);
                checkLoginCounter(Component);
                Component.EmailAddressLayout.setError(null);
                Component.PasswordLayout.setError(null);
                if (ValidatedState.equals(LoginState.WrongEmailPassword)) {
                    String text = "wrong" + userEmailAddress;
                    Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
                }
            }
        });

        Component.SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Component.PasswordConfirmLayout.setVisibility(View.INVISIBLE);

                Component.SignUp.setVisibility(View.GONE);

                Component.SignUpText.setVisibility(View.VISIBLE);
                Component.Login.setVisibility(View.VISIBLE);
            }
        });

        Component.SignUpText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Component.PasswordConfirmLayout.setVisibility(View.VISIBLE);
                Component.SignUp.setVisibility(View.VISIBLE);

                Component.SignUpText.setVisibility(View.GONE);
                Component.Login.setVisibility(View.GONE);
            }
        });

    }

    private LoginState validate(String userEmailAddress, String userPassword, final MainActivityComponent Component) {
        if (validateEmailAddress(userEmailAddress, Component) && userPassword.equals(AdminAccount.Password)) {
            return LoginState.AdminSuccess;
        } else {
            LoginFailedCounter ++;
            return LoginState.WrongEmailPassword;
        }
        // should not reach here;
        // return LoginState.InvalidState;
    }

    private boolean validateEmailAddress(String userEmailAddress, final MainActivityComponent Component) {
        if (userEmailAddress.isEmpty()) {
            Component.EmailAddressLayout.setError("Field can't be empty");
            return false;
        } else {
            return true;
        }
    }

    private void checkLoginCounter(final MainActivityComponent Component) {
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
}