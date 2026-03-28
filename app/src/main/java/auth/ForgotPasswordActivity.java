package auth;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.cookfeed.databinding.ActivityForgotPasswordBinding;

public class ForgotPasswordActivity extends AppCompatActivity {

    private ActivityForgotPasswordBinding binding;
    private FirebaseAuthHelper authHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        authHelper = new FirebaseAuthHelper();

        binding.btnSendReset.setOnClickListener(v -> sendReset());

        binding.tvBackToLogin.setOnClickListener(v -> finish());
    }

    private void sendReset() {
        String email = binding.etEmail.getText().toString().trim();

        if (TextUtils.isEmpty(email) || !authHelper.isValidEmail(email)) {
            binding.tilEmail.setError("Please enter a valid email address");
            return;
        }
        binding.tilEmail.setError(null);

        setLoading(true);

        authHelper.sendPasswordReset(email, new FirebaseAuthHelper.AuthCallback() {
            @Override
            public void onSuccess() {
                setLoading(false);
                binding.tvConfirmation.setVisibility(View.VISIBLE);
                binding.btnSendReset.setEnabled(false);
                Toast.makeText(ForgotPasswordActivity.this,
                        "Reset email sent! Check your inbox.", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(String errorMessage) {
                setLoading(false);
                Toast.makeText(ForgotPasswordActivity.this, errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setLoading(boolean loading) {
        binding.btnSendReset.setEnabled(!loading);
        binding.progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
    }
}
