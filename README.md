# StrongholdConfig

![GitHub Workflow Status](https://img.shields.io/github/workflow/status/Gaming32/StrongholdConfig/maven) ![GitHub](https://img.shields.io/github/license/Gaming32/StrongholdConfig)

Stronghold generation configuration plugin for Bukkit

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
