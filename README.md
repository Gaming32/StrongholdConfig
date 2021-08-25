# StrongholdConfig

![GitHub Workflow Status](https://img.shields.io/github/workflow/status/Gaming32/StrongholdConfig/maven)
![GitHub](https://img.shields.io/github/license/Gaming32/StrongholdConfig)
<!-- ![GitHub all releases](https://img.shields.io/github/downloads/Gaming32/StrongholdConfig/total) -->

Stronghold generation configuration plugin for Spigot. It allows you to configure the number of strongholds in the world, the width of the stronghold rings, and the number of strongholds in the rings. I would highly recommend reading the [Minecraft Wiki page on strongholds](https://minecraft.fandom.com/wiki/Stronghold#Java_Edition) for more info.
For more info on exactly *how* to configure it, see [config.yml](strongholdconfig/src/main/resources/config.yml).

## Installing the plugin

**Please note: This plugin requires Minecraft 1.17 or later, as well as Java 16 or later, to run.**

1. Download the plugin from the [GitHub Releases page](https://github.com/Gaming32/StrongholdConfig/releases) and drop it in your server's `plugins` folder.
2. When you run your server, you *need* to pass the `--add-opens=java.base/java.lang.reflect=ALL-UNNAMED` JVM argument to `java`. JVM arguments are the arguments between `java` and `-jar`. If you use a server hosting service, please check with your hosting service on how to add or modify JVM arguments (if the hosting service even allows it at all).

## Downloading the latest development build

You can download the latest dev build from [nightly.link](https://nightly.link/Gaming32/StrongholdConfig/workflows/maven/main). If this doesn't work, you can also download it from the [GitHub Actions page](https://github.com/Gaming32/StrongholdConfig/actions). 

## Building manually

Alternatively, you can build it manually:
1. Download the [Spigot Build Tools](https://www.spigotmc.org/wiki/buildtools/): `curl -O https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar`
2. Run the Build Tools: `java -jar BuildTools.jar --dev`
3. Clone the repo: `git clone https://github.com/Gaming32/StrongholdConfig`
4. cd into the project: `cd StrongholdConfig/strongholdconfig`
5. Build the project: `mvn clean install`

Here's the whole process:
```shell
curl -O https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar
java -jar BuildTools.jar --dev
git clone https://github.com/Gaming32/StrongholdConfig
cd StrongholdConfig/strongholdconfig
mvn clean install
```

You can find the built JAR in the `StrongholdConfig/strongholdconfig/target` directory. 
