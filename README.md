# 🔥 CinderStride

> **A vanilla-friendly Minecraft mod that adds lava-walking boots to Bastion loot.**  
> Find CinderStride Boots in Bastion Remnant Treasure Rooms and cross Nether lava oceans by creating temporary basalt underfoot.  
> Gives you a clear reason to locate and conquer Bastions while encouraging deeper Nether exploration.

---

## 🌋 Why It Feels Vanilla

* Progression is tied to exploration, not free power
* One focused mechanic with high impact and low clutter
* Risk stays meaningful: stand still too long and the path collapses

> Built to feel like a missing Nether traversal mechanic.

---

## 🛡️ Features

### 👢 Core Mechanic
* Rare boots found in **Bastion Remnant Treasure Room loot**
* Lets the wearer **walk across lava**
* Lava underfoot is converted into **temporary basalt**
* Temporary basalt **decays back into lava** over time
* Durability is consumed while traversing lava
* Boots will not break, but can become unusable until repaired
* Boots are enchantable; **Mending** and **Unbreaking** are recommended

### ⚙️ Configuration
* Fully configurable through **commands** and **in-game UI**
* Suitable for singleplayer, multiplayer, and server administration

<details>
<summary><strong>🔧 Advanced Config & Commands (Click to Expand)</strong></summary>

### 📁 Config File

`config/cinderstride/cinderstride.json`

### 🧰 Config Options

* `radius` (default: `1`, range: `1` to `4`)  
  Horizontal radius around the player where lava is converted while wearing CinderStride Boots.
* `decayTicks` (default: `40`, range: `20` to `120`)  
  Ticks per temporary basalt stage before it decays.  
  Basalt has 4 stages, then returns to lava.
* `durabilityLossChance` (default: `0.25`, range: `0.0` to `1.0`)  
  Chance per converted block to consume 1 durability.

### 💬 Commands

All commands are under:

`/cinderstride config ...`

* `/cinderstride config show`
* `/cinderstride config get <setting>`
* `/cinderstride config set <setting> <value>`
* `/cinderstride config reload`

Available `<setting>` values:

* `radius`
* `decayTicks`
* `durabilityLossChance`

Examples:

* `/cinderstride config set radius 2`
* `/cinderstride config set decayTicks 60`
* `/cinderstride config set durabilityLossChance 0.15`

### 🔐 Permissions

* Config commands require admin/operator permissions.
* In-game config UI is editable only with config permission.

### 🖥️ UI Support

* Fabric: Mod Menu + Cloth Config
* NeoForge: Cloth Config integration

</details>

### 🧭 Exploration Loop
* Push deeper into the Nether to secure high-value loot
* Turn lava seas into traversal routes
* Keep moving or risk being stranded as basalt decays

---

## 🧩 Compatibility

* Fabric and NeoForge builds available
* Safe to add to existing worlds  
  (loot appears in newly generated, unexplored Bastions)

---

## 📦 Installation

1. Download the correct file for your mod loader and Minecraft version.
2. Place the `.jar` in your `mods` folder.
3. Launch Minecraft.

---

## 📝 Changelog

### 1.0.0
* Initial release
* Added Bastion-loot CinderStride Boots
* Added lava-walking with temporary basalt decay
* Added command and UI configuration support

---

## ✅ Modpack Permission

You are allowed to include CinderStride in modpacks.

---

## 👤 Credits

Created by Drahlek.

---

## 🔒 License

MIT. See [LICENSE](LICENSE).
