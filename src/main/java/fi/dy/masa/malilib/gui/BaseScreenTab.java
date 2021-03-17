package fi.dy.masa.malilib.gui;

import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiScreen;
import fi.dy.masa.malilib.gui.widget.button.ButtonActionListener;
import fi.dy.masa.malilib.util.StringUtils;

public class BaseScreenTab implements ScreenTab
{
    protected final String translationKey;
    protected final Function<GuiScreen, BaseScreen> screenFactory;
    protected final Function<BaseTabbedScreen, ButtonActionListener> listenerFactory;
    protected final Predicate<GuiScreen> screenChecker;
    @Nullable protected String hoverTextTranslationKey;

    public BaseScreenTab(String translationKey, Predicate<GuiScreen> screenChecker, Function<GuiScreen, BaseScreen> screenFactory)
    {
        this.translationKey = translationKey;
        this.screenChecker = screenChecker;
        this.screenFactory = screenFactory;
        this.listenerFactory = (scr) -> (btn, mbtn) -> this.switchTab(scr);
    }

    public BaseScreenTab(String translationKey, Predicate<GuiScreen> screenChecker,
                         Function<GuiScreen, BaseScreen> screenFactory,
                         Function<BaseTabbedScreen, ButtonActionListener> listenerFactory)
    {
        this.translationKey = translationKey;
        this.screenChecker = screenChecker;
        this.screenFactory = screenFactory;
        this.listenerFactory = listenerFactory;
    }

    @Override
    public String getDisplayName()
    {
        return StringUtils.translate(this.translationKey);
    }

    @Nullable
    @Override
    public String getHoverText()
    {
        return this.hoverTextTranslationKey;
    }

    public BaseScreenTab setHoverText(@Nullable String hoverTextTranslationKey)
    {
        this.hoverTextTranslationKey = hoverTextTranslationKey;
        return this;
    }

    @Override
    public boolean canUseCurrentScreen(@Nullable GuiScreen currentScreen)
    {
        return this.screenChecker.test(currentScreen);
    }

    @Override
    public BaseScreen createScreen(GuiScreen currentScreen)
    {
        return this.screenFactory.apply(currentScreen);
    }

    @Override
    public ButtonActionListener getButtonActionListener(final BaseTabbedScreen currentScreen)
    {
        return this.listenerFactory.apply(currentScreen);
    }

    /**
     * Switches the active tab, or opens a new screen if the current screen
     * is not suitable for this tab.
     * @param currentScreen
     */
    public void switchTab(@Nullable BaseTabbedScreen currentScreen)
    {
        if (currentScreen != null && this.canUseCurrentScreen(currentScreen))
        {
            currentScreen.switchToTab(this);
        }
        else
        {
            this.openScreen(currentScreen);
        }
    }
}
