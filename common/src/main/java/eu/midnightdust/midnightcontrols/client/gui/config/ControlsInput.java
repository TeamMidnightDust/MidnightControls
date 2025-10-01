package eu.midnightdust.midnightcontrols.client.gui.config;

import eu.midnightdust.midnightcontrols.client.controller.ButtonBinding;

import java.util.List;

public interface ControlsInput {
    void setWaiting(boolean value);
    boolean isWaiting();

    List<Integer> getCurrentButtons();

    ButtonBinding getFocusedBinding();

    void finishBindingEdit(int[] buttons);

    default void update() {};
}
