/*
 * Copyright © 2021 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of midnightcontrols.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package eu.midnightdust.midnightcontrols.client.gui;

import eu.midnightdust.midnightcontrols.client.MidnightControlsClient;
import eu.midnightdust.midnightcontrols.client.MidnightControlsConfig;
import eu.midnightdust.midnightcontrols.client.controller.Controller;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import org.thinkingstudio.obsidianui.Position;
import org.thinkingstudio.obsidianui.option.SpruceOption;
import org.thinkingstudio.obsidianui.option.SpruceSimpleActionOption;
import org.thinkingstudio.obsidianui.widget.container.SpruceContainerWidget;
import org.thinkingstudio.obsidianui.widget.text.SpruceTextAreaWidget;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.Text;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Represents the controller mappings file editor screen.
 *
 * @author LambdAurora
 * @version 1.7.0
 * @since 1.4.3
 */
public class MappingsStringInputWidget extends SpruceContainerWidget {
    private final SpruceOption copyGuidOption;
    private final SpruceOption reloadMappingsOption;
    private String mappings;
    private SpruceTextAreaWidget textArea;

    protected MappingsStringInputWidget(Position position, int width, int height) {
        super(position, width, height);

        this.reloadMappingsOption = ReloadControllerMappingsOption.newOption(btn -> {
            this.writeMappings();
        });
        this.copyGuidOption = SpruceSimpleActionOption.of("midnightcontrols.menu.copy_controller_guid", button -> client.keyboard.setClipboard(MidnightControlsConfig.getController().getGuid()));

        this.init();
    }

    public void removed() {
        this.writeMappings();
        Controller.updateMappings();
    }

    public void onClose() {
        this.removed();
    }

    public void writeMappings() {
        if (this.textArea != null) {
            this.mappings = this.textArea.getText();
            try {
                var fw = new FileWriter(MidnightControlsClient.MAPPINGS_FILE, false);
                fw.write(this.mappings);
                fw.close();
            } catch (IOException e) {
                if (this.client != null)
                    this.client.getToastManager().add(SystemToast.create(this.client, SystemToast.Type.PERIODIC_NOTIFICATION,
                            Text.translatable("midnightcontrols.controller.mappings.error.write"), Text.empty()));
                e.fillInStackTrace();
            }
        }
    }

    protected void init() {
        if (this.textArea != null) {
            this.mappings = this.textArea.getText();
        }

        var mappings = "";

        if (this.mappings != null)
            mappings = this.mappings;
        else if (MidnightControlsClient.MAPPINGS_FILE.exists()) {
            try {
                mappings = String.join("\n", Files.readAllLines(MidnightControlsClient.MAPPINGS_FILE.toPath()));
                this.mappings = mappings;
            } catch (IOException e) {
                /* Ignored */
            }
        }

        int textFieldWidth = (int) (this.width * (5.0 / 6.0));
        this.textArea = new SpruceTextAreaWidget(Position.of(this, this.width / 2 - textFieldWidth / 2, 0), textFieldWidth, this.height - 50, Text.literal(mappings));
        this.textArea.setText(mappings);
        // Display as many lines as possible
        this.textArea.setDisplayedLines(this.textArea.getInnerHeight() / this.client.textRenderer.fontHeight);
        this.addChild(this.textArea);

        this.addChild(this.reloadMappingsOption.createWidget(Position.of(this.width / 2 - 155, this.height - 29), 257));
        this.addChild(this.copyGuidOption.createWidget(Position.of(this.width / 2 + 105, this.height - 29), 65));
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        super.renderWidget(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.client.textRenderer, Text.translatable("midnightcontrols.menu.multiple_mapping_tip"), this.textArea.getX() + this.textArea.getWidth() / 2, this.textArea.getY() + this.textArea.getHeight() - 12, 0x888888);
        context.drawCenteredTextWithShadow(this.client.textRenderer, Text.translatable("midnightcontrols.menu.current_controller_guid", MidnightControlsConfig.getController().getGuid()), this.textArea.getX() + this.textArea.getWidth() / 2, this.height - 21, 0xFFFFFF);
    }
}
