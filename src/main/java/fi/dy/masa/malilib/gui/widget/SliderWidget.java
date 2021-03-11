package fi.dy.masa.malilib.gui.widget;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import fi.dy.masa.malilib.gui.callback.SliderCallback;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.render.ShapeRenderUtils;
import fi.dy.masa.malilib.render.text.StyledTextLine;

public class SliderWidget extends InteractableWidget
{
    public static final ResourceLocation VANILLA_WIDGETS = new ResourceLocation("textures/gui/widgets.png");

    protected final SliderCallback callback;
    protected int sliderWidth;
    protected int lastMouseX;
    protected boolean dragging;
    protected boolean locked;

    public SliderWidget(int x, int y, int width, int height, SliderCallback callback)
    {
        super(x, y, width, height);

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
    public void renderAt(int x, int y, float z, int mouseX, int mouseY, boolean isActiveGui, boolean hovered)
    {
        if (this.dragging && mouseX != this.lastMouseX)
        {
            this.callback.setRelativeValue(this.getRelativePosition(mouseX));
            this.lastMouseX = mouseX;
        }

        int width = this.getWidth();

        this.bindTexture(VANILLA_WIDGETS);
        RenderUtils.color(1f, 1f, 1f, 1f);

        // Render the slider background texture
        ShapeRenderUtils.renderTexturedRectangle(x + 1        , y, z,   0, 46, width - 6, 20);
        ShapeRenderUtils.renderTexturedRectangle(x + width - 5, y, z, 196, 46,         4, 20);

        double relPos = this.callback.getRelativeValue();
        int sw = this.sliderWidth;
        int usableWidth = width - 4 - sw;
        int s = sw / 2;
        int v = this.locked ? 46 : 66;

        // Render the slider bar texture
        ShapeRenderUtils.renderTexturedRectangle(x + 2 + (int) (relPos * usableWidth)    , y, z,       0, v, s, 20);
        ShapeRenderUtils.renderTexturedRectangle(x + 2 + (int) (relPos * usableWidth) + s, y, z, 200 - s, v, s, 20);

        StyledTextLine text = StyledTextLine.raw(this.callback.getFormattedDisplayValue());
        int textWidth = text.renderWidth;
        int textColor = this.locked ? 0xFF909090 : 0xFFFFFFA0;
        this.renderTextLine(x + (width / 2) - textWidth / 2, y + this.getCenteredTextOffsetY(), z, textColor, false, text);

        RenderUtils.color(1f, 1f, 1f, 1f);
    }

    protected double getRelativePosition(int mouseX)
    {
        int relPos = mouseX - this.getX() - this.sliderWidth / 2;
        return MathHelper.clamp((double) relPos / (double) (this.getWidth() - this.sliderWidth - 4), 0, 1);
    }
}
