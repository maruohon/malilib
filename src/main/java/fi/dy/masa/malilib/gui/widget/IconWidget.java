package fi.dy.masa.malilib.gui.widget;

import javax.annotation.Nullable;
import fi.dy.masa.malilib.gui.icon.Icon;
import fi.dy.masa.malilib.gui.util.ScreenContext;
import fi.dy.masa.malilib.util.data.EdgeInt;

public class IconWidget extends InteractableWidget
{
    protected boolean doHighlight;
    protected boolean enabled;

    public IconWidget(Icon icon)
    {
        super(icon.getWidth(), icon.getHeight());

        this.setIcon(icon);
    }

    @Override
    public void setIcon(@Nullable Icon icon)
    {
        super.setIcon(icon);
        this.updateSize();
    }

    public IconWidget setUseEnabledVariant(boolean enabled)
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
        Icon icon = this.getIcon();
        int width = 0;

        if (icon != null)
        {
            width = icon.getWidth();

            if (this.getBackgroundRenderer().getNormalSettings().isEnabled())
            {
                int bw = this.getBorderRenderer().getNormalSettings().getActiveBorderWidth();
                width += this.padding.getHorizontalTotal() + bw * 2;
            }
        }

        this.setWidthNoUpdate(width);
    }

    @Override
    public void updateHeight()
    {
        Icon icon = this.getIcon();
        int height = 0;

        if (icon != null)
        {
            height = icon.getHeight();

            if (this.getBackgroundRenderer().getNormalSettings().isEnabled())
            {
                int bw = this.getBorderRenderer().getNormalSettings().getActiveBorderWidth();
                height += this.padding.getVerticalTotal() + bw * 2;
            }
        }

        this.setHeightNoUpdate(height);
    }

    @Override
    public void renderAt(int x, int y, float z, ScreenContext ctx)
    {
        boolean hovered = this.doHighlight && this.isHoveredForRender(ctx);
        int color = this.getTextColorForRender(hovered);

        this.renderWidgetBackgroundAndBorder(x, y, z, ctx);
        this.renderText(x, y, z, color, ctx);

        if (this.getBackgroundRenderer().getNormalSettings().isEnabled())
        {
            int bw = this.getBorderRenderer().getNormalSettings().getActiveBorderWidth();
            EdgeInt padding = this.padding;
            x += padding.getLeft() + bw;
            y += padding.getTop() + bw;
        }

        this.renderIcon(x, y, z, this.enabled, hovered, ctx);
    }
}
