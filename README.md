# Planeswalk

Ein Multiplayer-3D-Sandbox-Spiel in Unity: Spieler laufen frei durch eine Welt und duellieren sich in Magic: The Gathering. Dazu kommt eine automatische Deck-gegen-Deck-Simulation. Persoenliches Lernprojekt.

## Aufbau

| Ordner | Inhalt |
|---|---|
| `unity/Planeswalk/` | Unity-Projekt (Client und spaeter Welt-Server) |
| `server/` | Headless-Forge-Spielserver (Java), kommt in Schritt 2 |
| `docs/` | Design-Dokumente, zuerst [Forge-Bridge](docs/forge-bridge.md) |

## Regel-Engine

Die MTG-Regeln kommen von [Forge](https://github.com/Card-Forge/forge) (Java, GPL-3.0). Forge laeuft als eigener Prozess; der Unity-Client spricht per WebSocket/JSON mit ihm. Details stehen in [docs/forge-bridge.md](docs/forge-bridge.md). Kartendaten und Bilder kommen von der [Scryfall-API](https://scryfall.com/docs/api).

## Einrichtung

1. Git LFS installieren: `git lfs install`
2. Repo klonen, dann `unity/Planeswalk/` im Unity Hub als Projekt hinzufuegen (Unity 6 LTS empfohlen). Unity erzeugt `Library/` und die Projekteinstellungen beim ersten Oeffnen.
3. Gekaufte Asset-Store-Pakete nach `Assets/ThirdParty/` importieren. Dieser Ordner ist absichtlich in `.gitignore`, weil Store-Lizenzen das Weitergeben meist verbieten.

## Fahrplan

1. Repo und Bridge-Design (dieses Commit)
2. Headless Forge plus Scryfall-Anbindung
3. Unity-Duell-Client
4. Multiplayer-Sandbox-Welt

## Rechtliches

Inoffizielles Fan-Projekt, nicht mit Wizards of the Coast verbunden. Magic: The Gathering ist eine Marke von Wizards of the Coast. Kartenbilder und -texte unterliegen deren Rechten und den [Scryfall-Richtlinien](https://scryfall.com/docs/api). Forge ist GPL-3.0: Wer den Server mit Forge-Code verteilt, muss dessen Quellcode unter GPL mitliefern.
