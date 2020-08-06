package fi.dy.masa.malilib.gui.button;

import java.util.function.Supplier;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import fi.dy.masa.malilib.gui.interfaces.IGuiIcon;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.data.HorizontalAlignment;

public class ButtonGeneric extends ButtonBase
{
    @Nullable
    protected final Supplier<IGuiIcon> iconSupplier;
    protected HorizontalAlignment alignment = HorizontalAlignment.LEFT;
    protected boolean textCentered;
    protected boolean customIconOffset;
    protected boolean renderDefaultBackground = true;
    protected boolean renderOutline;
    protected boolean useTextShadow = true;
    protected int iconOffsetX;
    protected int iconOffsetY;
    protected int textOffsetX = 6;
    protected int textOffsetY;
    protected int outlineColorHover = 0xFFFFFFFF;
    protected int outlineColorNormal = 0x00000000;
    protected int textColorDisabled = 0xA0A0A0;
    protected int textColorNormal = 0xE0E0E0;
    protected int textColorHovered = 0xFFFFA0;

    public ButtonGeneric(int x, int y, int width, boolean rightAlign, String translationKey, Object... args)
    {
        this(x, y, width, 20, fi.dy.masa.malilib.util.StringUtils.translate(translationKey, args));

        this.setRightAlign(rightAlign, x, true);
    }

    public ButtonGeneric(int x, int y, int width, int height, String text, String... hoverStrings)
    {
        this(x, y, width, height, text, (Supplier<IGuiIcon>) null, hoverStrings);

        this.textCentered = true;
    }

    public ButtonGeneric(int x, int y, int width, int height, String text, IGuiIcon icon, String... hoverStrings)
    {
        this(x, y, width, height, text, () -> icon, hoverStrings);
    }

    public ButtonGeneric(int x, int y, int width, int height, String text, Supplier<IGuiIcon> iconSupplier, String... hoverStrings)
    {
        super(x, y, width, height, text);

        this.iconSupplier = iconSupplier;
        IGuiIcon icon = iconSupplier != null ? iconSupplier.get() : null;

        if (this.automaticWidth && icon != null)
        {
            this.setWidth(this.getWidth() + icon.getWidth() + 8);
        }

        if (hoverStrings.length > 0)
        {
            this.addHoverStrings(hoverStrings);
        }
    }

    public ButtonGeneric(int x, int y, IGuiIcon icon, String... hoverStrings)
    {
        this(x, y, () -> icon, hoverStrings);
    }

    public ButtonGeneric(int x, int y, Supplier<IGuiIcon> iconSupplier, String... hoverStrings)
    {
        this(x, y, iconSupplier.get().getWidth(), iconSupplier.get().getHeight(), "", iconSupplier, hoverStrings);

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

    public ButtonGeneric setUseTextShadow(boolean useShadow)
    {
        this.useTextShadow = useShadow;
        return this;
    }

    public ButtonGeneric setIconOffset(int offsetX, int offsetY)
    {
        this.iconOffsetX = offsetX;
        this.iconOffsetY = offsetY;
        this.customIconOffset = true;
        return this;
    }

    public ButtonGeneric setTextOffset(int offsetX, int offsetY)
    {
        this.textOffsetX = offsetX;
        this.textOffsetX = offsetY;
        return this;
    }

    public ButtonGeneric setTextColorDisabled(int color)
    {
        this.textColorDisabled = color;
        return this;
    }

    public ButtonGeneric setTextColorNormal(int color)
    {
        this.textColorNormal = color;
        return this;
    }

    public ButtonGeneric setTextColorHovered(int color)
    {
        this.textColorHovered = color;
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
    public void render(int mouseX, int mouseY, boolean isActiveGui, boolean hovered)
    {
        if (this.visible)
        {
            int x = this.getX();
            int y = this.getY();
            int z = this.getZLevel();
            int width = this.getWidth();
            int height = this.getHeight();
            boolean textBlank = StringUtils.isBlank(this.displayString);

            RenderUtils.color(1f, 1f, 1f, 1f);
            RenderUtils.setupBlendSimple();

            if (this.renderOutline)
            {
                int color = hovered ? this.outlineColorHover : this.outlineColorNormal;
                RenderUtils.drawOutline(x, y, width, height, 1, color, z);
            }

            if (this.renderDefaultBackground)
            {
                this.bindTexture(BUTTON_TEXTURES);
                int w1 = width / 2;
                // Account for odd widths
                int w2 = (width % 2) != 0 ? w1 + 1 : w1;
                int buttonStyle = this.getTextureOffset(hovered);

                RenderUtils.drawTexturedRect(x     , y,        0, 46 + buttonStyle * 20, w1, height, z);
                RenderUtils.drawTexturedRect(x + w1, y, 200 - w2, 46 + buttonStyle * 20, w2, height, z);
            }

            int iconClearing = 0;
            IGuiIcon icon = this.iconSupplier != null ? this.iconSupplier.get() : null;

            if (icon != null)
            {
                int offX;
                int iconWidth = icon.getWidth();
                iconClearing = iconWidth + 2;

                if (this.customIconOffset)
                {
                    offX = this.iconOffsetX;
                }
                // With icon-only buttons, center it horizontally
                else if (textBlank)
                {
                    offX = (width - iconWidth) / 2;
                }
                else
                {
                    offX = this.renderDefaultBackground ? 4 : 0;
                }

                int offY = this.customIconOffset ? this.iconOffsetY : (height - icon.getHeight()) / 2;
                int ix = this.alignment == HorizontalAlignment.LEFT ? x + offX : x + width - iconWidth - offX;
                int iy = y + offY;

                icon.renderAt(ix, iy, this.getZLevel(), this.enabled, hovered);
            }

            if (textBlank == false)
            {
                int tx = this.textCentered ? x + width / 2 : x + this.textOffsetX;
                int ty = y + (height - 8) / 2 + this.textOffsetY;

                if (this.alignment == HorizontalAlignment.LEFT)
                {
                    tx += iconClearing;
                }

                int color = this.enabled == false ? this.textColorDisabled : (hovered ? this.textColorHovered : this.textColorNormal);
                this.getTextRenderer(this.useTextShadow, textCentered).renderText(tx, ty, color, this.displayString);
            }
        }
    }

    public static ButtonGeneric createIconOnly(int x, int y, IGuiIcon icon)
    {
        return createIconOnly(x, y, icon.getWidth() + 2, icon.getHeight() + 2, () -> icon);
    }

    public static ButtonGeneric createIconOnly(int x, int y, Supplier<IGuiIcon> iconSupplier)
    {
        IGuiIcon icon = iconSupplier.get();
        return createIconOnly(x, y, icon.getWidth() + 2, icon.getHeight() + 2, iconSupplier);
    }

    public static ButtonGeneric createIconOnly(int x, int y, int width, int height, Supplier<IGuiIcon> iconSupplier)
    {
        ButtonGeneric button = new ButtonGeneric(x, y, iconSupplier);

        button.setRenderDefaultBackground(false);
        button.setPlayClickSound(false);
        button.setRenderOutline(true);
        button.setOutlineColorNormal(0x00000000);
        button.setWidth(width);
        button.setHeight(height);

        return button;
    }
}
