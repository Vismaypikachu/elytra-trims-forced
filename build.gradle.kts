plugins {
    id("dev.architectury.loom") version "1.4-SNAPSHOT"
    id("me.fallenbreath.yamlang") version "1.3.1"
    id("me.modmuss50.mod-publish-plugin") version "0.4.+"
}

val loader = if (loom.isForgeLike) "forge" else "fabric"
val isFabric = !loom.isForgeLike
val mcVersion = property("deps.minecraft").toString()
val mcDep = property("mod.mc_dep").toString()
val modId = property("mod.id").toString()
val modName = property("mod.name").toString()
val modVersion = property("mod.version").toString()
val modGroup = property("mod.group").toString()

version = "$modVersion+$mcVersion"
group = modGroup
base { archivesName.set(modId) }

repositories {
    exclusiveContent {
        forRepository { maven("https://www.cursemaven.com") { name = "CurseForge" } }
        filter { includeGroup("curse.maven") }
    }
    exclusiveContent {
        forRepository { maven("https://api.modrinth.com/maven") { name = "Modrinth" } }
        filter { includeGroup("maven.modrinth") }
    }
    maven("https://jitpack.io") { name = "Jitpack" }
    maven("https://maven.terraformersmc.com/releases/") { name = "TerraformersMC" }
    maven("https://maven.isxander.dev/releases")
    maven("https://maven.shedaniel.me/")
    maven("https://maven.quiltmc.org/repository/release/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
}

dependencies {
    minecraft("com.mojang:minecraft:${mcVersion}")
    mappings("net.fabricmc:yarn:${mcVersion}+build.${property("deps.yarn_build")}:v2")
    val mixinExtras = "io.github.llamalad7:mixinextras-%s:${property("deps.mixin_extras")}"
    if (isFabric) {
        modImplementation("net.fabricmc:fabric-loader:${property("deps.fabric_loader")}")
        modImplementation("com.terraformersmc:modmenu:${property("deps.modmenu")}")
    } else {
        "forge"("net.minecraftforge:forge:${mcVersion}-${property("deps.fml")}")
        implementation(mixinExtras.format("common"))
        implementation(mixinExtras.format("forge"))
        annotationProcessor(mixinExtras.format("common"))
        include(mixinExtras.format("forge"))
    }
    // Config
//    modImplementation("dev.isxander.yacl:yet-another-config-lib-$loader:${property("deps.yacl")}")
//    modImplementation("me.shedaniel.cloth:cloth-config-$loader:${property("deps.cloth")}") {
//        exclude(group = "net.fabricmc.fabric-api")
//    }

    // Compat
    modCompileOnly("maven.modrinth:stacked-armor-trims:1.1.0")
    modCompileOnly("maven.modrinth:allthetrims:${if (isFabric) "3.3.7" else "Ga7vvJCQ"}")
}

stonecutter.addExpression {
    when (it) {
        "fabric" -> isFabric
        "forge" -> !isFabric
        else -> null
    }
}

if (stonecutter.current().isActiveVersion) {
    loom {
        accessWidenerPath.set(rootProject.file("src/main/resources/elytratrims.accesswidener"))

        runConfigs["client"].apply {
            // to make sure it generates all "Minecraft Client (:subproject_name)" applications
            ideConfigGenerated(true)
            vmArgs("-Dmixin.debug.export=true")
            runDir = "../../run"
        }

        if (!isFabric) {
            forge {
                mixinConfigs("$modId.mixins.json")
            }
        }
    }
}

tasks.processResources {
    inputs.property("id", modId)
    inputs.property("name", modName)
    inputs.property("version", modVersion)
    inputs.property("mc", mcDep)

    val map = mapOf(
        "version" to modVersion,
        "mc" to mcDep
    )

    if (isFabric) filesMatching("fabric.mod.json") {
        expand(map)
    } else filesMatching("META-INF/mods.toml") {
        expand(map)
    }
}

yamlang {
    targetSourceSets.set(mutableListOf(sourceSets["main"]))
    inputDir.set("assets/$modId/lang")
}

java {
    withSourcesJar()
}
