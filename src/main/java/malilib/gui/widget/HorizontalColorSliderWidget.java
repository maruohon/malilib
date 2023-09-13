package malilib.gui.widget;

import java.util.function.IntSupplier;

import net.minecraft.util.math.MathHelper;

import malilib.gui.callback.SliderCallback;
import malilib.gui.util.ScreenContext;
import malilib.render.ShapeRenderUtils;
import malilib.render.buffer.VanillaWrappingVertexBuilder;
import malilib.render.buffer.VertexBuilder;
import malilib.util.data.Color4f;

public class HorizontalColorSliderWidget extends BaseSliderWidget<SliderCallback>
{
    protected final IntSupplier color1;
    protected final IntSupplier color2;
    protected int sliderThickness;

    public HorizontalColorSliderWidget(int width, int height, IntSupplier color1, IntSupplier color2, SliderCallback callback)
    {
        super(width, height, callback);

        this.color1 = color1;
        this.color2 = color2;
        this.sliderThickness = 4;
        this.borderRenderer.getNormalSettings().setBorderWidthAndColor(1, 0xFFC0C0C0);
    }

    @Override
    protected int getSliderTravelDistance()
    {
        return this.getUsableDistance() - this.sliderThickness;
    }

    @Override
    protected double getRelativePosition(int mouseX, int mouseY)
    {
        int relPos = mouseX - (this.getX() + 1) - this.sliderThickness / 2;
        return MathHelper.clamp((double) relPos / (double) (this.getSliderTravelDistance() - 2), 0, 1);
    }

    protected int getUsableDistance()
    {
        return this.getWidth() - 2;
    }

    @Override
    public void renderAt(int x, int y, float z, ScreenContext ctx)
    {
        super.renderAt(x, y, z, ctx);

        this.renderBarColor(x, y, z, ctx);
        this.renderPositionMarker(x, y, z + 0.0125f);
    }

    protected void renderBarColor(int x, int y, float z, ScreenContext ctx)
    {
        int width = this.getWidth();
        int height = this.getHeight();
        int color1 = this.color1.getAsInt();
        int color2 = this.color2.getAsInt();

        ShapeRenderUtils.renderHorizontalGradientRectangle(x + 1, y + 1, z, width - 2, height - 2, color1, color2, ctx);
    }

    protected void renderPositionMarker(int x, int y, float z)
    {
        x += 1 + (int) (this.getUsableDistance() * this.callback.getRelativeValue());
        y += 1;

        renderHorizontalBarPositionMarker(x, y, z, 2, this.getHeight() - 2);
    }

    public static void renderHorizontalBarPositionMarker(int x, int y, float z, int r, int height)
    {
        VertexBuilder builder = VanillaWrappingVertexBuilder.coloredTriangles();

        builder.posColor(x - r, y - r, z, Color4f.WHITE);
        builder.posColor(x    , y + r, z, Color4f.WHITE);
        builder.posColor(x + r, y - r, z, Color4f.WHITE);

        y += height;

        builder.posColor(x - r, y + r, z, Color4f.WHITE);
        builder.posColor(x + r, y + r, z, Color4f.WHITE);
        builder.posColor(x    , y - r, z, Color4f.WHITE);

        builder.draw();
    }
}
