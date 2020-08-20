package fi.dy.masa.malilib.gui.widget;

import javax.annotation.Nullable;
import fi.dy.masa.malilib.gui.icon.Icon;

public class IconWidget extends BackgroundWidget
{
    @Nullable protected Icon icon;
    protected boolean doHighlight;
    protected boolean enabled;

    public IconWidget(int x, int y, @Nullable Icon icon)
    {
        super(x, y, icon.getWidth(), icon.getHeight());

        this.setIcon(icon);
    }

    @Nullable
    public Icon getIcon()
    {
        return this.icon;
    }

    public IconWidget setIcon(@Nullable Icon icon)
    {
        this.icon = icon;

        this.updateWidth();
        this.updateHeight();

        return this;
    }

    public IconWidget setEnabled(boolean enabled)
    {
        this.enabled = enabled;
        return this;
    }

    public IconWidget setDoHighlight(boolean doHighlight)
    {
        this.doHighlight = doHighlight;
        return this;
    }

    @Override
    public void updateWidth()
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
    }

    @Override
    public void updateHeight()
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
    }

    @Override
    public void render(int mouseX, int mouseY, boolean isActiveGui, boolean hovered)
    {
        if (this.icon != null)
        {
            super.render(mouseX, mouseY, isActiveGui, hovered);

            int x = this.getX();
            int y = this.getY();

            if (this.backgroundEnabled)
            {
                x += this.paddingX + this.borderWidth;
                y += this.paddingY + this.borderWidth;
            }

            this.icon.renderAt(x, y, this.getZLevel() + 0.1f, this.enabled, this.doHighlight && this.isHoveredForRender(mouseX, mouseY));
        }
    }
}
