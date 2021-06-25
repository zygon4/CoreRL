# CoreRL
Started as RL game components, now is more of an opionated RL engine for my personal games. It will contain utils but also my style of world gen, my input context, etc. to rapidly develop RLs.

Features:
- Swing app using Zircon
- Renders a HUD with minimap
- Open world (using noise function)
- Character sheet is inspired by D&D includes status/eq slots, inventory, equipment, spells and abilities
- Simple melee combat
- Action command pattern for common action reuse
- Monster spawns which have aggression and hostile status (ie will attack you)
- Location utilities includes path finding, neighbor finding
- Field generation, propagation (ie the basis for spells)
- JSON templates so more items/monsters can be added easily
- Example vampire game (src/main/java/com/zygon/rl/game/example/BloodRLMain.java)


TODOs:
- Better UI interaction
    - Log is ugly
    - UI should have an inventory UI (currently printed to stdout)
- Character creation wizard
- Save/load of games
- Better status management
- More items, armor
    - Including magic items
- Ranged combat
- Spells
    - Can use F1-F3 for experimental spells

Can run with:
```
./gradlew run
```

to launch the Swing game app.

Requires java 14!

![Alt text](/images/PoisonGas.png?raw=true "Blood")
