import groovy.json.JsonOutput
import groovy.json.JsonSlurper

plugins {
    id("net.fabricmc.fabric-loom") version "1.15-SNAPSHOT" // For unobfuscated releases (>= 26.1)
    id("me.modmuss50.mod-publish-plugin")
    `maven-publish`
}

val minecraft = stonecutter.current.version
val loader = stonecutter.current.project.substringAfterLast('-')

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
    strictMaven( "https://staging.alexiil.uk/maven/", "AlexIIL (LibGUI)", "io.github.cottonmc")
}
dependencies {
    minecraft("com.mojang:minecraft:$minecraft")

    // MidnightLib
    val midnightlib = "eu.midnightdust:midnightlib:${mod.dep("midnightlib_version")}+${minecraft}-${loader}"
    implementation(midnightlib)
    include(midnightlib)

    // Compatibility mods
    compileOnlyApi ("com.terraformersmc:modmenu:${mod.dep("modmenu_version")}") {
        exclude(group = "net.fabricmc.fabric-api")
    }
    compileOnlyApi ("io.github.cottonmc:LibGui:${mod.dep("libgui_version")}")
    compileOnlyApi ("org.quiltmc:quilt-json5:1.0.0")
    implementation ("maven.modrinth:sodium:${mod.dep("sodium_version")}-fabric")
    compileOnlyApi ("maven.modrinth:emi:${mod.dep("emi_version")}+${loader}")
    compileOnlyApi ("maven.modrinth:emotecraft:${mod.dep("emotecraft_version")}")
    compileOnlyApi ("io.github.kosmx:bendy-lib:${mod.dep("bendylib_version")}")
    compileOnlyApi ("dev.isxander:yet-another-config-lib:${mod.dep("yacl_version")}+${minecraft}-${loader}") {
        exclude(group = "org.quiltmc.parser")
    }
    compileOnlyApi ("maven.modrinth:inventory-tabs-updated:${mod.dep("inventorytabs_version")}")
    compileOnlyApi ("maven.modrinth:bedrockify:${mod.dep("bedrockify_version")}")
    // Required for Inventory Tabs
    compileOnlyApi("me.shedaniel.cloth:cloth-config-fabric:${mod.dep("clothconfig_version")}") {
        exclude(group = "net.fabricmc.fabric-api")
    }

    val spruceui = "dev.lambdaurora:spruceui:${mod.dep("spruceui_version")}"

    if (loader == "fabric") {
        implementation("net.fabricmc:fabric-loader:${mod.dep("fabric_loader")}")
        implementation("net.fabricmc.fabric-api:fabric-api:${mod.dep("fabric_version")}")

        implementation(spruceui)
        include(spruceui)
        include("dev.yumi.mc.core:yumi-mc-foundation:${mod.dep("yumimc_version")}")
        //modCompileOnly "maven.modrinth:emi:${mod.dep("emi_version")}"
    }
    if (loader == "neoforge") {
        "neoForge"("net.neoforged:neoforge:${mod.dep("neoforge_loader")}")

        implementation(spruceui)

        include("dev.yumi.mc.core:yumi-mc-foundation:${mod.dep("yumimc_version")}")
        include(spruceui)
    }
}
loom {
    accessWidenerPath = rootProject.file("src/main/resources/midnightcontrols-26.1.accesswidener")
}

publishMods {
    val modrinthToken = System.getenv("MODRINTH_TOKEN")
    val curseforgeToken = System.getenv("CURSEFORGE_TOKEN")
    val githubToken = System.getenv("GITHUB_TOKEN").orEmpty()

    file = project.tasks.jar.get().archiveFile
    dryRun = modrinthToken == null || curseforgeToken == null

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
        if (loader == "fabric") {
            requires("fabric-api")
        }
    }

    curseforge {
        projectId = property("publish.curseforge").toString()
        accessToken = curseforgeToken.toString()
        targets.forEach(minecraftVersions::add)
        if (loader == "fabric") {
            requires("fabric-api")
        }
    }

//    github {
//        accessToken = githubToken
//        repository = "TeamMidnightDust/MidnightLib"
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

val requiredJava = when {
    sc.current.parsed >= "26.1-pre-1" -> JavaVersion.VERSION_25
    sc.current.parsed >= "1.20.5" -> JavaVersion.VERSION_21
    sc.current.parsed >= "1.18" -> JavaVersion.VERSION_17
    sc.current.parsed >= "1.17" -> JavaVersion.VERSION_16
    else -> JavaVersion.VERSION_1_8
}

java {
    withSourcesJar()
    targetCompatibility = requiredJava
    sourceCompatibility = requiredJava
}

tasks.jar {
    inputs.property("archivesName", base.archivesName)
}

val buildAndCollect = tasks.register<Copy>("buildAndCollect") {
    group = "build"
    from(tasks.jar.get().archiveFile)
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

tasks.processResources {
    // Minify json resources
    doLast {
        fileTree(outputs.files.singleFile).matching {
            include("**/*.json")
        }.forEach { file ->
            file.writeText(JsonOutput.toJson(JsonSlurper().parse(file)))
        }
    }
}

sourceSets {
    test {
        compileClasspath.plus(main.get().compileClasspath)
        runtimeClasspath.plus(main.get().runtimeClasspath)
        java {
            srcDirs.add(File("src/test/java"))
        }
        resources {
            srcDirs.add(File("src/test/resources"))
        }
    }
}
tasks.withType<AbstractTestTask>().configureEach {
    failOnNoDiscoveredTests = false
}

loom {
    runs {
        create("testClient"
        ) {
            client()
            configName = "Test Minecraft Client"
            source(sourceSets.test.get())
        }
        create("testServer"
        ) {
            server()
            configName = "Test Minecraft Server"
            source(sourceSets.test.get())
        }
    }
}

stonecutter {
    constants {
        arrayOf("fabric", "neoforge", "forge").forEach { it -> put(it, loader == it) }
    }
}
