# 📦 KaratioCase

**KaratioCase** is a highly configurable Minecraft crate plugin for Paper servers.
The plugin provides animated crate openings, configurable rewards, customizable keys, GUI previews and a fully configurable messaging system.

---

## 📖 About

KaratioCase is an advanced crate system designed for Minecraft servers that need a lightweight, modern and fully configurable solution.

Every crate can have:

* Custom rewards
* Custom key names
* Custom model data
* Individual animation settings
* Configurable locations
* Custom GUI layouts

All plugin messages, animations and GUI elements can be configured without touching the source code.

---

## ✨ Features

* 📦 Unlimited configurable crates
* 🔑 Custom keys per crate
* 🎨 CustomModelData support
* 🎰 Animated crate opening
* ⚡ Instant opening mode
* 🖥️ Fully configurable GUI
* 💬 Fully configurable messages
* 📍 Physical crate locations
* 🔒 Anti-dupe protections
* 🔄 Reload command
* 🚀 Adventure MiniMessage support

---

## 📂 Configuration Files

```text
plugins/KaratioCase/

├── config.yml
├── messages.yml
└── cases.yml
```

### config.yml

Global plugin settings:

* GUI configuration
* Animation settings
* Security settings
* Default key settings

### messages.yml

All plugin messages:

* Errors
* Notifications
* Broadcasts
* Admin messages

### cases.yml

All crate definitions:

* Rewards
* Locations
* Keys
* Animation overrides

---

## 📦 Installation

1. Download the latest release.
2. Place the `.jar` file into your server plugins folder.
3. Restart the server.
4. Configure the plugin files.

```text
plugins/KaratioCase/
```

---

## 🔑 Requirements

* Paper 1.21.4+
* Java 21+

---

## 🛡️ Security

KaratioCase includes built-in protections against:

* Reward duplication
* GUI abuse
* Animation interruption
* Invalid crate access
* Invalid key usage

---

## 🧑‍💻 Commands

```text
/case create <name> <material>
/case delete <name>
/case edit <name>
/case reload
/case givekey <player> <case> <amount>
/case givekeyall <case> <amount>
```

---

## 🐛 Bug Reports

If you find a bug or have an idea for a new feature, please create an Issue on this GitHub repository.
