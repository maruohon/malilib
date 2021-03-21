package fi.dy.masa.malilib.gui.widget.button;

import java.util.List;
import java.util.function.BooleanSupplier;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.widget.BackgroundWidget;
import fi.dy.masa.malilib.listener.EventListener;
import fi.dy.masa.malilib.render.text.StyledText;
import fi.dy.masa.malilib.render.text.StyledTextLine;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.malilib.util.data.LeftRight;

public abstract class BaseButton extends BackgroundWidget
{
    protected final ImmutableList<StyledTextLine> hoverHelp;
    protected StyledTextLine styledDisplayString;
    protected String displayString;
    protected String fullDisplayString;
    protected boolean canScrollToClick;
    protected boolean enabled = true;
    protected boolean hoverInfoRequiresShift;
    protected boolean playClickSound = true;
    protected boolean visible = true;
    protected int horizontalLabelPadding = 5;
    @Nullable protected ButtonActionListener actionListener;
    @Nullable protected BooleanSupplier enabledStatusSupplier;

    public BaseButton(int x, int y, int width, int height)
    {
        this(x, y, width, height, "");
    }

    public BaseButton(int x, int y, int width, int height, String translationKey)
    {
        this(x, y, width, height, translationKey, null);
    }

    public BaseButton(int x, int y, int width, int height, String translationKey, @Nullable ButtonActionListener actionListener)
    {
        super(x, y, width, height);

        this.actionListener = actionListener;
        this.hoverHelp = StyledText.translatedOf("malilib.gui.button.hover.hold_shift_for_info").lines;
        this.setHoverStringProvider("full_label", this::getFullLabelHoverString, 99);

        this.setDisplayString(StringUtils.translate(translationKey));
        this.updateWidth();
    }

    public BaseButton setActionListener(EventListener actionListener)
    {
        this.actionListener = (btn, mbtn) -> {
            if (mbtn == 0)
            {
                actionListener.onEvent();
            }
        };
        return this;
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

    /**
     * Sets the horizontal padding for the display string (on one side)
     * @param padding
     * @return
     */
    public BaseButton setHorizontalLabelPadding(int padding)
    {
        this.horizontalLabelPadding = padding;
        return this;
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
        this.fullDisplayString = text;
        this.styledDisplayString = StyledTextLine.of(text);
        return this;
    }

    @Override
    protected void onPositionOrSizeChanged(int oldX, int oldY)
    {
        super.onPositionOrSizeChanged(oldX, oldY);

        // This check is required to prevent infinite recursion FIXME wtf
        if (this.automaticWidth == false)
        {
            this.updateDisplayString();
        }
    }

    @Override
    protected void onSizeChanged()
    {
        super.onSizeChanged();

        // This check is required to prevent infinite recursion FIXME wtf
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
            this.setWidth(this.getStringWidth(this.displayString) + this.horizontalLabelPadding * 2);
        }
    }

    @Override
    protected boolean onMouseClicked(int mouseX, int mouseY, int mouseButton)
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
                this.updateButtonState();
            }
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
        return this.getWidth() - this.horizontalLabelPadding * 2;
    }

    public void updateDisplayString()
    {
        String str = this.generateDisplayString();
        int maxWidth = this.getMaxDisplayStringWidth();
        this.fullDisplayString = str;

        if (this.automaticWidth == false &&
            org.apache.commons.lang3.StringUtils.isBlank(str) == false &&
            this.getStringWidth(str) > maxWidth)
        {
            str = StringUtils.clampTextToRenderLength(str, maxWidth, LeftRight.RIGHT, " ...");
        }

        this.displayString = str;
        this.styledDisplayString = StyledTextLine.of(str);

        this.updateHoverStrings();
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
    public ImmutableList<StyledTextLine> getHoverText()
    {
        if (this.hoverInfoRequiresShift && BaseScreen.isShiftDown() == false)
        {
            return this.hoverHelp;
        }

        return super.getHoverText();
    }

    protected List<String> getFullLabelHoverString()
    {
        String str = this.fullDisplayString != null ? this.fullDisplayString : this.displayString;
        int maxWidth = this.getMaxDisplayStringWidth();

        if (this.automaticWidth == false &&
            org.apache.commons.lang3.StringUtils.isBlank(str) == false &&
            this.getStringWidth(str) > maxWidth)
        {
            return ImmutableList.of(BaseScreen.TXT_WHITE + str);
        }

        return EMPTY_STRING_LIST;
    }
}
