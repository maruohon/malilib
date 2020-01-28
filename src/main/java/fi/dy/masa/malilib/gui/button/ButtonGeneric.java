package fi.dy.masa.malilib.gui.button;

import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import fi.dy.masa.malilib.gui.interfaces.IGuiIcon;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.HorizontalAlignment;

public class ButtonGeneric extends ButtonBase
{
    @Nullable
    protected final IGuiIcon icon;
    protected HorizontalAlignment alignment = HorizontalAlignment.LEFT;
    protected boolean textCentered;
    protected boolean customIconOffset;
    protected boolean renderDefaultBackground = true;
    protected boolean renderOutline;
    protected int iconOffsetX;
    protected int iconOffsetY;
    protected int outlineColorHover = 0xFFFFFFFF;
    protected int outlineColorNormal = 0xFF000000;

    public ButtonGeneric(int x, int y, int width, boolean rightAlign, String translationKey, Object... args)
    {
        this(x, y, width, 20, fi.dy.masa.malilib.util.StringUtils.translate(translationKey, args));

        this.setRightAlign(rightAlign, x);
    }

    public ButtonGeneric(int x, int y, int width, int height, String text, String... hoverStrings)
    {
        this(x, y, width, height, text, null, hoverStrings);

        this.textCentered = true;
    }

    public ButtonGeneric(int x, int y, int width, int height, String text, IGuiIcon icon, String... hoverStrings)
    {
        super(x, y, width, height, text);

        this.icon = icon;

        if (this.automaticWidth && icon != null)
        {
            this.setWidth(this.width + icon.getWidth() + 8);
        }

        if (hoverStrings.length > 0)
        {
            this.addHoverStrings(hoverStrings);
        }
    }

    public ButtonGeneric(int x, int y, IGuiIcon icon, String... hoverStrings)
    {
        this(x, y, icon.getWidth(), icon.getHeight(), "", icon, hoverStrings);

        this.setRenderDefaultBackground(false);
    }

    @Override
    public ButtonGeneric setActionListener(@Nullable IButtonActionListener actionListener)
    {
        this.actionListener = actionListener;
        return this;
    }

    public ButtonGeneric setTextCentered(boolean centered)
    {
        this.textCentered = centered;
        return this;
    }

    public ButtonGeneric setIconOffset(int offX, int offY)
    {
        this.iconOffsetX = offX;
        this.iconOffsetY = offY;
        this.customIconOffset = true;
        return this;
    }

    /**
     * Set the icon aligment.<br>
     * Note: Only LEFT and RIGHT alignments work properly.
     * @param alignment
     * @return
     */
    public ButtonGeneric setIconAlignment(HorizontalAlignment alignment)
    {
        this.alignment = alignment;
        return this;
    }

    public ButtonGeneric setRenderDefaultBackground(boolean render)
    {
        this.renderDefaultBackground = render;
        return this;
    }

    public ButtonGeneric setRenderOutline(boolean render)
    {
        this.renderOutline = render;
        return this;
    }

    public ButtonGeneric setOutlineColorNormal(int color)
    {
        this.outlineColorNormal = color;
        return this;
    }

    public ButtonGeneric setOutlineColorHover(int color)
    {
        this.outlineColorHover = color;
        return this;
    }

    @Override
    public void render(int mouseX, int mouseY, boolean selected)
    {
        if (this.visible)
        {
            int x = this.x;
            int y = this.y;
            int width = this.width;
            int height = this.height;

            this.hovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;

            int buttonStyle = this.getTextureOffset(this.hovered);
            boolean textBlank = StringUtils.isBlank(this.displayString);

            RenderUtils.color(1f, 1f, 1f, 1f);

            if (this.renderOutline)
            {
                int color = this.hovered ? this.outlineColorHover : this.outlineColorNormal;
                RenderUtils.drawOutline(x, y, width, height, color, this.zLevel);
            }

            RenderUtils.setupBlendSimple();

            if (this.renderDefaultBackground)
            {
                this.bindTexture(BUTTON_TEXTURES);
                int w1 = width / 2;
                // Account for odd widths
                int w2 = (width % 2) != 0 ? w1 + 1 : w1;
                RenderUtils.drawTexturedRect(x     , y,        0, 46 + buttonStyle * 20, w1, height);
                RenderUtils.drawTexturedRect(x + w1, y, 200 - w2, 46 + buttonStyle * 20, w2, height);
            }

            if (this.icon != null)
            {
                int offX;

                if (this.customIconOffset)
                {
                    offX = this.iconOffsetX;
                }
                // With icon-only buttons, center it horizontally
                else if (textBlank)
                {
                    offX = (width - this.icon.getWidth()) / 2;
                }
                else
                {
                    offX = this.renderDefaultBackground ? 4 : 0;
                }

                int offY = this.customIconOffset ? this.iconOffsetY : (height - this.icon.getHeight()) / 2;
                x = this.alignment == HorizontalAlignment.LEFT ? this.x + offX : this.x + width - this.icon.getWidth() - offX;
                y = this.y + offY;
                int u = this.icon.getU() + buttonStyle * this.icon.getWidth();

                this.bindTexture(this.icon.getTexture());
                RenderUtils.drawTexturedRect(x, y, u, this.icon.getV(), this.icon.getWidth(), this.icon.getHeight());
            }

            if (textBlank == false)
            {
                y = this.y + (height - 8) / 2;
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
                    this.drawCenteredStringWithShadow(x + width / 2, y, color, this.displayString);
                }
                else
                {
                    x = this.x + 6;

                    if (this.icon != null && this.alignment == HorizontalAlignment.LEFT)
                    {
                        x += this.icon.getWidth() + 2;
                    }

                    this.drawStringWithShadow(x, y, color, this.displayString);
                }
            }
        }
    }
}
