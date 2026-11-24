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
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import org.json.JSONObject
import java.util.Locale

private val Canvas = Color(0xFFF6F7F9)
private val Paper = Color.White
private val Ink = Color(0xFF151C2B)
private val Secondary = Color(0xFF7C8493)
private val Accent = Color(0xFF315EFb)
private val Hairline = Color(0xFFE9ECF1)
private val SoftBlue = Color(0xFFEEF2FF)
private val Green = Color(0xFF16845B)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { AshGpsPremium() }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AshGpsPremium() {
    var lat by remember { mutableStateOf("30.044400") }
    var lon by remember { mutableStateOf("31.235700") }
    var query by remember { mutableStateOf("") }
    var mapStatus by remember { mutableStateOf("CAIRO, EGYPT") }
    var preview by remember { mutableStateOf(false) }
    var routeMode by remember { mutableStateOf(false) }
    var speed by remember { mutableFloatStateOf(5f) }
    var map by remember { mutableStateOf<WebView?>(null) }
    var tab by remember { mutableIntStateOf(0) }
    var saved by remember { mutableStateOf(false) }

    MaterialTheme(colorScheme = lightColorScheme(primary = Accent, onPrimary = Color.White, background = Canvas, surface = Paper, onSurface = Ink)) {
        Scaffold(
            containerColor = Canvas,
            bottomBar = {
                NavigationBar(containerColor = Paper, tonalElevation = 0.dp) {
                    NavigationBarItem(selected = tab == 0, onClick = { tab = 0 }, icon = { Icon(Icons.Default.Explore, null) }, label = { Text("Explore") })
                    NavigationBarItem(selected = tab == 1, onClick = { tab = 1 }, icon = { Icon(Icons.Default.Route, null) }, label = { Text("Routes") })
                    NavigationBarItem(selected = tab == 2, onClick = { tab = 2 }, icon = { Icon(Icons.Default.BookmarkBorder, null) }, label = { Text("Saved") })
                }
            }
        ) { inset ->
            Column(
                Modifier.fillMaxSize().padding(inset).verticalScroll(rememberScrollState()).padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(46.dp).clip(RoundedCornerShape(15.dp)).background(Ink), Alignment.Center) {
                        Text("A", color = Color.White, fontSize = 25.sp, fontWeight = FontWeight.Black)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text("ASH GPS", fontSize = 20.sp, color = Ink, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.1.sp)
                        Text("LOCATION STUDIO", fontSize = 9.sp, color = Secondary, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                    }
                    Surface(color = Color(0xFFEAF7F1), shape = CircleShape) {
                        Row(Modifier.padding(horizontal = 11.dp, vertical = 7.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.size(6.dp).background(Green, CircleShape))
                            Spacer(Modifier.width(6.dp))
                            Text("READY", fontSize = 9.sp, color = Green, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        }
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Text(if (tab == 1) "Route studio" else if (tab == 2) "Your places" else "Explore the world", fontSize = 28.sp, color = Ink, fontWeight = FontWeight.Bold, letterSpacing = (-.7).sp)
                    Text(
                        when (tab) {
                            1 -> "Build and preview a route inside the app."
                            2 -> "Keep the coordinates you want close."
                            else -> "A precise, elegant workspace for location research."
                        },
                        color = Secondary, fontSize = 12.sp
                    )
                }

                if (tab == 2) {
                    Surface(color = Paper, shape = RoundedCornerShape(22.dp), border = androidx.compose.foundation.BorderStroke(1.dp, Hairline)) {
                        Column(Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text("SAVED LOCATIONS", color = Secondary, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
                            if (saved) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Place, null, tint = Accent)
                                    Spacer(Modifier.width(10.dp))
                                    Column(Modifier.weight(1f)) {
                                        Text("Saved point", color = Ink, fontWeight = FontWeight.SemiBold)
                                        Text("$lat, $lon", color = Secondary, fontSize = 11.sp)
                                    }
                                    TextButton(onClick = { map?.evaluateJavascript("setPoint($lat,$lon)", null); tab = 0 }) { Text("VIEW") }
                                }
                            } else {
                                Text("No saved places yet.", color = Ink, fontWeight = FontWeight.SemiBold)
                                Text("Select a point on the map and save it for quick access.", color = Secondary, fontSize = 12.sp)
                            }
                        }
                    }
                } else {
                    Surface(color = Paper, shape = RoundedCornerShape(24.dp), border = androidx.compose.foundation.BorderStroke(1.dp, Hairline)) {
                        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(11.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Column(Modifier.weight(1f)) {
                                    Text("LIVE MAP", color = Ink, fontWeight = FontWeight.Bold, fontSize = 11.sp, letterSpacing = 1.4.sp)
                                    Text(mapStatus, color = Secondary, fontSize = 9.sp, letterSpacing = 1.sp)
                                }
                                Icon(Icons.Default.Public, null, tint = Accent, modifier = Modifier.size(19.dp))
                            }
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                                OutlinedTextField(
                                    value = query, onValueChange = { query = it }, modifier = Modifier.weight(1f),
                                    placeholder = { Text("Search a city or address", fontSize = 12.sp) },
                                    leadingIcon = { Icon(Icons.Default.Search, null, tint = Secondary) },
                                    singleLine = true, shape = RoundedCornerShape(14.dp),
                                    colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Hairline, focusedBorderColor = Accent)
                                )
                                FilledIconButton(
                                    onClick = { map?.evaluateJavascript("searchPlace(" + JSONObject.quote(query) + ")", null) },
                                    modifier = Modifier.size(52.dp),
                                    colors = IconButtonDefaults.filledIconButtonColors(containerColor = Ink)
                                ) { Icon(Icons.Default.Search, null, tint = Color.White) }
                            }
                            Box(Modifier.fillMaxWidth().height(330.dp).clip(RoundedCornerShape(18.dp)).background(Color(0xFFE8EDF4))) {
                                PremiumMap(
                                    onPoint = { a, b ->
                                        lat = String.format(Locale.US, "%.6f", a)
                                        lon = String.format(Locale.US, "%.6f", b)
                                        mapStatus = "POINT SELECTED"
                                    },
                                    onReady = { map = it }
                                )
                                Surface(Modifier.align(Alignment.TopEnd).padding(10.dp), color = Paper.copy(alpha = .96f), shape = CircleShape, shadowElevation = 3.dp) {
                                    IconButton(onClick = { map?.evaluateJavascript("map.setView([30.0444,31.2357],12)", null) }) {
                                        Icon(Icons.Default.CenterFocusStrong, null, tint = Ink)
                                    }
                                }
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Info, null, tint = Secondary, modifier = Modifier.size(13.dp))
                                Spacer(Modifier.width(5.dp))
                                Text("Map data © OpenStreetMap contributors", color = Secondary, fontSize = 9.sp)
                                Spacer(Modifier.weight(1f))
                                Text("LIVE", color = Green, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                            }
                        }
                    }

                    Surface(color = Paper, shape = RoundedCornerShape(24.dp), border = androidx.compose.foundation.BorderStroke(1.dp, Hairline)) {
                        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(13.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Column(Modifier.weight(1f)) {
                                    Text("SELECTED COORDINATES", color = Ink, fontWeight = FontWeight.Bold, fontSize = 11.sp, letterSpacing = 1.2.sp)
                                    Text("Tap the map or enter a point manually", color = Secondary, fontSize = 10.sp)
                                }
                                Icon(Icons.Default.MyLocation, null, tint = Accent)
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                CoordinateInput("LATITUDE", lat, { lat = it }, Modifier.weight(1f))
                                CoordinateInput("LONGITUDE", lon, { lon = it }, Modifier.weight(1f))
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                                OutlinedButton(
                                    onClick = { map?.evaluateJavascript("setPoint(Number(" + JSONObject.quote(lat) + "),Number(" + JSONObject.quote(lon) + "))", null) },
                                    modifier = Modifier.weight(1f).height(48.dp), shape = RoundedCornerShape(13.dp)
                                ) {
                                    Icon(Icons.Default.Place, null, modifier = Modifier.size(17.dp))
                                    Spacer(Modifier.width(6.dp))
                                    Text("Show on map", fontSize = 11.sp)
                                }
                                FilledTonalIconButton(onClick = { saved = true }, modifier = Modifier.size(48.dp), shape = RoundedCornerShape(13.dp)) {
                                    Icon(if (saved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder, null)
                                }
                            }
                        }
                    }

                    Surface(color = Paper, shape = RoundedCornerShape(24.dp), border = androidx.compose.foundation.BorderStroke(1.dp, Hairline)) {
                        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(11.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Column(Modifier.weight(1f)) {
                                    Text("ROUTE PREVIEW", color = Ink, fontWeight = FontWeight.Bold, fontSize = 11.sp, letterSpacing = 1.2.sp)
                                    Text("Visualize a route within ASH GPS", color = Secondary, fontSize = 10.sp)
                                }
                                Switch(checked = routeMode, onCheckedChange = { routeMode = it })
                            }
                            if (routeMode || tab == 1) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Preview speed", color = Secondary, fontSize = 11.sp, modifier = Modifier.weight(1f))
                                    Text("${speed.toInt()} km/h", color = Accent, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                                Slider(value = speed, onValueChange = { speed = it }, valueRange = 1f..60f)
                            }
                            Button(
                                onClick = { preview = !preview },
                                modifier = Modifier.fillMaxWidth().height(50.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = if (preview) Color(0xFFE9EDF4) else Ink, contentColor = if (preview) Ink else Color.White)
                            ) {
                                Icon(if (preview) Icons.Default.Stop else Icons.Default.PlayArrow, null)
                                Spacer(Modifier.width(8.dp))
                                Text(if (preview) "STOP PREVIEW" else "START LOCAL PREVIEW", fontWeight = FontWeight.Bold, letterSpacing = .5.sp)
                            }
                            Text("In-app QA simulation only. This does not modify the device location or other apps.", color = Secondary, fontSize = 10.sp)
                        }
                    }
                }

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.VerifiedUser, null, tint = Secondary, modifier = Modifier.size(13.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("ASH GPS  •  ULTRA PREMIUM  •  AD-FREE", color = Secondary, fontSize = 9.sp, letterSpacing = 1.sp)
                }
                Spacer(Modifier.height(10.dp))
            }
        }
    }
}

@Composable
private fun CoordinateInput(label: String, value: String, onChange: (String) -> Unit, modifier: Modifier = Modifier) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(label, color = Secondary, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        OutlinedTextField(
            value = value, onValueChange = onChange, modifier = Modifier.fillMaxWidth(), singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            shape = RoundedCornerShape(13.dp),
            colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Hairline, focusedBorderColor = Accent)
        )
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun PremiumMap(onPoint: (Double, Double) -> Unit, onReady: (WebView) -> Unit) {
    val html = """
<!doctype html><html><head><meta name="viewport" content="width=device-width,initial-scale=1,maximum-scale=1,user-scalable=no">
<link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css">
<style>
html,body,#map{height:100%;width:100%;margin:0;background:#edf1f6}
.leaflet-container{font-family:Arial,sans-serif}
.leaflet-control-attribution{font-size:8px!important;background:rgba(255,255,255,.8)!important}
.leaflet-control-zoom a{border:0!important;color:#172033!important}
.leaflet-bar{border:0!important;border-radius:12px!important;overflow:hidden;box-shadow:0 2px 12px #17203322!important}
</style></head><body><div id="map"></div>
<script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script><script>
const map=L.map('map',{zoomControl:true,preferCanvas:true}).setView([30.0444,31.2357],12);
L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png',{maxZoom:19,attribution:'© OpenStreetMap'}).addTo(map);
const pin=L.divIcon({className:'ash-pin',html:'<div style="width:22px;height:22px;background:#315efb;border:4px solid white;border-radius:50%;box-shadow:0 2px 12px #17203366"></div>',iconSize:[22,22],iconAnchor:[11,11]});
let marker=L.marker([30.0444,31.2357],{icon:pin}).addTo(map);
function setPoint(a,b){if(!Number.isFinite(a)||!Number.isFinite(b)||a < -90||a>90||b < -180||b>180)return;marker.setLatLng([a,b]);map.setView([a,b],15);if(window.AndroidBridge)AndroidBridge.pointSelected(a,b);}
map.on('click',e=>setPoint(e.latlng.lat,e.latlng.lng));
function searchPlace(q){if(!q||!q.trim())return;fetch('https://nominatim.openstreetmap.org/search?format=json&limit=1&q='+encodeURIComponent(q),{headers:{'Accept-Language':'en'}}).then(r=>r.json()).then(rows=>{if(rows&&rows.length)setPoint(Number(rows[0].lat),Number(rows[0].lon));}).catch(()=>{});}
</script></body></html>
""".trimIndent()
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.cacheMode = android.webkit.WebSettings.LOAD_DEFAULT
                webChromeClient = WebChromeClient()
                webViewClient = WebViewClient()
                addJavascriptInterface(object {
                    @JavascriptInterface fun pointSelected(a: Double, b: Double) {
                        post { onPoint(a, b) }
                    }
                }, "AndroidBridge")
                loadDataWithBaseURL("https://localhost/", html, "text/html", "UTF-8", null)
                onReady(this)
            }
        },
        update = {}
    )
}
