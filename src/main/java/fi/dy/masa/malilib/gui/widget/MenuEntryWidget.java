package fi.dy.masa.malilib.gui.widget;

import javax.annotation.Nullable;
import fi.dy.masa.malilib.listener.EventListener;
import fi.dy.masa.malilib.render.text.StyledTextLine;

public class MenuEntryWidget extends InteractableWidget
{
    protected final EventListener action;
    @Nullable protected EventListener menuCloseHook;
    protected boolean enabled = true;

    public MenuEntryWidget(StyledTextLine text, EventListener action)
    {
        this(10, 12, text, action);
    }

    public MenuEntryWidget(int width, int height, StyledTextLine text, EventListener action)
    {
        super(width, height);

        this.action = action;
        this.setText(text);
        this.setWidth(this.text.renderWidth + 10);
    }

    public MenuEntryWidget setEnabled(boolean enabled)
    {
        this.enabled = enabled;

        int color = enabled ? 0xFFFFFFFF : 0xFF808080;
        this.getTextSettings().setTextColor(color);

        return this;
    }

    public void setMenuCloseHook(@Nullable EventListener menuCloseHook)
    {
        this.menuCloseHook = menuCloseHook;
    }

    @Override
    protected boolean onMouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (this.enabled)
        {
            this.action.onEvent();

            if (this.menuCloseHook != null)
            {
                this.menuCloseHook.onEvent();
            }
        }

        return true;
    }
}
