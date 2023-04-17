package malilib.gui.widget;

import malilib.render.text.StyledTextLine;

public class MenuEntryWidget extends InteractableWidget
{
    protected final Runnable action;

    public MenuEntryWidget(StyledTextLine text, Runnable action)
    {
        this(text, action, true);
    }

    public MenuEntryWidget(StyledTextLine text, Runnable action, boolean enabled)
    {
        this(10, 12, text, action);

        this.setEnabled(enabled);
    }

    public MenuEntryWidget(int width, int height, StyledTextLine text, Runnable action)
    {
        super(width, height);

        this.canReceiveMouseClicks = true;
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

    /**
     * Handle the mouse click.<br>
     * <b>Note:</b> MenuEntryWidget should return false, if it wants the MenuWidget to close the menu,
     * or return true if it wants the menu to stay open.
     */
    @Override
    protected boolean onMouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (this.isEnabled() && mouseButton == 0)
        {
            this.action.run();
            return false;
        }

        return true;
    }
}
