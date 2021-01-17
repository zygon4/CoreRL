# CoreRL
Started as RL game components, now is more of an opionated RL engine for my personal games. It will contain utils but also my style of world gen, my input context, etc. to rapidly develop RLs.

Features:
- Swing app using Zircon
- Renders a HUD with minimap
- Open world (using noise function)
- Character sheet includes status/eq slots, inventory, equipment, spells and abilities
- Simple melee combat
- Action command pattern for common action reuse
- Monster spawns which have aggression and hostile status (ie will attack you)
- Location class includes path finding, neighbor finding
- Field generation, propagation (ie the basis for spells)
- JSON templates so more items/monsters can be added easily
- Example vampire game (src/main/java/com/zygon/rl/game/example/BloodRLMain.java)


TODOs:
- Better UI interaction
    - UI should render log (currently printed to stdout)
    - UI should have an inventory UI (currently printed to stdout)
- Save/load of games
- Better status management
- More items, armor
    - Including magic items
- Ranged combat
- Spells

Can run with:
```
./gradlew run
```

to launch the Swing game app.

Requires java 14!
