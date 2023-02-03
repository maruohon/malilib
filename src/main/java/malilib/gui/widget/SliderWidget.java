package malilib.gui.widget;

import net.minecraft.util.math.MathHelper;

import malilib.gui.callback.SliderCallback;
import malilib.gui.icon.DefaultIcons;
import malilib.gui.util.GuiUtils;
import malilib.gui.util.ScreenContext;

public class SliderWidget extends InteractableWidget
{
    protected final SliderCallback callback;
    protected int sliderWidth;
    protected int lastMouseX;
    protected boolean dragging;

    public SliderWidget(int width, int height, SliderCallback callback)
    {
        super(width, height);

        this.callback = callback;
        int usableWidth = this.getWidth() - 4;
        this.sliderWidth = MathHelper.clamp(usableWidth / callback.getMaxSteps(), 8, usableWidth / 2);
        this.textOffset.setCenterHorizontally(true);
        this.textOffset.setXOffset(0);
    }

    @Override
    public void updateWidgetState()
    {
        this.callback.updateDisplayText();
    }

    @Override
    protected boolean onMouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (this.isEnabled())
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
        if (this.isEnabled())
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
    public boolean onMouseMoved(int mouseX, int mouseY)
    {
        if (this.dragging && mouseX != this.lastMouseX)
        {
            this.callback.setRelativeValue(this.getRelativePosition(mouseX));
            this.lastMouseX = mouseX;
            return true;
        }

        return false;
    }

    @Override
    public void renderAt(int x, int y, float z, ScreenContext ctx)
    {
        int width = this.getWidth();
        int height = this.getHeight();

        // Render the background texture
        DefaultIcons.BUTTON_BACKGROUND.renderFourSplicedAt(x + 1, y, z, width - 2, height);

        // Render the slider bar texture
        double relPos = this.callback.getRelativeValue();
        int sw = this.sliderWidth;
        int usableWidth = width - sw - 4;
        int sx = x + 2 + (int) (relPos * usableWidth);
        boolean enabled = this.isEnabled();
        boolean hovered = GuiUtils.isMouseInRegion(ctx.mouseX, ctx.mouseY, sx, y, sw, height);
        DefaultIcons.BUTTON_BACKGROUND.renderFourSplicedAt(sx, y, z, sw, height, IconWidget.getVariantIndex(enabled, hovered));

        int textColor = enabled ? 0xFFFFFFA0 : 0xFF909090;
        this.renderTextLine(x, y, z, textColor, this.callback.getDisplayText(), ctx);
    }

    protected double getRelativePosition(int mouseX)
    {
        int relPos = mouseX - this.getX() - this.sliderWidth / 2;
        return MathHelper.clamp((double) relPos / (double) (this.getWidth() - this.sliderWidth - 4), 0, 1);
    }
}
