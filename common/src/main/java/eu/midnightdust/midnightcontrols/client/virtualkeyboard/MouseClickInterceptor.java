package eu.midnightdust.midnightcontrols.client.virtualkeyboard;

import eu.midnightdust.midnightcontrols.client.virtualkeyboard.clickhandler.AbstractScreenClickHandler;
import eu.midnightdust.midnightcontrols.client.virtualkeyboard.clickhandler.BookEditScreenClickHandler;
import eu.midnightdust.midnightcontrols.client.virtualkeyboard.clickhandler.DefaultScreenClickHandler;
import eu.midnightdust.midnightcontrols.client.virtualkeyboard.clickhandler.SignEditScreenClickHandler;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.BookEditScreen;
import net.minecraft.client.gui.screen.ingame.SignEditScreen;

import java.util.HashMap;
import java.util.Map;


public class MouseClickInterceptor {

    private final Map<Class<?>, AbstractScreenClickHandler<?>> clickHandlers;

    public MouseClickInterceptor() {
        this.clickHandlers = new HashMap<>();
        this.clickHandlers.put(BookEditScreen.class, new BookEditScreenClickHandler());
        this.clickHandlers.put(SignEditScreen.class, new SignEditScreenClickHandler());
        this.clickHandlers.put(Screen.class, new DefaultScreenClickHandler());
    }

    @SuppressWarnings("unchecked")
    public <T extends Screen> void intercept(T screen, double mouseX, double mouseY) {
        AbstractScreenClickHandler<T> handler = (AbstractScreenClickHandler<T>) clickHandlers.get(screen.getClass());

        if (handler == null) {
            handler = (AbstractScreenClickHandler<T>) clickHandlers.get(Screen.class);
        }

        handler.handle(screen, mouseX, mouseY);
    }
}
