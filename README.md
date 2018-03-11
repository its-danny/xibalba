# Xibalba

![Screenshot](https://img.itch.zone/aW1hZ2UvMTQ3NDExLzY3NDg2My5naWY=/347x500/zwB1Pb.gif)

Your goal is to make it to the bottom of Xibalba, kill the 10 Lords of Xibalba, resurrect your family member, and take them back to the world of the living. To do this, you must progress from the jungle to the caves, and from the caves to the dungeons of Xibalba.

During character creation you can choose a god to pledge to, obeying their requirements during your attempt will grant you access to their abilities. Going against them, though, will have extreme consequences.

Stats and skills advance through usage, no allotting points.

---

**Xibalba is currently in development, a playable alpha will hopefully be available soon.**

Get the game on [itch.io](https://ohokcool.itch.io/xibalba).

## Building

### If you have Intellij

- Clone this repo
- Install the Gradle plugin if it's not already installed
- Import Xibalba
- Gradle -> Refresh all Gradle projects
- Add a new configuration
    - **Name:** Desktop
    - **Main class:** me.dannytatom.xibalba.desktop.DesktopLauncher
    - **Working directory:** ~/path/to/xibalba/core/assets
    - **Use classpath of module:** desktop_main 

### Gradle command line

```zsh
cd ~/path/to/xibalba/

# If gradle wrapper isn't setup yet
gradle wrapper

# Run
./gradlew desktop:run

# Build
./gradlew desktop:dist
```

## Contributing

There's a lot of features and bugs on the issue tracker, so have at it. If you have ideas just open a ticket with the label `idea` and I'll check it out. The wiki also has a lot of information about overall concepts and such to build features off of.

Also Xibalba was my first attempt at Java, so it's probably quite a mess.