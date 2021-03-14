package fi.dy.masa.malilib.gui;

import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiScreen;
import fi.dy.masa.malilib.gui.widget.button.ButtonActionListener;

public interface ScreenTab
{
    /**
     * Returns the display name for this config tab.
     * This is used in the tab buttons.
     * @return the display name for this tab (button)
     */
    String getDisplayName();

    /**
     * Returns the button action listener that should be used for this tab's selection button
     * @return the button action listener that handles this tab's button's presses
     */
    ButtonActionListener getButtonActionListener(BaseTabbedScreen currentScreen);

    /**
     * Returns true if this tab can use the current screen,
     * and does not need the screen to be switched.
     * @param currentScreen
     * @return true if no screen switch is needed
     */
    boolean canUseCurrentScreen(@Nullable GuiScreen currentScreen);

    /**
     * Creates a new screen for this tab.<br>
     * This may be called to create the appropriate screen if the current
     * screen is not suitable for this tab.
     * @return
     */
    GuiScreen createScreen(@Nullable GuiScreen currentScreen);

    /**
     * Opens the screen for this tab
     * @param currentScreen
     */
    default void openScreen(@Nullable GuiScreen currentScreen)
    {
        GuiScreen screen = this.createScreen(currentScreen);

        if (screen instanceof BaseTabbedScreen)
        {
            ((BaseTabbedScreen) screen).setCurrentTab(this);
        }

        BaseScreen.openScreen(screen);
    }
}
