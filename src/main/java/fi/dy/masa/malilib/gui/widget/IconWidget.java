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

            if (this.renderBackground)
            {
                width += this.padding.getLeft() + this.padding.getRight() + this.borderWidthNormal * 2;
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

            if (this.renderBackground)
            {
                height += this.padding.getTop() + this.padding.getBottom() + this.borderWidthNormal * 2;
            }

            this.setHeight(height);
        }
        else
        {
            this.setHeight(0);
        }
    }

    @Override
    public void renderAt(int x, int y, float z, ScreenContext ctx)
    {
        if (this.icon != null)
        {
            super.renderAt(x, y, z, ctx);

            if (this.renderBackground)
            {
                x += this.padding.getLeft() + this.borderWidthNormal;
                y += this.padding.getTop() + this.borderWidthNormal;
            }

            this.icon.renderAt(x, y, z + 0.1f, this.enabled, this.doHighlight && this.isHoveredForRender(ctx));
        }
    }
}
