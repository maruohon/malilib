package fi.dy.masa.malilib.gui.widget.button;

import java.util.function.BooleanSupplier;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.widget.BackgroundWidget;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.malilib.util.data.LeftRight;

public abstract class BaseButton extends BackgroundWidget
{
    protected static final ResourceLocation BUTTON_TEXTURES = new ResourceLocation("minecraft", "textures/gui/widgets.png");

    protected final ImmutableList<String> hoverHelp;
    protected String displayString;
    protected boolean canScrollToClick;
    protected boolean enabled = true;
    protected boolean hoverInfoRequiresShift;
    protected boolean playClickSound = true;
    protected boolean visible = true;
    @Nullable protected ButtonActionListener actionListener;
    @Nullable protected BooleanSupplier enabledStatusSupplier;

    public BaseButton(int x, int y, int width, int height)
    {
        this(x, y, width, height, "");
    }

    public BaseButton(int x, int y, int width, int height, String text)
    {
        this(x, y, width, height, text, null);
    }

    public BaseButton(int x, int y, int width, int height, String text, @Nullable ButtonActionListener actionListener)
    {
        super(x, y, width, height);

        this.displayString = StringUtils.translate(text);
        this.actionListener = actionListener;
        this.hoverHelp = ImmutableList.of(StringUtils.translate("malilib.gui.button.hover.hold_shift_for_info"));

        this.updateWidth();
    }

    public BaseButton setActionListener(@Nullable ButtonActionListener actionListener)
    {
        this.actionListener = actionListener;
        return this;
    }

    /**
     * Sets a supplier that provides the enabled status for the button.
     * The status can be refreshed by calling updateButtonState()
     * @param enabledStatusSupplier
     * @return
     */
    public BaseButton setEnabledStatusSupplier(@Nullable BooleanSupplier enabledStatusSupplier)
    {
        this.enabledStatusSupplier = enabledStatusSupplier;
        return this;
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    public BaseButton setCanScrollToClick(boolean canScroll)
    {
        this.canScrollToClick = canScroll;
        return this;
    }

    public BaseButton setPlayClickSound(boolean playSound)
    {
        this.playClickSound = playSound;
        return this;
    }

    public BaseButton setDisplayString(String text)
    {
        this.displayString = text;
        return this;
    }

    @Override
    protected void onPositionOrSizeChanged(int oldX, int oldY)
    {
        super.onPositionOrSizeChanged(oldX, oldY);

        // This check is required to prevent infinite recursion
        if (this.automaticWidth == false)
        {
            this.updateDisplayString();
        }
    }

    @Override
    protected void onSizeChanged()
    {
        super.onSizeChanged();

        // This check is required to prevent infinite recursion
        if (this.automaticWidth == false)
        {
            this.updateDisplayString();
        }
    }

    @Override
    public void updateWidth()
    {
        if (this.automaticWidth)
        {
            this.setWidth(this.getStringWidth(this.displayString) + 10);
        }
    }

    @Override
    protected boolean onMouseClickedImpl(int mouseX, int mouseY, int mouseButton)
    {
        if (this.enabled && this.visible)
        {
            if (this.playClickSound)
            {
                this.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            }

            if (this.actionListener != null)
            {
                this.actionListener.actionPerformedWithButton(this, mouseButton);
            }
        }

        return true;
    }

    @Override
    public boolean onMouseScrolledImpl(int mouseX, int mouseY, double mouseWheelDelta)
    {
        if (this.canScrollToClick)
        {
            int mouseButton = mouseWheelDelta < 0 ? 1 : 0;
            return this.onMouseClickedImpl(mouseX, mouseY, mouseButton);
        }

        return false;
    }

    /**
     * Updates the display string and the enabled status,
     * if the enabled status supplier has been set.
     */
    public void updateButtonState()
    {
        this.updateDisplayString();

        if (this.enabledStatusSupplier != null)
        {
            this.setEnabled(this.enabledStatusSupplier.getAsBoolean());
        }
    }

    protected int getMaxDisplayStringWidth()
    {
        return this.getWidth() - 10;
    }

    public void updateDisplayString()
    {
        String str = this.generateDisplayString();
        int maxWidth = this.getMaxDisplayStringWidth();

        if (this.automaticWidth == false &&
            org.apache.commons.lang3.StringUtils.isBlank(str) == false &&
            this.getStringWidth(str) > maxWidth)
        {
            this.automaticHoverStrings.clear();
            this.automaticHoverStrings.add(BaseScreen.TXT_AQUA + str);
            this.updateCombinedHoverStrings();
            str = StringUtils.clampTextToRenderLength(str, maxWidth, LeftRight.RIGHT, " ...");
        }

        this.displayString = str;
        this.updateWidth();
    }

    protected String generateDisplayString()
    {
        return this.displayString;
    }

    public void setHoverInfoRequiresShift(boolean requireShift)
    {
        this.hoverInfoRequiresShift = requireShift;
    }

    @Override
    public ImmutableList<String> getHoverStrings()
    {
        if (this.hoverInfoRequiresShift && BaseScreen.isShiftDown() == false)
        {
            return this.hoverHelp;
        }

        return super.getHoverStrings();
    }
}
