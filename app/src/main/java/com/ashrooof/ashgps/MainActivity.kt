package com.ashrooof.ashgps

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import org.json.JSONObject

private val CanvasBg = Color(0xFFF5F7FA)
private val SurfaceWhite = Color.White
private val Ink = Color(0xFF172338)
private val Muted = Color(0xFF758196)
private val Accent = Color(0xFF246BFD)
private val Line = Color(0xFFE6EAF0)

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
    var search by remember { mutableStateOf("") }
    var speed by remember { mutableFloatStateOf(5f) }
    var routeMode by remember { mutableStateOf(false) }
    var previewRunning by remember { mutableStateOf(false) }
    var mapStatus by remember { mutableStateOf("OpenStreetMap · Cairo") }
    var mapController by remember { mutableStateOf<WebView?>(null) }

    MaterialTheme(colorScheme = lightColorScheme(primary = Accent, onPrimary = Color.White, background = CanvasBg, surface = SurfaceWhite, onSurface = Ink, onBackground = Ink)) {
        Scaffold(containerColor = CanvasBg) { padding ->
            Column(
                Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(horizontal = 18.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(44.dp).background(Accent, RoundedCornerShape(14.dp)), Alignment.Center) {
                        Icon(Icons.Default.Explore, null, tint = Color.White, modifier = Modifier.size(25.dp))
                    }
                    Spacer(Modifier.width(11.dp))
                    Column(Modifier.weight(1f)) {
                        Text("ASH GPS", color = Ink, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
                        Text("LOCATION WORKSPACE", color = Muted, fontSize = 9.sp, letterSpacing = 1.7.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Surface(color = Color(0xFFEAF1FF), shape = RoundedCornerShape(30.dp)) {
                        Row(Modifier.padding(horizontal = 10.dp, vertical = 7.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.size(7.dp).background(Color(0xFF2DA66F), RoundedCornerShape(50)))
                            Spacer(Modifier.width(6.dp))
                            Text("READY", color = Color(0xFF236B4C), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Text("Explore any place", color = Ink, fontSize = 23.sp, fontWeight = FontWeight.Bold)
                    Text("Search, select a point and inspect coordinates on a live map.", color = Muted, fontSize = 12.sp)
                }
                Card(shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = SurfaceWhite), elevation = CardDefaults.cardElevation(1.dp)) {
                    Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Map, null, tint = Accent, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(7.dp))
                            Text("LIVE MAP", color = Ink, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                            Spacer(Modifier.weight(1f))
                            Text(mapStatus, color = Muted, fontSize = 9.sp)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                            OutlinedTextField(value = search, onValueChange = { search = it }, modifier = Modifier.weight(1f), singleLine = true,
                                placeholder = { Text("Search city or place…", fontSize = 12.sp) },
                                leadingIcon = { Icon(Icons.Default.Search, null, tint = Muted) }, shape = RoundedCornerShape(13.dp),
                                colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Line, focusedBorderColor = Accent))
                            FilledIconButton(onClick = { mapController?.evaluateJavascript("searchPlace(" + JSONObject.quote(search) + ")", null) },
                                modifier = Modifier.size(52.dp), colors = IconButtonDefaults.filledIconButtonColors(containerColor = Accent)) {
                                Icon(Icons.Default.Search, null, tint = Color.White)
                            }
                        }
                        Box(Modifier.fillMaxWidth().height(310.dp).background(Color(0xFFE9EEF5), RoundedCornerShape(16.dp))) {
                            RealMap(
                                onPoint = { lat, lon ->
                                    latitude = String.format(java.util.Locale.US, "%.6f", lat)
                                    longitude = String.format(java.util.Locale.US, "%.6f", lon)
                                    mapStatus = "Point selected"
                                },
                                onReady = { mapController = it }
                            )
                        }
                        Text("© OpenStreetMap contributors · Requires internet connection", color = Muted, fontSize = 9.sp)
                    }
                }
                Card(shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = SurfaceWhite), elevation = CardDefaults.cardElevation(1.dp)) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("SELECTED COORDINATES", color = Ink, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                            Spacer(Modifier.weight(1f))
                            Icon(Icons.Default.MyLocation, null, tint = Accent, modifier = Modifier.size(17.dp))
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            CoordinateField("LATITUDE", latitude, { latitude = it }, Modifier.weight(1f))
                            CoordinateField("LONGITUDE", longitude, { longitude = it }, Modifier.weight(1f))
                        }
                        OutlinedButton(onClick = {
                            mapController?.evaluateJavascript("setPoint(Number(" + JSONObject.quote(latitude) + "),Number(" + JSONObject.quote(longitude) + "))", null)
                        }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                            Icon(Icons.Default.Place, null, modifier = Modifier.size(17.dp))
                            Spacer(Modifier.width(7.dp))
                            Text("Show coordinates on map")
                        }
                    }
                }
                Card(shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = SurfaceWhite), elevation = CardDefaults.cardElevation(1.dp)) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text("ROUTE PLAYGROUND", color = Ink, fontWeight = FontWeight.Bold, fontSize = 12.sp, letterSpacing = 1.sp)
                                Text("Visual route preview inside ASH GPS", color = Muted, fontSize = 10.sp)
                            }
                            Switch(checked = routeMode, onCheckedChange = { routeMode = it })
                        }
                        if (routeMode) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Preview speed", color = Muted, fontSize = 12.sp, modifier = Modifier.weight(1f))
                                Text("${speed.toInt()} km/h", color = Accent, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                            Slider(value = speed, onValueChange = { speed = it }, valueRange = 1f..60f)
                        }
                        Button(onClick = { previewRunning = !previewRunning }, modifier = Modifier.fillMaxWidth().height(50.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = if (previewRunning) Color(0xFFEAF0F8) else Accent, contentColor = if (previewRunning) Ink else Color.White)) {
                            Icon(if (previewRunning) Icons.Default.Stop else Icons.Default.PlayArrow, null)
                            Spacer(Modifier.width(8.dp))
                            Text(if (previewRunning) "STOP LOCAL PREVIEW" else "START LOCAL PREVIEW", fontWeight = FontWeight.Bold)
                        }
                        Text("In-app QA preview only. It never changes device location or affects other apps.", color = Muted, fontSize = 10.sp)
                    }
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.VerifiedUser, null, tint = Muted, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("ASH GPS  ·  MAP & LOCATION QA  ·  v0.2.0", color = Muted, fontSize = 9.sp, letterSpacing = .7.sp)
                }
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun CoordinateField(label: String, value: String, onValueChange: (String) -> Unit, modifier: Modifier = Modifier) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(label, color = Muted, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        OutlinedTextField(value = value, onValueChange = onValueChange, modifier = Modifier.fillMaxWidth(), singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Line, focusedBorderColor = Accent, focusedTextColor = Ink, unfocusedTextColor = Ink))
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun RealMap(onPoint: (Double, Double) -> Unit, onReady: (WebView) -> Unit) {
    val html = """
<!DOCTYPE html><html><head><meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
<link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css">
<style>html,body,#map{height:100%;width:100%;margin:0;background:#e9eef5}.leaflet-control-attribution{font-size:9px!important}</style>
</head><body><div id="map"></div>
<script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
<script>
const map=L.map('map',{zoomControl:true}).setView([30.0444,31.2357],12);
L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png',{maxZoom:19,attribution:'© OpenStreetMap'}).addTo(map);
let marker=L.marker([30.0444,31.2357]).addTo(map);
function setPoint(lat,lon){if(!Number.isFinite(lat)||!Number.isFinite(lon)||lat < -90||lat>90||lon < -180||lon>180)return;marker.setLatLng([lat,lon]);map.setView([lat,lon],15);if(window.AndroidBridge)AndroidBridge.pointSelected(lat,lon);}
map.on('click',e=>setPoint(e.latlng.lat,e.latlng.lng));
function searchPlace(q){if(!q||!q.trim())return;fetch('https://nominatim.openstreetmap.org/search?format=json&limit=1&q='+encodeURIComponent(q)).then(r=>r.json()).then(a=>{if(a&&a.length){setPoint(Number(a[0].lat),Number(a[0].lon));}}).catch(()=>{});}
</script></body></html>
""".trimIndent()
    AndroidView(modifier = Modifier.fillMaxSize(), factory = { context ->
        WebView(context).apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            webChromeClient = WebChromeClient()
            webViewClient = WebViewClient()
            addJavascriptInterface(object {
                @JavascriptInterface fun pointSelected(lat: Double, lon: Double) { onPoint(lat, lon) }
            }, "AndroidBridge")
            loadDataWithBaseURL("https://localhost/", html, "text/html", "UTF-8", null)
            onReady(this)
        }
    }, update = {})
}
