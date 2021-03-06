package fi.dy.masa.malilib.gui.widget;

import javax.annotation.Nullable;
import fi.dy.masa.malilib.gui.icon.MultiIcon;

public class IconWidget extends BackgroundWidget
{
    @Nullable protected MultiIcon icon;
    protected boolean doHighlight;
    protected boolean enabled;

    public IconWidget(int x, int y, @Nullable MultiIcon icon)
    {
        super(x, y, icon.getWidth(), icon.getHeight());

        this.setIcon(icon);
    }

    @Nullable
    public MultiIcon getIcon()
    {
        return this.icon;
    }

    public IconWidget setIcon(@Nullable MultiIcon icon)
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
                width += this.padding.getLeft() + this.padding.getRight() + this.borderWidth * 2;
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
                height += this.padding.getTop() + this.padding.getBottom() + this.borderWidth * 2;
            }

            this.setHeight(height);
        }
        else
        {
            this.setHeight(0);
        }
    }

    @Override
    public void renderAt(int x, int y, float z, int mouseX, int mouseY, boolean isActiveGui, boolean hovered)
    {
        if (this.icon != null)
        {
            super.renderAt(x, y, z, mouseX, mouseY, isActiveGui, hovered);

            if (this.backgroundEnabled)
            {
                x += this.padding.getLeft() + this.borderWidth;
                y += this.padding.getTop() + this.borderWidth;
            }

            this.icon.renderAt(x, y, z + 0.1f, this.enabled, this.doHighlight && hovered);
        }
    }
}
