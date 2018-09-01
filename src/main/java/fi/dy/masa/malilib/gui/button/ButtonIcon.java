package fi.dy.masa.malilib.gui.button;

import fi.dy.masa.malilib.gui.interfaces.IGuiIcon;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;

public class ButtonIcon extends ButtonBase
{
    protected final IGuiIcon icon;

    public ButtonIcon(int id, int x, int y, int width, int height, IGuiIcon icon)
    {
        super(id, x, y, width, height, "");

        this.icon = icon;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks)
    {
        if (this.visible)
        {
            this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;

            mc.getTextureManager().bindTexture(this.icon.getTexture());
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

            int buttonStyle = this.getHoverState(this.hovered);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

            int u = this.icon.getU() + buttonStyle * this.icon.getWidth();
            int v = this.icon.getV();
            this.drawTexturedModalRect(this.x, this.y, u, v, this.width, this.height);
            this.mouseDragged(mc, mouseX, mouseY);
        }
    }
}
