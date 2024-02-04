package malilib.gui.widget;

import java.awt.Color;
import org.lwjgl.opengl.GL11;

import malilib.gui.callback.SliderCallback;
import malilib.gui.util.ScreenContext;
import malilib.render.ShapeRenderUtils;
import malilib.render.buffer.VanillaWrappingVertexBuilder;
import malilib.render.buffer.VertexBuilder;
import malilib.util.MathUtils;
import malilib.util.data.Color4f;
import malilib.util.data.FloatSupplier;
import malilib.util.game.wrap.RenderWrap;

public class VerticalHueSliderWidget extends BaseSliderWidget<SliderCallback>
{
    protected final FloatSupplier saturation;
    protected final FloatSupplier value;
    protected int sliderThickness;

    public VerticalHueSliderWidget(int width, int height, FloatSupplier saturation, FloatSupplier value, SliderCallback callback)
    {
        super(width, height, callback);

        this.saturation = saturation;
        this.value = value;
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
        int relPos = mouseY - (this.getY() + 1) - this.sliderThickness / 2;
        return MathUtils.clamp((double) relPos / (double) (this.getSliderTravelDistance() - 2), 0.0, 1.0);
    }

    protected int getUsableDistance()
    {
        return this.getHeight() - 2;
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
        int width = this.getWidth() - 2;
        int usableHeight = this.getUsableDistance();
        float saturation = this.saturation.getAsFloat();
        float value = this.value.getAsFloat();
        VertexBuilder builder = VanillaWrappingVertexBuilder.coloredQuads();

        for (int i = 0; i < 6; ++i)
        {
            int y1 = y + 1 + usableHeight *  i      / 6;
            int y2 = y + 1 + usableHeight * (i + 1) / 6;
            int color1 = Color.HSBtoRGB((6f - i) / 6f, saturation, value);
            int color2 = Color.HSBtoRGB((5f - i) / 6f, saturation, value);

            ShapeRenderUtils.renderVerticalGradientRectangle(x + 1, y1, z, width, y2 - y1, color1, color2, builder);
        }

        RenderWrap.shadeModel(GL11.GL_SMOOTH);

        builder.draw();

        RenderWrap.shadeModel(GL11.GL_FLAT);
    }

    protected void renderPositionMarker(int x, int y, float z)
    {
        y += 1 + (int) (this.getUsableDistance() * this.callback.getRelativeValue());

        int s = 2;

        VertexBuilder builder = VanillaWrappingVertexBuilder.coloredTriangles();

        builder.posColor(x - s, y + s, z, Color4f.WHITE);
        builder.posColor(x + s, y    , z, Color4f.WHITE);
        builder.posColor(x - s, y - s, z, Color4f.WHITE);

        x += this.getWidth();

        builder.posColor(x + s, y - s, z, Color4f.WHITE);
        builder.posColor(x - s, y    , z, Color4f.WHITE);
        builder.posColor(x + s, y + s, z, Color4f.WHITE);

        builder.draw();
    }
}
