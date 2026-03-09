package com.example.mobilecomputinghw1

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.sqrt

@Composable
fun SensorScreen(navController: NavHostController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val sensorManager = remember {
        context.getSystemService(android.content.Context.SENSOR_SERVICE) as SensorManager
    }

    val db = remember { ShakeDatabase.getDatabase(context) }
    val shakeEvents by db.shakeDao().getAllEvents().collectAsState(initial = emptyList())

    var x by remember { mutableFloatStateOf(0f) }
    var y by remember { mutableFloatStateOf(0f) }
    var z by remember { mutableFloatStateOf(0f) }
    var magnitude by remember { mutableFloatStateOf(0f) }
    var lastShakeTime by remember { mutableLongStateOf(0L) }

    DisposableEffect(Unit) {
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                event?.let {
                    x = it.values[0]
                    y = it.values[1]
                    z = it.values[2]
                    magnitude = sqrt(x * x + y * y + z * z)

                    val now = System.currentTimeMillis()
                    if (magnitude > 12f && now - lastShakeTime > 1000) {
                        lastShakeTime = now
                        scope.launch {
                            db.shakeDao().insert(ShakeEvent())
                        }
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorManager.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_UI)

        onDispose {
            sensorManager.unregisterListener(listener)
        }
    }

    val dateFormat = remember { SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.primary
        ) {
            Text(
                text = "Sensorinäkymä",
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                color = MaterialTheme.colorScheme.onPrimary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Kiihtyvyysanturi (Accelerometer)", fontWeight = FontWeight.Bold, fontSize = 18.sp)

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("X: ${"%.2f".format(x)} m/s²")
                Text("Y: ${"%.2f".format(y)} m/s²")
                Text("Z: ${"%.2f".format(z)} m/s²")
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Kokonaiskiihtyvyys: ${"%.2f".format(magnitude)} m/s²",
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = if (shakeEvents.isNotEmpty())
                    MaterialTheme.colorScheme.tertiaryContainer
                else
                    MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Ravistuksia havaittu:", fontSize = 16.sp)
                Text("${shakeEvents.size}", fontSize = 32.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            "Ravista puhelinta! Kun sovellus on taustalla,\nravistus lähettää notifikaation.",
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text("Ravistelut:", fontWeight = FontWeight.Bold, fontSize = 16.sp)

        Spacer(modifier = Modifier.height(4.dp))

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            items(shakeEvents) { event ->
                Column(modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp)) {
                    Text(
                        text = dateFormat.format(Date(event.timestamp)),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                HorizontalDivider()
            }
            if (shakeEvents.isEmpty()) {
                item {
                    Text(
                        text = "Ei vielä ravisteluja",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Takaisin")
        }
    }
}
