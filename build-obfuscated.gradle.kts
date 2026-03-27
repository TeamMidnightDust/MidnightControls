plugins {
    id("dev.architectury.loom") version "1.13-SNAPSHOT"
    id("me.modmuss50.mod-publish-plugin")
    `maven-publish`
}

val minecraft = stonecutter.current.version
val loader = loom.platform.get().name.lowercase()

version = "${mod.version}+$minecraft"
group = mod.group
base {
    archivesName.set("${mod.id}-$loader")
}

repositories {
    maven("https://maven.neoforged.net/releases/")
    maven("https://api.modrinth.com/maven")

    // modmenu
    maven("https://maven.terraformersmc.com/")
    maven("https://maven.nucleoid.xyz/")

    // MidnightLib
    maven("https://maven.midnightdust.eu/releases/")

    // LambdAurora
    maven("https://aperlambda.github.io/maven")
    maven("https://maven.gegy.dev")

    // Compat
    maven("https://maven.terraformersmc.com/releases/")
    maven("https://maven.kosmx.dev")
    maven("https://maven.isxander.dev/releases")
    maven("https://maven.shedaniel.me/")
    maven("https://jitpack.io")
    maven("https://api.modrinth.com/maven")
    maven("https://maven.quiltmc.org/repository/release")
}
dependencies {
    minecraft("com.mojang:minecraft:$minecraft")
    implementation ("org.aperlambda:lambdajcommon:1.8.1") {
        exclude(group = "com.google.code.gson")
        exclude(group = "com.google.guava")
    }

    // MidnightLib
    val midnightlib = "eu.midnightdust:midnightlib:${mod.dep("midnightlib_version")}+${minecraft}-${loader}"
    modImplementation(midnightlib)
    include(midnightlib)

    // Compatibility mods
    modCompileOnlyApi ("com.terraformersmc:modmenu:${mod.dep("modmenu_version")}") {
        exclude(group = "net.fabricmc.fabric-api")
    }
    //modCompileOnlyApi ("io.github.cottonmc:LibGui:${mod.dep("libgui_version")}")
    modCompileOnlyApi ("org.quiltmc:quilt-json5:1.0.0")
    modImplementation ("maven.modrinth:sodium:${mod.dep("sodium_version")}-fabric")
    modCompileOnlyApi ("maven.modrinth:emi:${mod.dep("emi_version")}+${loader}")
    modCompileOnlyApi ("maven.modrinth:emotecraft:${mod.dep("emotecraft_version")}+${minecraft}-${loader.replace("neo","")}")
    modCompileOnlyApi ("io.github.kosmx:bendy-lib:${mod.dep("bendylib_version")}")
    modCompileOnlyApi ("dev.isxander:yet-another-config-lib:${mod.dep("yacl_version")}+${minecraft}-${loader}") {
        exclude(group = "org.quiltmc.parser")
    }
    modCompileOnlyApi ("maven.modrinth:inventory-tabs-updated:${mod.dep("inventorytabs_version")}")
    modCompileOnlyApi ("maven.modrinth:bedrockify:${mod.dep("bedrockify_version")}")
    // Required for Inventory Tabs
    modCompileOnlyApi("me.shedaniel.cloth:cloth-config-fabric:${mod.dep("clothconfig_version")}") {
        exclude(group = "net.fabricmc.fabric-api")
    }

    val spruceui = "dev.lambdaurora:spruceui:${mod.dep("spruceui_version")}"

    if (loader == "fabric") {
        modImplementation("net.fabricmc:fabric-loader:${mod.dep("fabric_loader")}")
        modImplementation("net.fabricmc.fabric-api:fabric-api:${mod.dep("fabric_version")}")

        modImplementation (spruceui)
        include (spruceui)
        include("dev.yumi.mc.core:yumi-mc-foundation:${mod.dep("yumimc_version")}")
        include("org.aperlambda:lambdajcommon:1.8.1")
        //modCompileOnly "maven.modrinth:emi:${mod.dep("emi_version")}"
    }
    if (loader == "neoforge") {
        "neoForge"("net.neoforged:neoforge:${mod.dep("neoforge_loader")}")

        val mappingsAttribute = Attribute.of("net.minecraft.mappings", String::class.java)

        implementation(spruceui) {
            attributes {
                attribute(mappingsAttribute, "mojmap")
            }
        }

        include("dev.yumi.mc.core:yumi-mc-foundation:${mod.dep("yumimc_version")}") {
            attributes {
                attribute(mappingsAttribute, "mojmap")
            }
        }
        include(spruceui)
    }
    mappings (loom.officialMojangMappings())
}

loom {
    accessWidenerPath = rootProject.file("src/main/resources/midnightcontrols.accesswidener")

    decompilers {
        get("vineflower").apply { // Adds names to lambdas - useful for mixins
            options.put("mark-corresponding-synthetics", "1")
        }
    }
}

publishMods {
    val modrinthToken = System.getenv("MODRINTH_TOKEN").orEmpty()
    val curseforgeToken = System.getenv("CURSEFORGE_TOKEN").orEmpty()
    val githubToken = System.getenv("GITHUB_TOKEN").orEmpty()

    file = project.tasks.remapJar.get().archiveFile
    dryRun = modrinthToken.isEmpty() || curseforgeToken.isEmpty()

    displayName = "${mod.name} ${mod.version} - ${loader.replaceFirstChar { it.uppercase() }} ${property("mod.mc_title")}"
    version = "${mod.version}+${property("mod.mc_title")}-${loader}"
    changelog = rootProject.file("CHANGELOG.md").readText()
    type = STABLE

    modLoaders.add(loader)
    if (loader == "fabric") {
        modLoaders.add("quilt")
    }

    val targets = property("mod.mc_targets").toString().split(' ')
    modrinth {
        projectId = property("publish.modrinth").toString()
        accessToken = modrinthToken
        targets.forEach(minecraftVersions::add)
        requires("midnightlib")
        if (loader == "fabric") {
            requires("fabric-api")
        }
    }

    curseforge {
        projectId = property("publish.curseforge").toString()
        accessToken = curseforgeToken.toString()
        targets.forEach(minecraftVersions::add)
        requires("midnightlib")
        if (loader == "fabric") {
            requires("fabric-api")
        }
    }

//    github {
//        accessToken = githubToken
//        repository = "TeamMidnightDust/CullLeaves"
//        commitish = "multiversion" // This is the branch the release tag will be created from
//
//        tagName = "v" + properties["mod.version"]
//
//        // Allow the release to be initially created without any files.
//        allowEmptyFiles = true
//    }
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
                artifactId = project.mod.id
                version = "${project.version}-${loader}"

                from(components["java"])
            }
        }
    }
}


java {
    withSourcesJar()
    val java = if (stonecutter.eval(minecraft, ">=1.20.5")) JavaVersion.VERSION_21 else JavaVersion.VERSION_17
    targetCompatibility = java
    sourceCompatibility = java
}

tasks.remapJar {
    injectAccessWidener = true
    input = tasks.jar.get().archiveFile
    archiveClassifier = null
    dependsOn(tasks.jar)
}

tasks.jar {
    archiveClassifier = "dev"
}

val buildAndCollect = tasks.register<Copy>("buildAndCollect") {
    group = "build"
    from(tasks.remapJar.get().archiveFile, tasks.remapSourcesJar.get().archiveFile)
    into(rootProject.layout.buildDirectory.file("libs/${mod.version}/$loader"))
    dependsOn("build")
}

if (stonecutter.current.isActive) {
    rootProject.tasks.register("buildActive") {
        group = "project"
        dependsOn(buildAndCollect)
    }

    rootProject.tasks.register("runActive") {
        group = "project"
        dependsOn(tasks.named("runClient"))
    }
}

tasks.processResources {
    properties(
        listOf("fabric.mod.json"),
        "id" to mod.id,
        "name" to mod.name,
        "version" to mod.version,
        "minecraft" to mod.prop("mc_dep_fabric")
    )
    properties(
        listOf("META-INF/mods.toml", "pack.mcmeta"),
        "id" to mod.id,
        "name" to mod.name,
        "version" to mod.version,
        "minecraft" to mod.prop("mc_dep_forgelike")
    )
    properties(
        listOf("META-INF/neoforge.mods.toml", "pack.mcmeta"),
        "id" to mod.id,
        "name" to mod.name,
        "version" to mod.version,
        "minecraft" to mod.prop("mc_dep_forgelike")
    )
}

tasks.build {
    group = "versioned"
    description = "Must run through 'chiseledBuild'"
}


stonecutter {
    constants {
        arrayOf("fabric", "neoforge", "forge").forEach { it -> put(it, loader == it) }
    }
}
