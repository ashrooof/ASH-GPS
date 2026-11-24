package com.ashrooof.ashgps

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Ink = androidx.compose.ui.graphics.Color(0xFF10151D)
private val Panel = androidx.compose.ui.graphics.Color(0xFF19212D)
private val Muted = androidx.compose.ui.graphics.Color(0xFF9AA8B8)
private val Accent = androidx.compose.ui.graphics.Color(0xFF79E0C0)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { AshGpsApp() }
    }
}

@Composable
fun AshGpsApp() {
    var latitude by remember { mutableStateOf("30.0444") }
    var longitude by remember { mutableStateOf("31.2357") }
    var speed by remember { mutableFloatStateOf(5f) }
    var running by remember { mutableStateOf(false) }
    var routeMode by remember { mutableStateOf(false) }

    MaterialTheme(colorScheme = darkColorScheme(
        background = Ink, surface = Panel, primary = Accent, onPrimary = Ink,
        onBackground = androidx.compose.ui.graphics.Color.White,
        onSurface = androidx.compose.ui.graphics.Color.White
    )) {
        Surface(Modifier.fillMaxSize(), color = Ink) {
            Column(
                Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(42.dp).background(Accent, RoundedCornerShape(13.dp)), Alignment.Center) {
                        Icon(Icons.Default.Navigation, null, tint = Ink)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text("ASH GPS", fontSize = 21.sp, fontWeight = FontWeight.Black, letterSpacing = 1.5.sp)
                        Text("LOCATION LAB  •  ANDROID", color = Muted, fontSize = 10.sp, letterSpacing = 1.4.sp)
                    }
                    Icon(Icons.Default.MoreVert, null, tint = Muted)
                }
                Row(Modifier.fillMaxWidth().background(Panel, RoundedCornerShape(14.dp)).padding(13.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Science, null, tint = Accent)
                    Spacer(Modifier.width(9.dp))
                    Column {
                        Text("DEVELOPER SIMULATION", color = Accent, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text("Preview coordinates inside this app only", color = Muted, fontSize = 11.sp)
                    }
                }
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("LOCATION PREVIEW", color = Muted, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
                    Box(Modifier.fillMaxWidth().height(210.dp).background(Panel, RoundedCornerShape(20.dp)).border(1.dp, androidx.compose.ui.graphics.Color(0xFF293545), RoundedCornerShape(20.dp))) {
                        Canvas(Modifier.fillMaxSize().padding(12.dp)) {
                            val step = 42f
                            for (x in 0..(size.width / step).toInt()) drawLine(androidx.compose.ui.graphics.Color(0xFF293545), Offset(x * step, 0f), Offset(x * step, size.height), 1f)
                            for (y in 0..(size.height / step).toInt()) drawLine(androidx.compose.ui.graphics.Color(0xFF293545), Offset(0f, y * step), Offset(size.width, y * step), 1f)
                            val route = Path().apply {
                                moveTo(size.width * .18f, size.height * .72f)
                                cubicTo(size.width * .30f, size.height * .62f, size.width * .42f, size.height * .76f, size.width * .52f, size.height * .48f)
                                cubicTo(size.width * .63f, size.height * .20f, size.width * .76f, size.height * .43f, size.width * .84f, size.height * .22f)
                            }
                            drawPath(route, Accent.copy(alpha = .75f), style = Stroke(width = 4f, cap = StrokeCap.Round))
                            drawCircle(Accent, 8f, Offset(size.width * .52f, size.height * .48f))
                            drawCircle(Accent.copy(alpha = .18f), 19f, Offset(size.width * .52f, size.height * .48f))
                        }
                        Column(Modifier.align(Alignment.TopStart).padding(16.dp)) {
                            Text("EGYPT · CAIRO", color = Muted, fontSize = 10.sp, letterSpacing = 1.sp)
                            Text("Coordinate canvas", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        }
                        Text("SIMULATED VIEW", Modifier.align(Alignment.BottomEnd).padding(14.dp), color = Muted, fontSize = 9.sp, letterSpacing = 1.sp)
                    }
                }
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("COORDINATES", color = Muted, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        CoordinateField("LATITUDE", latitude, { latitude = it }, Modifier.weight(1f))
                        CoordinateField("LONGITUDE", longitude, { longitude = it }, Modifier.weight(1f))
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Place, null, tint = Accent, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Preview: ${latitude}, ${longitude}", color = Muted, fontSize = 11.sp)
                    }
                }
                Column(Modifier.fillMaxWidth().background(Panel, RoundedCornerShape(18.dp)).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("ROUTE LAB", fontWeight = FontWeight.Bold, letterSpacing = 1.sp, modifier = Modifier.weight(1f))
                        Text(if (routeMode) "ROUTE MODE" else "SINGLE POINT", color = Accent, fontSize = 10.sp)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Movement speed", color = Muted, fontSize = 12.sp, modifier = Modifier.weight(1f))
                        Text("${speed.toInt()} km/h", color = Accent, fontWeight = FontWeight.Bold)
                    }
                    Slider(value = speed, onValueChange = { speed = it }, valueRange = 1f..60f)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Route preview", color = Muted, modifier = Modifier.weight(1f), fontSize = 12.sp)
                        Switch(checked = routeMode, onCheckedChange = { routeMode = it })
                    }
                    Button(onClick = { running = !running }, Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = if (running) androidx.compose.ui.graphics.Color(0xFF354253) else Accent)) {
                        Icon(if (running) Icons.Default.Stop else Icons.Default.PlayArrow, null, tint = if (running) androidx.compose.ui.graphics.Color.White else Ink)
                        Spacer(Modifier.width(8.dp))
                        Text(if (running) "STOP PREVIEW" else "START PREVIEW", color = if (running) androidx.compose.ui.graphics.Color.White else Ink, fontWeight = FontWeight.Bold)
                    }
                    Text(if (running) "Preview session active — no device location is being changed." else "Run a local visual preview. No GPS provider or other apps are affected.", color = Muted, fontSize = 10.sp)
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.Shield, null, tint = Muted, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("ASH GPS  •  QA TOOL  •  v0.1.0", color = Muted, fontSize = 9.sp, letterSpacing = 1.sp)
                }
            }
        }
    }
}

@Composable
private fun CoordinateField(label: String, value: String, onValueChange: (String) -> Unit, modifier: Modifier = Modifier) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(7.dp)) {
        Text(label, color = Muted, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        OutlinedTextField(value = value, onValueChange = onValueChange, modifier = Modifier.fillMaxWidth(), singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), shape = RoundedCornerShape(13.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Accent, unfocusedBorderColor = androidx.compose.ui.graphics.Color(0xFF354253),
                focusedTextColor = androidx.compose.ui.graphics.Color.White, unfocusedTextColor = androidx.compose.ui.graphics.Color.White))
    }
}
