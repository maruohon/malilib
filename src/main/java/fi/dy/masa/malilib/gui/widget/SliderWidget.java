package fi.dy.masa.malilib.gui.widget;

import net.minecraft.util.math.MathHelper;
import fi.dy.masa.malilib.gui.callback.SliderCallback;
import fi.dy.masa.malilib.gui.icon.DefaultIcons;
import fi.dy.masa.malilib.gui.util.GuiUtils;
import fi.dy.masa.malilib.gui.util.ScreenContext;
import fi.dy.masa.malilib.render.text.StyledTextLine;

public class SliderWidget extends InteractableWidget
{
    protected final SliderCallback callback;
    protected int sliderWidth;
    protected int lastMouseX;
    protected boolean dragging;
    protected boolean locked;

    public SliderWidget(int width, int height, SliderCallback callback)
    {
        super(width, height);

        this.callback = callback;
        int usableWidth = this.getWidth() - 4;
        this.sliderWidth = MathHelper.clamp(usableWidth / callback.getMaxSteps(), 8, usableWidth / 2);
    }

    public void setLocked(boolean locked)
    {
        this.locked = locked;
    }

    @Override
    protected boolean onMouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (this.locked == false)
        {
            this.callback.setRelativeValue(this.getRelativePosition(mouseX));
            this.lastMouseX = mouseX;
            this.dragging = true;
        }

        return true;
    }

    @Override
    public void onMouseReleased(int mouseX, int mouseY, int mouseButton)
    {
        this.dragging = false;
    }

    @Override
    protected boolean onMouseScrolled(int mouseX, int mouseY, double mouseWheelDelta)
    {
        if (this.locked == false)
        {
            double relPos = this.callback.getRelativeValue();
            double delta = 1.0 / (double) (this.getWidth() - this.sliderWidth);

            if (mouseWheelDelta < 0)
            {
                delta = -delta;
            }

            this.callback.setRelativeValue(relPos + delta);

            return true;
        }

        return false;
    }

    @Override
    public void renderAt(int x, int y, float z, ScreenContext ctx)
    {
        int mouseX = ctx.mouseX;
        int width = this.getWidth();
        int height = this.getHeight();

        if (this.dragging && mouseX != this.lastMouseX)
        {
            this.callback.setRelativeValue(this.getRelativePosition(mouseX));
            this.lastMouseX = mouseX;
        }

        // Render the background texture
        DefaultIcons.BUTTON_BACKGROUND.renderFourSplicedAt(x + 1, y, z, width - 2, height);

        double relPos = this.callback.getRelativeValue();
        int sw = this.sliderWidth;
        int usableWidth = width - 4 - sw;

        // Render the slider bar texture
        int sx = x + 2 + (int) (relPos * usableWidth);
        boolean hovered = GuiUtils.isMouseInRegion(mouseX, ctx.mouseY, sx, y, sw, height);
        DefaultIcons.BUTTON_BACKGROUND.renderFourSplicedAt(sx, y, z, sw, height, this.locked == false, hovered);

        StyledTextLine text = StyledTextLine.raw(this.callback.getFormattedDisplayValue());
        int textWidth = text.renderWidth;
        int textColor = this.locked ? 0xFF909090 : 0xFFFFFFA0;
        int tx = x + (width / 2) - textWidth / 2;
        int ty = y + this.getCenteredTextOffsetY();

        this.renderTextLine(tx, ty, z, textColor, false, ctx, text);
    }

    protected double getRelativePosition(int mouseX)
    {
        int relPos = mouseX - this.getX() - this.sliderWidth / 2;
        return MathHelper.clamp((double) relPos / (double) (this.getWidth() - this.sliderWidth - 4), 0, 1);
    }
}
