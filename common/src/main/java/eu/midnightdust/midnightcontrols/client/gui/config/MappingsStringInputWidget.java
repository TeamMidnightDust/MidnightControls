/*
 * Copyright © 2021 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of midnightcontrols.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package eu.midnightdust.midnightcontrols.client.gui.config;

import com.google.common.collect.Lists;
import dev.lambdaurora.spruceui.Position;
import dev.lambdaurora.spruceui.widget.text.SpruceTextAreaWidget;
import eu.midnightdust.lib.config.EntryInfo;
import eu.midnightdust.lib.config.MidnightConfigListWidget;
import eu.midnightdust.lib.config.MidnightConfigScreen;
import eu.midnightdust.midnightcontrols.client.MidnightControlsConfig;
import net.minecraft.client.gui.widget.*;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

import static eu.midnightdust.midnightcontrols.client.MidnightControlsClient.client;

/**
 * Represents the controller mappings file editor screen.
 *
 * @author LambdAurora
 * @version 1.7.0
 * @since 1.4.3
 */
public class MappingsStringInputWidget {

    public static void add(EntryInfo centered, MidnightConfigListWidget list, MidnightConfigScreen screen) {
        //SpruceTextAreaWidget editButton = new SpruceTextAreaWidget(Position.of(0, 0), 20, 20, Text.empty());
        MultilineTextFieldWidget editButton = new MultilineTextFieldWidget(screen.getTextRenderer(), screen.width / 2 - 128, 0, 256, 60, Text.of("TESTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT\nnew line!"));
        TextIconButtonWidget resetButton = TextIconButtonWidget.builder(Text.translatable("controls.reset"), (button -> {
            screen.updateList();
        }), true).texture(Identifier.of("midnightlib","icon/reset"), 12, 12).dimension(20, 60).build();
        resetButton.setPosition(screen.width / 2 - 128 + 256 + 4, 0);
        editButton.setChangedListener(string -> {
            resetButton.active = !string.isEmpty();
        });

        list.addButton(List.of(), Text.translatable("midnightcontrols.menu.title.mappings.string"), centered);
        //screen.addDrawableChild(editButton);
        list.addButton(Lists.newArrayList(editButton, resetButton), Text.empty(), centered);
        list.addButton(List.of(), Text.empty(), centered);
        list.addButton(List.of(), Text.empty(), centered);
        list.addButton(List.of(), Text.translatable("midnightcontrols.menu.multiple_mapping_tip"), centered);
        //list.addButton();

        ButtonWidget copyButton = TextIconButtonWidget.builder(Text.of("Copy GUID"), widget -> {
            client.keyboard.setClipboard(MidnightControlsConfig.getController().getGuid());
        }).dimensions(screen.width - 185, 0, 150, 20).build();

        list.addButton(List.of(copyButton), Text.translatable("midnightcontrols.menu.current_controller_guid", MidnightControlsConfig.getController().getGuid()), new EntryInfo(null, screen.modid));
    }
}
