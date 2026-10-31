@file:OptIn(ExperimentalTextApi::class)

package de.lebensmittelampel.app.design

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.lebensmittelampel.app.R
import de.lebensmittelampel.app.data.TrafficLight

object Fonts {
    private fun v(res: Int, w: Int) =
        Font(res, FontWeight(w), variationSettings = FontVariation.Settings(FontVariation.weight(w)))

    val shareTech = FontFamily(Font(R.font.share_tech_mono))
    val orbitron = FontFamily(v(R.font.orbitron, 500), v(R.font.orbitron, 700), v(R.font.orbitron, 900))
    val vt323 = FontFamily(Font(R.font.vt323))
    val shrikhand = FontFamily(Font(R.font.shrikhand))
    val rubik = FontFamily(v(R.font.rubik, 400), v(R.font.rubik, 600), v(R.font.rubik, 800))
    val cinzel = FontFamily(v(R.font.cinzel, 500), v(R.font.cinzel, 700))
    val imFell = FontFamily(Font(R.font.im_fell_english))
    val rajdhani = FontFamily(
        Font(R.font.rajdhani_semibold, FontWeight.SemiBold),
        Font(R.font.rajdhani_bold, FontWeight.Bold),
    )
    val fredoka = FontFamily(v(R.font.fredoka, 400), v(R.font.fredoka, 600), v(R.font.fredoka, 700))
    val bangers = FontFamily(Font(R.font.bangers))
    val comicNeue = FontFamily(Font(R.font.comic_neue_bold, FontWeight.Bold))
}

/** Farben, Schriften und Formen eines Designs. */
data class DesignSpec(
    val design: AppDesign,
    val dark: Boolean,
    val bg: Color,
    val text: Color,
    val muted: Color,
    val accent: Color,
    val onAccent: Color,
    val panelBg: Color,
    val panelText: Color,
    val panelBorder: Color,
    val red: Color,
    val yellow: Color,
    val green: Color,
    /** Lesbare Textfarbe für Gelb (auf hellem Grund oft dunkler als die Füllfarbe). */
    val yellowText: Color = yellow,
    val greenText: Color = green,
    val titleFont: FontFamily,
    val bodyFont: FontFamily,
    val titleWeight: FontWeight = FontWeight.Normal,
    val bodyWeight: FontWeight = FontWeight.Normal,
    val titleSize: TextUnit = 22.sp,
    val bodySize: TextUnit = 15.sp,
    val smallSize: TextUnit = 11.sp,
    val corner: Dp = 4.dp,
    val borderWidth: Dp = 1.dp,
    val glow: Boolean = false,
    val words: Words = wordsFor(design),
) {
    fun color(l: TrafficLight) = when (l) {
        TrafficLight.RED -> red
        TrafficLight.YELLOW -> yellow
        TrafficLight.GREEN -> green
    }

    fun textColor(l: TrafficLight) = when (l) {
        TrafficLight.RED -> red
        TrafficLight.YELLOW -> yellowText
        TrafficLight.GREEN -> greenText
    }
}

val LocalDesign = staticCompositionLocalOf { specFor(AppDesign.RADAR) }


fun specFor(design: AppDesign): DesignSpec = when (design) {
    AppDesign.RADAR -> DesignSpec(
        design, dark = true,
        bg = Color(0xFF010804), text = Color(0xFF5DFF8F), muted = Color(0xA65DFF8F),
        accent = Color(0xFF5DFF8F), onAccent = Color(0xFF010804),
        panelBg = Color(0x145DFF8F), panelText = Color(0xFF5DFF8F), panelBorder = Color(0xFF1F7A3F),
        red = Color(0xFFFF4B3E), yellow = Color(0xFFFFD23B), green = Color(0xFF5DFF8F),
        titleFont = Fonts.shareTech, bodyFont = Fonts.shareTech, titleSize = 20.sp, glow = true,
    )
    AppDesign.ZEITREISE -> DesignSpec(
        design, dark = true,
        bg = Color(0xFF2A2C31), text = Color(0xFFE9E6DC), muted = Color(0xFF9A9CA3),
        accent = Color(0xFFFF8A1F), onAccent = Color(0xFF1A0D00),
        panelBg = Color(0xFF121316), panelText = Color(0xFFE9E6DC), panelBorder = Color(0xFF57595F),
        red = Color(0xFFFF3A22), yellow = Color(0xFFFFAE00), green = Color(0xFF3DFF57),
        titleFont = Fonts.orbitron, bodyFont = Fonts.orbitron, titleWeight = FontWeight.Black,
        bodyWeight = FontWeight.Medium, titleSize = 18.sp, bodySize = 13.sp, borderWidth = 2.dp, glow = true,
    )
    AppDesign.ENDZEIT -> DesignSpec(
        design, dark = true,
        bg = Color(0xFF021208), text = Color(0xFF41FF7A), muted = Color(0x9941FF7A),
        accent = Color(0xFF41FF7A), onAccent = Color(0xFF021208),
        panelBg = Color(0xFF021208), panelText = Color(0xFF41FF7A), panelBorder = Color(0xFF41FF7A),
        red = Color(0xFFFF6A4A), yellow = Color(0xFFFFD15A), green = Color(0xFF41FF7A),
        titleFont = Fonts.vt323, bodyFont = Fonts.vt323, titleSize = 26.sp, bodySize = 20.sp,
        smallSize = 16.sp, corner = 0.dp, borderWidth = 2.dp, glow = true,
    )
    AppDesign.RETRO -> DesignSpec(
        design, dark = false,
        bg = Color(0xFFF5E6C8), text = Color(0xFF4A2A18), muted = Color(0xB34A2A18),
        accent = Color(0xFF2F7F78), onAccent = Color(0xFFF5E6C8),
        panelBg = Color(0xFFFFF6E4), panelText = Color(0xFF4A2A18), panelBorder = Color(0xFF4A2A18),
        red = Color(0xFFD9442B), yellow = Color(0xFFEFA72A), green = Color(0xFF5C8F3A),
        yellowText = Color(0xFFB97A0E),
        titleFont = Fonts.shrikhand, bodyFont = Fonts.rubik, bodyWeight = FontWeight.SemiBold,
        titleSize = 30.sp, corner = 16.dp, borderWidth = 3.dp,
    )
    AppDesign.STEAMPUNK -> DesignSpec(
        design, dark = true,
        bg = Color(0xFF3A2112), text = Color(0xFFEFE0BC), muted = Color(0xCCEFE0BC),
        accent = Color(0xFFE2BF6A), onAccent = Color(0xFF2B1A0E),
        panelBg = Color(0xFFEFE0BC), panelText = Color(0xFF2B1A0E), panelBorder = Color(0xFFA57A2C),
        red = Color(0xFFB4281C), yellow = Color(0xFFD39A1C), green = Color(0xFF4C7A2B),
        yellowText = Color(0xFF9A6D0B),
        titleFont = Fonts.cinzel, bodyFont = Fonts.imFell, titleWeight = FontWeight.Bold,
        titleSize = 17.sp, bodySize = 16.sp, smallSize = 13.sp, corner = 4.dp, borderWidth = 2.dp,
    )
    AppDesign.HUD -> DesignSpec(
        design, dark = true,
        bg = Color(0xFF020812), text = Color(0xFF49D8FF), muted = Color(0xBF49D8FF),
        accent = Color(0xFF49D8FF), onAccent = Color(0xFF020812),
        panelBg = Color(0x1A49D8FF), panelText = Color(0xFFDFF6FF), panelBorder = Color(0xFF0F4C66),
        red = Color(0xFFFF4D3D), yellow = Color(0xFFFFB43C), green = Color(0xFF3DFFB4),
        titleFont = Fonts.orbitron, bodyFont = Fonts.rajdhani, titleWeight = FontWeight.Bold,
        bodyWeight = FontWeight.SemiBold, titleSize = 15.sp, bodySize = 16.sp, glow = true,
    )
    AppDesign.KAWAII -> DesignSpec(
        design, dark = false,
        bg = Color(0xFFFFF4F8), text = Color(0xFF6B3A55), muted = Color(0xBF6B3A55),
        accent = Color(0xFFFF8FB8), onAccent = Color.White,
        panelBg = Color.White, panelText = Color(0xFF6B3A55), panelBorder = Color(0xFFFFD6E5),
        red = Color(0xFFFF5C8A), yellow = Color(0xFFFFC94D), green = Color(0xFF5FD3A0),
        yellowText = Color(0xFFE8A20F), greenText = Color(0xFF2FAE78),
        titleFont = Fonts.fredoka, bodyFont = Fonts.fredoka, titleWeight = FontWeight.Bold,
        bodyWeight = FontWeight.SemiBold, titleSize = 21.sp, corner = 20.dp, borderWidth = 2.dp,
    )
    AppDesign.CARTOON -> DesignSpec(
        design, dark = false,
        bg = Color(0xFFFFD400), text = Color(0xFF111111), muted = Color(0xCC111111),
        accent = Color(0xFFFF2E2E), onAccent = Color.White,
        panelBg = Color.White, panelText = Color(0xFF111111), panelBorder = Color(0xFF111111),
        red = Color(0xFFFF2E2E), yellow = Color(0xFFFFB800), green = Color(0xFF19C24A),
        yellowText = Color(0xFF8A5A00), greenText = Color(0xFF0E7F2F),
        titleFont = Fonts.bangers, bodyFont = Fonts.comicNeue, bodyWeight = FontWeight.Bold,
        titleSize = 30.sp, corner = 0.dp, borderWidth = 3.dp,
    )
    AppDesign.SCHLICHT -> DesignSpec(
        design, dark = false,
        bg = Color(0xFFF6F7F4), text = Color(0xFF1C1C1A), muted = Color(0xFF5E605A),
        accent = Color(0xFF2E7D32), onAccent = Color.White,
        panelBg = Color.White, panelText = Color(0xFF1C1C1A), panelBorder = Color(0xFFE0E2DC),
        red = Color(0xFFE53935), yellow = Color(0xFFFBC02D), green = Color(0xFF43A047),
        yellowText = Color(0xFF9A7300),
        titleFont = FontFamily.Default, bodyFont = FontFamily.Default, titleWeight = FontWeight.SemiBold,
        titleSize = 22.sp, corner = 12.dp,
    )
}
