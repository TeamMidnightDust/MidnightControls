/*
 * Copyright © 2020 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdacontrols.client.gui;

import me.lambdaurora.lambdacontrols.client.LambdaControlsClient;
import me.lambdaurora.lambdacontrols.client.controller.Controller;
import me.lambdaurora.spruceui.Position;
import me.lambdaurora.spruceui.SpruceTexts;
import me.lambdaurora.spruceui.option.SpruceOption;
import me.lambdaurora.spruceui.screen.SpruceScreen;
import me.lambdaurora.spruceui.widget.SpruceButtonWidget;
import me.lambdaurora.spruceui.widget.container.SpruceContainerWidget;
import me.lambdaurora.spruceui.widget.text.SpruceTextAreaWidget;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import org.jetbrains.annotations.Nullable;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Represents the controller mappings file editor screen.
 *
 * @author LambdAurora
 * @version 1.4.3
 * @since 1.4.3
 */
public class MappingsStringInputWidget extends SpruceContainerWidget {
    private final SpruceOption reloadMappingsOption;
    private String mappings;
    private SpruceTextAreaWidget textArea;

    protected MappingsStringInputWidget(Position position, int width, int height) {
        super(position, width, height);
        //super(new TranslatableText("lambdacontrols.menu.title.mappings.string"));

        this.reloadMappingsOption = ReloadControllerMappingsOption.newOption(btn -> {
            this.writeMappings();
        });

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
                FileWriter fw = new FileWriter(LambdaControlsClient.MAPPINGS_FILE, false);
                fw.write(this.mappings);
                fw.close();
            } catch (IOException e) {
                if (this.client != null)
                    this.client.getToastManager().add(SystemToast.create(this.client, SystemToast.Type.TUTORIAL_HINT,
                            new TranslatableText("lambdacontrols.controller.mappings.error.write"), LiteralText.EMPTY));
                e.printStackTrace();
            }
        }
    }

    protected void init() {
        if (this.textArea != null) {
            this.mappings = this.textArea.getText();
        }

        String mappings = "";

        if (this.mappings != null)
            mappings = this.mappings;
        else if (LambdaControlsClient.MAPPINGS_FILE.exists()) {
            try {
                mappings = String.join("\n", Files.readAllLines(LambdaControlsClient.MAPPINGS_FILE.toPath()));
                this.mappings = mappings;
            } catch (IOException e) {
                /* Ignored */
            }
        }

        int textFieldWidth = (int) (this.width * (5.0 / 6.0));
        this.textArea = new SpruceTextAreaWidget(Position.of(this, this.width / 2 - textFieldWidth / 2, 0), textFieldWidth, this.height - 50, new LiteralText(mappings));
        this.textArea.setText(mappings);
        // Display as many lines as possible
        this.textArea.setDisplayedLines(this.textArea.getInnerHeight() / this.client.textRenderer.fontHeight);
        this.addChild(this.textArea);

        this.addChild(this.reloadMappingsOption.createWidget(Position.of(this.width / 2 - 155, this.height - 29), 310));
    }

    /*public void renderTitle(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 8, 16777215);
    }*/
}
