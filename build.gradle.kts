import net.minecrell.pluginyml.paper.PaperPluginDescription

plugins {
    `java-library`

    alias(libs.plugins.shadow) // Shades and relocates dependencies, see https://gradleup.com/shadow/
    alias(libs.plugins.run.paper) // Built in test server using runServer and runMojangMappedServer tasks
    alias(libs.plugins.plugin.yml.bukkit) // Automatic plugin.yml generation
    alias(libs.plugins.plugin.yml.paper) // Automatic plugin.yml generation
    projectextensions
    versioner

    eclipse
    idea
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21)) // Configure the java toolchain. This allows gradle to auto-provision JDK 21 on systems that only have JDK 8 installed for example.
    withJavadocJar() // Enable javadoc jar generation
    withSourcesJar() // Enable sources jar generation
}

repositories {
    mavenCentral()

    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://mvn-repo.arim.space/lesser-gpl3/")

    maven("https://maven.athyrium.eu/releases")

    maven("https://maven.citizensnpcs.co/repo")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/") // PlaceholderAPI
    maven("https://repo.codemc.org/repository/maven-public/") {
        content {
            includeGroup("com.github.retrooper") // PacketEvents
        }
    }
    maven("https://jitpack.io/") {
        content {
            includeGroup("com.github.MilkBowl") // VaultAPI
        }
    }
    maven("https://repo.glaremasters.me/repository/towny/") { // Towny
        content { includeGroup("com.palmergames.bukkit.towny") }
    }
    maven("https://nexus.betonquest.org/repository/betonquest/") // BetonQuest

    maven("https://repo.triumphteam.dev/snapshots/")
}

dependencies {
    // Core dependencies
    compileOnly(libs.annotations)
    annotationProcessor(libs.annotations)
    compileOnly(libs.paper.api)
    implementation(libs.morepaperlib)

    // API
    implementation(libs.javasemver) // Required by VersionWatch
    implementation(libs.versionwatch)
    implementation(libs.wordweaver) {
        exclude("com.google.code.gson") // Already ships with Paper
    }
    implementation(libs.crate.api)
    implementation(libs.crate.yaml)
    implementation(libs.colorparser) {
        exclude("net.kyori")
    }
    implementation(libs.threadutil.bukkit)
    implementation(libs.commandapi.shade)
    implementation(libs.triumph.gui)

    // Plugin dependencies
    compileOnly(libs.citizens) {
        exclude ("*", "*")
    }
    compileOnly(libs.settlers)
    implementation(libs.bstats)
    compileOnly(libs.vault)
    compileOnly(libs.packetevents)
    compileOnly(libs.placeholderapi) {
        exclude("me.clip.placeholderapi.libs", "kyori")
    }
    compileOnly(libs.towny)
    compileOnly(libs.betonquest) {
        exclude("com.comphenix.packetwrapper", "PacketWrapper")
        exclude("de.themoep", "minedown-adventure")
    }
    compileOnly(files("libs/AlathraPorts-1.0.3.jar"))

    // Testing - Core
    testImplementation(libs.annotations)
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.bundles.junit)
    testRuntimeOnly(libs.slf4j)
    testImplementation(platform(libs.testcontainers.bom))
    testImplementation(libs.bundles.testcontainers)
}

tasks {
    build {
        dependsOn(shadowJar)
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything

        // Set the release flag. This configures what version bytecode the compiler will emit, as well as what JDK APIs are usable.
        // See https://openjdk.java.net/jeps/247 for more information.
        options.release.set(21)
        options.compilerArgs.addAll(arrayListOf("-Xlint:all", "-Xlint:-processing", "-Xdiags:verbose"))
    }

    javadoc {
        isFailOnError = false
        exclude("**/database/schema/**") // Exclude generated jOOQ sources from javadocs
        val options = options as StandardJavadocDocletOptions
        options.encoding = Charsets.UTF_8.name()
        options.overview = "src/main/javadoc/overview.html"
        options.windowTitle = "${rootProject.name} Javadoc"
        options.tags("apiNote:a:API Note:", "implNote:a:Implementation Note:", "implSpec:a:Implementation Requirements:")
        options.addStringOption("Xdoclint:none", "-quiet")
        options.use()
    }

    processResources {
        filteringCharset = Charsets.UTF_8.name() // We want UTF-8 for everything
    }

    shadowJar {
        archiveBaseName.set(project.name)
        archiveClassifier.set("")

        // Shadow classes
        fun reloc(originPkg: String, targetPkg: String) = relocate(originPkg, "${project.relocationPackage}.${targetPkg}")

        reloc("space.arim.morepaperlib", "morepaperlib")
        reloc("io.github.milkdrinkers.javasemver", "javasemver")
        reloc("io.github.milkdrinkers.versionwatch", "versionwatch")
        reloc("org.json", "json")
        reloc("io.github.milkdrinkers.wordweaver", "wordweaver")
        reloc("io.github.milkdrinkers.crate", "crate")
        reloc("org.yaml.snakeyaml", "snakeyaml")
        reloc("io.github.milkdrinkers.colorparser", "colorparser")
        reloc("io.github.milkdrinkers.threadutil", "threadutil")
        reloc("dev.jorel.commandapi", "commandapi")
        reloc("dev.triumphteam.gui", "triumphgui")
        reloc("com.zaxxer.hikari", "hikaricp")
        reloc("org.bstats", "bstats")

        mergeServiceFiles()
    }

    test {
        useJUnitPlatform()
        failFast = false
    }

    runServer {
        // Configure the Minecraft version for our task.
        minecraftVersion(libs.versions.paper.run.get())

        // IntelliJ IDEA debugger setup: https://docs.papermc.io/paper/dev/debugging#using-a-remote-debugger
        jvmArgs("-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005", "-DPaper.IgnoreJavaVersion=true", "-Dcom.mojang.eula.agree=true", "-DIReallyKnowWhatIAmDoingISwear", "-Dpaper.playerconnection.keepalive=6000")
        systemProperty("terminal.jline", false)
        systemProperty("terminal.ansi", true)

        // Automatically install dependencies
        downloadPlugins {
//            modrinth("carbon", "2.1.0-beta.21")
//            github("jpenilla", "MiniMOTD", "v2.0.13", "minimotd-bukkit-2.0.13.jar")
//            hangar("squaremap", "1.2.0")
//            url("https://download.luckperms.net/1515/bukkit/loader/LuckPerms-Bukkit-5.4.102.jar")
            github("MilkBowl", "Vault", "1.7.3", "Vault.jar")
            github("retrooper", "packetevents", "v2.9.4", "packetevents-spigot-2.9.4.jar")
            github("milkdrinkers", "Settlers", "0.0.18", "Settlers-0.0.18.jar")
            github("Alathra", "AlathraPorts", "1.0.5", "AlathraPorts-1.0.5.jar")
            url("https://ci.citizensnpcs.co/job/Citizens2/3851/artifact/dist/target/Citizens-2.0.39-b3851.jar")
            hangar("PlaceholderAPI", "2.11.6")
            hangar("ViaVersion", "5.3.2")
            hangar("ViaBackwards", "5.3.2")
        }
    }
}

tasks.named<Jar>("sourcesJar") { // Required for sources jar generation with jOOQ
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

bukkit { // Options: https://github.com/eldoriarpg/plugin-yml/wiki/Bukkit
    main = project.entryPointClass
    load = net.minecrell.pluginyml.bukkit.BukkitPluginDescription.PluginLoadOrder.POSTWORLD

    // Info
    name = project.name
    prefix = project.name
    version = "${project.version}"
    description = "${project.description}"
    authors = listOf("rooooose-b", "darksaid98", "ShermansWorld")
    contributors = listOf()
    apiVersion = libs.versions.paper.api.get().substringBefore("-R")
    foliaSupported = false

    // Dependencies
    depend = listOf("Citizens", "Settlers", "Towny", "BetonQuest", "AlathraPorts")
    softDepend = listOf("PacketEvents", "Vault", "PlaceholderAPI")
    loadBefore = listOf()
    provides = listOf()
}

paper { // Options: https://github.com/eldoriarpg/plugin-yml/wiki/Paper
    main = project.entryPointClass
    loader = project.entryPointClass + "PluginLoader"
    generateLibrariesJson = true
    load = net.minecrell.pluginyml.bukkit.BukkitPluginDescription.PluginLoadOrder.POSTWORLD

    // Info
    name = project.name
    prefix = project.name
    version = "${project.version}"
    description = "${project.description}"
    authors = listOf("rooooose-b", "darksaid98", "ShermansWorld")
    contributors = listOf()
    apiVersion = libs.versions.paper.api.get().substringBefore("-R")
    foliaSupported = false

    // Dependencies
    hasOpenClassloader = true
    bootstrapDependencies {}
    serverDependencies {
        // Hard depends
        register("Citizens") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }
        register("Settlers") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }
        register("Towny") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }
        register("BetonQuest") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }
        register("AlathraPorts") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }

        // Soft depends
        register("PacketEvents") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
            required = false
        }
        register("Vault") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
            required = false
        }
        register("PlaceholderAPI") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
            required = false
        }
    }
    provides = listOf()
}