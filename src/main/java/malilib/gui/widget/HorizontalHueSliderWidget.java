package malilib.gui.widget;

import java.awt.Color;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.GlStateManager;

import malilib.gui.callback.SliderCallback;
import malilib.gui.util.ScreenContext;
import malilib.render.ShapeRenderUtils;
import malilib.render.buffer.VanillaWrappingVertexBuilder;
import malilib.render.buffer.VertexBuilder;
import malilib.util.MathUtils;
import malilib.util.data.FloatSupplier;

public class HorizontalHueSliderWidget extends BaseSliderWidget<SliderCallback>
{
    protected final FloatSupplier saturation;
    protected final FloatSupplier value;
    protected int sliderThickness;

    public HorizontalHueSliderWidget(int width, int height, FloatSupplier saturation, FloatSupplier value, SliderCallback callback)
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
        int relPos = mouseX - (this.getX() + 1) - this.sliderThickness / 2;
        return MathUtils.clamp((double) relPos / (double) (this.getSliderTravelDistance() - 2), 0.0, 1.0);
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
        int height = this.getHeight() - 2;
        int usableWidth = this.getUsableDistance();
        float saturation = this.saturation.getAsFloat();
        float value = this.value.getAsFloat();
        VertexBuilder builder = VanillaWrappingVertexBuilder.coloredQuads();

        for (int i = 0; i < 6; ++i)
        {
            int x1 = x + 1 + usableWidth *  i      / 6;
            int x2 = x + 1 + usableWidth * (i + 1) / 6;
            int color1 = Color.HSBtoRGB((float)  i      / 6f, saturation, value);
            int color2 = Color.HSBtoRGB((float) (i + 1) / 6f, saturation, value);

            ShapeRenderUtils.renderHorizontalGradientRectangle(x1, y + 1, z, x2 - x1, height, color1, color2, builder);
        }

        GlStateManager.shadeModel(GL11.GL_SMOOTH);

        builder.draw();

        GlStateManager.shadeModel(GL11.GL_FLAT);
    }

    protected void renderPositionMarker(int x, int y, float z)
    {
        x += 1 + (int) (this.getUsableDistance() * this.callback.getRelativeValue());
        y += 1;

        HorizontalColorSliderWidget.renderHorizontalBarPositionMarker(x, y, z, 2, this.getHeight() - 2);
    }
}
