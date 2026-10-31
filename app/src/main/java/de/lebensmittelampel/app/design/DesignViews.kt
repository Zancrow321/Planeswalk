package de.lebensmittelampel.app.design

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.lebensmittelampel.app.data.Product
import de.lebensmittelampel.app.data.TrafficLight
import de.lebensmittelampel.app.data.daysUntil
import de.lebensmittelampel.app.data.remainingText
import de.lebensmittelampel.app.data.restShort
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val DATE = DateTimeFormatter.ofPattern("dd.MM.yyyy")
private val MONL = listOf("Jan.", "Feb.", "März", "Apr.", "Mai", "Juni", "Juli", "Aug.", "Sept.", "Okt.", "Nov.", "Dez.")

fun worstLight(counts: Map<TrafficLight, Int>): TrafficLight = when {
    (counts[TrafficLight.RED] ?: 0) > 0 -> TrafficLight.RED
    (counts[TrafficLight.YELLOW] ?: 0) > 0 -> TrafficLight.YELLOW
    else -> TrafficLight.GREEN
}

/** Kopfbereich der Liste. Langes Drücken darauf öffnet die Design-Auswahl. */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ListHeader(
    s: DesignSpec,
    products: List<Product>,
    counts: Map<TrafficLight, Int>,
    today: LocalDate,
    onLongPress: () -> Unit,
) {
    val total = counts.values.sum()
    val red = counts[TrafficLight.RED] ?: 0
    val press = Modifier.combinedClickable(
        interactionSource = remember { MutableInteractionSource() }, indication = null,
        onClick = {}, onLongClick = onLongPress,
    )
    Column(Modifier.fillMaxWidth().then(press), horizontalAlignment = Alignment.CenterHorizontally) {
        when (s.design) {
            AppDesign.RADAR -> {
                Row(
                    Modifier.fillMaxWidth().drawBehind {
                        drawLine(Color(0xFF1F7A3F), Offset(0f, size.height), Offset(size.width, size.height), 1.dp.toPx())
                    }.padding(bottom = 6.dp),
                    verticalAlignment = Alignment.Bottom,
                ) {
                    DTitle(s.words.appTitle, s)
                    Grow()
                    DText("SEKTOR KÜCHE · $total KONTAKTE", s, size = 9.sp, color = s.muted)
                }
                Spacer(Modifier.height(8.dp))
                RadarScope(products, today, s)
            }
            AppDesign.ZEITREISE -> {
                HazardStripe()
                Row(Modifier.fillMaxWidth().padding(vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                    DTitle(s.words.appTitle, s)
                    Grow()
                    DText("⚡", s, size = 22.sp, color = Color(0xFFFFD23B))
                }
                TimeCircuit(today, "ROT", "%02d".format(red), Color(0xFF4FD4FF), "GEGENWART", "$total PRODUKTE", rightColor = s.red)
            }
            AppDesign.ENDZEIT -> {
                Row(Modifier.fillMaxWidth().border(2.dp, s.accent).padding(horizontal = 8.dp, vertical = 2.dp)) {
                    DTitle(s.words.appTitle, s, size = 22.sp)
                    Grow()
                    DText(s.words.listCaption, s, size = 22.sp)
                }
                Spacer(Modifier.height(4.dp))
                DText("> VORRAT: $total EINTRÄGE", s, Modifier.fillMaxWidth())
            }
            AppDesign.RETRO -> {
                Box(
                    Modifier.fillMaxWidth().height(30.dp).padding(horizontal = 0.dp)
                        .background(
                            Brush.verticalGradient(
                                0f to Color(0xFFB8461F), 0.25f to Color(0xFFB8461F), 0.25f to Color(0xFFE8702A), 0.5f to Color(0xFFE8702A),
                                0.5f to Color(0xFFF2B134), 0.75f to Color(0xFFF2B134), 0.75f to Color(0xFF2F7F78), 1f to Color(0xFF2F7F78),
                            ),
                            RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp),
                        ),
                )
                Spacer(Modifier.height(10.dp))
                Column(Modifier.fillMaxWidth()) {
                    DTitle(s.words.appTitle, s)
                    DText("$total ${s.words.listCaption} · Stand ${today.format(DateTimeFormatter.ofPattern("dd.MM."))}", s, size = 12.sp, color = s.muted)
                }
            }
            AppDesign.STEAMPUNK -> BrassPlate(s, s.words.appTitle, "$red rot · ${counts[TrafficLight.YELLOW] ?: 0} gelb · ${counts[TrafficLight.GREEN] ?: 0} grün")
            AppDesign.HUD -> {
                Row(Modifier.fillMaxWidth()) {
                    DText(s.words.appTitle, s, size = 10.sp, font = Fonts.orbitron, weight = FontWeight.Bold, spacing = 2.sp)
                    Grow()
                    DText("SYS OK", s, size = 10.sp, font = Fonts.orbitron, weight = FontWeight.Bold, spacing = 2.sp)
                }
                Spacer(Modifier.height(4.dp))
                HudCore(counts, total, s)
            }
            AppDesign.KAWAII -> Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                BunnyMascot(worstLight(counts), 66.dp)
                Spacer(Modifier.width(6.dp))
                Column(Modifier.weight(1f)) {
                    DTitle(s.words.appTitle, s, color = Color(0xFFE0457B))
                    Spacer(Modifier.height(4.dp))
                    Box(
                        Modifier.background(Color.White, RoundedCornerShape(16.dp))
                            .border(2.dp, s.accent, RoundedCornerShape(16.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                    ) {
                        DText(
                            when {
                                total == 0 -> "Hallo! Ich bin Mimi. Lass uns deinen Vorrat füllen!"
                                red > 0 -> "$red ${if (red == 1) "Sache ist" else "Sachen sind"} schon rot. Lass sie uns bald essen!"
                                else -> "Alles frisch! Ich bin stolz auf dich ♡"
                            },
                            s, size = 12.sp,
                        )
                    }
                }
            }
            AppDesign.CARTOON -> Row(
                Modifier.fillMaxWidth()
                    .drawBehind { drawRect(Color(0xFF111111), topLeft = Offset(4.dp.toPx(), 4.dp.toPx()), size = size) }
                    .background(Color(0xFF1E6BFF))
                    .border(3.dp, Color(0xFF111111))
                    .padding(start = 6.dp, end = 8.dp, top = 6.dp),
                verticalAlignment = Alignment.Bottom,
            ) {
                CanManMascot(if (red > 0) TrafficLight.YELLOW else TrafficLight.GREEN, 92.dp)
                Column(Modifier.weight(1f).padding(bottom = 6.dp)) {
                    Box(
                        Modifier.background(Color.White, RoundedCornerShape(18.dp))
                            .border(3.dp, Color(0xFF111111), RoundedCornerShape(18.dp))
                            .padding(horizontal = 9.dp, vertical = 5.dp),
                    ) {
                        DText(
                            when {
                                total == 0 -> "Leeres Regal? Nicht mit mir, Kumpel!"
                                red > 0 -> "$red ${if (red == 1) "Dose ist" else "Dosen sind"} rot. Ran an die Power, Kumpel!"
                                else -> "Alles im grünen Bereich. STARK!"
                            },
                            s, size = 12.sp,
                        )
                    }
                    Spacer(Modifier.height(6.dp))
                    DTitle(s.words.appTitle, s, size = 28.sp, color = Color.White)
                }
            }
            AppDesign.SCHLICHT -> Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                DTitle(s.words.appTitle, s)
            }
        }
    }
}

@Composable
fun BrassPlate(s: DesignSpec, title: String, sub: String) {
    Row(
        Modifier.fillMaxWidth()
            .drawBehind { drawRoundRect(Color(0x99000000), topLeft = Offset(0f, 3.dp.toPx()), size = size, cornerRadius = androidx.compose.ui.geometry.CornerRadius(8.dp.toPx())) }
            .background(Brush.linearGradient(listOf(Color(0xFFE2BF6A), Color(0xFFA57A2C), Color(0xFF7A5520))), RoundedCornerShape(8.dp))
            .drawBehind {
                val r = 3.dp.toPx(); val m = 7.dp.toPx()
                for (p in listOf(Offset(m, m), Offset(size.width - m, m), Offset(m, size.height - m), Offset(size.width - m, size.height - m))) {
                    drawCircle(Brush.radialGradient(listOf(Color(0xFFFFF3C4), Color(0xFF6B4A17)), center = p, radius = r), r, p)
                }
            }
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SpinningGear(42.dp)
        Spacer(Modifier.width(10.dp))
        Column {
            DText(title, s, size = 16.sp, font = Fonts.cinzel, weight = FontWeight.Bold, color = Color(0xFF2B1A0E), spacing = 1.sp)
            DText(sub, s, size = 13.sp, color = Color(0xFF2B1A0E))
        }
    }
}

/** Eine Zeile der Produktliste im jeweiligen Design. */
@Composable
fun ProductRow(s: DesignSpec, p: Product, today: LocalDate, index: Int, onClick: () -> Unit) {
    val l = p.trafficLight(today)
    val days = daysUntil(p.expiry, today)
    val short = restShort(p.expiry, today)
    val qty = p.quantity?.let { " · ${it}x" } ?: ""
    val date = p.expiry.format(DATE)
    when (s.design) {
        AppDesign.RADAR -> DesignPanel(s, Modifier.fillMaxWidth(), edge = s.color(l), onClick = onClick, padding = 8.dp) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Dot(s.color(l), 14.dp, glow = true)
                Spacer(Modifier.width(8.dp))
                Column(Modifier.weight(1f)) {
                    DText(p.name.uppercase(), s, size = 13.sp, maxLines = 1)
                    DText("MHD $date$qty", s, size = 10.sp, color = s.muted)
                }
                Column(horizontalAlignment = Alignment.End) {
                    DText(short, s, size = 12.sp, color = s.color(l))
                    DText("PEIL %03d".format(((index * 137.5 + 30) % 360).toInt()), s, size = 9.sp, color = s.muted)
                }
            }
        }
        AppDesign.ZEITREISE -> Box(Modifier.fillMaxWidth().combinedClick(onClick)) {
            TimeCircuit(
                p.expiry, if (days < 0) "ÜBER" else "RESTTAGE", "%03d".format(kotlin.math.abs(days).coerceAtMost(999)),
                s.color(l), p.name, (p.quantity?.let { "${it}x · " } ?: "") + s.words.zone.getValue(l),
            )
        }
        AppDesign.ENDZEIT -> {
            val tag = when (l) { TrafficLight.RED -> "[!!]"; TrafficLight.YELLOW -> "[! ]"; TrafficLight.GREEN -> "[OK]" }
            Row(Modifier.fillMaxWidth().combinedClick(onClick).padding(vertical = 3.dp), verticalAlignment = Alignment.CenterVertically) {
                DText(tag, s, color = s.color(l))
                Spacer(Modifier.width(6.dp))
                Column(Modifier.weight(1f)) {
                    DText(p.name.uppercase(), s, maxLines = 1)
                    DText("  MHD $date$qty", s, size = 15.sp, color = s.muted)
                }
                DText(short, s, color = s.color(l))
            }
        }
        AppDesign.RETRO -> DesignPanel(s, Modifier.fillMaxWidth(), onClick = onClick, padding = 8.dp) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                RetroLamp(l, s)
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    DText(p.name, s, size = 15.sp, maxLines = 1)
                    DText("MHD $date$qty", s, size = 12.sp, color = s.muted, weight = FontWeight.Normal)
                }
                DText(short, s, size = 13.sp, color = s.textColor(l), weight = FontWeight.ExtraBold)
            }
        }
        AppDesign.STEAMPUNK -> DesignPanel(s, Modifier.fillMaxWidth(), onClick = onClick, padding = 6.dp) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                SteamGauge(days)
                Spacer(Modifier.width(8.dp))
                Column(Modifier.weight(1f)) {
                    DText(p.name, s, size = 14.sp, font = Fonts.cinzel, weight = FontWeight.Bold, color = s.panelText, maxLines = 1)
                    DText("haltbar bis ${p.expiry.dayOfMonth}. ${MONL[p.expiry.monthValue - 1]} ${p.expiry.year}$qty", s, size = 13.sp, color = s.panelText)
                    DText("${s.words.count.getValue(l).uppercase()} · $short", s, size = 11.sp, color = s.textColor(l), spacing = 1.sp)
                }
            }
        }
        AppDesign.HUD -> DesignPanel(s, Modifier.fillMaxWidth(), edge = s.color(l), onClick = onClick, padding = 6.dp) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                HudRing(days, s.color(l))
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    DText(p.name, s, size = 15.sp, color = s.panelText, maxLines = 1, glow = false)
                    DText("MHD $date$qty", s, size = 11.sp, color = s.muted, glow = false)
                }
                DText(short, s, size = 11.sp, font = Fonts.orbitron, weight = FontWeight.Bold, color = s.color(l))
            }
        }
        AppDesign.KAWAII -> DesignPanel(s, Modifier.fillMaxWidth(), onClick = onClick, padding = 9.dp) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                DText("♥", s, size = 20.sp, color = s.color(l))
                Spacer(Modifier.width(8.dp))
                Column(Modifier.weight(1f)) {
                    DText(p.name, s, size = 15.sp, maxLines = 1)
                    DText("MHD $date$qty", s, size = 11.sp, color = s.muted, weight = FontWeight.Normal)
                }
                Pill(s, short, s.color(l), if (l == TrafficLight.YELLOW) s.text else Color.White)
            }
        }
        AppDesign.CARTOON -> DesignPanel(
            s, Modifier.fillMaxWidth(), tilt = if (index % 2 == 0) -0.7f else 0.6f, onClick = onClick, padding = 4.dp,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f).padding(start = 6.dp)) {
                    DText(p.name, s, size = 20.sp, font = Fonts.bangers, spacing = 1.sp, maxLines = 1)
                    DText("MHD $date$qty", s, size = 11.sp)
                }
                Burst(s.color(l), short, 56.dp, 13.sp, if (l == TrafficLight.YELLOW) Color(0xFF111111) else Color.White)
            }
        }
        AppDesign.SCHLICHT -> DesignPanel(s, Modifier.fillMaxWidth(), onClick = onClick, padding = 14.dp) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Dot(s.color(l), 18.dp)
                Spacer(Modifier.width(14.dp))
                Column(Modifier.weight(1f)) {
                    DText(p.name, s, size = 16.sp, weight = FontWeight.SemiBold, maxLines = 1)
                    DText("MHD $date$qty", s, size = 14.sp, color = s.muted)
                    DText(remainingText(p.expiry, today), s, size = 12.sp, color = if (l == TrafficLight.RED) s.red else s.muted)
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
private fun Modifier.combinedClick(onClick: () -> Unit) = this.then(Modifier.combinedClickable(onClick = onClick))

/** Ampel-Anzeige für das gewählte MHD auf der Eingabeseite. */
@Composable
fun ZoneIndicator(s: DesignSpec, date: LocalDate, name: String) {
    val today = LocalDate.now()
    val l = de.lebensmittelampel.app.data.TrafficLight.of(date, today)
    val rest = remainingText(date, today)
    val word = s.words.zone.getValue(l)
    when (s.design) {
        AppDesign.RADAR -> Box(Modifier.fillMaxWidth().border(1.dp, s.color(l)).padding(horizontal = 10.dp, vertical = 8.dp)) {
            DText("$word · ${rest.uppercase()}", s, size = 12.sp, color = s.color(l))
        }
        AppDesign.ZEITREISE -> TimeCircuit(
            date, if (daysUntil(date, today) < 0) "ÜBER" else "RESTTAGE",
            "%03d".format(kotlin.math.abs(daysUntil(date, today)).coerceAtMost(999)), s.color(l),
            name.ifBlank { "NEUES ZIEL" }, word,
        )
        AppDesign.ENDZEIT -> DText(">> STATUS: ${s.words.count.getValue(l)} · ${rest.uppercase()}", s, color = s.color(l))
        AppDesign.RETRO -> Row(verticalAlignment = Alignment.CenterVertically) {
            RetroLamp(l, s)
            Spacer(Modifier.width(8.dp))
            Pill(s, word, s.color(l), Color(0xFF4A2A18))
            Spacer(Modifier.width(8.dp))
            DText(rest, s, size = 13.sp)
        }
        AppDesign.STEAMPUNK -> DesignPanel(s, Modifier.fillMaxWidth(), padding = 8.dp) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    DText("ZUSTAND", s, size = 9.sp, font = Fonts.cinzel, color = s.panelText.copy(alpha = 0.75f), spacing = 2.sp)
                    DText(word, s, size = 16.sp, color = s.textColor(l))
                    DText(rest, s, size = 13.sp, color = s.panelText)
                }
                SteamGauge(daysUntil(date, today))
            }
        }
        AppDesign.HUD -> DesignPanel(s, Modifier.fillMaxWidth(), edge = s.color(l), padding = 8.dp) {
            DText("STATUS", s, size = 8.sp, font = Fonts.orbitron, weight = FontWeight.Bold, spacing = 2.sp, color = s.muted)
            DText("$word · ${rest.uppercase()}", s, size = 15.sp, color = s.color(l))
        }
        AppDesign.KAWAII -> Row(verticalAlignment = Alignment.CenterVertically) {
            BunnyMascot(l, 44.dp)
            Spacer(Modifier.width(6.dp))
            Pill(s, word, s.color(l), if (l == TrafficLight.YELLOW) s.text else Color.White)
            Spacer(Modifier.width(6.dp))
            DText(rest, s, size = 12.sp, weight = FontWeight.Normal)
        }
        AppDesign.CARTOON -> Row(verticalAlignment = Alignment.CenterVertically) {
            Burst(s.color(l), word, 64.dp, 13.sp, if (l == TrafficLight.YELLOW) Color(0xFF111111) else Color.White)
            Spacer(Modifier.width(8.dp))
            DText("${s.words.count.getValue(l)} · ${rest.uppercase()}", s, size = 14.sp)
        }
        AppDesign.SCHLICHT -> Row(verticalAlignment = Alignment.CenterVertically) {
            Dot(s.color(l), 14.dp)
            Spacer(Modifier.width(8.dp))
            DText("Ampel: $word · $rest", s, size = 14.sp, color = s.textColor(l))
        }
    }
}

/** Vorschaukachel in der Design-Auswahl, gezeichnet im jeweiligen Design. */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DesignTile(design: AppDesign, selected: Boolean, labelColor: Color, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val t = specFor(design)
    val shape = RoundedCornerShape(14.dp)
    Column(
        modifier
            .background(Color.Transparent, shape)
            .border(if (selected) 3.dp else 1.dp, if (selected) t.accent else Color(0x55888888), shape)
            .combinedClickable(onClick = onClick)
            .padding(3.dp),
    ) {
        Column(
            Modifier.fillMaxWidth().height(130.dp)
                .background(Color.Transparent, RoundedCornerShape(11.dp))
                .then(Modifier.designBackground(t))
                .padding(10.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                Dot(t.red, 12.dp, t.glow); Dot(t.yellow, 12.dp, t.glow); Dot(t.green, 12.dp, t.glow)
            }
            when (design) {
                AppDesign.KAWAII -> BunnyMascot(TrafficLight.GREEN, 48.dp)
                AppDesign.CARTOON -> CanManMascot(TrafficLight.GREEN, 52.dp)
                AppDesign.STEAMPUNK -> SpinningGear(36.dp)
                else -> Spacer(Modifier.size(1.dp))
            }
            Text(
                t.words.appTitle,
                maxLines = 1,
                style = TextStyle(
                    fontFamily = t.titleFont, fontWeight = t.titleWeight,
                    fontSize = if (design == AppDesign.ENDZEIT) 20.sp else 14.sp,
                    color = when (design) {
                        AppDesign.RETRO -> Color(0xFFE8702A)
                        AppDesign.KAWAII -> Color(0xFFE0457B)
                        else -> t.text
                    },
                    shadow = if (t.glow) Shadow(t.text.copy(alpha = 0.6f), blurRadius = 8f) else null,
                ),
            )
        }
        Text(
            design.title + if (selected) "  ✓" else "",
            modifier = Modifier.fillMaxWidth().widthIn(min = 1.dp).padding(top = 4.dp, bottom = 2.dp),
            textAlign = TextAlign.Center,
            style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = labelColor),
        )
    }
}
