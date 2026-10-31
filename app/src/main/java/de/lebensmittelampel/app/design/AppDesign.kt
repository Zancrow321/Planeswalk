package de.lebensmittelampel.app.design

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

enum class AppDesign(val title: String) {
    RADAR("Radar"),
    ZEITREISE("Zurück in die Zukunft"),
    ENDZEIT("Fallout"),
    RETRO("Retro"),
    STEAMPUNK("Steampunk"),
    HUD("Iron Man"),
    KAWAII("Kawaii"),
    CARTOON("90er-Cartoon"),
    SCHLICHT("Schlicht"),
}

/** Speichert das gewählte Design dauerhaft auf dem Gerät. */
class DesignPrefs(context: Context) {
    private val sp = context.getSharedPreferences(FILE, Context.MODE_PRIVATE)

    private val _design = MutableStateFlow(read(context))
    val design: StateFlow<AppDesign> = _design

    private val _hintSeen = MutableStateFlow(sp.getBoolean(KEY_HINT, false))
    val hintSeen: StateFlow<Boolean> = _hintSeen

    fun set(design: AppDesign) {
        sp.edit().putString(KEY, design.name).putBoolean(KEY_HINT, true).apply()
        _design.value = design
        _hintSeen.value = true
    }

    fun markHintSeen() {
        sp.edit().putBoolean(KEY_HINT, true).apply()
        _hintSeen.value = true
    }

    companion object {
        private const val FILE = "design"
        private const val KEY = "design"
        private const val KEY_HINT = "hint_seen"

        fun read(context: Context): AppDesign {
            val name = context.getSharedPreferences(FILE, Context.MODE_PRIVATE).getString(KEY, null)
            return AppDesign.entries.firstOrNull { it.name == name } ?: AppDesign.RADAR
        }
    }
}
