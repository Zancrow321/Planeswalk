package de.lebensmittelampel.app.data

/** Die Listen der App. Neue Einträge hier ergänzen. */
enum class Category(val label: String) {
    VORRAT("Vorrat"),
    TIEFKUEHL("Tiefkühlschrank");

    companion object {
        fun from(name: String?): Category = entries.firstOrNull { it.name == name } ?: VORRAT
    }
}
