package de.lebensmittelampel.app.design

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.lebensmittelampel.app.data.TrafficLight
import kotlin.math.cos
import kotlin.math.sin

// Eigene Maskottchen (frei gestaltet, keine bekannten Figuren).

private fun DrawScope.ellipse(cx: Float, cy: Float, rx: Float, ry: Float, fill: Color, stroke: Color? = null, sw: Float = 0f) {
    val tl = Offset(cx - rx, cy - ry)
    val sz = Size(rx * 2, ry * 2)
    drawOval(fill, tl, sz)
    if (stroke != null) drawOval(stroke, tl, sz, style = Stroke(sw))
}

private fun DrawScope.shape(path: Path, fill: Color?, stroke: Color?, sw: Float) {
    if (fill != null) drawPath(path, fill, style = Fill)
    if (stroke != null) drawPath(path, stroke, style = Stroke(sw, cap = StrokeCap.Round, join = StrokeJoin.Round))
}

private fun line(vararg pts: Float) = Path().apply {
    moveTo(pts[0], pts[1])
    var i = 2
    while (i < pts.size) { lineTo(pts[i], pts[i + 1]); i += 2 }
}

/** Häschen Mimi. Stimmung: Grün strahlt, Gelb überlegt, Rot ist besorgt. */
@Composable
fun BunnyMascot(mood: TrafficLight, size: Dp, modifier: Modifier = Modifier) {
    Canvas(modifier.size(size)) {
        val o = Color(0xFF6B3A55)
        scale(this.size.width / 100f, pivot = Offset.Zero) {
            rotate(-12f, Offset(37f, 24f)) { ellipse(37f, 24f, 9f, 22f, Color.White, o, 2.5f) }
            rotate(-12f, Offset(37f, 26f)) { ellipse(37f, 26f, 4f, 14f, Color(0xFFFFC2D6)) }
            rotate(12f, Offset(63f, 24f)) { ellipse(63f, 24f, 9f, 22f, Color.White, o, 2.5f) }
            rotate(12f, Offset(63f, 26f)) { ellipse(63f, 26f, 4f, 14f, Color(0xFFFFC2D6)) }
            ellipse(50f, 62f, 34f, 28f, Color.White, o, 2.5f)
            // Blümchen am Ohr
            for (a in listOf(0, 72, 144, 216, 288)) {
                val r = Math.toRadians(a.toDouble())
                val c = Offset(72f + 6f * cos(r).toFloat(), 14f + 6f * sin(r).toFloat())
                drawCircle(Color(0xFF8FE3CF), 4.6f, c)
                drawCircle(o, 4.6f, c, style = Stroke(1.4f))
            }
            drawCircle(Color(0xFFFFC94D), 3.4f, Offset(72f, 14f))
            drawCircle(o, 3.4f, Offset(72f, 14f), style = Stroke(1.4f))
            ellipse(29f, 67f, 6f, 3.6f, Color(0xFFFFB6CF))
            ellipse(71f, 67f, 6f, 3.6f, Color(0xFFFFB6CF))
            if (mood == TrafficLight.RED) {
                shape(line(33f, 49f, 42f, 52f), null, o, 2.4f)
                shape(line(67f, 49f, 58f, 52f), null, o, 2.4f)
            }
            if (mood == TrafficLight.GREEN) {
                shape(Path().apply { moveTo(34f, 58f); quadraticBezierTo(39f, 52f, 44f, 58f) }, null, o, 3f)
                shape(Path().apply { moveTo(56f, 58f); quadraticBezierTo(61f, 52f, 66f, 58f) }, null, o, 3f)
            } else {
                drawCircle(o, 3.6f, Offset(39f, 58f)); drawCircle(o, 3.6f, Offset(61f, 58f))
                drawCircle(Color.White, 1.2f, Offset(40.2f, 56.8f)); drawCircle(Color.White, 1.2f, Offset(62.2f, 56.8f))
            }
            shape(Path().apply { moveTo(48f, 63f); lineTo(52f, 63f); lineTo(50f, 65.4f); close() }, Color(0xFFFF8FB8), o, 1f)
            when (mood) {
                TrafficLight.GREEN -> shape(
                    Path().apply { moveTo(44f, 68f); quadraticBezierTo(50f, 76f, 56f, 68f); close() },
                    Color(0xFFFF5C8A), o, 2f,
                )
                TrafficLight.YELLOW -> shape(
                    Path().apply { moveTo(45f, 68f); quadraticBezierTo(47.5f, 71f, 50f, 68f); quadraticBezierTo(52.5f, 71f, 55f, 68f) },
                    null, o, 2.2f,
                )
                TrafficLight.RED -> {
                    shape(
                        Path().apply { moveTo(43f, 71f); quadraticBezierTo(46.5f, 67f, 50f, 71f); quadraticBezierTo(53.5f, 75f, 57f, 71f) },
                        null, o, 2.2f,
                    )
                    shape(
                        Path().apply { moveTo(68f, 62f); quadraticBezierTo(71f, 68f, 68f, 70f); quadraticBezierTo(65f, 68f, 68f, 62f); close() },
                        Color(0xFF8FD3FF), o, 1.2f,
                    )
                }
            }
        }
    }
}

/** Dosen-Dieter, eine Muskel-Konserve mit Sonnenbrille und Tolle. */
@Composable
fun CanManMascot(mood: TrafficLight, size: Dp, modifier: Modifier = Modifier) {
    Canvas(modifier.size(size)) {
        val o = Color(0xFF111111)
        val skin = Color(0xFFFFCF9E)
        scale(this.size.width / 120f, pivot = Offset.Zero) {
            for (left in listOf(true, false)) {
                fun t(v: Float) = if (left) v else 120f - v
                val arm = line(t(34f), 70f, t(16f), 66f, t(14f), 40f)
                drawPath(arm, o, style = Stroke(13f, cap = StrokeCap.Round, join = StrokeJoin.Round))
                drawPath(arm, skin, style = Stroke(8f, cap = StrokeCap.Round, join = StrokeJoin.Round))
                ellipse(t(22f), 61f, 9f, 7f, skin, o, 2.5f)
                drawCircle(skin, 7f, Offset(t(14f), 34f)); drawCircle(o, 7f, Offset(t(14f), 34f), style = Stroke(2.5f))
            }
            shape(line(48f, 104f, 45f, 114f, 36f, 114f), null, o, 3f)
            shape(line(72f, 104f, 75f, 114f, 84f, 114f), null, o, 3f)
            val body = Path().apply {
                addRoundRect(androidx.compose.ui.geometry.RoundRect(34f, 26f, 86f, 106f, 6f, 6f))
            }
            shape(body, Color(0xFFC9CCD3), o, 3f)
            drawRect(Color(0xFFFF2E2E), Offset(34f, 64f), Size(52f, 32f))
            drawRect(o, Offset(34f, 64f), Size(52f, 32f), style = Stroke(3f))
            // "POWER"-Schriftzug als kräftige Balken
            for (i in 0 until 5) drawRect(Color.White, Offset(39f + i * 9f, 76f), Size(6f, 8f))
            ellipse(60f, 26f, 26f, 6f, Color(0xFFE5E7EB), o, 3f)
            val hair = Path().apply {
                moveTo(38f, 24f)
                quadraticBezierTo(34f, 4f, 60f, 4f)
                quadraticBezierTo(82f, 4f, 80f, 14f)
                quadraticBezierTo(74f, 10f, 66f, 12f)
                quadraticBezierTo(74f, 16f, 72f, 24f)
                quadraticBezierTo(62f, 14f, 38f, 24f)
                close()
            }
            shape(hair, Color(0xFF2A1D12), o, 2.5f)
            for (x in listOf(42f, 62f)) {
                drawPath(
                    Path().apply { moveTo(x, 38f); lineTo(x + 16f, 38f); lineTo(x + 16f, 46f); quadraticBezierTo(x + 8f, 50f, x, 46f); close() },
                    o,
                )
            }
            drawLine(o, Offset(58f, 40f), Offset(62f, 40f), 3f)
            when (mood) {
                TrafficLight.GREEN -> {
                    drawLine(Color.White, Offset(46f, 40f), Offset(49f, 37f), 2f)
                    drawLine(Color.White, Offset(70f, 40f), Offset(73f, 37f), 2f)
                    shape(line(92f, 18f, 94f, 24f, 100f, 26f, 94f, 28f, 92f, 34f, 90f, 28f, 84f, 26f, 90f, 24f).apply { close() }, Color.White, o, 1.2f)
                    shape(Path().apply { moveTo(48f, 54f); quadraticBezierTo(60f, 63f, 72f, 54f); close() }, Color.White, o, 2.5f)
                    drawLine(o, Offset(54f, 55f), Offset(54f, 59f), 1.5f)
                    drawLine(o, Offset(60f, 55f), Offset(60f, 60f), 1.5f)
                    drawLine(o, Offset(66f, 55f), Offset(66f, 59f), 1.5f)
                }
                TrafficLight.YELLOW -> drawLine(o, Offset(50f, 56f), Offset(70f, 56f), 3f, cap = StrokeCap.Round)
                TrafficLight.RED -> {
                    ellipse(60f, 57f, 6f, 5f, Color(0xFF7A1010), o, 2.5f)
                    shape(
                        Path().apply { moveTo(88f, 30f); quadraticBezierTo(92f, 37f, 88f, 39f); quadraticBezierTo(84f, 37f, 88f, 30f); close() },
                        Color(0xFF8FD3FF), o, 1.5f,
                    )
                }
            }
        }
    }
}

/** Comic-Explosionsstern mit Text. */
@Composable
fun Burst(fill: Color, text: String, size: Dp, fontSize: TextUnit = 14.sp, textColor: Color = Color(0xFF111111), modifier: Modifier = Modifier) {
    Box(modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(Modifier.size(size)) {
            val s = this.size.width / 56f
            val p = Path()
            val n = 12
            for (i in 0 until n * 2) {
                val r = if (i % 2 == 1) 20f else 27f
                val a = i.toDouble() / (n * 2) * Math.PI * 2
                val x = (28f + r * cos(a).toFloat()) * s
                val y = (28f + r * sin(a).toFloat()) * s
                if (i == 0) p.moveTo(x, y) else p.lineTo(x, y)
            }
            p.close()
            drawPath(p, fill)
            drawPath(p, Color(0xFF111111), style = Stroke(2.5f * s, join = StrokeJoin.Round))
        }
        Text(text, style = TextStyle(fontFamily = Fonts.bangers, fontSize = fontSize, color = textColor))
    }
}
