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
import me.lambdaurora.spruceui.SpruceTextAreaWidget;
import me.lambdaurora.spruceui.SpruceTexts;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.options.Option;
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
public class MappingsStringInputScreen extends Screen
{
    private final Screen               parent;
    private final Option               reloadMappingsOption;
    private       String               mappings;
    private       SpruceTextAreaWidget textArea;

    protected MappingsStringInputScreen(@Nullable Screen parent)
    {
        super(new TranslatableText("lambdacontrols.menu.title.mappings.string"));
        this.parent = parent;

        this.reloadMappingsOption = new ReloadControllerMappingsOption(btn -> {
            this.writeMappings();
        });
    }


    @Override
    public void removed()
    {
        this.writeMappings();
        Controller.updateMappings();
        super.removed();
    }

    @Override
    public void onClose()
    {
        this.removed();
        super.onClose();
    }

    public void writeMappings()
    {
        if (this.textArea != null) {
            this.mappings = this.textArea.getText();
            System.out.println(this.mappings);
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

    @Override
    protected void init()
    {
        super.init();

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

        int textFieldWidth = (int) (this.width * (3.0 / 4.0));
        this.textArea = new SpruceTextAreaWidget(this.textRenderer, this.width / 2 - textFieldWidth / 2, 50, textFieldWidth, this.height - 100, new LiteralText(mappings));
        this.textArea.setText(mappings);
        // Display as many lines as possible
        this.textArea.setDisplayedLines(this.textArea.getInnerHeight() / this.textRenderer.fontHeight);
        this.addButton(this.textArea);

        this.addButton(this.reloadMappingsOption.createButton(this.client.options, this.width / 2 - 155, this.height - 29, 150));
        this.addButton(new ButtonWidget(this.width / 2 - 155 + 160, this.height - 29, 150, 20, SpruceTexts.GUI_DONE,
                (buttonWidget) -> this.client.openScreen(this.parent)));
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta)
    {
        this.renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 8, 16777215);
    }
}
