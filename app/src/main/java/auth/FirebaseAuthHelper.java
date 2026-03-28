package auth;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Centralized helper for all Firebase Authentication operations.
 * Handles registration, login, password reset, and username lookups.
 */
public class FirebaseAuthHelper {

    // Password: 8–16 chars, at least one letter and one number
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*#?&]{8,16}$");

    private final FirebaseAuth auth;
    private final FirebaseFirestore db;

    public FirebaseAuthHelper() {
        auth = FirebaseAuth.getInstance();
        db   = FirebaseFirestore.getInstance();
    }

    // ─────────────────────────────────────────────
    // Validation helpers
    // ─────────────────────────────────────────────

    public boolean isValidPassword(String password) {
        return PASSWORD_PATTERN.matcher(password).matches();
    }

    public boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public boolean isValidUsername(String username) {
        // 3–20 chars, letters/numbers/underscores only
        return username != null && username.matches("^[a-zA-Z0-9_]{3,20}$");
    }

    // ─────────────────────────────────────────────
    // Register
    // ─────────────────────────────────────────────

    /**
     * Creates a new account with email + password, then saves user profile
     * data (username, displayName) to Firestore under /users/{uid}.
     */
    public void register(String email, String username, String displayName,
                         String password, AuthCallback callback) {

        // First check if username is already taken
        db.collection("usernames").document(username.toLowerCase())
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        callback.onFailure("Username is already taken.");
                        return;
                    }
                    // Username is free — create the auth account
                    auth.createUserWithEmailAndPassword(email, password)
                            .addOnSuccessListener(result -> {
                                FirebaseUser user = result.getUser();
                                if (user == null) {
                                    callback.onFailure("Registration failed. Please try again.");
                                    return;
                                }
                                // Save profile to Firestore
                                saveUserProfile(user.getUid(), email, username, displayName, callback);
                            })
                            .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    private void saveUserProfile(String uid, String email, String username,
                                  String displayName, AuthCallback callback) {
        Map<String, Object> user = new HashMap<>();
        user.put("uid",           uid);
        user.put("email",         email);
        user.put("username",      username.toLowerCase());
        user.put("displayName",   displayName.isEmpty() ? username : displayName);
        user.put("profilePicUrl", "");
        user.put("bio",           "");
        user.put("followersCount", 0);
        user.put("followingCount", 0);
        user.put("createdAt",     com.google.firebase.Timestamp.now());

        // Save the main user document
        db.collection("users").document(uid)
                .set(user)
                .addOnSuccessListener(unused -> {
                    // Also reserve the username so no one else can take it
                    Map<String, Object> usernameEntry = new HashMap<>();
                    usernameEntry.put("uid", uid);
                    db.collection("usernames")
                            .document(username.toLowerCase())
                            .set(usernameEntry)
                            .addOnSuccessListener(u -> callback.onSuccess())
                            .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    // ─────────────────────────────────────────────
    // Login  (supports email OR username)
    // ─────────────────────────────────────────────

    public void login(String identifier, String password, AuthCallback callback) {
        if (isValidEmail(identifier)) {
            // It looks like an email — log in directly
            signInWithEmail(identifier, password, callback);
        } else {
            // It's a username — look up the associated email first
            db.collection("usernames").document(identifier.toLowerCase())
                    .get()
                    .addOnSuccessListener(doc -> {
                        if (!doc.exists()) {
                            callback.onFailure("No account found with that username.");
                            return;
                        }
                        String uid = doc.getString("uid");
                        db.collection("users").document(uid)
                                .get()
                                .addOnSuccessListener(userDoc -> {
                                    String email = userDoc.getString("email");
                                    if (email == null) {
                                        callback.onFailure("Could not retrieve account info.");
                                        return;
                                    }
                                    signInWithEmail(email, password, callback);
                                })
                                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
                    })
                    .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
        }
    }

    private void signInWithEmail(String email, String password, AuthCallback callback) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(result -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure("Incorrect email/username or password."));
    }

    // ─────────────────────────────────────────────
    // Password Reset
    // ─────────────────────────────────────────────

    public void sendPasswordReset(String email, AuthCallback callback) {
        auth.sendPasswordResetEmail(email)
                .addOnSuccessListener(unused -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    // ─────────────────────────────────────────────
    // Sign Out
    // ─────────────────────────────────────────────

    public void signOut() {
        auth.signOut();
    }

    // ─────────────────────────────────────────────
    // Callback interface
    // ─────────────────────────────────────────────

    public interface AuthCallback {
        void onSuccess();
        void onFailure(String errorMessage);
    }
}
