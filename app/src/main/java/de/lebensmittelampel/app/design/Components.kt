package de.lebensmittelampel.app.design

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.lebensmittelampel.app.data.TrafficLight
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

// ---------- Text ----------

@Composable
fun DText(
    text: String,
    s: DesignSpec,
    modifier: Modifier = Modifier,
    size: TextUnit = s.bodySize,
    color: Color = s.text,
    font: FontFamily = s.bodyFont,
    weight: FontWeight = s.bodyWeight,
    spacing: TextUnit = TextUnit.Unspecified,
    maxLines: Int = Int.MAX_VALUE,
    align: TextAlign? = null,
    glow: Boolean = s.glow,
) {
    Text(
        text,
        modifier = modifier,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis,
        textAlign = align,
        style = glowStyle(
            TextStyle(fontFamily = font, fontWeight = weight, fontSize = size, color = color, letterSpacing = spacing),
            color, glow,
        ),
    )
}

@Composable
fun DTitle(text: String, s: DesignSpec, modifier: Modifier = Modifier, size: TextUnit = s.titleSize, color: Color = s.text) {
    if (s.design == AppDesign.RETRO) {
        // Mehrfach versetzter Schatten wie im Mockup
        Box(modifier) {
            val st = TextStyle(fontFamily = Fonts.shrikhand, fontSize = size, lineHeight = size * 1.1f)
            Text(text, Modifier.padding(start = 6.dp, top = 6.dp), style = st.copy(color = Color(0xFF4A2A18)))
            Text(text, Modifier.padding(start = 4.dp, top = 4.dp), style = st.copy(color = Color(0xFFB8461F)))
            Text(text, Modifier.padding(start = 2.dp, top = 2.dp), style = st.copy(color = Color(0xFFF2B134)))
            Text(text, style = st.copy(color = Color(0xFFE8702A)))
        }
    } else if (s.design == AppDesign.CARTOON) {
        Box(modifier) {
            val st = TextStyle(fontFamily = Fonts.bangers, fontSize = size, letterSpacing = 1.sp)
            Text(text, Modifier.padding(start = 2.dp, top = 2.dp), style = st.copy(color = Color(0xFF111111)))
            Text(text, style = st.copy(color = color))
        }
    } else {
        DText(text, s, modifier, size = size, color = color, font = s.titleFont, weight = s.titleWeight, spacing = if (s.dark) 1.5.sp else 0.sp)
    }
}

// ---------- Flächen ----------

private fun panelShape(s: DesignSpec): Shape = when (s.design) {
    AppDesign.HUD -> CutCornerShape(topEnd = 10.dp, bottomStart = 10.dp)
    AppDesign.RADAR, AppDesign.ENDZEIT, AppDesign.CARTOON -> RectangleShape
    else -> RoundedCornerShape(s.corner)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DesignPanel(
    s: DesignSpec,
    modifier: Modifier = Modifier,
    edge: Color? = null,
    tilt: Float = 0f,
    selected: Boolean = false,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    padding: Dp = 10.dp,
    content: @Composable ColumnScope.() -> Unit,
) {
    val shape = panelShape(s)
    val bg: Brush = when (s.design) {
        AppDesign.HUD -> Brush.horizontalGradient(listOf(Color(0x2649D8FF), Color(0x0849D8FF)))
        AppDesign.STEAMPUNK -> Brush.radialGradient(listOf(Color(0xFFFBF3DD), s.panelBg))
        else -> SolidColor(s.panelBg)
    }
    val border: BorderStroke? = when (s.design) {
        AppDesign.KAWAII -> if (selected) BorderStroke(2.dp, s.accent) else null
        AppDesign.HUD -> if (selected) BorderStroke(1.dp, s.accent) else null
        else -> BorderStroke(s.borderWidth, if (selected) s.accent else s.panelBorder)
    }
    val click = if (onClick != null || onLongClick != null) {
        Modifier.combinedClickable(onClick = { onClick?.invoke() }, onLongClick = onLongClick)
    } else Modifier
    Column(
        modifier
            .graphicsLayer(rotationZ = tilt)
            .drawBehind {
                when (s.design) {
                    AppDesign.CARTOON -> drawRect(Color(0xFF111111), topLeft = Offset(4.dp.toPx(), 4.dp.toPx()), size = size)
                    AppDesign.KAWAII -> drawRoundRect(
                        Color(0xFFFFD6E5), topLeft = Offset(0f, 3.dp.toPx()), size = size,
                        cornerRadius = CornerRadius(s.corner.toPx()),
                    )
                    AppDesign.RETRO -> drawRoundRect(
                        Color(0x334A2A18), topLeft = Offset(0f, 3.dp.toPx()), size = size,
                        cornerRadius = CornerRadius(s.corner.toPx()),
                    )
                    else -> Unit
                }
            }
            .clip(shape)
            .background(bg)
            .then(if (border != null) Modifier.border(border, shape) else Modifier)
            .then(click)
            .drawBehind {
                if (edge != null && (s.design == AppDesign.HUD || s.design == AppDesign.RADAR)) {
                    drawRect(edge, size = size.copy(width = 2.dp.toPx()))
                }
            }
            .padding(padding),
        content = content,
    )
}

// ---------- Knöpfe ----------

@Composable
fun DesignButton(
    s: DesignSpec,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    primary: Boolean = true,
    enabled: Boolean = true,
) {
    val shape: Shape = when (s.design) {
        AppDesign.HUD -> CutCornerShape(12.dp)
        AppDesign.RETRO, AppDesign.STEAMPUNK, AppDesign.KAWAII -> RoundedCornerShape(30.dp)
        AppDesign.ZEITREISE -> RoundedCornerShape(4.dp)
        AppDesign.SCHLICHT -> RoundedCornerShape(12.dp)
        else -> RectangleShape
    }
    val bg: Brush = when (s.design) {
        AppDesign.RADAR -> SolidColor(Color(0x245DFF8F))
        AppDesign.ZEITREISE -> if (primary) Brush.verticalGradient(listOf(Color(0xFFFF8A1F), Color(0xFFD65A00))) else SolidColor(Color(0xFF3A3C41))
        AppDesign.ENDZEIT -> SolidColor(if (primary) s.accent else Color.Transparent)
        AppDesign.RETRO -> SolidColor(if (primary) s.accent else Color(0xFFF2B134))
        AppDesign.STEAMPUNK -> Brush.verticalGradient(listOf(Color(0xFFE2BF6A), Color(0xFF8A6224)))
        AppDesign.HUD -> SolidColor(if (primary) Color(0x3349D8FF) else Color(0x1449D8FF))
        AppDesign.KAWAII -> SolidColor(if (primary) s.accent else Color(0xFF8FE3CF))
        AppDesign.CARTOON -> SolidColor(if (primary) s.red else Color(0xFF1E6BFF))
        AppDesign.SCHLICHT -> SolidColor(if (primary) s.accent else Color(0xFFE3EBE2))
    }
    val fg: Color = when (s.design) {
        AppDesign.RADAR, AppDesign.HUD -> s.accent
        AppDesign.ZEITREISE -> if (primary) Color(0xFF1A0D00) else s.text
        AppDesign.ENDZEIT -> if (primary) s.bg else s.accent
        AppDesign.RETRO -> if (primary) Color(0xFFF5E6C8) else Color(0xFF4A2A18)
        AppDesign.STEAMPUNK -> Color(0xFF2B1A0E)
        AppDesign.KAWAII -> if (primary) Color.White else s.text
        AppDesign.CARTOON -> Color.White
        AppDesign.SCHLICHT -> if (primary) Color.White else s.accent
    }
    val border: BorderStroke? = when (s.design) {
        AppDesign.RADAR, AppDesign.HUD, AppDesign.ENDZEIT -> BorderStroke(if (s.design == AppDesign.ENDZEIT) 2.dp else 1.dp, s.accent)
        AppDesign.RETRO, AppDesign.CARTOON -> BorderStroke(3.dp, Color(0xFF111111).takeIf { s.design == AppDesign.CARTOON } ?: Color(0xFF4A2A18))
        AppDesign.STEAMPUNK -> BorderStroke(2.dp, Color(0xFF1C0F06))
        else -> null
    }
    val shadow: Color? = when (s.design) {
        AppDesign.ZEITREISE -> if (primary) Color(0xFF7A3300) else Color(0xFF18191C)
        AppDesign.RETRO -> Color(0xFF4A2A18)
        AppDesign.KAWAII -> if (primary) Color(0xFFE0457B) else Color(0xFF5FB8A3)
        AppDesign.CARTOON -> Color(0xFF111111)
        AppDesign.STEAMPUNK -> Color(0xFF1C0F06)
        else -> null
    }
    val (font, weight, fSize, spacing) = when (s.design) {
        AppDesign.RADAR -> Quad(Fonts.shareTech, FontWeight.Normal, 14.sp, 2.sp)
        AppDesign.ZEITREISE -> Quad(Fonts.orbitron, FontWeight.Black, 12.sp, 2.sp)
        AppDesign.ENDZEIT -> Quad(Fonts.vt323, FontWeight.Normal, 22.sp, 1.sp)
        AppDesign.RETRO -> Quad(Fonts.shrikhand, FontWeight.Normal, 18.sp, 0.sp)
        AppDesign.STEAMPUNK -> Quad(Fonts.cinzel, FontWeight.Bold, 14.sp, 1.sp)
        AppDesign.HUD -> Quad(Fonts.orbitron, FontWeight.Bold, 11.sp, 3.sp)
        AppDesign.KAWAII -> Quad(Fonts.fredoka, FontWeight.SemiBold, 16.sp, 0.sp)
        AppDesign.CARTOON -> Quad(Fonts.bangers, FontWeight.Normal, 24.sp, 1.sp)
        AppDesign.SCHLICHT -> Quad(FontFamily.Default, FontWeight.SemiBold, 15.sp, 0.sp)
    }
    val corner = when (s.design) {
        AppDesign.RETRO, AppDesign.STEAMPUNK, AppDesign.KAWAII -> 30.dp
        AppDesign.ZEITREISE -> 4.dp
        else -> 0.dp
    }
    Box(
        modifier
            .fillMaxWidth()
            .graphicsLayer(alpha = if (enabled) 1f else 0.45f)
            .drawBehind {
                if (shadow != null) {
                    val off = if (s.design == AppDesign.CARTOON || s.design == AppDesign.RETRO) Offset(4.dp.toPx(), 4.dp.toPx()) else Offset(0f, 4.dp.toPx())
                    drawRoundRect(shadow, topLeft = off, size = size, cornerRadius = CornerRadius(corner.toPx()))
                }
            }
            .clip(shape)
            .background(bg)
            .then(if (border != null) Modifier.border(border, shape) else Modifier)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text,
            textAlign = TextAlign.Center,
            style = TextStyle(
                fontFamily = font, fontWeight = weight, fontSize = fSize, letterSpacing = spacing, color = fg,
                shadow = if (s.design == AppDesign.CARTOON) androidx.compose.ui.graphics.Shadow(Color(0xFF111111), Offset(3f, 3f)) else null,
            ),
        )
    }
}

private data class Quad(val a: FontFamily, val b: FontWeight, val c: TextUnit, val d: TextUnit)

// ---------- Ampel-Zähler (dienen gleichzeitig als Filter) ----------

@Composable
fun CountRow(s: DesignSpec, counts: Map<TrafficLight, Int>, filter: TrafficLight?, onToggle: (TrafficLight) -> Unit) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        for (l in TrafficLight.entries) {
            val n = counts[l] ?: 0
            val sel = filter == l
            DesignPanel(
                s, Modifier.weight(1f), edge = s.color(l), selected = sel,
                onClick = { onToggle(l) }, padding = 6.dp,
            ) {
                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    when (s.design) {
                        AppDesign.KAWAII -> DText("♥ $n", s, size = 20.sp, color = s.textColor(l), weight = FontWeight.Bold)
                        AppDesign.CARTOON -> Burst(s.color(l), "$n", 40.dp, 18.sp, if (l == TrafficLight.YELLOW) Color(0xFF111111) else Color.White)
                        AppDesign.RETRO -> DText("$n", s, size = 26.sp, font = Fonts.shrikhand, color = s.textColor(l))
                        AppDesign.ENDZEIT -> DText("$n", s, size = 26.sp, color = s.color(l))
                        AppDesign.SCHLICHT -> Row(verticalAlignment = Alignment.CenterVertically) {
                            Dot(s.color(l), 10.dp); Spacer(Modifier.width(6.dp))
                            DText("$n", s, size = 20.sp, weight = FontWeight.SemiBold)
                        }
                        else -> DText("$n", s, size = 22.sp, font = s.titleFont, weight = s.titleWeight, color = s.textColor(l))
                    }
                    DText(
                        s.words.count.getValue(l) + if (sel) " ✓" else "", s,
                        size = s.smallSize, color = if (s.design == AppDesign.CARTOON || s.design == AppDesign.STEAMPUNK || s.design == AppDesign.KAWAII || s.design == AppDesign.RETRO) s.panelText else s.textColor(l),
                        maxLines = 1, glow = false,
                    )
                }
            }
        }
    }
}

// ---------- Sprechblase mit Maskottchen ----------

@Composable
fun MascotBubble(s: DesignSpec, mood: TrafficLight, text: String, mascotSize: Dp = 56.dp) {
    Row(verticalAlignment = Alignment.Bottom) {
        if (s.design == AppDesign.KAWAII) BunnyMascot(mood, mascotSize) else CanManMascot(mood, mascotSize)
        Spacer(Modifier.width(6.dp))
        val shape = RoundedCornerShape(if (s.design == AppDesign.KAWAII) 16.dp else 18.dp)
        Box(
            Modifier
                .weight(1f)
                .background(Color.White, shape)
                .border(if (s.design == AppDesign.KAWAII) 2.dp else 3.dp, if (s.design == AppDesign.KAWAII) s.accent else Color(0xFF111111), shape)
                .padding(horizontal = 10.dp, vertical = 6.dp),
        ) {
            DText(text, s, size = 13.sp, color = s.panelText, glow = false)
        }
    }
}

// ---------- Eingabefeld (Tastatur nur für Namen und Notiz) ----------

@Composable
fun DesignTextField(s: DesignSpec, label: String, value: String, onChange: (String) -> Unit, singleLine: Boolean = true) {
    DesignPanel(s, Modifier.fillMaxWidth(), padding = 8.dp) {
        DText(label, s, size = s.smallSize, color = s.panelText.copy(alpha = 0.7f), font = if (s.design == AppDesign.STEAMPUNK) Fonts.cinzel else s.bodyFont, glow = false, spacing = 1.sp)
        BasicTextField(
            value = value,
            onValueChange = onChange,
            singleLine = singleLine,
            cursorBrush = SolidColor(s.accent),
            textStyle = TextStyle(
                fontFamily = if (s.design == AppDesign.CARTOON) Fonts.bangers else s.bodyFont,
                fontWeight = s.bodyWeight,
                fontSize = if (s.design == AppDesign.CARTOON) 20.sp else (s.bodySize.value + 1).sp,
                color = s.panelText,
                letterSpacing = if (s.design == AppDesign.CARTOON) 1.sp else TextUnit.Unspecified,
            ),
            modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
        )
    }
}

// ---------- Wischrädchen ----------

private val ITEM_H = 40.dp

/** Eine Spalte zum Wischen. Der mittlere Eintrag ist ausgewählt; Antippen wählt ebenfalls aus. */
@Composable
fun WheelColumn(s: DesignSpec, items: List<String>, selected: Int, onSelect: (Int) -> Unit, modifier: Modifier = Modifier) {
    val state = rememberLazyListState(initialFirstVisibleItemIndex = selected.coerceIn(0, items.lastIndex))
    val fling = rememberSnapFlingBehavior(lazyListState = state)
    val scope = rememberCoroutineScope()
    val currentItems by rememberUpdatedState(items)
    val currentOnSelect by rememberUpdatedState(onSelect)
    val itemPx = with(androidx.compose.ui.platform.LocalDensity.current) { ITEM_H.toPx() }

    // Von außen geänderte Auswahl (z. B. Tag gekürzt) nachziehen
    LaunchedEffect(selected, items.size) {
        val target = selected.coerceIn(0, items.lastIndex)
        if (!state.isScrollInProgress && state.firstVisibleItemIndex != target) state.scrollToItem(target)
    }
    LaunchedEffect(state) {
        snapshotFlow { state.isScrollInProgress }
            .distinctUntilChanged()
            .filter { !it }
            .collect {
                val idx = state.firstVisibleItemIndex + if (state.firstVisibleItemScrollOffset > itemPx / 2) 1 else 0
                currentOnSelect(idx.coerceIn(0, currentItems.lastIndex))
            }
    }
    Box(modifier.height(ITEM_H * 3)) {
        // Auswahlbalken in der Mitte
        Box(
            Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .height(ITEM_H)
                .background(s.accent.copy(alpha = if (s.dark) 0.16f else 0.22f), wheelShape(s)),
        )
        LazyColumn(state = state, flingBehavior = fling, modifier = Modifier.fillMaxWidth().height(ITEM_H * 3)) {
            item { Spacer(Modifier.height(ITEM_H)) }
            itemsIndexed(items) { i, label ->
                val on = i == selected
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(ITEM_H)
                        .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                            scope.launch { state.animateScrollToItem(i) }
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    DText(
                        label, s,
                        size = if (on) (s.bodySize.value + 3).sp else s.bodySize,
                        color = if (on) wheelSelText(s) else s.panelText.copy(alpha = 0.45f),
                        font = wheelFont(s),
                        weight = if (on) FontWeight.Bold else s.bodyWeight,
                        maxLines = 1,
                        glow = on && s.glow,
                    )
                }
            }
            item { Spacer(Modifier.height(ITEM_H)) }
        }
    }
}

private fun wheelShape(s: DesignSpec): Shape = when (s.design) {
    AppDesign.KAWAII, AppDesign.RETRO, AppDesign.STEAMPUNK -> RoundedCornerShape(12.dp)
    AppDesign.HUD -> CutCornerShape(6.dp)
    else -> RectangleShape
}

private fun wheelFont(s: DesignSpec) = when (s.design) {
    AppDesign.CARTOON -> Fonts.bangers
    AppDesign.ZEITREISE -> Fonts.orbitron
    AppDesign.HUD -> Fonts.orbitron
    AppDesign.STEAMPUNK -> Fonts.cinzel
    else -> s.bodyFont
}

private fun wheelSelText(s: DesignSpec) = when (s.design) {
    AppDesign.ZEITREISE -> Color(0xFFFFAE00)
    AppDesign.KAWAII -> Color(0xFFE0457B)
    AppDesign.CARTOON -> Color(0xFFFF2E2E)
    AppDesign.RETRO -> Color(0xFFB8461F)
    AppDesign.STEAMPUNK -> Color(0xFF7A5520)
    else -> s.panelText
}

private val MONTHS = listOf("Jan.", "Feb.", "März", "Apr.", "Mai", "Juni", "Juli", "Aug.", "Sept.", "Okt.", "Nov.", "Dez.")

/** Datum per Wischrädchen: Tag, Monat, Jahr. */
@Composable
fun DateWheel(s: DesignSpec, date: LocalDate, onChange: (LocalDate) -> Unit) {
    val today = remember { LocalDate.now() }
    val years = remember { (today.year - 2..today.year + 15).toList() }
    val daysInMonth = YearMonth.of(date.year, date.monthValue).lengthOfMonth()
    val current by rememberUpdatedState(date)
    fun build(y: Int, m: Int, d: Int): LocalDate {
        val max = YearMonth.of(y, m).lengthOfMonth()
        return LocalDate.of(y, m, d.coerceAtMost(max))
    }
    DesignPanel(s, Modifier.fillMaxWidth(), padding = 6.dp) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            WheelColumn(
                s, (1..daysInMonth).map { "%02d".format(it) }, date.dayOfMonth - 1,
                { i -> onChange(build(current.year, current.monthValue, i + 1)) }, Modifier.weight(0.8f),
            )
            WheelColumn(
                s, MONTHS, date.monthValue - 1,
                { i -> onChange(build(current.year, i + 1, current.dayOfMonth)) }, Modifier.weight(1.1f),
            )
            WheelColumn(
                s, years.map { "$it" }, years.indexOf(date.year).coerceAtLeast(0),
                { i -> onChange(build(years[i], current.monthValue, current.dayOfMonth)) }, Modifier.weight(1.1f),
            )
        }
    }
}

/** Menge per Wischrädchen ("–" = keine Angabe). */
@Composable
fun QuantityWheel(s: DesignSpec, quantity: Int?, onChange: (Int?) -> Unit) {
    val items = remember { listOf("–") + (1..99).map { "$it" } }
    DesignPanel(s, Modifier.fillMaxWidth(), padding = 6.dp) {
        WheelColumn(s, items, quantity ?: 0, { i -> onChange(if (i == 0) null else i) }, Modifier.fillMaxWidth())
    }
}

@Composable
fun WheelLabel(s: DesignSpec, text: String) {
    Row(Modifier.fillMaxWidth().padding(top = 4.dp, bottom = 2.dp)) {
        DText(text, s, Modifier.weight(1f), size = s.smallSize, color = s.muted, spacing = 1.sp, glow = false)
        DText("↕ wischen", s, size = s.smallSize, color = s.muted, glow = false)
    }
}

@Composable
fun ColumnScope.Gap(h: Dp = 8.dp) = Spacer(Modifier.height(h))

@Composable
fun RowScope.Grow() = Spacer(Modifier.weight(1f))

@Composable
fun Pill(s: DesignSpec, text: String, color: Color, textColor: Color) {
    Box(
        Modifier
            .background(color, CircleShape)
            .then(if (s.design == AppDesign.RETRO) Modifier.border(2.dp, Color(0xFF4A2A18), CircleShape) else Modifier)
            .padding(horizontal = 9.dp, vertical = 2.dp),
    ) {
        DText(text, s, size = 12.sp, color = textColor, weight = FontWeight.Bold, glow = false, maxLines = 1)
    }
}
