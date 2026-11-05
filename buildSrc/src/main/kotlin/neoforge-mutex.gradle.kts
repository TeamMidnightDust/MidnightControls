import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceParameters

// This whole thing prevents neoforge from frying your computer by recompiling Minecraft on multiple versions
interface NeoForgeMutex : BuildService<BuildServiceParameters.None>

val mutex = gradle.sharedServices.registerIfAbsent("createMinecraftArtifactsMutex", NeoForgeMutex::class.java) {
    maxParallelUsages.set(1)
}

tasks.named { it == "createMinecraftArtifacts" }.configureEach {
    usesService(mutex)
}
