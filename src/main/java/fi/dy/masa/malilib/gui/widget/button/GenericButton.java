package fi.dy.masa.malilib.gui.widget.button;

import java.util.ArrayList;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.gui.icon.DefaultIcons;
import fi.dy.masa.malilib.gui.icon.MultiIcon;
import fi.dy.masa.malilib.gui.position.HorizontalAlignment;
import fi.dy.masa.malilib.gui.widget.ScreenContext;
import fi.dy.masa.malilib.listener.EventListener;
import fi.dy.masa.malilib.util.StringUtils;

public class GenericButton extends BaseButton
{
    @Nullable protected final Supplier<MultiIcon> iconSupplier;
    protected MultiIcon backgroundIcon = DefaultIcons.BUTTON_BACKGROUND;
    protected HorizontalAlignment iconAlignment = HorizontalAlignment.LEFT;
    protected boolean customIconOffset;
    protected boolean renderButtonBackgroundTexture;
    protected boolean textCentered;
    protected boolean textShadow = true;
    protected int iconOffsetX;
    protected int iconOffsetY;
    protected int textOffsetX;
    protected int textOffsetY;
    protected int textColorDisabled = 0xFF606060;
    protected int textColorNormal = 0xFFE0E0E0;
    protected int textColorHovered = 0xFFFFFFA0;

    public GenericButton(int x, int y, int width, boolean rightAlign, String translationKey, Object... args)
    {
        this(x, y, width, 20, StringUtils.translate(translationKey, args));

        this.setRightAlign(rightAlign, x, true);
    }

    public GenericButton(int x, int y, int width, int height, String translationKey, String... hoverStrings)
    {
        this(x, y, width, height, translationKey, (Supplier<MultiIcon>) null, hoverStrings);

        this.textCentered = true;
    }

    public GenericButton(int x, int y, int width, int height, String translationKey, MultiIcon icon, String... hoverStrings)
    {
        this(x, y, width, height, translationKey, () -> icon, hoverStrings);
    }

    public GenericButton(int x, int y, int width, int height, String translationKey, @Nullable Supplier<MultiIcon> iconSupplier, String... hoverStrings)
    {
        super(x, y, width, height, translationKey);

        this.iconSupplier = iconSupplier;
        MultiIcon icon = iconSupplier != null ? iconSupplier.get() : null;

        if (this.automaticWidth && icon != null)
        {
            this.setWidth(this.getWidth() + icon.getWidth() + 8);
        }

        if (hoverStrings.length > 0)
        {
            ArrayList<String> hoverStringList = new ArrayList<>();
            for (String key : hoverStrings) { hoverStringList.add(StringUtils.translate(key)); }
            this.setHoverStringProvider("_default", () -> hoverStringList);
        }

        this.renderButtonBackgroundTexture = true;
        this.setNormalBorderColor(0x00000000);
        this.setHoveredBorderColor(0xFFFFFFFF);
    }

    public GenericButton(int x, int y, MultiIcon icon, String... hoverStrings)
    {
        this(x, y, () -> icon, hoverStrings);
    }

    public GenericButton(int x, int y, Supplier<MultiIcon> iconSupplier, String... hoverStrings)
    {
        this(x, y, iconSupplier.get().getWidth(), iconSupplier.get().getHeight(), "", iconSupplier, hoverStrings);

        this.setRenderButtonBackgroundTexture(false);
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
        this.textShadow = useShadow;
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

    public GenericButton setBackgroundIcon(MultiIcon icon)
    {
        this.backgroundIcon = icon;
        return this;
    }

    public GenericButton setRenderButtonBackgroundTexture(boolean renderButtonBackgroundTexture)
    {
        this.renderButtonBackgroundTexture = renderButtonBackgroundTexture;
        return this;
    }

    /**
     * Set the icon alignment.<br>
     * Note: Only LEFT and RIGHT alignments work properly.
     * @param alignment
     * @return
     */
    public GenericButton setIconAlignment(HorizontalAlignment alignment)
    {
        this.iconAlignment = alignment;
        return this;
    }

    protected int getTextColorForRender(boolean hovered)
    {
        return this.enabled == false ? this.textColorDisabled : (hovered ? this.textColorHovered : this.textColorNormal);
    }

    protected int getTextStartX(int baseX, int usableWidth, int textWidth)
    {
        if (this.textCentered)
        {
            return baseX + usableWidth / 2 - textWidth / 2 + this.textOffsetX;
        }

        return baseX + this.textOffsetX;
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

    protected void renderButtonBackground(int x, int y, float z, int width, int height,
                                          boolean hovered, ScreenContext ctx)
    {
        this.backgroundIcon.renderFourSplicedAt(x, y, z, width, height, this.enabled, hovered);
    }

    @Override
    public void renderAt(int x, int y, float z, ScreenContext ctx)
    {
        if (this.visible)
        {
            super.renderAt(x, y, z, ctx);

            int width = this.getWidth();
            int height = this.getHeight();
            boolean textBlank = this.styledDisplayString == null || this.styledDisplayString.renderWidth == 0;
            boolean hovered = this.isHoveredForRender(ctx);

            if (this.renderButtonBackgroundTexture)
            {
                this.renderButtonBackground(x, y, z, width, height, hovered, ctx);
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
                int ix = this.iconAlignment == HorizontalAlignment.LEFT ? x + offX : x + width - iconWidth - offX;
                int iy = y + offY;

                icon.renderAt(ix, iy, z + 0.1f, this.enabled, hovered);
            }

            if (textBlank == false)
            {
                int tx = this.getTextStartX(x, width, this.styledDisplayString.renderWidth);
                int ty = y + height / 2 - this.getFontHeight() / 2 + this.textOffsetY;

                if (this.iconAlignment == HorizontalAlignment.LEFT)
                {
                    tx += iconClearing;
                }

                int color = this.getTextColorForRender(hovered);
                this.renderTextLine(tx, ty, z, color, this.textShadow, ctx, this.styledDisplayString);
            }
        }
    }

    public static GenericButton simple(int width, int height, String translationKey, EventListener actionListener)
    {
        GenericButton button = new GenericButton(0, 0, width, height, translationKey);
        button.setActionListener(actionListener);
        return button;
    }

    public static GenericButton simple(int width, int height, String translationKey,
                                       EventListener actionListener, String hoverTextTranslationKey)
    {
        GenericButton button = new GenericButton(0, 0, width, height, translationKey, hoverTextTranslationKey);
        button.setActionListener(actionListener);
        return button;
    }

    public static GenericButton createIconOnly(int x, int y, MultiIcon icon)
    {
        return createIconOnly(x, y, icon.getWidth(), icon.getHeight(), () -> icon);
    }

    public static GenericButton createIconOnly(int x, int y, MultiIcon icon, int outlineColorNormal, int outlineColorHover)
    {
        return createIconOnly(x, y, icon.getWidth() + 2, icon.getHeight() + 2, () -> icon, outlineColorNormal, outlineColorHover);
    }

    public static GenericButton createIconOnly(int x, int y, Supplier<MultiIcon> iconSupplier)
    {
        MultiIcon icon = iconSupplier.get();
        return createIconOnly(x, y, icon.getWidth(), icon.getHeight(), iconSupplier);
    }

    public static GenericButton createIconOnly(int x, int y, int width, int height,
                                               Supplier<MultiIcon> iconSupplier)
    {
        GenericButton button =  new GenericButton(x, y, width, height, "", iconSupplier);
        button.setRenderButtonBackgroundTexture(false);
        return button;
    }

    public static GenericButton createIconOnly(int x, int y, int width, int height,
                                               Supplier<MultiIcon> iconSupplier,
                                               int outlineColorNormal, int outlineColorHover)
    {
        GenericButton button = createIconOnly(x, y, width, height, iconSupplier);

        button.setRenderNormalBorder(true);
        button.setNormalBorderColor(outlineColorNormal);
        button.setHoveredBorderColor(outlineColorHover);

        return button;
    }
}
