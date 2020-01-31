package fi.dy.masa.malilib.gui.widgets;

import javax.annotation.Nullable;
import fi.dy.masa.malilib.gui.interfaces.IGuiIcon;

public class WidgetIcon extends WidgetBackground
{
    @Nullable protected IGuiIcon icon;
    protected boolean doHighlight;
    protected boolean enabled;

    public WidgetIcon(int x, int y, @Nullable IGuiIcon icon)
    {
        super(x, y, icon.getWidth(), icon.getHeight());

        this.setIcon(icon);
    }

    public WidgetIcon setIcon(@Nullable IGuiIcon icon)
    {
        this.icon = icon;

        this.updateWidth();
        this.updateHeight();

        return this;
    }

    public WidgetIcon setEnabled(boolean enabled)
    {
        this.enabled = enabled;
        return this;
    }

    public WidgetIcon setDoHighlight(boolean doHighlight)
    {
        this.doHighlight = doHighlight;
        return this;
    }

    @Override
    public int updateWidth()
    {
        if (this.icon != null)
        {
            int width = this.icon.getWidth();

            if (this.backgroundEnabled)
            {
                width += this.paddingX * 2 + this.borderWidth * 2;
            }

            this.setWidth(width);
        }
        else
        {
            this.setWidth(0);
        }

        return this.getWidth();
    }

    @Override
    public int updateHeight()
    {
        if (this.icon != null)
        {
            int height = this.icon.getHeight();

            if (this.backgroundEnabled)
            {
                height += this.paddingY * 2 + this.borderWidth * 2;
            }

            this.setHeight(height);
        }
        else
        {
            this.setHeight(0);
        }

        return this.getHeight();
    }

    @Override
    public void render(int mouseX, int mouseY, boolean selected)
    {
        if (this.icon != null)
        {
            this.renderBackground();

            int x = this.getX();
            int y = this.getY();

            if (this.backgroundEnabled)
            {
                x += this.paddingX + this.borderWidth;
                y += this.paddingY + this.borderWidth;
            }

            this.icon.renderAt(x, y, this.getZLevel() + 0.1f, this.enabled, this.doHighlight && this.isMouseOver(mouseX, mouseY));
        }
    }
}
