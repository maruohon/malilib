package fi.dy.masa.malilib.gui.button;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

public abstract class ButtonBase extends GuiButton
{
    protected final List<String> hoverStrings = new ArrayList<>();
    protected final ImmutableList<String> hoverHelp;
    protected boolean hoverInfoRequiresShift;

    public ButtonBase(int x, int y, int width, int height)
    {
        this(x, y, width, height, "");
    }

    public ButtonBase(int x, int y, int width, int height, String text)
    {
        super(0, x, y, width, height, text);

        if (width == -1)
        {
            this.width = Minecraft.getMinecraft().fontRenderer.getStringWidth(text) + 10;
        }

        this.hoverHelp = ImmutableList.of(I18n.format("malilib.gui.button.hover.hold_shift_for_info"));
    }

    public int getButtonHeight()
    {
        return this.height;
    }

    public void onMouseButtonClicked(int mouseButton)
    {
        this.playPressSound(Minecraft.getMinecraft().getSoundHandler());
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
}
