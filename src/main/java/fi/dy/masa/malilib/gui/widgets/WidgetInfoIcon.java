package fi.dy.masa.malilib.gui.widgets;

import fi.dy.masa.malilib.gui.interfaces.IGuiIcon;
import net.minecraft.client.renderer.GlStateManager;

public class WidgetInfoIcon extends WidgetHoverInfo
{
    protected final IGuiIcon icon;
    protected final float zLevel;

    public WidgetInfoIcon(int x, int y, float zLevel, IGuiIcon icon, String key, Object... args)
    {
        super(x, y, icon.getWidth(), icon.getHeight(), key, args);

        this.zLevel = zLevel;
        this.icon = icon;
    }

    @Override
    public void render(int mouseX, int mouseY, boolean selected)
    {
        GlStateManager.color(1f, 1f, 1f, 1f);
        this.mc.getTextureManager().bindTexture(this.icon.getTexture());
        this.icon.renderAt(this.x, this.y, this.zLevel, false, selected);
    }
}
