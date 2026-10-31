# Design: Wie Unity mit Forge spricht

Stand: 2026-10-31, Entwurf

## Ziel

Unity (C#) zeigt die 3D-Welt und das Spielbrett. Forge (Java) kennt alle Magic-Regeln und tausende Karten. Wir schreiben die Regeln nicht neu, sondern lassen Forge als Server laufen und schicken nur Zustaende und Entscheidungen hin und her.

## Architektur

```
 Unity-Client A ─┐                         ┌─ Forge-Match 1 (Thread)
                 ├─ WebSocket/JSON ─► Forge-Server (Java, headless) ─┼─ Forge-Match 2
 Unity-Client B ─┘                         └─ Sim-Worker (KI vs KI)
        │
        └─ Netcode for GameObjects ─► Unity-Welt-Server (Schritt 4: Laufen, Chat, Duell-Anfragen)
```

Zwei getrennte Server mit klaren Aufgaben:

* **Welt-Server (Unity, dediziert):** Positionen, Avatare, Chat, wer wen herausfordert. Kennt keine Magic-Regeln.
* **Forge-Server (Java):** Fuehrt Duelle aus. Kennt keine 3D-Welt. Ein Duell ist ein `Match` mit eigener ID.

Wenn zwei Spieler in der Welt ein Duell starten, fragt der Welt-Server beim Forge-Server ein Match an, bekommt eine `matchId` und ein Token pro Spieler. Die Clients verbinden sich dann direkt mit dem Forge-Server.

## Warum ein eigener Java-Prozess

| Option | Bewertung |
|---|---|
| **Forge als eigener Prozess, WebSocket/JSON** | Gewaehlt. Forge bleibt unveraendert Java, Unity bleibt C#. Leicht zu debuggen (JSON lesbar), Server austauschbar. |
| Forge per IKVM nach .NET uebersetzen | Forge ist riesig und nutzt viele Java-Bibliotheken; Uebersetzen ist fragil und schwer aktuell zu halten. |
| JNI / eingebettete JVM in Unity | Absturzgefahr, schwierig auf allen Plattformen, Lizenzvermischung (GPL) im Client. |
| Regeln selbst in C# schreiben | Jahre Arbeit. |

Bonus: Weil der Client nur JSON spricht, steckt kein GPL-Code im Unity-Client. Der Forge-Server selbst bleibt GPL.

## Forge headless betreiben

Forge trennt Spiellogik (`forge-game`, `forge-ai`, `forge-core`) von der Oberflaeche (`forge-gui-desktop`, `forge-gui-mobile`). Entscheidungen eines menschlichen Spielers laufen ueber den `PlayerController`. Plan fuer Schritt 2:

1. Forge als Maven-Abhaengigkeit bzw. Git-Submodul einbinden, nur die Module ohne GUI.
2. Eigene Klasse `RemotePlayerController extends PlayerController`: Jede Frage von Forge (Ziel waehlen, Blocker zuweisen, Mulligan ...) wird zu einer JSON-Nachricht `decision.request`. Der Forge-Thread wartet, bis `decision.response` vom Client kommt (mit Timeout).
3. Fuer KI-Gegner und Simulation einfach Forges `PlayerControllerAi`.
4. Ein Spielzustands-Listener (Forge-Events) erzeugt `state.delta`-Nachrichten.

Offener Punkt: Wie sauber `forge-game` ohne GUI-Klassen startet, muss in Schritt 2 geprueft werden (Forge hat bereits einen Simulationsmodus `forge sim`, das ist ein gutes Zeichen).

## Protokoll (Entwurf)

WebSocket, eine JSON-Nachricht pro Frame. Jede Nachricht hat `type`, `matchId` und eine laufende `seq`.

Client → Server:

```json
{ "type": "match.join",        "matchId": "m42", "token": "..." }
{ "type": "decision.response", "matchId": "m42", "requestId": 17, "choice": { "cardIds": [301] } }
{ "type": "action.pass",       "matchId": "m42" }
{ "type": "match.concede",     "matchId": "m42" }
```

Server → Client:

```json
{ "type": "state.full",  "matchId": "m42", "seq": 1,  "state": { "turn": 1, "phase": "MAIN1", "players": [...], "zones": {...} } }
{ "type": "state.delta", "matchId": "m42", "seq": 2,  "events": [ { "e": "zoneChange", "card": 301, "from": "Hand", "to": "Battlefield" } ] }
{ "type": "decision.request", "matchId": "m42", "requestId": 17, "kind": "chooseTargets", "prompt": "Ziel fuer Lightning Bolt waehlen", "options": [ ... ], "min": 1, "max": 1, "timeoutMs": 60000 }
{ "type": "match.end",   "matchId": "m42", "winner": "playerA", "reason": "lifeZero" }
```

Regeln:

* Der Server ist die einzige Wahrheit. Der Client sendet nur Entscheidungen, nie Zustaende.
* Verdeckte Informationen (Hand, Bibliothek des Gegners) filtert der Server pro Spieler.
* Nach einem Verbindungsabbruch schickt der Client `match.join` erneut und bekommt `state.full`.
* Karten werden per Forge-ID und Kartenname referenziert; der Client holt Bilder ueber Scryfall (`/cards/named?exact=`) und cached sie lokal.

## Automatische Simulation

Gleicher Server, anderer Einstieg: `POST /sim` mit zwei Decklisten und Anzahl Spiele. Der Server startet KI-gegen-KI-Matches parallel in einem Thread-Pool ohne WebSocket-Clients und liefert Statistik (Siegquote, durchschnittliche Zuglaenge). Optional kann ein Unity-Client als Zuschauer per `match.join` mit Rolle `spectator` zusehen.

## Technik-Wahl

* Forge-Server: Java 17, Javalin (WebSocket + REST, klein und einfach), Jackson fuer JSON.
* Unity: Unity 6 LTS, `ClientWebSocket` (oder NativeWebSocket fuer WebGL), Newtonsoft.Json, Netcode for GameObjects fuer die Welt.
* Gemeinsames Schema: Nachrichtentypen in `docs/protocol/*.json` als JSON-Schema, daraus spaeter C#- und Java-Klassen generieren.

## Naechste Schritte (Schritt 2)

1. Forge bauen, `forge sim` lokal laufen lassen.
2. Minimalen Server: zwei KI-Decks gegeneinander, Ergebnis per REST.
3. `RemotePlayerController` und WebSocket-Protokoll mit einem Test-Client (Konsole).
4. Scryfall-Bild-Cache.
