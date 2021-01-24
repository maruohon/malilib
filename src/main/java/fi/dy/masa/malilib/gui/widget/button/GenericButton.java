package fi.dy.masa.malilib.gui.widget.button;

import java.util.Arrays;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.gui.icon.MultiIcon;
import fi.dy.masa.malilib.gui.position.HorizontalAlignment;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.StringUtils;

public class GenericButton extends BaseButton
{
    @Nullable protected final Supplier<MultiIcon> iconSupplier;
    protected HorizontalAlignment alignment = HorizontalAlignment.LEFT;
    protected boolean textCentered;
    protected boolean customIconOffset;
    protected boolean renderBackground = true;
    protected boolean renderOutline;
    protected boolean useTextShadow = true;
    protected int iconOffsetX;
    protected int iconOffsetY;
    protected int textOffsetX;
    protected int textOffsetY;
    protected int outlineColorHover = 0xFFFFFFFF;
    protected int outlineColorNormal = 0x00000000;
    protected int textColorDisabled = 0xFF606060;
    protected int textColorNormal = 0xFFE0E0E0;
    protected int textColorHovered = 0xFFFFFFA0;

    public GenericButton(int x, int y, int width, boolean rightAlign, String translationKey, Object... args)
    {
        this(x, y, width, 20, StringUtils.translate(translationKey, args));

        this.setRightAlign(rightAlign, x, true);
    }

    public GenericButton(int x, int y, int width, int height, String text, String... hoverStrings)
    {
        this(x, y, width, height, text, (Supplier<MultiIcon>) null, hoverStrings);

        this.textCentered = true;
    }

    public GenericButton(int x, int y, int width, int height, String text, MultiIcon icon, String... hoverStrings)
    {
        this(x, y, width, height, text, () -> icon, hoverStrings);
    }

    public GenericButton(int x, int y, int width, int height, String text, @Nullable Supplier<MultiIcon> iconSupplier, String... hoverStrings)
    {
        super(x, y, width, height, text);

        this.iconSupplier = iconSupplier;
        MultiIcon icon = iconSupplier != null ? iconSupplier.get() : null;

        if (this.automaticWidth && icon != null)
        {
            this.setWidth(this.getWidth() + icon.getWidth() + 8);
        }

        if (hoverStrings.length > 0)
        {
            this.setHoverStringProvider("_default", () -> Arrays.asList(hoverStrings));
        }
    }

    public GenericButton(int x, int y, MultiIcon icon, String... hoverStrings)
    {
        this(x, y, () -> icon, hoverStrings);
    }

    public GenericButton(int x, int y, Supplier<MultiIcon> iconSupplier, String... hoverStrings)
    {
        this(x, y, iconSupplier.get().getWidth(), iconSupplier.get().getHeight(), "", iconSupplier, hoverStrings);

        this.setRenderBackground(false);
    }

    @Override
    public GenericButton setActionListener(@Nullable ButtonActionListener actionListener)
    {
        this.actionListener = actionListener;
        return this;
    }

    public GenericButton setTextCentered(boolean centered)
    {
        this.textCentered = centered;

        if (centered == false)
        {
            this.textOffsetX = 6;
        }

        return this;
    }

    public GenericButton setUseTextShadow(boolean useShadow)
    {
        this.useTextShadow = useShadow;
        return this;
    }

    public GenericButton setIconOffset(int offsetX, int offsetY)
    {
        this.iconOffsetX = offsetX;
        this.iconOffsetY = offsetY;
        this.customIconOffset = true;
        return this;
    }

    public GenericButton setTextOffset(int offsetX, int offsetY)
    {
        this.textOffsetX = offsetX;
        this.textOffsetY = offsetY;
        return this;
    }

    public GenericButton setTextColorDisabled(int color)
    {
        this.textColorDisabled = color;
        return this;
    }

    public GenericButton setTextColorNormal(int color)
    {
        this.textColorNormal = color;
        return this;
    }

    public GenericButton setTextColorHovered(int color)
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
    public GenericButton setIconAlignment(HorizontalAlignment alignment)
    {
        this.alignment = alignment;
        return this;
    }

    public GenericButton setRenderBackground(boolean render)
    {
        this.renderBackground = render;
        return this;
    }

    public GenericButton setRenderOutline(boolean render)
    {
        this.renderOutline = render;
        return this;
    }

    public GenericButton setOutlineColorNormal(int color)
    {
        this.outlineColorNormal = color;
        return this;
    }

    public GenericButton setOutlineColorHover(int color)
    {
        this.outlineColorHover = color;
        return this;
    }

    protected int getTextColorForRender(boolean hovered)
    {
        return this.enabled == false ? this.textColorDisabled : (hovered ? this.textColorHovered : this.textColorNormal);
    }

    protected int getTextStartX(int x, int width)
    {
        return this.textCentered ? x + width / 2 + this.textOffsetX: x + this.textOffsetX;
    }

    protected int getTextureOffset(boolean isMouseOver)
    {
        return (this.enabled == false) ? 0 : (isMouseOver ? 2 : 1);
    }

    @Override
    protected int getMaxDisplayStringWidth()
    {
        MultiIcon icon = this.iconSupplier != null ? this.iconSupplier.get() : null;

        if (icon != null)
        {
            return this.getWidth() - icon.getWidth() - this.horizontalLabelPadding * 3;
        }

        return super.getMaxDisplayStringWidth();
    }

    protected void renderButtonBackground(int x, int y, float z, int width, int height, boolean hovered)
    {
        this.bindTexture(BUTTON_TEXTURES);

        int w1 = width / 2;
        // Account for odd widths
        int w2 = (width % 2) != 0 ? w1 + 1 : w1;
        int buttonStyle = this.getTextureOffset(hovered);

        RenderUtils.renderTexturedRectangle(x     , y, 0, 46 + buttonStyle * 20, w1, height, z);
        RenderUtils.renderTexturedRectangle(x + w1, y, 200 - w2, 46 + buttonStyle * 20, w2, height, z);
    }

    @Override
    public void renderAt(int x, int y, float z, int mouseX, int mouseY, boolean isActiveGui, boolean hovered)
    {
        if (this.visible)
        {
            super.renderAt(x, y, z, mouseX, mouseY, isActiveGui, hovered);

            int width = this.getWidth();
            int height = this.getHeight();
            boolean textBlank = org.apache.commons.lang3.StringUtils.isBlank(this.displayString);

            RenderUtils.color(1f, 1f, 1f, 1f);
            RenderUtils.setupBlendSimple();

            if (this.renderOutline)
            {
                int color = hovered && this.enabled ? this.outlineColorHover : this.outlineColorNormal;
                RenderUtils.renderOutline(x, y, width, height, 1, color, z);
            }

            if (this.renderBackground)
            {
                this.renderButtonBackground(x, y, z, width, height, hovered);
            }

            int iconClearing = 0;
            MultiIcon icon = this.iconSupplier != null ? this.iconSupplier.get() : null;

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
                    offX = this.renderBackground ? 4 : 0;
                }

                int offY = this.customIconOffset ? this.iconOffsetY : (height - icon.getHeight()) / 2;
                int ix = this.alignment == HorizontalAlignment.LEFT ? x + offX : x + width - iconWidth - offX;
                int iy = y + offY;

                icon.renderAt(ix, iy, z + 0.1f, this.enabled, hovered);
            }

            if (textBlank == false)
            {
                int tx = this.getTextStartX(x, width);
                int ty = y + (height - 8) / 2 + this.textOffsetY;

                if (this.alignment == HorizontalAlignment.LEFT)
                {
                    tx += iconClearing;
                }

                int color = this.getTextColorForRender(hovered);
                this.getTextRenderer(this.useTextShadow, this.textCentered).renderText(tx, ty, z, color, this.displayString);
            }
        }
    }

    public static GenericButton createIconOnly(int x, int y, MultiIcon icon)
    {
        return createIconOnly(x, y, icon.getWidth() + 2, icon.getHeight() + 2, () -> icon);
    }

    public static GenericButton createIconOnly(int x, int y, Supplier<MultiIcon> iconSupplier)
    {
        MultiIcon icon = iconSupplier.get();
        return createIconOnly(x, y, icon.getWidth() + 2, icon.getHeight() + 2, iconSupplier);
    }

    public static GenericButton createIconOnly(int x, int y, int width, int height, Supplier<MultiIcon> iconSupplier)
    {
        GenericButton button = new GenericButton(x, y, iconSupplier);

        button.setRenderBackground(false);
        button.setPlayClickSound(false);
        button.setRenderOutline(true);
        button.setOutlineColorNormal(0x00000000);
        button.setWidth(width);
        button.setHeight(height);

        return button;
    }
}
