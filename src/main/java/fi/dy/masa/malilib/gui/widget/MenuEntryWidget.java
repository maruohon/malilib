package fi.dy.masa.malilib.gui.widget;

import javax.annotation.Nullable;
import fi.dy.masa.malilib.listener.EventListener;
import fi.dy.masa.malilib.render.text.StyledTextLine;

public class MenuEntryWidget extends BackgroundWidget
{
    public final EventListener action;

    public MenuEntryWidget(StyledTextLine text, EventListener action)
    {
        this(0, 0, 10, 12, text, action);
    }

    public MenuEntryWidget(int x, int y, int width, int height, StyledTextLine text, EventListener action)
    {
        this(x, y, width, height, text, action, null);
    }

    public MenuEntryWidget(int x, int y, int width, int height, StyledTextLine text, EventListener action, @Nullable String hoverText)
    {
        super(x, y, width, height);

        this.action = action;

        this.setText(text);
        this.setBackgroundColor(0xFF000000);
        this.setBackgroundColorHovered(0xFF009090);
        this.setRenderBackground(true);
        this.setRenderHoverBackground(true);

        this.setWidth(this.text.renderWidth + 10);

        if (hoverText != null)
        {
            this.addHoverStrings(hoverText);
        }
    }

    @Override
    protected boolean onMouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        this.action.onEvent();
        return true;
    }
}
