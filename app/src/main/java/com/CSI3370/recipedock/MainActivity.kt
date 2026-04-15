package com.CSI3370.recipedock

import com.example.platemate.TrendingActivity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.CSI3370.recipedock.ui.theme.RecipeDockTheme
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import coil.compose.AsyncImage


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RecipeDockTheme {
                AppRoot()
            }
        }
    }
}

private enum class Screen { LOGIN, SIGNUP, FORGOT, PROFILE, TRENDING }

@Composable
fun AppRoot() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val activity = context as? android.app.Activity
    val startScreen = activity?.intent?.getStringExtra("start_screen")

    var screen by remember {
        mutableStateOf(
            when (startScreen) {
                "profile" -> Screen.PROFILE
                "trending" -> Screen.TRENDING
                else -> Screen.LOGIN
            }
        )
    }

    when (screen) {
        Screen.LOGIN -> LoginScreen(
            onGoSignup = { screen = Screen.SIGNUP },
            onGoForgot = { screen = Screen.FORGOT },
            onLoggedIn = { screen = Screen.TRENDING }
        )

        Screen.SIGNUP -> SignupScreen(
            onGoLogin = { screen = Screen.LOGIN }
        )

        Screen.FORGOT -> ForgotPasswordScreen(
            onGoLogin = { screen = Screen.LOGIN }
        )

        Screen.PROFILE -> ProfileScreen()

        Screen.TRENDING -> TrendingLauncher()
    }
}

@Composable
fun LoginScreen(
    onGoSignup: () -> Unit,
    onGoForgot: () -> Unit,
    onLoggedIn: () -> Unit
) {
    val repo = remember { LoginRepo() }

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Login", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = {
                repo.loginWithUsername(username, password) { result ->
                    status = result.msg
                    if (result.ok) onLoggedIn()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Login")
        }

        Spacer(Modifier.height(8.dp))

        TextButton(
            onClick = onGoForgot,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Forgot Password")
        }

        Spacer(Modifier.height(8.dp))

        Button(
            onClick = onGoSignup,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Sign Up")
        }

        Spacer(Modifier.height(12.dp))
        if (status.isNotBlank()) Text(status)
    }
}

@Composable
fun ForgotPasswordScreen(onGoLogin: () -> Unit) {
    val repo = remember { LoginRepo() }

    var email by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Reset Password", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = confirm,
            onValueChange = { confirm = it },
            label = { Text("Confirm Email") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = {
                repo.sendReset(email, confirm) { result ->
                    status = result.msg
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Send Reset Email")
        }

        Spacer(Modifier.height(8.dp))

        TextButton(
            onClick = onGoLogin,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back to Login")
        }

        Spacer(Modifier.height(12.dp))
        if (status.isNotBlank()) Text(status)
    }
}

@Composable
fun SignupScreen(onGoLogin: () -> Unit) {
    val repo = remember { AuthRepo() }

    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Sign Up", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = {
                repo.signUp(username, email, password) { result ->
                    status = result.msg
                    if (result.ok) onGoLogin()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Create Account")
        }

        Spacer(Modifier.height(8.dp))

        TextButton(
            onClick = onGoLogin,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back to Login")
        }

        Spacer(Modifier.height(12.dp))
        if (status.isNotBlank()) Text(status)
    }
}
@Composable
fun ProfileScreen() {
    val repo = remember { ProfileRepo() }
    val context = androidx.compose.ui.platform.LocalContext.current
    var displayName by remember { mutableStateOf("Loading...") }
    var bio by remember { mutableStateOf("") }
    var profileImageUrl by remember { mutableStateOf("") }

    var editingName by remember { mutableStateOf(false) }
    var editingBio by remember { mutableStateOf(false) }

    var newDisplayName by remember { mutableStateOf("") }
    var newBio by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("") }

    val imagePicker = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            repo.uploadProfileImage(uri) { result, imageUrl ->
                status = result.msg
                if (result.ok && imageUrl != null) {
                    profileImageUrl = imageUrl
                }

            }
        }
    }

    LaunchedEffect(Unit) {
        repo.loadProfile { profile ->
            displayName = profile.displayName
            bio = profile.bio
            profileImageUrl = profile.profileImageUrl
            newDisplayName = profile.displayName
            newBio = profile.bio
        }
    }

    LaunchedEffect(status) {
        if (status.isNotBlank()) {
            kotlinx.coroutines.delay(2000)
            status = ""
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Button(
            onClick = {
                context.startActivity(
                    android.content.Intent(
                        context,
                        TrendingActivity::class.java
                    )
                )
            }
        ) {
            Text("← Back")
        }

        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Card(
                modifier = Modifier
                    .size(90.dp),
                onClick = {
                    imagePicker.launch("image/*")
                }
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    if (profileImageUrl.isNotBlank()) {
                        coil.compose.AsyncImage(
                            model = profileImageUrl,
                            contentDescription = "Profile Picture",
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Text("Add\nPhoto")
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                if (editingName) {
                    OutlinedTextField(
                        value = newDisplayName,
                        onValueChange = { newDisplayName = it },
                        label = { Text("Display Name") },
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(onClick = {
                        repo.updateDisplayName(newDisplayName) { result ->
                            status = result.msg
                            if (result.ok) {
                                displayName = newDisplayName
                                editingName = false
                            }
                        }
                    }) {
                        Text("Save Name")
                    }
                } else {
                    Text(
                        text = displayName,
                        style = MaterialTheme.typography.headlineSmall
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    TextButton(onClick = {
                        editingName = true
                    }) {
                        Text("Edit Display Name")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (editingBio) {
                    OutlinedTextField(
                        value = newBio,
                        onValueChange = { newBio = it },
                        label = { Text("Bio") }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(onClick = {
                        repo.updateBio(newBio) { result ->
                            status = result.msg
                            if (result.ok) {
                                bio = newBio
                                editingBio = false
                            }
                        }
                    }) {
                        Text("Save Bio")
                    }
                } else {
                    Text(
                        text = if (bio.isBlank()) "Tap to add a bio" else bio,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    TextButton(onClick = {
                        editingBio = true
                    }) {
                        Text("Edit Bio")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (status.isNotBlank()) {
            Text(status)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Pinned",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Text("Pinned videos will go here")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Recent Posts",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(4) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = androidx.compose.ui.Alignment.Center
                    ) {
                        Text("Post ${it + 1}")
                    }
                }
            }
        }
    }
}
@Composable
fun TrendingLauncher() {
    val context = androidx.compose.ui.platform.LocalContext.current

    LaunchedEffect(Unit) {
        val intent = android.content.Intent(
            context,
            TrendingActivity::class.java
        )
        context.startActivity(intent)
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        Text("Opening feed...")
    }
}