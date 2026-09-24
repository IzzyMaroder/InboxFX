# InboxFX
JavaFX email client-server over TCP sockets — MVC architecture, file-based persistence, concurrent connections.

## Overview

- **Mail Server** (`mailbox.Server`)
  - Manages the mailboxes of all registered users and persists messages to plain text files (no database).
  - Has a small JavaFX GUI that logs every client interaction in real time: connections/disconnections, mail sent, mail received, mail deleted, and delivery errors.
  - Listens on TCP port `8189` and spawns a new thread per incoming client connection, so multiple clients can be served concurrently.
  - Mailbox writes are synchronized to avoid race conditions when several clients send mail at the same time.

- **Mail Client** (`mailbox.ClientMain`)
  - A JavaFX desktop application associated with a single, pre-configured email account (no signup/login screen — accounts are hardcoded per the assignment spec).
  - Lets the user:
    - Compose and send a message to one or more recipients
    - Read incoming mail (Inbox), sent mail (Sent), and deleted mail (Trash)
    - Reply, Reply-all, and Forward a message
    - Delete a message from the mailbox
  - Polls the server periodically on a background thread to refresh the mailbox and pops up a notification dialog when new mail arrives.
  - Detects if the server is unreachable, shows an error alert instead of crashing, and keeps retrying until the server comes back up.

- **Data model** (`mailbox.utils.Email`)
  - A `Serializable` class holding `id`, sender, recipient(s), subject, body, and a delivery `state` (received / sent / deleted / etc.), serialized to/from the mailbox `.txt` files and over the socket (via `ObjectOutputStream`/`ObjectInputStream`) when the server pushes the full mailbox to a client.

## Architecture

- **MVC + Observer/Observable** — `Model` exposes JavaFX observable properties/lists; views bind to them; `Controller` is the only bridge between `Model` and the network layer.
- **Networking** — plain Java `Socket`/`ServerSocket`, no frameworks. Requests and responses use a simple text-based protocol.
- **Persistence** — one text file per mailbox, plus a shared `login.txt` of registered users, all under `src/mailbox/files/`.
- **Concurrency** — the server handles each client connection on its own thread; the client polls the server on a background thread so the UI never freezes.

## Project structure

```
src/
├── mailbox/
│   ├── Server.java         # Server application (JavaFX GUI + socket listener + mailbox logic)
│   ├── ClientMain.java     # Client application entry point (JavaFX GUI + polling thread)
│   ├── Client.java         # Client-side networking (send/delete mail, refresh, login list, ping)
│   ├── Controller.java     # JavaFX controller wiring views <-> model <-> network layer
│   ├── Model.java          # Observable application state (mail lists, current user/email)
│   ├── utils/Email.java    # Serializable Email data class
│   ├── files/              # Persisted mailboxes + login.txt (sample data for 3 demo users)
│   └── fxml/                # FXML view definitions + icons
└── Creators/                # Helper "view holder" classes used by the Controller
```

## Requirements

- **JDK 19** (or compatible)
- **JavaFX SDK** matching your JDK version (the project does not use Maven/Gradle — JavaFX is linked as an external SDK, as in the original IntelliJ project's `lib` library pointing at a local JavaFX SDK path)

## Building & running

This is a plain IntelliJ IDEA project (no Maven/Gradle build file is included), so you need to compile/run it either from IntelliJ or manually with `javac`/`java`, pointing at your local JavaFX SDK.

### Option A — IntelliJ IDEA (recommended)

1. Open this repository's folder as an IntelliJ project.
2. Go to **File → Project Structure → Libraries**, and point the `lib` library at your local JavaFX SDK `lib` folder (download the JavaFX SDK for your OS/JDK from [gluonhq.com/products/javafx](https://gluonhq.com/products/javafx/) if you don't have one).
3. Make sure the Project SDK is set to JDK 19 (or update the language level if using a newer JDK).
4. Add VM options to both run configurations (Server and Client):
   ```
   --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml
   ```
5. **Important — working directory:** both `Server` and `Client` read/write files using the relative path `src/mailbox/files/...`, so the run configuration's working directory must be the repository root, which is IntelliJ's default.
6. Run `mailbox.Server` first, then run `mailbox.ClientMain` (you can launch multiple client instances to simulate different users — see [Demo accounts](#demo-accounts) below).

### Option B — Command line

From the repository root, with `$FX` pointing at your JavaFX SDK `lib` folder:

```bash
# Compile
javac --module-path $FX --add-modules javafx.controls,javafx.fxml -d out $(find src -name "*.java")

# Copy non-Java resources (FXML, images, data files) into the output folder
cp -r src/mailbox/fxml out/mailbox/
cp -r src/mailbox/files out/mailbox/

# Run the server (from the repository root, so the relative "src/mailbox/files" paths resolve)
java --module-path $FX --add-modules javafx.controls,javafx.fxml -cp out mailbox.Server

# Run one or more clients (in separate terminals)
java --module-path $FX --add-modules javafx.controls,javafx.fxml -cp out mailbox.ClientMain
```

> Note: since the client currently hardcodes which account it logs in as (per the assignment's "no login screen" requirement), running multiple clients for different demo users requires adjusting the account the client binds to in code/config before building each instance — check `ClientMain`/`Controller` for where the current user is set.

### Demo accounts

Three sample mailboxes are preloaded under `src/mailbox/files/` for demonstration purposes:

- `leo@mail.com`
- `tizio@mail.com`
- `caio@mail.com`

## License

This project is licensed under the MIT License — see the [LICENSE](LICENSE) file for details.
