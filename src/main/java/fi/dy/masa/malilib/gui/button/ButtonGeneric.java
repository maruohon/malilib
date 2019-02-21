package fi.dy.masa.malilib.gui.button;

import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import fi.dy.masa.malilib.gui.LeftRight;
import fi.dy.masa.malilib.gui.interfaces.IGuiIcon;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;

public class ButtonGeneric extends ButtonBase
{
    @Nullable
    protected final IGuiIcon icon;
    protected LeftRight alignment = LeftRight.LEFT;
    protected boolean textCentered;
    protected boolean renderDefaultBackground = true;

    public ButtonGeneric(int x, int y, int width, int height, String text, String... hoverStrings)
    {
        this(x, y, width, height, text, null, hoverStrings);

        this.textCentered = true;
    }

    public ButtonGeneric(int x, int y, int width, int height, String text, IGuiIcon icon, String... hoverStrings)
    {
        super(x, y, width, height, text);

        this.icon = icon;

        if (width == -1 && icon != null)
        {
            this.width += icon.getWidth() + 8;
        }

        if (hoverStrings.length > 0)
        {
            this.setHoverStrings(hoverStrings);
        }
    }

    public ButtonGeneric(int x, int y, IGuiIcon icon, String... hoverStrings)
    {
        this(x, y, icon.getWidth(), icon.getHeight(), "", icon, hoverStrings);

        this.setRenderDefaultBackground(false);
    }

    public ButtonGeneric setTextCentered(boolean centered)
    {
        this.textCentered = centered;
        return this;
    }

    /**
     * Set the icon aligment.<br>
     * Note: Only LEFT and RIGHT alignments work properly.
     * @param alignment
     * @return
     */
    public ButtonGeneric setIconAlignment(LeftRight alignment)
    {
        this.alignment = alignment;
        return this;
    }

    public ButtonGeneric setRenderDefaultBackground(boolean render)
    {
        this.renderDefaultBackground = render;
        return this;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks)
    {
        if (this.visible)
        {
            this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;

            FontRenderer fontRenderer = mc.fontRenderer;
            int buttonStyle = this.getHoverState(this.hovered);

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

            if (this.renderDefaultBackground)
            {
                mc.getTextureManager().bindTexture(BUTTON_TEXTURES);
                this.drawTexturedModalRect(this.x, this.y, 0, 46 + buttonStyle * 20, this.width / 2, this.height);
                this.drawTexturedModalRect(this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + buttonStyle * 20, this.width / 2, this.height);
            }

            this.mouseDragged(mc, mouseX, mouseY);

            if (this.icon != null)
            {
                int offset = this.renderDefaultBackground ? 4 : 0;
                int x = this.alignment == LeftRight.LEFT ? this.x + offset : this.x + this.width - this.icon.getWidth() - offset;
                int y = this.y + (this.height - this.icon.getHeight()) / 2;
                int u = this.icon.getU() + buttonStyle * this.icon.getWidth();

                mc.getTextureManager().bindTexture(this.icon.getTexture());
                this.drawTexturedModalRect(x, y, u, this.icon.getV(), this.icon.getWidth(), this.icon.getHeight());
            }

            if (StringUtils.isBlank(this.displayString) == false)
            {
                int y = this.y + (this.height - 8) / 2;
                int color = 0xE0E0E0;

                if (this.enabled == false)
                {
                    color = 0xA0A0A0;
                }
                else if (this.hovered)
                {
                    color = 0xFFFFA0;
                }

                if (this.textCentered)
                {
                    this.drawCenteredString(fontRenderer, this.displayString, this.x + this.width / 2, y, color);
                }
                else
                {
                    int x = this.x + 6;

                    if (this.icon != null && this.alignment == LeftRight.LEFT)
                    {
                        x += this.icon.getWidth() + 2;
                    }

                    this.drawString(fontRenderer, this.displayString, x, y, color);
                }
            }
        }
    }

    public static ButtonGeneric createGeneric(int x, int y, int width, boolean rightAlign, String translationKey, Object... args)
    {
        String label = I18n.format(translationKey, args);

        if (width < 0)
        {
            Minecraft mc = Minecraft.getMinecraft();
            width = mc.fontRenderer.getStringWidth(label) + 10;
        }

        if (rightAlign)
        {
            x -= width;
        }

        return new ButtonGeneric(x, y, width, 20, label);
    }
}
