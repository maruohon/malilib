package fi.dy.masa.malilib.gui.widget;

import javax.annotation.Nullable;
import fi.dy.masa.malilib.listener.EventListener;
import fi.dy.masa.malilib.render.text.StyledTextLine;

public class MenuEntryWidget extends InteractableWidget
{
    protected final EventListener action;
    @Nullable protected EventListener menuCloseHook;

    public MenuEntryWidget(StyledTextLine text, EventListener action)
    {
        this(10, 12, text, action, null);
    }

    public MenuEntryWidget(int width, int height, StyledTextLine text, EventListener action)
    {
        this(width, height, text, action, null);
    }

    public MenuEntryWidget(int width, int height, StyledTextLine text, EventListener action, @Nullable String hoverText)
    {
        super(width, height);

        this.action = action;
        this.setText(text);
        this.setWidth(this.text.renderWidth + 10);

        if (hoverText != null)
        {
            this.addHoverStrings(hoverText);
        }
    }

    public void setMenuCloseHook(@Nullable EventListener menuCloseHook)
    {
        this.menuCloseHook = menuCloseHook;
    }

    @Override
    protected boolean onMouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        this.action.onEvent();

        if (this.menuCloseHook != null)
        {
            this.menuCloseHook.onEvent();
        }

        return true;
    }
}
