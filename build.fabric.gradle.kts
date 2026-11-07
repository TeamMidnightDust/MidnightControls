
plugins {
    // This plugin applies the correct loom variant based on the Minecraft version
    id("dev.kikugie.loom-back-compat")
    id("me.modmuss50.mod-publish-plugin")
    `maven-publish`
}

// DO NOT set group = ...!
version = "${property("mod.version")}+${sc.current.version}"
base.archivesName = "${property("mod.id") as String}-fabric"

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

// This list contains all required Fabric API modules, as defined in `stonecutter.properties.yaml`
val fapiModules: List<String> = sc.properties.rawOrNull("deps", "api_modules")
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
    /**
     * Fetches only the required Fabric API modules to not waste time downloading all of them for each version.
     * @see <a href="https://github.com/FabricMC/fabric">List of Fabric API modules</a>
     */
    fun fapi(vararg modules: String) {
        for (it in modules) modImplementation(fabricApi.module(it, sc.properties["deps.fabric_api"]))
    }
    minecraft("com.mojang:minecraft:${sc.current.version}")
    // Applies Mojang Mappings on obfuscated versions
    loomx.applyMojangMappings()

    // MidnightLib
    val midnightlib = "eu.midnightdust:midnightlib:${property("deps.midnightlib")}+${sc.current.version}-fabric"
    modImplementation(midnightlib) {
        exclude(group = "net.fabricmc.fabric-api")
        exclude(group = "com.terraformersmc")
    }
    include(midnightlib)

    // Use `mod{dependency type}` even on 26.1+ - loom-back-compat converts them
    modImplementation("net.fabricmc:fabric-loader:${property("deps.fabric_loader")}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${property("deps.fabric_api")}")

    // Compatibility mods
    modCompileOnly ("com.terraformersmc:modmenu:${property("deps.modmenu")}") {
        exclude(group = "net.fabricmc.fabric-api")
    }
    modCompileOnly ("io.github.cottonmc:LibGui:${property("deps.libgui")}")
    modCompileOnly ("org.quiltmc:quilt-json5:1.0.0")
    modImplementation ("maven.modrinth:sodium:${property("deps.sodium")}")
    modCompileOnly ("maven.modrinth:emi:${property("deps.emi")}+fabric")
    modCompileOnly ("maven.modrinth:emotecraft:${property("deps.emotecraft")}")
    modCompileOnly ("io.github.kosmx:bendy-lib:${property("deps.bendylib")}")
    modCompileOnly ("dev.isxander:yet-another-config-lib:${property("deps.yacl")}") {
        exclude(group = "org.quiltmc.parser")
    }
    modCompileOnly ("maven.modrinth:inventory-tabs-updated:${property("deps.inventorytabs")}")
    modCompileOnly ("maven.modrinth:bedrockify:${property("deps.bedrockify")}")
    // Required for Inventory Tabs
    modCompileOnly("me.shedaniel.cloth:cloth-config-fabric:${property("deps.clothconfig")}") {
        exclude(group = "net.fabricmc.fabric-api")
    }

    val spruceui = "dev.lambdaurora:spruceui:${property("deps.spruceui")}"

    modImplementation (spruceui)
    include (spruceui)
    include("dev.yumi.mc.core:yumi-mc-foundation:${property("deps.yumimc")}")
    include("org.aperlambda:lambdajcommon:1.8.1")
    //modCompileOnly "maven.modrinth:emi:${property("emi_version")}"
}

val accesswidener = when {
    stonecutter.eval(sc.current.version, ">=26.1") -> "midnightcontrols-26.1.accesswidener"
    else -> "midnightcontrols.accesswidener"
}
loom {
    fabricModJsonPath = rootProject.file("src/main/resources/fabric.mod.json") // Useful for interface injection
    accessWidenerPath = rootProject.file("src/main/resources/$accesswidener")

    decompilerOptions.named("vineflower") {
        options.put("mark-corresponding-synthetics", "1") // Adds names to lambdas - useful for mixins
    }

    runConfigs.all {
        preferGradleTask = true
        generateRunConfig = true
        runDirectory = rootProject.file("run") // Shares the run directory between versions
        jvmArguments.add("-Dmixin.debug.export=true") // Exports transformed classes for debugging
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
            register("minecraft", "mod.fabric_mc_range")
            set("aw_file", accesswidener)
        }

        filesMatching("fabric.mod.json") { expand(props) }

        val mixinJava = "JAVA_${requiredJava.majorVersion}"
        filesMatching("*.mixins.json") { expand("java" to mixinJava) }

        exclude("META-INF/neoforge.mods.toml", "midnightlib.png")
    }

    register<Copy>("buildAndCollect") {
        group = "build"
        description = "Builds mod jars and copies results to `build/libs/{mod version}/`"

        inputs.property("version", project.property("mod.version"))
        // loomx.mod(Sources)Jar returns the jar task for the applied loom variant
        from(loomx.modJar.flatMap { it.archiveFile }, loomx.modSourcesJar.flatMap { it.archiveFile })
        into(rootProject.layout.buildDirectory.file("libs/${project.property("mod.version")}"))
    }
}

publishMods {
    val modrinthToken = System.getenv("MODRINTH_TOKEN")
    val curseforgeToken = System.getenv("CURSEFORGE_TOKEN")

    file = loomx.modJar.flatMap { it.archiveFile }
    dryRun = modrinthToken == null || curseforgeToken == null

    displayName = "${property("mod.name")} ${property("mod.version")} - Fabric ${sc.current.version}"
    version = "${property("mod.version")}+${sc.current.version}-fabric"
    changelog = rootProject.file("CHANGELOG.md").readText()
    type = STABLE

    modLoaders.addAll("fabric", "quilt")

    modrinth {
        projectId = property("publish.modrinth").toString()
        accessToken = modrinthToken
        compatibleVersions.forEach(minecraftVersions::add)
        requires("fabric-api")

        environment = CLIENT_ONLY
    }

    curseforge {
        projectId = property("publish.curseforge").toString()
        accessToken = curseforgeToken
        compatibleVersions.forEach(minecraftVersions::add)
        requires("fabric-api")

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
                version = "${project.version}-fabric"

                from(components["java"])
            }
        }
    }
}
