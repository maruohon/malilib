package fi.dy.masa.malilib.gui.widget;

import net.minecraft.util.math.MathHelper;
import fi.dy.masa.malilib.gui.callback.SliderCallback;
import fi.dy.masa.malilib.gui.icon.DefaultIcons;
import fi.dy.masa.malilib.gui.util.GuiUtils;
import fi.dy.masa.malilib.gui.util.ScreenContext;

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
        this.textOffset.setCenterHorizontally(true);
    }

    public void setLocked(boolean locked)
    {
        this.locked = locked;
    }

    @Override
    public void updateWidgetState()
    {
        this.callback.updateDisplayText();
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
        boolean hovered = GuiUtils.isMouseInRegion(ctx.mouseX, ctx.mouseY, sx, y, sw, height);
        DefaultIcons.BUTTON_BACKGROUND.renderFourSplicedAt(sx, y, z, sw, height, this.locked == false, hovered);

        int textColor = this.locked ? 0xFF909090 : 0xFFFFFFA0;
        this.renderTextLine(x, y, z, textColor, this.callback.getDisplayText(), ctx);
    }

    protected double getRelativePosition(int mouseX)
    {
        int relPos = mouseX - this.getX() - this.sliderWidth / 2;
        return MathHelper.clamp((double) relPos / (double) (this.getWidth() - this.sliderWidth - 4), 0, 1);
    }
}
