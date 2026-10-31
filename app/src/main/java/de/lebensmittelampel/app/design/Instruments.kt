package de.lebensmittelampel.app.design

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.lebensmittelampel.app.data.Product
import de.lebensmittelampel.app.data.TrafficLight
import de.lebensmittelampel.app.data.daysUntil
import java.time.LocalDate
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

/** Hintergrund des ganzen Bildschirms je Design. */
fun Modifier.designBackground(s: DesignSpec): Modifier = when (s.design) {
    AppDesign.RADAR -> background(Brush.radialGradient(listOf(Color(0xFF062A13), Color(0xFF010804))))
    AppDesign.ZEITREISE -> background(Brush.verticalGradient(listOf(Color(0xFF3A3D42), Color(0xFF23252A))))
        .drawBehind {
            var x = 0f
            while (x < size.width) { drawLine(Color(0x07FFFFFF), Offset(x, 0f), Offset(x, size.height)); x += 3.dp.toPx() }
        }
    AppDesign.ENDZEIT -> background(s.bg).drawWithContent {
        drawContent()
        var y = 0f
        val step = 3.dp.toPx()
        while (y < size.height) { drawRect(Color(0x40000000), Offset(0f, y + step * 0.66f), Size(size.width, step / 3)); y += step }
        drawRect(Brush.radialGradient(listOf(Color.Transparent, Color(0x99000000)), radius = size.maxDimension * 0.75f))
    }
    AppDesign.RETRO -> background(s.bg)
    AppDesign.STEAMPUNK -> background(s.bg).drawBehind {
        var x = 0f
        var i = 0
        val cols = listOf(Color(0xFF3A2112), Color(0xFF33200F), Color(0xFF40261A))
        while (x < size.width) {
            val w = listOf(6f, 3f, 7f)[i % 3].dp.toPx()
            drawRect(cols[i % 3], Offset(x, 0f), Size(w, size.height)); x += w; i++
        }
    }
    AppDesign.HUD -> background(Brush.radialGradient(listOf(Color(0xFF0A2A44), Color(0xFF020812)))).drawBehind {
        val step = 20.dp.toPx()
        var x = 0f
        while (x < size.width) { drawLine(Color(0x0D49D8FF), Offset(x, 0f), Offset(x, size.height)); x += step }
        var y = 0f
        while (y < size.height) { drawLine(Color(0x0D49D8FF), Offset(0f, y), Offset(size.width, y)); y += step }
    }
    AppDesign.KAWAII -> background(s.bg).drawBehind { dots(Color(0xFFFFD6E5), 18.dp.toPx(), 1.8.dp.toPx()) }
    AppDesign.CARTOON -> background(s.bg).drawBehind { dots(Color(0xFFFFB800), 12.dp.toPx(), 2.2.dp.toPx()) }
    AppDesign.SCHLICHT -> background(s.bg)
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.dots(c: Color, step: Float, r: Float) {
    var y = step / 2
    while (y < size.height) {
        var x = step / 2
        while (x < size.width) { drawCircle(c, r, Offset(x, y)); x += step }
        y += step
    }
}

/** Leuchtende Textschatten für Neon-Designs. */
fun glowStyle(base: TextStyle, color: Color, on: Boolean): TextStyle =
    if (on) base.copy(shadow = Shadow(color.copy(alpha = 0.6f), blurRadius = 10f)) else base

@Composable
fun Dot(color: Color, size: Dp = 12.dp, glow: Boolean = false) {
    Canvas(Modifier.size(size)) {
        if (glow) drawCircle(color.copy(alpha = 0.35f), radius = this.size.minDimension / 2)
        drawCircle(color, radius = this.size.minDimension / (if (glow) 3f else 2f))
    }
}

/** Kleine Ampel mit drei Lämpchen (Retro). */
@Composable
fun RetroLamp(light: TrafficLight, s: DesignSpec) {
    Column(
        Modifier.background(Color(0xFF4A2A18), RoundedCornerShape(8.dp)).padding(3.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        for (l in TrafficLight.entries) {
            val on = l == light
            val c = when (l) { TrafficLight.RED -> s.red; TrafficLight.YELLOW -> Color(0xFFF2B134); TrafficLight.GREEN -> Color(0xFF7BC24F) }
            Box(Modifier.size(10.dp).background(if (on) c else Color(0xFF7A5A46), CircleShape))
        }
    }
}

/** Manometer mit roter, gelber und grüner Skala (Steampunk). */
@Composable
fun SteamGauge(days: Long, modifier: Modifier = Modifier) {
    Canvas(modifier.size(64.dp, 42.dp)) {
        val k = size.width / 64f
        val c = Offset(32f * k, 34f * k)
        val r = 26f * k
        val f = min(days.coerceAtLeast(0).toFloat(), 548f) / 548f
        fun arc(radius: Float, start: Float, sweep: Float, col: Color, w: Float) = drawArc(
            col, start, sweep, false, Offset(c.x - radius, c.y - radius), Size(radius * 2, radius * 2), style = Stroke(w),
        )
        arc(r + 3 * k, 180f, 180f, Color(0xFF7A5520), 3 * k)
        val z1 = 91f / 548f * 180f
        val z2 = 182f / 548f * 180f
        arc(r, 180f, z1, Color(0xFFB4281C), 5 * k)
        arc(r, 180f + z1, z2 - z1, Color(0xFFD39A1C), 5 * k)
        arc(r, 180f + z2, 180f - z2, Color(0xFF4C7A2B), 5 * k)
        val ang = Math.toRadians((180f + f * 180f).toDouble())
        val n = Offset(c.x + (r - 6 * k) * cos(ang).toFloat(), c.y + (r - 6 * k) * sin(ang).toFloat())
        drawLine(Color(0xFF2B1A0E), c, n, 2 * k, cap = StrokeCap.Round)
        drawCircle(Color(0xFFA57A2C), 3.5f * k, c)
        drawCircle(Color(0xFF2B1A0E), 3.5f * k, c, style = Stroke(1 * k))
    }
}

/** Sich drehendes Zahnrad (Steampunk). */
@Composable
fun SpinningGear(size: Dp) {
    val t = rememberInfiniteTransition(label = "gear")
    val a by t.animateFloat(0f, 360f, infiniteRepeatable(tween(12000, easing = LinearEasing)), label = "gearA")
    Canvas(Modifier.size(size)) {
        val k = this.size.width / 48f
        rotate(a) {
            val p = Path()
            val n = 12
            for (i in 0 until n * 2) {
                val rr = (if (i % 2 == 1) 18f else 23f) * k
                val a1 = Math.toRadians(i * 360.0 / (n * 2))
                val a2 = Math.toRadians((i + 1) * 360.0 / (n * 2))
                val cx = 24f * k
                val p1 = Offset(cx + rr * cos(a1).toFloat(), cx - rr * sin(a1).toFloat())
                val p2 = Offset(cx + rr * cos(a2).toFloat(), cx - rr * sin(a2).toFloat())
                if (i == 0) p.moveTo(p1.x, p1.y) else p.lineTo(p1.x, p1.y)
                p.lineTo(p2.x, p2.y)
            }
            p.close()
            drawPath(p, Color(0xFF5A3D14))
            drawCircle(Color(0xFFC49A48), 12f * k)
            drawCircle(Color(0xFF3A2112), 5f * k)
        }
    }
}

/** Kreisanzeige für die Resthaltbarkeit (Iron Man). */
@Composable
fun HudRing(days: Long, color: Color) {
    Canvas(Modifier.size(34.dp)) {
        val k = size.width / 34f
        val r = 13f * k
        val tl = Offset(17f * k - r, 17f * k - r)
        val sz = Size(r * 2, r * 2)
        drawArc(Color(0xFF0F4C66), 0f, 360f, false, tl, sz, style = Stroke(3 * k))
        val f = min(days.coerceAtLeast(0).toFloat(), 548f) / 548f
        drawArc(color.copy(alpha = 0.35f), -90f, f * 360f, false, tl, sz, style = Stroke(6 * k))
        drawArc(color, -90f, f * 360f, false, tl, sz, style = Stroke(3 * k))
    }
}

/** Radarschirm: je näher an der Mitte, desto näher das MHD. */
@Composable
fun RadarScope(products: List<Product>, today: LocalDate, s: DesignSpec, size: Dp = 220.dp) {
    val t = rememberInfiniteTransition(label = "radar")
    val sweep by t.animateFloat(0f, 360f, infiniteRepeatable(tween(4000, easing = LinearEasing)), label = "sweep")
    val pulse by t.animateFloat(1f, 0.35f, infiniteRepeatable(tween(600), RepeatMode.Reverse), label = "pulse")
    Canvas(Modifier.size(size)) {
        val k = this.size.width / 220f
        val c = Offset(110f * k, 110f * k)
        fun rr(days: Float) = 110f * k * (0.06f + 0.9f * min(days.coerceAtLeast(0f), 365f) / 365f)
        val dash = PathEffect.dashPathEffect(floatArrayOf(3 * k, 3 * k))
        drawCircle(Color(0xFF02140A), 108 * k, c)
        drawCircle(s.green, 108 * k, c, style = Stroke(1.5f * k))
        drawCircle(s.yellow.copy(alpha = 0.08f), rr(182f), c)
        drawCircle(s.yellow.copy(alpha = 0.47f), rr(182f), c, style = Stroke(1 * k, pathEffect = dash))
        drawCircle(s.red.copy(alpha = 0.13f), rr(91f), c)
        drawCircle(s.red.copy(alpha = 0.6f), rr(91f), c, style = Stroke(1 * k, pathEffect = dash))
        drawCircle(s.green.copy(alpha = 0.27f), rr(365f), c, style = Stroke(1 * k))
        drawLine(s.green.copy(alpha = 0.2f), Offset(2 * k, c.y), Offset(218 * k, c.y))
        drawLine(s.green.copy(alpha = 0.2f), Offset(c.x, 2 * k), Offset(c.x, 218 * k))
        for (i in 0 until 36) {
            val a = Math.toRadians(i * 10.0)
            val r2 = if (i % 3 == 0) 100f else 104f
            drawLine(
                s.green.copy(alpha = 0.53f),
                Offset(c.x + 108 * k * cos(a).toFloat(), c.y - 108 * k * sin(a).toFloat()),
                Offset(c.x + r2 * k * cos(a).toFloat(), c.y - r2 * k * sin(a).toFloat()),
            )
        }
        rotate(sweep, c) {
            drawCircle(
                Brush.sweepGradient(
                    0f to Color.Transparent, 0.8f to Color.Transparent, 1f to s.green.copy(alpha = 0.5f),
                    center = c,
                ),
                108 * k, c,
            )
        }
        products.forEachIndexed { i, p ->
            val light = p.trafficLight(today)
            val a = Math.toRadians(((i * 137.5 + 30) % 360))
            val r = rr(daysUntil(p.expiry, today).toFloat())
            val pos = Offset(c.x + r * cos(a).toFloat(), c.y - r * sin(a).toFloat())
            val col = s.color(light)
            val alpha = if (light == TrafficLight.RED) pulse else 1f
            drawCircle(col.copy(alpha = 0.3f * alpha), 8 * k, pos)
            drawCircle(col.copy(alpha = alpha), 4.5f * k, pos)
        }
    }
}

/** Kernring mit Ampelverteilung (Iron Man). */
@Composable
fun HudCore(counts: Map<TrafficLight, Int>, total: Int, s: DesignSpec) {
    val t = rememberInfiniteTransition(label = "hud")
    val a1 by t.animateFloat(0f, 360f, infiniteRepeatable(tween(14000, easing = LinearEasing)), label = "a1")
    val a2 by t.animateFloat(360f, 0f, infiniteRepeatable(tween(9000, easing = LinearEasing)), label = "a2")
    Box(Modifier.size(180.dp), contentAlignment = Alignment.Center) {
        Canvas(Modifier.size(180.dp)) {
            val k = size.width / 180f
            val c = Offset(90f * k, 90f * k)
            fun box(r: Float) = Pair(Offset(c.x - r, c.y - r), Size(r * 2, r * 2))
            rotate(a1, c) {
                drawCircle(s.accent, 84 * k, c, style = Stroke(1 * k, pathEffect = PathEffect.dashPathEffect(floatArrayOf(2 * k, 6 * k))))
                val (tl, sz) = box(84 * k)
                drawArc(s.accent, -100f, 60f, false, tl, sz, style = Stroke(3 * k))
            }
            rotate(a2, c) {
                drawCircle(s.accent.copy(alpha = 0.33f), 76 * k, c, style = Stroke(1 * k, pathEffect = PathEffect.dashPathEffect(floatArrayOf(30 * k, 10 * k, 4 * k, 10 * k))))
            }
            val (tl, sz) = box(66 * k)
            if (total == 0) {
                drawCircle(s.accent.copy(alpha = 0.25f), 66 * k, c, style = Stroke(7 * k))
            } else {
                var start = -90f
                val gap = 3f
                for (l in TrafficLight.entries) {
                    val n = counts[l] ?: 0
                    if (n == 0) continue
                    val sweep = n.toFloat() / total * 360f
                    val drawn = (sweep - gap).coerceAtLeast(1f)
                    drawArc(s.color(l).copy(alpha = 0.3f), start, drawn, false, tl, sz, style = Stroke(12 * k))
                    drawArc(s.color(l), start, drawn, false, tl, sz, style = Stroke(7 * k))
                    start += sweep
                }
            }
            drawCircle(s.accent.copy(alpha = 0.06f), 54 * k, c)
            drawCircle(s.accent.copy(alpha = 0.4f), 54 * k, c, style = Stroke(1 * k))
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "$total",
                style = glowStyle(TextStyle(fontFamily = Fonts.orbitron, fontWeight = FontWeight.Black, fontSize = 36.sp, color = s.accent), s.accent, true),
            )
            Text("OBJEKTE", style = TextStyle(fontFamily = Fonts.rajdhani, fontWeight = FontWeight.Bold, fontSize = 10.sp, letterSpacing = 2.sp, color = s.accent))
        }
    }
}

/** Zeitanzeige mit leuchtenden Ziffern (Zurück in die Zukunft). */
@Composable
fun TimeCircuit(date: LocalDate, rightLabel: String, rightValue: String, color: Color, plate: String, plateSmall: String, rightColor: Color = color) {
    val months = listOf("JAN", "FEB", "MÄR", "APR", "MAI", "JUN", "JUL", "AUG", "SEP", "OKT", "NOV", "DEZ")
    Column(
        Modifier
            .fillMaxWidth()
            .background(Brush.verticalGradient(listOf(Color(0xFF18191C), Color(0xFF0E0F11))), RoundedCornerShape(4.dp))
            .border(2.dp, Color(0xFF57595F), RoundedCornerShape(4.dp))
            .padding(start = 8.dp, end = 8.dp, top = 6.dp, bottom = 5.dp),
    ) {
        Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            LedCell("MONAT", months[date.monthValue - 1], color)
            LedCell("TAG", "%02d".format(date.dayOfMonth), color)
            LedCell("JAHR", "${date.year}", color)
            Spacer(Modifier.weight(1f))
            LedCell(rightLabel, rightValue, rightColor)
        }
        Row(Modifier.fillMaxWidth().padding(top = 5.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(
                plate.uppercase(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f, fill = false)
                    .background(Color(0xFFE9E2C8), RoundedCornerShape(1.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp),
                style = TextStyle(fontFamily = Fonts.orbitron, fontWeight = FontWeight.Bold, fontSize = 9.sp, letterSpacing = 1.sp, color = Color(0xFF1B1B1B)),
            )
            Spacer(Modifier.size(6.dp))
            Text(plateSmall.uppercase(), maxLines = 1, style = TextStyle(fontFamily = Fonts.orbitron, fontSize = 8.sp, letterSpacing = 1.sp, color = Color(0xFFB9BBC2)))
        }
    }
}

@Composable
private fun LedCell(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = TextStyle(fontFamily = Fonts.orbitron, fontSize = 7.sp, letterSpacing = 1.sp, color = Color(0xFF9A9CA3)))
        Text(
            value,
            modifier = Modifier.background(Color.Black, RoundedCornerShape(2.dp)).padding(horizontal = 4.dp, vertical = 1.dp),
            style = TextStyle(
                fontFamily = Fonts.orbitron, fontWeight = FontWeight.Bold, fontSize = 17.sp, color = color,
                shadow = Shadow(color, blurRadius = 14f),
            ),
        )
    }
}

/** Warnstreifen (Zurück in die Zukunft). */
@Composable
fun HazardStripe() {
    Canvas(Modifier.fillMaxWidth().size(height = 10.dp, width = 1.dp)) {
        drawRect(Color(0xFF141414))
        val w = 8.dp.toPx()
        var x = -size.height
        while (x < size.width + size.height) {
            val p = Path().apply {
                moveTo(x, size.height); lineTo(x + w, size.height); lineTo(x + w + size.height, 0f); lineTo(x + size.height, 0f); close()
            }
            drawPath(p, Color(0xFFF3C41A))
            x += w * 2
        }
    }
}
