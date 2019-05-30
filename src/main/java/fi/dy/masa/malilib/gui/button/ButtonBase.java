package fi.dy.masa.malilib.gui.button;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.gui.widgets.WidgetBase;
import fi.dy.masa.malilib.render.RenderUtils;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;

public abstract class ButtonBase extends WidgetBase
{
    protected static final ResourceLocation BUTTON_TEXTURES = new ResourceLocation("minecraft", "textures/gui/widgets.png");

    protected final List<String> hoverStrings = new ArrayList<>();
    protected final ImmutableList<String> hoverHelp;
    protected String displayString;
    protected boolean enabled = true;
    protected boolean visible = true;
    protected boolean hovered;
    protected boolean hoverInfoRequiresShift;
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

        if (width < 0)
        {
            this.width = this.getStringWidth(text) + 10;
        }

        this.displayString = text;
        this.hoverHelp = ImmutableList.of(I18n.format("malilib.gui.button.hover.hold_shift_for_info"));
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

    public void setDisplayString(String text)
    {
        this.displayString = text;
    }

    public boolean isMouseOver()
    {
        return this.hovered;
    }

    @Override
    protected boolean onMouseClickedImpl(int mouseX, int mouseY, int mouseButton)
    {
        this.mc.getSoundHandler().play(SimpleSound.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));

        if (this.actionListener != null)
        {
            this.actionListener.actionPerformedWithButton(this, mouseButton);
        }

        return true;
    }

    @Override
    public boolean onMouseScrolledImpl(int mouseX, int mouseY, double mouseWheelDelta)
    {
        int mouseButton = mouseWheelDelta < 0 ? 1 : 0;
        return this.onMouseClickedImpl(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean isMouseOver(int mouseX, int mouseY)
    {
        return this.enabled && this.visible && super.isMouseOver(mouseX, mouseY);
    }

    public void updateDisplayString()
    {
    }

    public boolean hasHoverText()
    {
        return this.hoverStrings.isEmpty() == false;
    }

    public void setHoverInfoRequiresShift(boolean requireShift)
    {
        this.hoverInfoRequiresShift = requireShift;
    }

    public void setHoverStrings(String... hoverStrings)
    {
        this.setHoverStrings(Arrays.asList(hoverStrings));
    }

    public void setHoverStrings(List<String> hoverStrings)
    {
        this.hoverStrings.clear();

        for (String str : hoverStrings)
        {
            str = I18n.format(str);

            String[] parts = str.split("\\\\n");

            for (String part : parts)
            {
                this.hoverStrings.add(I18n.format(part));
            }
        }
    }

    public List<String> getHoverStrings()
    {
        if (this.hoverInfoRequiresShift && GuiScreen.isShiftKeyDown() == false)
        {
            return this.hoverHelp;
        }

        return this.hoverStrings;
    }

    public void clearHoverStrings()
    {
        this.hoverStrings.clear();
    }

    protected int getTextureOffset(boolean isMouseOver)
    {
        return (this.enabled == false) ? 0 : (isMouseOver ? 2 : 1);
    }

    @Override
    public void postRenderHovered(int mouseX, int mouseY, boolean selected)
    {
        if (this.hasHoverText() && this.isMouseOver())
        {
            RenderUtils.drawHoverText(mouseX, mouseY, this.getHoverStrings());
            RenderHelper.disableStandardItemLighting();
        }
    }
}
