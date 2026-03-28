package auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.cookfeed.databinding.ActivityLoginBinding;
import com.example.cookfeed.main.MainActivity;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private FirebaseAuthHelper authHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        authHelper = new FirebaseAuthHelper();

        // Login button
        binding.btnLogin.setOnClickListener(v -> attemptLogin());

        // Go to Register
        binding.tvCreateAccount.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));

        // Go to Forgot Password
        binding.tvForgotPassword.setOnClickListener(v ->
                startActivity(new Intent(this, ForgotPasswordActivity.class)));
    }

    private void attemptLogin() {
        String identifier = binding.etIdentifier.getText().toString().trim();
        String password   = binding.etPassword.getText().toString().trim();

        // Basic validation
        if (TextUtils.isEmpty(identifier)) {
            binding.tilIdentifier.setError("Please enter your email or username");
            return;
        } else {
            binding.tilIdentifier.setError(null);
        }

        if (TextUtils.isEmpty(password)) {
            binding.tilPassword.setError("Please enter your password");
            return;
        } else {
            binding.tilPassword.setError(null);
        }

        setLoading(true);

        authHelper.login(identifier, password, new FirebaseAuthHelper.AuthCallback() {
            @Override
            public void onSuccess() {
                setLoading(false);
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(String errorMessage) {
                setLoading(false);
                Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setLoading(boolean loading) {
        binding.btnLogin.setEnabled(!loading);
        binding.progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        binding.btnLogin.setText(loading ? "" : getString(com.example.cookfeed.R.string.login));
    }
}
