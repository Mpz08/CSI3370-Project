# CookFeed – Android App

A cooking social media app built with Java + Firebase for Android Studio.

---

## Phase 1: Auth Screens (this drop)

### Files included
```
app/
├── build.gradle                          ← Firebase + library dependencies
├── src/main/
│   ├── AndroidManifest.xml
│   ├── java/com/example/cookfeed/
│   │   ├── SplashActivity.java           ← Entry point, routes to Login or Main
│   │   ├── auth/
│   │   │   ├── FirebaseAuthHelper.java   ← ALL auth logic (register, login, reset)
│   │   │   ├── LoginActivity.java
│   │   │   ├── RegisterActivity.java
│   │   │   └── ForgotPasswordActivity.java
│   │   └── main/
│   │       └── MainActivity.java         ← Stub, full tabs come in Phase 2
│   └── res/
│       ├── layout/
│       │   ├── activity_splash.xml
│       │   ├── activity_login.xml
│       │   ├── activity_register.xml
│       │   ├── activity_forgot_password.xml
│       │   └── activity_main.xml
│       └── values/
│           ├── strings.xml
│           ├── colors.xml
│           └── themes.xml
```

---

## How to Set Up Firebase

1. Go to https://console.firebase.google.com
2. Create a new project → name it **CookFeed**
3. Add an **Android app** with package name `com.example.cookfeed`
4. Download `google-services.json` and place it in the `app/` folder
5. Enable **Authentication** → Sign-in method → **Email/Password**
6. Enable **Firestore Database** → Start in test mode

---

## Firestore Data Structure

```
users/
  {uid}/
    uid, email, username, displayName,
    profilePicUrl, bio, followersCount,
    followingCount, createdAt

usernames/
  {username}/         ← lowercase, used to resolve username → uid at login
    uid
```

---

## Password Rules
- 8–16 characters
- At least one letter AND one number
- Validated client-side in `FirebaseAuthHelper.isValidPassword()`

## Login
- Accepts **email** OR **username**
- Username login: looks up `/usernames/{username}` → gets uid → fetches email → signs in

---

## Next: Phase 2 – Main App (3 tabs)
- Feed tab
- Trending tab  
- User/Profile tab
