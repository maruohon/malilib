package fi.dy.masa.malilib.gui.button;

import java.util.List;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.widgets.WidgetBase;
import fi.dy.masa.malilib.util.StringUtils;

public abstract class ButtonBase extends WidgetBase
{
    protected static final ResourceLocation BUTTON_TEXTURES = new ResourceLocation("minecraft", "textures/gui/widgets.png");

    protected final ImmutableList<String> hoverHelp;
    protected String displayString;
    protected boolean enabled = true;
    protected boolean hovered;
    protected boolean hoverInfoRequiresShift;
    protected boolean playClickSound = true;
    protected boolean visible = true;
    @Nullable protected IButtonActionListener actionListener;

    public ButtonBase(int x, int y, int width, int height)
    {
        this(x, y, width, height, "");
    }

    public ButtonBase(int x, int y, int width, int height, String text)
    {
        this(x, y, width, height, text, null);
    }

    public ButtonBase(int x, int y, int width, int height, String text, @Nullable IButtonActionListener actionListener)
    {
        super(x, y, width, height);

        this.displayString = StringUtils.translate(text);
        this.hoverHelp = ImmutableList.of(StringUtils.translate("malilib.gui.button.hover.hold_shift_for_info"));

        this.updateWidth();
    }

    public ButtonBase setActionListener(@Nullable IButtonActionListener actionListener)
    {
        this.actionListener = actionListener;
        return this;
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    public ButtonBase setPlayClickSound(boolean playSound)
    {
        this.playClickSound = playSound;
        return this;
    }

    public ButtonBase setDisplayString(String text)
    {
        this.displayString = text;
        return this;
    }

    @Override
    public void updateWidth()
    {
        if (this.automaticWidth)
        {
            this.setWidth(this.getStringWidth(this.displayString) + 10);
        }
    }

    public boolean isMouseOver()
    {
        return this.hovered;
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
        int mouseButton = mouseWheelDelta < 0 ? 1 : 0;
        return this.onMouseClickedImpl(mouseX, mouseY, mouseButton);
    }

    public void updateDisplayString()
    {
        this.displayString = this.generateDisplayString();
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
    public List<String> getHoverStrings()
    {
        if (this.hoverInfoRequiresShift && GuiBase.isShiftDown() == false)
        {
            return this.hoverHelp;
        }

        return super.getHoverStrings();
    }

    protected int getTextureOffset(boolean isMouseOver)
    {
        return (this.enabled == false) ? 0 : (isMouseOver ? 2 : 1);
    }
}
