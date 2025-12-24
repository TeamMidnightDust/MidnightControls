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
import eu.midnightdust.lib.config.EntryInfo;
import eu.midnightdust.lib.config.MidnightConfigListWidget;
import eu.midnightdust.lib.config.MidnightConfigScreen;
import eu.midnightdust.midnightcontrols.client.MidnightControlsConfig;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
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
        MultilineTextFieldWidget editButton = new MultilineTextFieldWidget(screen.getFont(), screen.width / 2 - 128, 0, 256, 60, Component.nullToEmpty("TESTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT\nnew line!"));
        SpriteIconButton resetButton = SpriteIconButton.builder(Component.translatable("controls.reset"), (button -> {
            screen.updateList();
        }), true).sprite(Identifier.fromNamespaceAndPath("midnightlib","icon/reset"), 12, 12).size(20, 60).build();
        resetButton.setPosition(screen.width / 2 - 128 + 256 + 4, 0);
        editButton.setResponder(string -> {
            resetButton.active = !string.isEmpty();
        });

        list.addButton(List.of(), Component.translatable("midnightcontrols.menu.title.mappings.string"), centered);
        //screen.addDrawableChild(editButton);
        list.addButton(Lists.newArrayList(editButton, resetButton), Component.empty(), centered);
        list.addButton(List.of(), Component.empty(), centered);
        list.addButton(List.of(), Component.empty(), centered);
        list.addButton(List.of(), Component.translatable("midnightcontrols.menu.multiple_mapping_tip"), centered);
        //list.addButton();

        Button copyButton = SpriteIconButton.builder(Component.nullToEmpty("Copy GUID"), widget -> {
            client.keyboardHandler.setClipboard(MidnightControlsConfig.getController().getGuid());
        }).bounds(screen.width - 185, 0, 150, 20).build();

        list.addButton(List.of(copyButton), Component.translatable("midnightcontrols.menu.current_controller_guid", MidnightControlsConfig.getController().getGuid()), new EntryInfo(null, screen.modid));
    }
}
