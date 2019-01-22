package fi.dy.masa.malilib.gui.widgets;

import com.mojang.blaze3d.platform.GlStateManager;
import fi.dy.masa.malilib.gui.interfaces.IGuiIcon;
import fi.dy.masa.malilib.render.RenderUtils;
import net.minecraft.client.MinecraftClient;

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
        GlStateManager.color4f(1f, 1f, 1f, 1f);
        MinecraftClient.getInstance().getTextureManager().bindTexture(this.icon.getTexture());

        RenderUtils.drawTexturedRect(this.x, this.y, this.icon.getU(), this.icon.getV(), this.width, this.height, this.zLevel);
    }
}
