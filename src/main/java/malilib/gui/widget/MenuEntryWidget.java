package malilib.gui.widget;

import javax.annotation.Nullable;

import malilib.listener.EventListener;
import malilib.render.text.StyledTextLine;

public class MenuEntryWidget extends InteractableWidget
{
    protected final EventListener action;
    @Nullable protected EventListener menuCloseHook;

    public MenuEntryWidget(StyledTextLine text, EventListener action)
    {
        this(text, action, true);
    }

    public MenuEntryWidget(StyledTextLine text, EventListener action, boolean enabled)
    {
        this(10, 12, text, action);

        this.setEnabled(enabled);
    }

    public MenuEntryWidget(int width, int height, StyledTextLine text, EventListener action)
    {
        super(width, height);

        this.canBeClicked = true;
        this.action = action;
        this.setText(text);
        this.setWidth(this.text.renderWidth + 10);
    }

    @Override
    protected void onEnabledStateChanged(boolean isEnabled)
    {
        int color = isEnabled ? 0xFFFFFFFF : 0xFF808080;
        this.getTextSettings().setTextColor(color);
    }

    @Override
    protected boolean onMouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (this.isEnabled())
        {
            this.action.onEvent();
            this.tryCloseMenu();
        }

        return true;
    }

    public void setMenuCloseHook(@Nullable EventListener menuCloseHook)
    {
        this.menuCloseHook = menuCloseHook;
    }

    public void tryCloseMenu()
    {
        if (this.menuCloseHook != null)
        {
            this.scheduleTask(this.menuCloseHook::onEvent);
        }
    }
}
