plugins {
    id("dev.kikugie.stonecutter")
    id("me.modmuss50.mod-publish-plugin") version "0.8.4" apply false
}
stonecutter active "26.2-fabric" /* [SC] DO NOT EDIT */

// See https://stonecutter.kikugie.dev/wiki/config/params
stonecutter parameters {
    swaps["mod_version"] = "\"" + property("mod.version") + "\";"
    swaps["minecraft"] = "\"" + node.metadata.version + "\";"
    constants["release"] = property("mod.id") != "template"
    dependencies["fapi"] = node.project.property("deps.fabric_version") as String

    replacements {
        string {
            direction = eval(current.version, ">=26.1")
            replace("render(", "extractRenderState(")
        }
        string {
            direction = eval(current.version, ">=26.1")
            replace("net.minecraft.client.gui.GuiGraphics", "net.minecraft.client.gui.GuiGraphicsExtractor")
        }
        string {
            direction = eval(current.version, ">=26.1")
            replace("renderListSeparators", "extractListSeparators")
        }
        string {
            direction = eval(current.version, ">=26.1")
            replace("renderContent", "extractContent")
        }
        string {
            direction = eval(current.version, ">=26.1")
            replace("drawCenteredString", "centeredText")
        }
        string {
            direction = eval(current.version, ">=26.1")
            replace("renderWidget", "extractWidgetRenderState")
        }
        string {
            direction = eval(current.version, ">=26.1")
            replace("renderBackground", "extractBackground")
        }
        string {
            direction = eval(current.version, ">=26.1")
            replace("ClickType", "ContainerInput")
        }
        string {
            direction = eval(current.version, ">=26.1")
            replace("net.minecraft.client.gui.render.state", "net.minecraft.client.renderer.state.gui")
        }
    }
}
