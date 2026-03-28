package auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.cookfeed.R;
import com.example.cookfeed.databinding.ActivityRegisterBinding;
import com.example.cookfeed.main.MainActivity;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private FirebaseAuthHelper authHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        authHelper = new FirebaseAuthHelper();

        binding.btnRegister.setOnClickListener(v -> attemptRegister());

        binding.tvHaveAccount.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void attemptRegister() {
        String email       = binding.etEmail.getText().toString().trim();
        String username    = binding.etUsername.getText().toString().trim();
        String displayName = binding.etDisplayName.getText().toString().trim();
        String password    = binding.etPassword.getText().toString().trim();
        String confirmPw   = binding.etConfirmPassword.getText().toString().trim();

        boolean valid = true;

        // Email
        if (TextUtils.isEmpty(email) || !authHelper.isValidEmail(email)) {
            binding.tilEmail.setError("Please enter a valid email address");
            valid = false;
        } else {
            binding.tilEmail.setError(null);
        }

        // Username
        if (TextUtils.isEmpty(username) || !authHelper.isValidUsername(username)) {
            binding.tilUsername.setError("Username must be 3–20 characters (letters, numbers, underscores)");
            valid = false;
        } else {
            binding.tilUsername.setError(null);
        }

        // Password
        if (!authHelper.isValidPassword(password)) {
            binding.tilPassword.setError("Password must be 8–16 characters with at least one letter and one number");
            valid = false;
        } else {
            binding.tilPassword.setError(null);
        }

        // Confirm password
        if (!password.equals(confirmPw)) {
            binding.tilConfirmPassword.setError("Passwords do not match");
            valid = false;
        } else {
            binding.tilConfirmPassword.setError(null);
        }

        if (!valid) return;

        setLoading(true);

        authHelper.register(email, username, displayName, password, new FirebaseAuthHelper.AuthCallback() {
            @Override
            public void onSuccess() {
                setLoading(false);
                Toast.makeText(RegisterActivity.this, "Account created! Welcome to CookFeed 🍳", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(String errorMessage) {
                setLoading(false);
                Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setLoading(boolean loading) {
        binding.btnRegister.setEnabled(!loading);
        binding.progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        binding.btnRegister.setText(loading ? "" : getString(R.string.register));
    }
}
