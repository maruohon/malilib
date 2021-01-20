package fi.dy.masa.malilib.gui.widget;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import fi.dy.masa.malilib.gui.callback.SliderCallback;
import fi.dy.masa.malilib.render.RenderUtils;

public class SliderWidget extends BaseWidget
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
    protected boolean onMouseClickedImpl(int mouseX, int mouseY, int mouseButton)
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
    public void onMouseReleasedImpl(int mouseX, int mouseY, int mouseButton)
    {
        this.dragging = false;
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

        RenderUtils.renderTexturedRectangle(x + 1        , y, 0, 46, width - 6, 20, z);
        RenderUtils.renderTexturedRectangle(x + width - 5, y, 196, 46, 4, 20, z);

        double relPos = this.callback.getRelativeValue();
        int sw = this.sliderWidth;
        int usableWidth = width - 4 - sw;
        int s = sw / 2;

        RenderUtils.renderTexturedRectangle(x + 2 + (int) (relPos * usableWidth)    , y, 0, 66, s, 20, z);
        RenderUtils.renderTexturedRectangle(x + 2 + (int) (relPos * usableWidth) + s, y, 200 - s, 66, s, 20, z);

        String str = this.callback.getFormattedDisplayValue();
        int tw = this.getStringWidth(str);
        this.drawString(x + (width / 2) - tw / 2, y + this.getCenteredTextOffsetY(), z, 0xFFFFFFA0, str);

        RenderUtils.color(1f, 1f, 1f, 1f);
    }

    protected double getRelativePosition(int mouseX)
    {
        int relPos = mouseX - this.getX() - this.sliderWidth / 2;
        return MathHelper.clamp((double) relPos / (double) (this.getWidth() - this.sliderWidth - 4), 0, 1);
    }
}
