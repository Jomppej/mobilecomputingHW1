package com.example.mobilecomputinghw1

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.mobilecomputinghw1.ui.theme.MobilecomputingHW1Theme
import java.io.File

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        NotificationHelper.createNotificationChannel(this)

        setContent {
            MobilecomputingHW1Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var showSplash by remember { mutableStateOf(true) }
                    var permissionGranted by remember { mutableStateOf(false) }
                    var permissionChecked by remember { mutableStateOf(false) }
                    var proceedAnyway by remember { mutableStateOf(false) }

                    if (showSplash) {
                        AnimatedSplashScreen(onFinished = { showSplash = false })
                    } else if (!permissionChecked) {
                        PermissionScreen(
                            onPermissionGranted = {
                                permissionGranted = true
                                permissionChecked = true
                            },
                            onDenied = {
                                permissionChecked = true
                            },
                            onContinueAnyway = {
                                proceedAnyway = true
                            }
                        )
                    } else if (permissionGranted || proceedAnyway) {
                        MainNavigation()
                    } else {
                        PermissionScreen(
                            onPermissionGranted = { permissionGranted = true },
                            onDenied = {},
                            onContinueAnyway = { proceedAnyway = true },
                            initiallyDenied = true
                        )
                    }
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        val intent = Intent(this, ShakeSensorService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        val intent = Intent(this, ShakeSensorService::class.java)
        stopService(intent)
    }
}

fun copyImageToInternalStorage(context: Context, uri: Uri): String {
    val inputStream = context.contentResolver.openInputStream(uri)
    val fileName = "profile_image.jpg"
    val outputFile = File(context.filesDir, fileName)

    inputStream?.use { input ->
        outputFile.outputStream().use { output ->
            input.copyTo(output)
        }
    }

    return outputFile.absolutePath
}

fun saveProfile(context: Context, username: String, imagePath: String) {
    val prefs = context.getSharedPreferences("user_profile", Context.MODE_PRIVATE)
    prefs.edit()
        .putString("username", username)
        .putString("image_path", imagePath)
        .apply()
}

fun getUsername(context: Context): String {
    val prefs = context.getSharedPreferences("user_profile", Context.MODE_PRIVATE)
    return prefs.getString("username", "") ?: ""
}

fun getImagePath(context: Context): String {
    val prefs = context.getSharedPreferences("user_profile", Context.MODE_PRIVATE)
    return prefs.getString("image_path", "") ?: ""
}

@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current

    var username by remember { mutableStateOf(getUsername(context)) }
    var imagePath by remember { mutableStateOf(getImagePath(context)) }

    NavHost(navController = navController, startDestination = "main") {
        composable("main") {
            MainScreen(
                navController = navController,
                username = username,
                imagePath = imagePath,
                onUsernameChange = { newUsername ->
                    username = newUsername
                    saveProfile(context, username, imagePath)
                },
                onImagePicked = { newPath ->
                    imagePath = newPath
                    saveProfile(context, username, imagePath)
                }
            )
        }
        composable("sensor") {
            SensorScreen(navController = navController)
        }
    }
}

@Composable
fun MainScreen(
    navController: NavHostController,
    username: String,
    imagePath: String,
    onUsernameChange: (String) -> Unit,
    onImagePicked: (String) -> Unit
) {
    val context = LocalContext.current

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let {
            val savedPath = copyImageToInternalStorage(context, it)
            onImagePicked(savedPath)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (imagePath.isNotEmpty() && File(imagePath).exists()) {
            AsyncImage(
                model = File(imagePath),
                contentDescription = "Profile picture",
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
                contentScale = ContentScale.Crop
            )
        } else {
            Image(
                painter = painterResource(R.drawable.profile_picture),
                contentDescription = "Default profile picture",
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = username,
            onValueChange = onUsernameChange,
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                photoPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Pick photo")
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                navController.navigate("sensor") {
                    launchSingleTop = true
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Sensor & Notifications")
        }

    }
}

