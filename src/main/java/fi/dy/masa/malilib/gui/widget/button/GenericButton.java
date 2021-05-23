package fi.dy.masa.malilib.gui.widget.button;

import java.util.ArrayList;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.gui.icon.DefaultIcons;
import fi.dy.masa.malilib.gui.icon.MultiIcon;
import fi.dy.masa.malilib.gui.position.HorizontalAlignment;
import fi.dy.masa.malilib.gui.util.ScreenContext;
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
    protected int iconOffsetX;
    protected int iconOffsetY;
    protected int textColorDisabled = 0xFF606060;
    protected int textColorNormal = 0xFFE0E0E0;
    protected int textColorHovered = 0xFFFFFFA0;

    public GenericButton(@Nullable String translationKey)
    {
        this(-1, 20, translationKey);
    }

    public GenericButton(int height, @Nullable String translationKey)
    {
        this(-1, height, translationKey);
    }

    public GenericButton(int width, boolean rightAlign, @Nullable String translationKey, Object... args)
    {
        this(width, 20, StringUtils.translate(translationKey, args));

        this.setRightAlign(rightAlign, 0, true);
    }

    public GenericButton(int width, int height, @Nullable String translationKey, String... hoverStrings)
    {
        this(width, height, translationKey, (Supplier<MultiIcon>) null, hoverStrings);

        this.textCentered = true;
    }

    public GenericButton(int width, int height, @Nullable String translationKey,
                         MultiIcon icon, String... hoverStrings)
    {
        this(width, height, translationKey, () -> icon, hoverStrings);
    }

    public GenericButton(int width, int height, @Nullable String translationKey,
                         @Nullable Supplier<MultiIcon> iconSupplier, String... hoverStrings)
    {
        super(width, height, translationKey);

        this.textOffsetX = 0;
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

    public GenericButton(MultiIcon icon, String... hoverStrings)
    {
        this(() -> icon, hoverStrings);
    }

    public GenericButton(Supplier<MultiIcon> iconSupplier, String... hoverStrings)
    {
        this(iconSupplier.get().getWidth(), iconSupplier.get().getHeight(), "", iconSupplier, hoverStrings);

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

    protected void renderButtonBackground(int x, int y, float z, int width, int height,
                                          boolean hovered, ScreenContext ctx)
    {
        this.backgroundIcon.renderFourSplicedAt(x, y, z, width, height, this.enabled, hovered);
    }

    protected int getIconOffsetX(int width, MultiIcon icon)
    {
        if (this.customIconOffset)
        {
            return this.iconOffsetX;
        }
        // With icon-only buttons, center it horizontally
        else if (this.text == null || this.text.renderWidth == 0)
        {
            return (width - icon.getWidth()) / 2;
        }
        else
        {
            return this.renderNormalBackground ? 4 : 0;
        }
    }

    @Override
    protected int getTextPositionX(int x, int elementWidth)
    {
        MultiIcon icon = this.iconSupplier != null ? this.iconSupplier.get() : null;
        x = this.getTextStartX(x, this.getWidth(), this.text.renderWidth);

        if (this.iconAlignment == HorizontalAlignment.LEFT && icon != null)
        {
            x += icon.getWidth() + 2;
        }

        return x;
    }

    protected void renderIcon(int x, int y, float z, int width, int height, boolean hovered, ScreenContext ctx)
    {
        MultiIcon icon = this.iconSupplier != null ? this.iconSupplier.get() : null;

        if (icon != null)
        {
            boolean leftAligned = this.iconAlignment == HorizontalAlignment.LEFT;
            int offX = this.getIconOffsetX(width, icon);
            int offY = this.customIconOffset ? this.iconOffsetY : (height - icon.getHeight()) / 2;
            int ix = leftAligned ? x + offX : x + width - icon.getWidth() - offX;

            icon.renderAt(ix, y + offY, z + 0.1f, this.enabled, hovered);
        }
    }

    @Override
    public void renderAt(int x, int y, float z, ScreenContext ctx)
    {
        if (this.visible)
        {
            int width = this.getWidth();
            int height = this.getHeight();
            boolean hovered = this.isHoveredForRender(ctx);
            this.defaultNormalTextColor = this.getTextColorForRender(hovered);

            if (this.renderButtonBackgroundTexture)
            {
                this.renderButtonBackground(x, y, z, width, height, hovered, ctx);
            }

            this.renderIcon(x, y, z, width, height, hovered, ctx);

            super.renderAt(x, y, z, ctx);
        }
    }

    public static GenericButton simple(String translationKey, EventListener actionListener)
    {
        GenericButton button = new GenericButton(translationKey);
        button.setActionListener(actionListener);
        return button;
    }

    public static GenericButton simple(String translationKey, ButtonActionListener actionListener)
    {
        GenericButton button = new GenericButton(translationKey);
        button.setActionListener(actionListener);
        return button;
    }

    public static GenericButton simple(int height, String translationKey, EventListener actionListener)
    {
        GenericButton button = new GenericButton(height, translationKey);
        button.setActionListener(actionListener);
        return button;
    }

    public static GenericButton simple(int height, String translationKey, ButtonActionListener actionListener)
    {
        GenericButton button = new GenericButton(height, translationKey);
        button.setActionListener(actionListener);
        return button;
    }

    public static GenericButton simple(int height, String translationKey,
                                       EventListener actionListener, String hoverTextTranslationKey)
    {
        GenericButton button = new GenericButton(-1, height, translationKey, hoverTextTranslationKey);
        button.setActionListener(actionListener);
        return button;
    }

    public static GenericButton createIconOnly(MultiIcon icon)
    {
        return createIconOnly(icon.getWidth(), icon.getHeight(), () -> icon);
    }

    public static GenericButton createIconOnly(MultiIcon icon, EventListener actionListener)
    {
        GenericButton button = createIconOnly(icon);
        button.setActionListener(actionListener);
        return button;
    }

    public static GenericButton createIconOnly(Supplier<MultiIcon> iconSupplier)
    {
        MultiIcon icon = iconSupplier.get();
        return createIconOnly(icon.getWidth(), icon.getHeight(), iconSupplier);
    }

    public static GenericButton createIconOnly(int width, int height, Supplier<MultiIcon> iconSupplier)
    {
        GenericButton button =  new GenericButton(width, height, "", iconSupplier);
        button.setRenderButtonBackgroundTexture(false);
        return button;
    }
}
