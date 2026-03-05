package com.CSI3370.recipedock

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

private enum class Screen { LOGIN, SIGNUP, FORGOT }

@Composable
fun AppRoot() {
    var screen by remember { mutableStateOf(Screen.LOGIN) }

    when (screen) {
        Screen.LOGIN -> LoginScreen(
            onGoSignup = { screen = Screen.SIGNUP },
            onGoForgot = { screen = Screen.FORGOT },
            onLoggedIn = {
                // TODO: later, navigate to your Home/Feed screen
            }
        )

        Screen.SIGNUP -> SignupScreen(
            onGoLogin = { screen = Screen.LOGIN }
        )

        Screen.FORGOT -> ForgotPasswordScreen(
            onGoLogin = { screen = Screen.LOGIN }
        )
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