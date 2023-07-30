package malilib.gui.widget;

import malilib.gui.icon.Icon;
import malilib.gui.util.ScreenContext;

public class InfoIconWidget extends HoverInfoWidget
{
    protected final Icon icon;

    public InfoIconWidget(Icon icon, String key, Object... args)
    {
        super(icon.getWidth(), icon.getHeight(), key, args);

        this.icon = icon;
    }

    @Override
    public void renderAt(int x, int y, float z, ScreenContext ctx)
    {
        this.icon.renderAt(x, y, z + 0.1f, IconWidget.getVariantIndex(true, this.isHoveredForRender(ctx)), ctx);
    }
}
