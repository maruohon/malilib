package malilib.gui.tab;

import java.util.List;
import javax.annotation.Nullable;

import net.minecraft.client.gui.screen.Screen;

import malilib.gui.BaseScreen;
import malilib.gui.BaseTabbedScreen;
import malilib.gui.widget.button.ButtonActionListener;
import malilib.util.data.NameIdentifiable;

public interface ScreenTab extends NameIdentifiable
{
    /**
     * Returns the display name for this config tab.
     * This is used in the tab buttons.
     * @return the display name for this tab (button)
     */
    String getDisplayName();

    /**
     * Returns the tab switch button hover text translation key, if the
     * button should have a hover text.
     * @return hover text translation key, or null if no hover text should be used
     */
    @Nullable
    String getHoverText();

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
    boolean canUseCurrentScreen(@Nullable Screen currentScreen);

    /**
     * Creates a new screen for this tab.<br>
     * This may be called to create the appropriate screen if the current
     * screen is not suitable for this tab.
     * @return
     */
    Screen createScreen();

    /**
     * Opens the screen for this tab
     */
    default void createAndOpenScreen()
    {
        Screen screen = this.createScreen();

        if (screen instanceof BaseTabbedScreen)
        {
            ((BaseTabbedScreen) screen).setCurrentTab(this);
        }

        BaseScreen.openScreen(screen);
    }

    /**
     * Returns the tab by the given name from the provided list
     * @param tabName
     * @param list
     * @param defaultTab the default value to return, if no matches are found in the provided list
     * @return the first found tab by the given name, or the provided default tab if there were no matches
     */
    static ScreenTab getTabByNameOrDefault(String tabName, List<ScreenTab> list, ScreenTab defaultTab)
    {
        for (ScreenTab tab : list)
        {
            if (tabName.equalsIgnoreCase(tab.getName()))
            {
                return tab;
            }
        }

        return defaultTab;
    }
}
