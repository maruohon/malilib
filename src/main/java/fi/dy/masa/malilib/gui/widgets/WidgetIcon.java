package fi.dy.masa.malilib.gui.widgets;

import fi.dy.masa.malilib.gui.interfaces.IGuiIcon;
import fi.dy.masa.malilib.render.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;

public class WidgetIcon extends WidgetBase
{
    protected final Minecraft mc;
    protected final IGuiIcon icon;

    public WidgetIcon(int x, int y, float zLevel, IGuiIcon icon, Minecraft mc)
    {
        super(x, y, icon.getWidth(), icon.getHeight(), zLevel);

        this.mc = mc;
        this.icon = icon;
    }

    public void render(boolean enabled, boolean selected)
    {
        GlStateManager.color(1f, 1f, 1f, 1f);
        this.mc.getTextureManager().bindTexture(this.icon.getTexture());
        this.icon.renderAt(this.x, this.y, this.zLevel, enabled, selected);

        if (selected)
        {
            RenderUtils.drawOutlinedBox(this.x, this.y, this.width, this.height, 0x20C0C0C0, 0xE0FFFFFF);
        }
    }
}
