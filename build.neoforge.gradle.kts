plugins {
    // This plugin applies the correct loom variant based on the Minecraft version
    id("net.neoforged.moddev") version "2.0.147"
    id("neoforge-mutex")
    id("me.modmuss50.mod-publish-plugin")
    `maven-publish`
}

// DO NOT set group = ...!
version = "${property("mod.version")}+${sc.current.version}"
base.archivesName = "${property("mod.id") as String}-neoforge"

val requiredJava: JavaVersion = when {
    sc.current.parsed >= "26.1" -> JavaVersion.VERSION_25
    sc.current.parsed >= "1.20.5" -> JavaVersion.VERSION_21
    sc.current.parsed >= "1.18" -> JavaVersion.VERSION_17
    sc.current.parsed >= "1.17" -> JavaVersion.VERSION_16
    else -> JavaVersion.VERSION_1_8
}

// This can be used for publishing on Modrinth and Curseforge
val compatibleVersions: List<String> = sc.properties.rawOrNull("mod.mc_releases")
    ?.asList().orEmpty().map { it.toString() }

repositories {
    /**
     * Restricts dependency search of the given [groups] to the [maven URL][url],
     * improving the setup speed.
     */
    fun strictMaven(url: String, alias: String, vararg groups: String) = exclusiveContent {
        forRepository { maven(url) { name = alias } }
        filter { groups.forEach(::includeGroup) }
    }
    strictMaven("https://www.cursemaven.com", "CurseForge", "curse.maven")
    strictMaven("https://api.modrinth.com/maven", "Modrinth", "maven.modrinth")
    strictMaven("https://maven.midnightdust.eu/releases/", "MidnightDust", "eu.midnightdust")
    strictMaven("https://maven.terraformersmc.com/", "Terraformers", "com.terraformersmc") // ModMenu
    strictMaven("https://maven.caffeinemc.net/releases", "Sodium", "net.caffeinemc")

    // LambdAurora
    strictMaven("https://aperlambda.github.io/maven", "Aperlambda", "org.aperlambda")
    strictMaven("https://maven.gegy.dev", "Gegy", "dev.lambdaurora")

    // Compat
    strictMaven("https://maven.kosmx.dev", "KosmX", "io.github.kosmx")
    strictMaven("https://maven.isxander.dev/releases", "isXander", "dev.isxander")
    strictMaven("https://maven.shedaniel.me/", "shedaniel", "me.shedaniel.cloth")
    //maven("https://jitpack.io")
    strictMaven("https://maven.quiltmc.org/repository/release", "Quilt", "org.quiltmc", "org.quiltmc.parsers")
    strictMaven( "https://staging.alexiil.uk/maven/", "AlexIIL (LibGUI)", "io.github.cottonmc")
}

dependencies {
    // MidnightLib
    val midnightlib = "eu.midnightdust:midnightlib:${property("deps.midnightlib")}+${sc.current.version}-neoforge"
    implementation(midnightlib) {
        exclude(group = "net.fabricmc.fabric-api")
        exclude(group = "com.terraformersmc")
    }
    //include(midnightlib)

    // Compatibility mods
    //compileOnlyApi ("io.github.cottonmc:LibGui:${property("libgui_version")}")
    compileOnlyApi ("maven.modrinth:emi:${property("deps.emi")}+neoforge")
    compileOnlyApi ("maven.modrinth:emotecraft:${property("deps.emotecraft")}")
    compileOnlyApi ("io.github.kosmx:bendy-lib:${property("deps.bendylib")}")
    compileOnlyApi ("dev.isxander:yet-another-config-lib:${property("deps.yacl")}") {
        exclude(group = "org.quiltmc.parser")
    }
    compileOnlyApi ("maven.modrinth:inventory-tabs-updated:${property("deps.inventorytabs")}") {
        exclude(group = "me.shedaniel.cloth:cloth-config-neoforge")
        exclude(group = "me.shedaniel.cloth:cloth-config-fabric")
    }

    if (sc.current.parsed >= "1.21.11")
        compileOnly("net.caffeinemc:sodium-neoforge-mod:${property("deps.sodium")}")
    else
        compileOnly("maven.modrinth:sodium:${property("deps.sodium")}")

    val spruceui = "dev.lambdaurora:spruceui:${property("deps.spruceui")}"
    val mappingsAttribute = Attribute.of("net.minecraft.mappings", String::class.java)
    implementation(spruceui) {
        attributes {
            attribute(mappingsAttribute, "mojmap")
        }
    }
    jarJar("dev.yumi.mc.core:yumi-mc-foundation:${property("deps.yumimc")}") {
        attributes {
            attribute(mappingsAttribute, "mojmap")
        }
    }
    jarJar(spruceui)
}

neoForge {
    version = property("deps.neoforge_loader") as String

    mods {
        register("midnightlib") {
            sourceSet(sourceSets.main.get())
        }
    }

    runs {
        register("client") {
            gameDirectory = file("../../run/")
            client()
        }

        register("server") {
            gameDirectory = file("../../run/")
            server()
        }
    }
}

java {
    withSourcesJar()
    targetCompatibility = requiredJava
    sourceCompatibility = requiredJava

    toolchain {
        vendor = JvmVendorSpec.ADOPTIUM
        languageVersion = JavaLanguageVersion.of(requiredJava.majorVersion)
    }
}

tasks {
    processResources {
        fun MutableMap<String, String>.register(key: String, property: String) {
            val value: String = sc.properties[property]
            inputs.property(key, value)
            set(key, value)
        }

        val props = buildMap {
            register("id", "mod.id")
            register("name", "mod.name")
            register("version", "mod.version")
            register("minecraft", "mod.neoforge_mc_range")
        }

        filesMatching("META-INF/neoforge.mods.toml") { expand(props) }

        val mixinJava = "JAVA_${requiredJava.majorVersion}"
        filesMatching("*.mixins.json") { expand("java" to mixinJava) }

        exclude("fabric.mod.json", "*.ct", "*.classtweaker")
    }

    register<Copy>("buildAndCollect") {
        group = "build"
        description = "Builds mod jars and copies results to `build/libs/{mod version}/`"

        inputs.property("version", project.property("mod.version"))
        from(jar.flatMap { it.archiveFile }, named<Jar>("sourcesJar").flatMap { it.archiveFile })
        into(rootProject.layout.buildDirectory.file("libs/${project.property("mod.version")}"))
    }
}

publishMods {
    val modrinthToken = System.getenv("MODRINTH_TOKEN")
    val curseforgeToken = System.getenv("CURSEFORGE_TOKEN")

    file = project.tasks.jar.get().archiveFile
    dryRun = modrinthToken == null || curseforgeToken == null

    displayName = "${property("mod.name")} ${property("mod.version")} - Neoforge ${sc.current.version}"
    version = "${property("mod.version")}+${sc.current.version}-neoforge"
    changelog = rootProject.file("CHANGELOG.md").readText()
    type = STABLE

    modLoaders.addAll("neoforge")

    modrinth {
        projectId = property("publish.modrinth").toString()
        accessToken = modrinthToken
        compatibleVersions.forEach(minecraftVersions::add)
        requires("midnightlib")

        environment = CLIENT_ONLY
    }

    curseforge {
        projectId = property("publish.curseforge").toString()
        accessToken = curseforgeToken
        compatibleVersions.forEach(minecraftVersions::add)
        requires("midnightlib")

        client.set(true)
        server.set(false)
    }
}

publishing {
    repositories {
        maven {
            name = "MidnightDust"
            url = uri("https://maven.midnightdust.eu/releases")
            credentials(PasswordCredentials::class)
        }
    }
    publications {
        create<MavenPublication>("mavenJava") {
            pom {
                groupId = "eu.midnightdust"
                artifactId = "${project.property("mod.id")}"
                version = "${project.version}-neoforge"

                from(components["java"])
            }
        }
    }
}
