package malilib.gui.widget.button;

import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;

import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;

import malilib.gui.icon.DefaultIcons;
import malilib.gui.icon.Icon;
import malilib.gui.util.ScreenContext;
import malilib.gui.widget.IconWidget;
import malilib.gui.widget.InteractableWidget;
import malilib.listener.EventListener;
import malilib.render.text.StyledTextLine;
import malilib.render.text.StyledTextUtils;
import malilib.util.StringUtils;
import malilib.util.data.Int2BooleanFunction;
import malilib.util.data.LeftRight;

public class GenericButton extends InteractableWidget
{
    @Nullable protected ButtonActionListener actionListener;
    @Nullable protected Supplier<Icon> buttonIconSupplier;
    @Nullable protected Supplier<String> displayStringSupplier;
    @Nullable protected Icon buttonIcon;
    @Nullable protected String fullDisplayString;
    protected Icon backgroundIcon = DefaultIcons.BUTTON_BACKGROUND;
    protected LeftRight iconAlignment = LeftRight.LEFT;
    protected boolean canScrollToClick;
    protected boolean playClickSound = true;
    protected boolean renderButtonBackgroundTexture = true;
    protected boolean rightAligned;
    protected int disabledTextColor = 0xFF606060;
    protected int iconVsLabelPadding = 5;

    public GenericButton(int width, int height)
    {
        super(width, height);

        this.textSettings.setTextColor(0xFFE0E0E0);
        this.textSettings.setHoveredTextColor(0xFFFFFFA0);
        this.textSettings.setUseHoverTextColor(true);
        this.textOffset.setCenterHorizontally(true);
        this.textOffset.setXOffset(0);
        this.padding.setLeftRight(5);

        this.getHoverInfoFactory().setStringListProvider("full_label", this::getFullLabelHoverString, 99);
        this.getBorderRenderer().getNormalSettings().setColor(0x00000000);
    }

    /**
     * Sets an action listener that takes no arguments.
     */
    public GenericButton setActionListener(@Nullable EventListener actionListener)
    {
        this.actionListener = (mBtn, btn) -> { if (mBtn == 0) { actionListener.onEvent(); return true; } return false; };
        return this;
    }

    /**
     * Sets an action listener that only takes in the mouse button that was clicked.
     */
    public GenericButton setActionListener(@Nullable Int2BooleanFunction actionListener)
    {
        this.actionListener = (mBtn, btn) -> actionListener.apply(mBtn);
        return this;
    }

    /**
     * Sets an action listener that gets in as arguments both the mouse button that was clicked,
     * and a reference to this button widget.
     */
    public GenericButton setActionListener(@Nullable ButtonActionListener actionListener)
    {
        this.actionListener = actionListener;
        return this;
    }

    /**
     * Sets the supplier that provides the full (non-clamped) display string.
     * The displayStringSupplier takes precedence over a possible fixed display string,
     * and will in fact override the value.
     */
    public GenericButton setDisplayStringSupplier(@Nullable Supplier<String> displayStringSupplier)
    {
        this.displayStringSupplier = displayStringSupplier;
        this.updateButtonState();
        return this;
    }

    /**
     * Sets the supplier that provides the button icon.
     * The buttonIconSupplier takes precedence over a possible fixed icon,
     * and will in fact override the value.
     */
    public GenericButton setButtonIconSupplier(@Nullable Supplier<Icon> buttonIconSupplier)
    {
        this.buttonIconSupplier = buttonIconSupplier;
        this.updateButtonState();
        return this;
    }

    public GenericButton setCanScrollToClick(boolean canScroll)
    {
        this.canScrollToClick = canScroll;
        return this;
    }

    public GenericButton setPlayClickSound(boolean playSound)
    {
        this.playClickSound = playSound;
        return this;
    }

    public GenericButton setIsRightAligned(boolean rightAligned)
    {
        this.rightAligned = rightAligned;
        return this;
    }

    /**
     * Sets the padding between the icon and the display string
     */
    public GenericButton setIconVsLabelPadding(int padding)
    {
        this.iconVsLabelPadding = padding;
        return this;
    }

    public GenericButton setDisabledTextColor(int color)
    {
        this.disabledTextColor = color;
        return this;
    }

    public GenericButton setBackgroundIcon(Icon icon)
    {
        this.backgroundIcon = icon;
        return this;
    }

    public GenericButton setRenderButtonBackgroundTexture(boolean renderButtonBackgroundTexture)
    {
        this.renderButtonBackgroundTexture = renderButtonBackgroundTexture;
        return this;
    }

    public GenericButton setIconAlignment(LeftRight alignment)
    {
        this.iconAlignment = alignment;
        return this;
    }

    /**
     * Sets the fixed display string. Note that if a displayStringSupplier
     * has been set, then that will overwrite this value.
     */
    public GenericButton setDisplayString(String text)
    {
        this.fullDisplayString = text;
        this.updateButtonState();
        return this;
    }

    /**
     * Sets the button icon. Note that if a buttonIconSupplier
     * has been set, then that will overwrite this value.
     */
    public GenericButton setButtonIcon(@Nullable Icon buttonIcon)
    {
        this.buttonIcon = buttonIcon;
        this.updateButtonState();
        return this;
    }

    protected List<String> getFullLabelHoverString()
    {
        if (this.text != null && this.fullDisplayString != null)
        {
            if (this.automaticWidth == false &&
                this.text.renderWidth > this.getMaxDisplayStringWidth())
            {
                return ImmutableList.of(StringUtils.translate("malilib.hover.button.full_button_label",
                                                              this.fullDisplayString));
            }
        }

        return EMPTY_STRING_LIST;
    }

    @Override
    public void updateWidgetState()
    {
        this.updateButtonState();
    }

    /**
     * Updates the display string and any other possible state
     */
    public void updateButtonState()
    {
        this.updateButtonIcon();
        this.updateDisplayString();
        this.updateSize();
    }

    @Override
    protected boolean onMouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (this.isEnabled() && this.tryExecuteAction(mouseButton))
        {
            if (this.playClickSound)
            {
                this.mc.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            }

            super.onMouseClicked(mouseX, mouseY, mouseButton); // Call the possible click listener

            this.updateButtonState();
        }

        return true;
    }

    @Override
    protected boolean onMouseScrolled(int mouseX, int mouseY, double mouseWheelDelta)
    {
        if (this.canScrollToClick)
        {
            int mouseButton = mouseWheelDelta < 0 ? 1 : 0;
            return this.onMouseClicked(mouseX, mouseY, mouseButton);
        }

        return false;
    }

    @Override
    protected void onSizeChanged()
    {
        super.onSizeChanged();
        this.updateDisplayString();
    }

    @Override
    public void updateWidth()
    {
        if (this.automaticWidth)
        {
            int width = this.padding.getHorizontalTotal();

            if (this.text != null)
            {
                width += this.text.renderWidth;
            }

            if (this.buttonIcon != null)
            {
                int extraPadding = this.text != null ? this.iconVsLabelPadding : 0;
                width += this.buttonIcon.getWidth() + extraPadding;
            }

            if (this.rightAligned)
            {
                int oldWidth = this.getWidth();

                if (width != oldWidth)
                {
                    this.setPositionNoUpdate(this.getX() - (width - oldWidth), this.getY());
                }
            }

            this.setWidthNoUpdate(width);
        }
    }

    @Override
    public void updateHeight()
    {
        if (this.automaticHeight)
        {
            int height = 0;

            if (this.text != null)
            {
                height = this.getLineHeight();
            }

            if (this.buttonIcon != null)
            {
                height = Math.max(height, this.buttonIcon.getHeight());
            }

            height += this.padding.getVerticalTotal();

            this.setHeightNoUpdate(height);
        }
    }

    protected boolean tryExecuteAction(int mouseButton)
    {
        return this.actionListener == null || this.actionListener.actionPerformedWithButton(mouseButton, this);
    }

    protected int getMaxDisplayStringWidth()
    {
        Icon icon = this.buttonIcon;
        int totalWidth = this.automaticWidth ? 8192 : this.getWidth();
        int usedWidth = this.padding.getHorizontalTotal();

        if (icon != null)
        {
            usedWidth += icon.getWidth() + this.iconVsLabelPadding;
        }

        return totalWidth - usedWidth;
    }

    protected void updateButtonIcon()
    {
        if (this.buttonIconSupplier != null)
        {
            this.buttonIcon = this.buttonIconSupplier.get();
        }
    }

    protected void updateDisplayString()
    {
        if (this.displayStringSupplier != null)
        {
            this.fullDisplayString = this.displayStringSupplier.get();
        }

        if (this.fullDisplayString != null)
        {
            StyledTextLine text = StyledTextLine.of(this.fullDisplayString);

            if (this.automaticWidth == false)
            {
                int maxWidth = this.getMaxDisplayStringWidth();

                if (text.renderWidth > maxWidth)
                {
                    text = StyledTextUtils.clampStyledTextToMaxWidth(text, maxWidth, LeftRight.RIGHT, " ...");
                }
            }

            this.text = text;
        }
        else
        {
            this.text = null;
        }

        this.updateHoverStrings();
    }

    /*
    protected boolean shouldUpdateDisplayString(String oldFullString)
    {
        return Objects.equals(oldFullString, this.fullDisplayString) == false ||
               ((this.displayString == null) != (this.fullDisplayString == null));
    }
    */

    @Override
    protected int getTextColorForRender(boolean hovered)
    {
        return this.isEnabled() == false ? this.disabledTextColor : super.getTextColorForRender(hovered);
    }

    @Override
    protected int getTextPositionX(int x, int usableWidth, int textWidth)
    {
        int textX = super.getTextPositionX(x, usableWidth, this.text.renderWidth);

        Icon icon = this.buttonIcon;

        if (this.iconAlignment == LeftRight.LEFT && icon != null &&
            this.textOffset.getCenterHorizontally() == false)
        {
            // The input x is this.x + this.padding.getLeft()
            return x + this.iconOffset.getXOffset() + icon.getWidth() + this.textOffset.getXOffset();
        }

        return textX;
    }

    protected int getIconOffsetX(int buttonWidth, int iconWidth)
    {
        int iconXOffset = this.padding.getLeft() + this.iconOffset.getXOffset();

        if (iconXOffset > 0)
        {
            return iconXOffset;
        }
        // With icon-only buttons, center it horizontally
        else if (this.text == null || this.text.renderWidth == 0)
        {
            return (buttonWidth - iconWidth) / 2;
        }
        else
        {
            boolean bgEnabled = this.getBackgroundRenderer().getNormalSettings().isEnabled();
            return bgEnabled ? 4 : 0;
        }
    }

    protected void renderButtonBackgroundIcon(int x, int y, float z, int width, int height,
                                              boolean hovered, ScreenContext ctx)
    {
        this.backgroundIcon.renderFourSplicedAt(x, y, z, width, height,
                                                IconWidget.getVariantIndex(this.isEnabled(), hovered));
    }

    protected void renderIcon(int x, int y, float z, int width, int height, boolean hovered, ScreenContext ctx)
    {
        Icon icon = this.buttonIcon;

        if (icon != null)
        {
            boolean leftAligned = this.iconAlignment == LeftRight.LEFT;
            int iconWidth = icon.getWidth();
            int iconYOffset = this.iconOffset.getYOffset();
            int offX = this.getIconOffsetX(width, iconWidth);
            int offY = iconYOffset > 0 ? iconYOffset : (height - icon.getHeight()) / 2;
            int ix = leftAligned ? x + offX : x + width - iconWidth - offX;

            icon.renderAt(ix, y + offY, z + 0.125f, IconWidget.getVariantIndex(this.isEnabled(), hovered));
        }
    }

    @Override
    public void renderAt(int x, int y, float z, ScreenContext ctx)
    {
        int width = this.getWidth();
        int height = this.getHeight();
        boolean hovered = this.isHoveredForRender(ctx);

        if (this.renderButtonBackgroundTexture)
        {
            this.renderButtonBackgroundIcon(x, y, z, width, height, hovered, ctx);
        }

        this.renderIcon(x, y, z, width, height, hovered, ctx);

        super.renderAt(x, y, z, ctx);
    }

    public static GenericButton create(int width, int height, String translationKey)
    {
        GenericButton button = new GenericButton(width, height);
        button.fullDisplayString = StringUtils.translate(translationKey);
        button.updateButtonState();
        return button;
    }

    public static GenericButton create(int height, String translationKey)
    {
        return create(-1, height, translationKey);
    }

    public static GenericButton create(int height, String translationKey, EventListener actionListener)
    {
        GenericButton button = create(height, translationKey);
        button.setActionListener(actionListener);
        return button;
    }

    public static GenericButton create(int height, String translationKey, ButtonActionListener actionListener)
    {
        GenericButton button = create(height, translationKey);
        button.setActionListener(actionListener);
        return button;
    }

    public static GenericButton create(String translationKey)
    {
        return create(20, translationKey);
    }

    public static GenericButton create(int height, Supplier<String> displayStringSupplier)
    {
        GenericButton button = new GenericButton(-1, height);
        button.setDisplayStringSupplier(displayStringSupplier);
        return button;
    }

    public static GenericButton create(int height, Supplier<String> displayStringSupplier, EventListener actionListener)
    {
        GenericButton button = create(height, displayStringSupplier);
        button.setActionListener(actionListener);
        return button;
    }

    public static GenericButton create(int height, Supplier<String> displayStringSupplier, ButtonActionListener actionListener)
    {
        GenericButton button = create(height, displayStringSupplier);
        button.setActionListener(actionListener);
        return button;
    }

    public static GenericButton create(Supplier<String> displayStringSupplier)
    {
        return create(20, displayStringSupplier);
    }

    public static GenericButton create(String translationKey, EventListener actionListener)
    {
        GenericButton button = create(translationKey);
        button.setActionListener(actionListener);
        return button;
    }

    public static GenericButton create(int width, int height, Icon icon)
    {
        GenericButton button = new GenericButton(width, height);
        button.getPadding().setAll(0);
        button.setButtonIcon(icon);
        button.setRenderButtonBackgroundTexture(false);
        button.updateButtonState();
        return button;
    }

    public static GenericButton create(Icon icon)
    {
        return create(icon.getWidth(), icon.getHeight(), icon);
    }

    public static GenericButton create(int height, String translationKey, Icon icon)
    {
        GenericButton button = new GenericButton(-1, height);
        button.fullDisplayString = StringUtils.translate(translationKey);
        button.setButtonIcon(icon);
        button.padding.setLeftRight(4);
        button.getTextOffset().setCenterHorizontally(false);
        button.getTextOffset().setXOffset(button.padding.getLeft() + button.iconOffset.getXOffset());
        button.updateButtonState();
        return button;
    }

    public static GenericButton create(String translationKey, Icon icon)
    {
        return create(20, translationKey, icon);
    }

    public static GenericButton create(Icon icon, EventListener actionListener)
    {
        GenericButton button = create(icon);
        button.setActionListener(actionListener);
        return button;
    }

    public static GenericButton create(@Nullable Supplier<Icon> iconSupplier, EventListener actionListener)
    {
        GenericButton button = new GenericButton(-1, -1);
        button.getPadding().setAll(0);
        button.setRenderButtonBackgroundTexture(false);
        button.setActionListener(actionListener);
        button.setButtonIconSupplier(iconSupplier);
        return button;
    }
}
