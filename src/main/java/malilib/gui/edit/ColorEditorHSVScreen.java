package malilib.gui.edit;

import java.awt.Color;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import javax.annotation.Nullable;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;

import malilib.gui.BaseScreen;
import malilib.gui.util.ScreenContext;
import malilib.gui.widget.BaseTextFieldWidget;
import malilib.gui.widget.IntegerTextFieldWidget.IntRangeValidator;
import malilib.gui.widget.LabelWidget;
import malilib.render.RenderContext;
import malilib.render.RenderUtils;
import malilib.render.ShapeRenderUtils;
import malilib.render.buffer.VanillaWrappingVertexBuilder;
import malilib.render.buffer.VertexBuilder;
import malilib.render.shader.ShaderProgram;
import malilib.util.data.Color4f;

public class ColorEditorHSVScreen extends BaseScreen
{
    protected static final ShaderProgram SHADER_HUE = new ShaderProgram("malilib", null, "shaders/sv_selector.frag");

    protected final IntConsumer valueConsumer;
    protected final int initialValue;
    @Nullable protected Element clickedElement;
    @Nullable protected Element currentTextInputElement;
    protected BaseTextFieldWidget textFieldFullColor;
    protected BaseTextFieldWidget textFieldH;
    protected BaseTextFieldWidget textFieldS;
    protected BaseTextFieldWidget textFieldV;
    protected BaseTextFieldWidget textFieldR;
    protected BaseTextFieldWidget textFieldG;
    protected BaseTextFieldWidget textFieldB;
    protected BaseTextFieldWidget textFieldA;
    protected boolean mouseDown;
    protected int color;
    protected int xHS;
    protected int yHS;
    protected int xHFullSV;
    protected int xH;
    protected int yH;
    protected int sizeHS;
    protected int widthHFullSV;
    protected int widthSlider;
    protected int heightSlider;
    protected int gapSlider;
    protected float relH;
    protected float relS;
    protected float relV;
    protected float relR;
    protected float relG;
    protected float relB;
    protected float relA;

    public ColorEditorHSVScreen(int initialValue, IntConsumer valueConsumer)
    {
        this.initialValue = initialValue;
        this.valueConsumer = valueConsumer;

        this.backgroundColor = 0xFF000000;
        this.renderBorder = true;
        this.useTitleHierarchy = false;

        this.setTitle("malilib.title.screen.color_editor");
        this.addPreScreenCloseListener(() -> this.valueConsumer.accept(this.color));
        this.setScreenWidthAndHeight(300, 180);
        this.centerOnScreen();
    }

    @Override
    public void setPosition(int x, int y)
    {
        super.setPosition(x, y);

        this.xHS = this.x + 6;
        this.yHS = this.y + 24;
        this.xH = this.x + 160;
        this.yH = this.y + 24;
        this.xHFullSV = this.xHS + 110;
        this.sizeHS = 102;
        this.widthHFullSV = 16;
        this.widthSlider = 90;
        this.heightSlider = 12;
        this.gapSlider = 6;
    }

    @Override
    protected void initScreen()
    {
        this.canDragMove = false; // TODO enable after rewriting this screen

        super.initScreen();

        int xLabel = this.x + 148;
        int xTextField = xLabel + 110;
        int y = this.y + 23;

        y += this.createComponentElements(xTextField, y, xLabel, Element.H);
        y += this.createComponentElements(xTextField, y, xLabel, Element.S);
        y += this.createComponentElements(xTextField, y, xLabel, Element.V);
        y += this.createComponentElements(xTextField, y, xLabel, Element.R);
        y += this.createComponentElements(xTextField, y, xLabel, Element.G);
        y += this.createComponentElements(xTextField, y, xLabel, Element.B);
        y += this.createComponentElements(xTextField, y, xLabel, Element.A);

        String str = "HEX:";
        int w = this.getStringWidth(str);
        LabelWidget label = new LabelWidget(0xFFC0C0C0).setLines(str);
        label.setPosition(this.xH - w - 4, y + 6);
        this.addWidget(label);
        this.textFieldFullColor = new BaseTextFieldWidget(68, 14);
        this.textFieldFullColor.setPosition(this.xH - 1, y + 2);
        this.textFieldFullColor.setTextValidator(BaseTextFieldWidget.VALIDATOR_HEX_COLOR_8_6_4_3);
        this.textFieldFullColor.setListener(new TextChangeListener(null, this));
        this.addWidget(this.textFieldFullColor);

        //String str = StringUtils.translate("malilib.gui.label.color_editor.current_color");
        //this.addLabel(this.xHS, this.yHS + this.sizeHS + 10, 60, 12, 0xFFFFFF, str);

        this.setColor(this.initialValue); // Set the text field values
    }

    protected int createComponentElements(int x, int y, int xLabel, Element element)
    {
        BaseTextFieldWidget textField = new BaseTextFieldWidget(32, 14);
        textField.setPosition(x, y);
        textField.setListener(new TextChangeListener(element, this));
        textField.setUpdateListenerAlways(true);

        switch (element)
        {
            case H:
                this.textFieldH = textField;
                textField.setTextValidator(new IntRangeValidator(0, 360));
                break;
            case S:
                this.textFieldS = textField;
                textField.setTextValidator(new IntRangeValidator(0, 100));
                break;
            case V:
                this.textFieldV = textField;
                textField.setTextValidator(new IntRangeValidator(0, 100));
                break;
            case R:
                this.textFieldR = textField;
                textField.setTextValidator(new IntRangeValidator(0, 255));
                break;
            case G:
                this.textFieldG = textField;
                textField.setTextValidator(new IntRangeValidator(0, 255));
                break;
            case B:
                this.textFieldB = textField;
                textField.setTextValidator(new IntRangeValidator(0, 255));
                break;
            case A:
                this.textFieldA = textField;
                textField.setTextValidator(new IntRangeValidator(0, 255));
                break;
            default:
        }

        LabelWidget label = new LabelWidget(0xFFC0C0C0).setLines(element.name() + ":");
        label.setPosition(xLabel, y + 3);
        this.addWidget(label);
        this.addWidget(textField);

        return this.heightSlider + this.gapSlider;
    }

    @Override
    protected void renderCustomContents(ScreenContext ctx)
    {
        if (this.mouseDown)
        {
            if (this.clickedElement != null)
            {
                this.updateColorFromMouseInput(this.clickedElement, ctx.mouseX, ctx.mouseY);
            }
        }

        this.drawColorSelector(ctx);
    }

    @Override
    public boolean onMouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        this.clickedElement = this.getHoveredElement(mouseX, mouseY);

        if (this.clickedElement != null)
        {
            this.mouseDown = true;
            this.updateColorFromMouseInput(this.clickedElement, mouseX, mouseY);
        }

        return super.onMouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean onMouseReleased(int mouseX, int mouseY, int mouseButton)
    {
        this.mouseDown = false;
        this.clickedElement = null;
        return super.onMouseReleased(mouseX, mouseY, mouseButton);
    }

    protected float[] getCurrentColorHSV()
    {
        return Color4f.convertRgb2Hsv(this.color);
    }

    protected void setColor(int color)
    {
        this.color = color;

        this.relA = ((color & 0xFF000000) >>> 24) / 255f;

        this.setHSVFromRGB(color);
        this.setRGBFromHSV();

        this.currentTextInputElement = null;
    }

    protected void setHSVFromRGB()
    {
        this.setHSVFromRGB(this.relR, this.relG, this.relB);
    }

    protected void setHSVFromRGB(float r, float g, float b)
    {
        float[] hsv = new float[3];

        int ri = (int) (r * 255f);
        int gi = (int) (g * 255f);
        int bi = (int) (b * 255f);
        int ai = (int) (this.relA * 255f);

        Color.RGBtoHSB(ri, gi, bi, hsv);

        this.relH = hsv[0];
        this.relS = hsv[1];
        this.relV = hsv[2];

        this.color = (ai << 24) | (ri << 16) | (gi << 8) | bi;

        this.updateTextFieldsHSV();
    }

    protected void setHSVFromRGB(int rgb)
    {
        float[] hsv = Color4f.convertRgb2Hsv(rgb);

        this.relH = hsv[0];
        this.relS = hsv[1];
        this.relV = hsv[2];

        this.updateTextFieldsHSV();
    }

    protected void setRGBFromHSV()
    {
        this.setRGBFromHSV(this.relH, this.relS, this.relV);
    }

    protected void setRGBFromHSV(float h, float s, float v)
    {
        int rgb = Color.HSBtoRGB(h, s, v);
        int ai = (int) (this.relA * 255f);

        this.color = (ai << 24) | (rgb & 0x00FFFFFF);

        this.relR = (float) ((rgb >>> 16) & 0xFF) / 255f;
        this.relG = (float) ((rgb >>>  8) & 0xFF) / 255f;
        this.relB = (float) ((rgb       ) & 0xFF) / 255f;

        this.updateTextFieldsRGB();
    }

    protected void updateColorFromMouseInput(Element element, int mouseX, int mouseY)
    {
        if (element == Element.SV)
        {
            mouseX = MathHelper.clamp(mouseX, this.xHS, this.xHS + this.sizeHS);
            mouseY = MathHelper.clamp(mouseY, this.yHS, this.yHS + this.sizeHS);
            int relX = mouseX - this.xHS;
            int relY = mouseY - this.yHS;
            float saturation = 1f - ((float) relY / (float) this.sizeHS);
            float value = (float) relX / (float) this.sizeHS;

            this.relS = saturation;
            this.relV = value;

            this.setRGBFromHSV();
            this.updateTextField(Element.S);
            this.updateTextField(Element.V);
        }
        else if (element == Element.H_FULL_SV)
        {
            mouseY = MathHelper.clamp(mouseY, this.yHS, this.yHS + this.sizeHS);
            int relY = mouseY - this.yHS;
            float hue = 1f - ((float) relY / (float) this.sizeHS);

            this.relH = hue;
            this.setRGBFromHSV();
            this.updateTextField(Element.H);
        }
        else
        {
            mouseX = MathHelper.clamp(mouseX, this.xH, this.xH + this.widthSlider);
            int relX = mouseX - this.xH;
            float relVal = (float) relX / (float) this.widthSlider;

            switch (element)
            {
                case H:
                {
                    this.relH = relVal;
                    this.setRGBFromHSV();
                    this.updateTextField(Element.H);
                    break;
                }
                case S:
                {
                    this.relS = relVal;
                    this.setRGBFromHSV();
                    this.updateTextField(Element.S);
                    break;
                }
                case V:
                {
                    this.relV = relVal;
                    this.setRGBFromHSV();
                    this.updateTextField(Element.V);
                    break;
                }
                case R:
                {
                    this.relR = relVal;
                    this.setHSVFromRGB();
                    this.updateTextField(Element.R);
                    break;
                }
                case G:
                {
                    this.relG = relVal;
                    this.setHSVFromRGB();
                    this.updateTextField(Element.G);
                    break;
                }
                case B:
                {
                    this.relB = relVal;
                    this.setHSVFromRGB();
                    this.updateTextField(Element.B);
                    break;
                }
                case A:
                {
                    this.relA = relVal;
                    this.setHSVFromRGB();
                    this.updateTextField(Element.A);
                    break;
                }
                default:
            }
        }
    }

    protected void updateTextFieldsHSV()
    {
        this.updateTextField(Element.HEX);
        this.updateTextField(Element.H);
        this.updateTextField(Element.S);
        this.updateTextField(Element.V);
    }

    protected void updateTextFieldsRGB()
    {
        this.updateTextField(Element.HEX);
        this.updateTextField(Element.R);
        this.updateTextField(Element.G);
        this.updateTextField(Element.B);
        this.updateTextField(Element.A);
    }

    protected void updateTextField(Element type)
    {
        // Don't update the text field that is currently being written into, as that would
        // make it impossible to type in properly
        if (this.currentTextInputElement != type)
        {
            switch (type)
            {
                case HEX:
                    this.textFieldFullColor.setText(String.format("#%08X", this.color));
                    break;

                case H:
                    this.textFieldH.setText(String.valueOf((int) (this.relH * 360)));
                    break;

                case S:
                    this.textFieldS.setText(String.valueOf((int) (this.relS * 100)));
                    break;

                case V:
                    this.textFieldV.setText(String.valueOf((int) (this.relV * 100)));
                    break;

                case R:
                    this.textFieldR.setText(String.valueOf((int) (this.relR * 255)));
                    break;

                case G:
                    this.textFieldG.setText(String.valueOf((int) (this.relG * 255)));
                    break;

                case B:
                    this.textFieldB.setText(String.valueOf((int) (this.relB * 255)));
                    break;

                case A:
                    this.textFieldA.setText(String.valueOf((int) (this.relA * 255)));
                    break;

                default:
            }
        }
    }

    protected void drawColorSelector(RenderContext ctx)
    {
        int x = this.xH - 1;
        int y = this.yH - 1;
        int w = this.widthSlider + 2;
        int h = this.heightSlider + 2;
        int z = (int) this.z;
        int yd = this.heightSlider + this.gapSlider;
        int cx = this.xHS;
        int cy = this.yHS + this.sizeHS + 8;
        int cw = this.sizeHS;
        int ch = 16;

        RenderUtils.color(1f, 1f, 1f, 1f);

        VertexBuilder builder = VanillaWrappingVertexBuilder.coloredQuads();
        ShapeRenderUtils.renderOutline(x, y, z, w, h, 1, 0xC0FFFFFF, builder); // H
        y += yd;
        ShapeRenderUtils.renderOutline(x, y, z, w, h, 1, 0xC0FFFFFF, builder); // S
        y += yd;
        ShapeRenderUtils.renderOutline(x, y, z, w, h, 1, 0xC0FFFFFF, builder); // V
        y += yd;
        ShapeRenderUtils.renderOutline(x, y, z, w, h, 1, 0xC0FFFFFF, builder); // R
        y += yd;
        ShapeRenderUtils.renderOutline(x, y, z, w, h, 1, 0xC0FFFFFF, builder); // G
        y += yd;
        ShapeRenderUtils.renderOutline(x, y, z, w, h, 1, 0xC0FFFFFF, builder); // B
        y += yd;
        ShapeRenderUtils.renderOutline(x, y, z, w, h, 1, 0xC0FFFFFF, builder); // A

        x = this.xHS;
        y = this.yHS;
        w = this.sizeHS;
        h = this.sizeHS;

        ShapeRenderUtils.renderOutline(x - 1 , y - 1 , z, w + 2 , h + 2 , 1, 0xC0FFFFFF, builder); // main color selector
        ShapeRenderUtils.renderOutline(cx - 1, cy - 1, z, cw + 2, ch + 2, 1, 0xC0FFFFFF, builder); // current color indicator
        ShapeRenderUtils.renderOutline(this.xHFullSV, y - 1, z, this.widthHFullSV, this.sizeHS + 2, 1, 0xC0FFFFFF, builder); // Hue vertical/full value
        builder.draw();

        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

        GlStateManager.disableRescaleNormal();
        GlStateManager.disableAlpha();
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.01F);

        GL20.glUseProgram(SHADER_HUE.getProgram());
        GL20.glUniform1f(GL20.glGetUniformLocation(SHADER_HUE.getProgram(), "hue_value"), this.relH);

        builder = VanillaWrappingVertexBuilder.texturedQuad();
        builder.posUv(x    , y    , z, 1, 0);
        builder.posUv(x    , y + h, z, 0, 0);
        builder.posUv(x + w, y + h, z, 0, 1);
        builder.posUv(x + w, y    , z, 1, 1);

        builder.draw();
        GL20.glUseProgram(0);

        int r = (int) (this.relR * 255f);
        int g = (int) (this.relG * 255f);
        int b = (int) (this.relB * 255f);
        int a = 255;

        builder = VanillaWrappingVertexBuilder.coloredQuads();
        // Current color indicator
        builder.posColor(cx     , cy     , z, r, g, b, a);
        builder.posColor(cx     , cy + ch, z, r, g, b, a);
        builder.posColor(cx + cw, cy + ch, z, r, g, b, a);
        builder.posColor(cx + cw, cy     , z, r, g, b, a);

        // SV selection marker for saturation, horizontal marker, vertical range
        int yt = y + (int) ((1 - this.relS) * h);
        builder.posColor(x - 1    , yt    , z, Color4f.WHITE);
        builder.posColor(x - 1    , yt + 1, z, Color4f.WHITE);
        builder.posColor(x + w + 1, yt + 1, z, Color4f.WHITE);
        builder.posColor(x + w + 1, yt    , z, Color4f.WHITE);

        // SV selection marker for value, vertical marker, horizontal range
        int xt = x + (int) (this.relV * w);
        builder.posColor(xt    , y - 1    , z, Color4f.WHITE);
        builder.posColor(xt    , y + h + 1, z, Color4f.WHITE);
        builder.posColor(xt + 1, y + h + 1, z, Color4f.WHITE);
        builder.posColor(xt + 1, y - 1    , z, Color4f.WHITE);

        x = this.xH;
        w = this.widthSlider;
        h = this.heightSlider;
        yd = this.heightSlider + this.gapSlider;

        // Full value Saturation & Value, Hue slider
        renderHueBarVertical(this.xHFullSV + 1, this.yHS, z, this.widthHFullSV - 2, this.sizeHS, 1f, 1f, builder);
        renderBarMarkerVerticalBar(this.xHFullSV, this.yHS, z, this.widthHFullSV, this.sizeHS, this.relH, builder);

        // Hue slider
        renderHueBarHorizontal(x, y, z, w, h, this.relS, this.relV, builder);
        renderBarMarkerHorizontalBar(x, y, z, w, h, this.relH, builder);
        y += yd;

        // Saturation slider
        int color1 = Color.HSBtoRGB(this.relH, 0, this.relV);
        int color2 = Color.HSBtoRGB(this.relH, 1, this.relV);
        renderGradientColorBar(x, y, z, w, h, color1, color2, builder);
        renderBarMarkerHorizontalBar(x, y, z, w, h, this.relS, builder);
        y += yd;

        // Value/Brightness slider
        color1 = Color.HSBtoRGB(this.relH, this.relS, 0);
        color2 = Color.HSBtoRGB(this.relH, this.relS, 1);
        renderGradientColorBar(x, y, z, w, h, color1, color2, builder);
        renderBarMarkerHorizontalBar(x, y, z, w, h, this.relV, builder);
        y += yd;

        // Red slider
        color1 = (this.color & 0xFF00FFFF) | 0xFF000000;
        color2 = this.color | 0xFFFF0000;
        renderGradientColorBar(x, y, z, w, h, color1, color2, builder);
        renderBarMarkerHorizontalBar(x, y, z, w, h, (float) r / 255f, builder);
        y += yd;

        // Green slider
        color1 = (this.color & 0xFFFF00FF) | 0xFF000000;
        color2 = this.color | 0xFF00FF00;
        renderGradientColorBar(x, y, z, w, h, color1, color2, builder);
        renderBarMarkerHorizontalBar(x, y, z, w, h, (float) g / 255f, builder);
        y += yd;

        // Blue slider
        color1 = (this.color & 0xFFFFFF00) | 0xFF000000;
        color2 = this.color | 0xFF0000FF;
        renderGradientColorBar(x, y, z, w, h, color1, color2, builder);
        renderBarMarkerHorizontalBar(x, y, z, w, h, (float) b / 255f, builder);
        y += yd;

        // Alpha slider
        a = (int) (this.relA * 255f);
        color1 = this.color & 0x00FFFFFF;
        color2 = this.color | 0xFF000000;
        renderGradientColorBar(x, y, z, w, h, color1, color2, builder);
        renderBarMarkerHorizontalBar(x, y, z, w, h, (float) a / 255f, builder);

        builder.draw();

        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.enableAlpha();
        GlStateManager.enableRescaleNormal();

        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
    }

    public static void renderGradientColorBar(int x, int y, float z,
                                              int width, int height,
                                              int colorStart, int colorEnd, VertexBuilder builder)
    {
        int a1 = ((colorStart >>> 24) & 0xFF);
        int r1 = ((colorStart >>> 16) & 0xFF);
        int g1 = ((colorStart >>>  8) & 0xFF);
        int b1 = (colorStart          & 0xFF);
        int a2 = ((colorEnd >>> 24) & 0xFF);
        int r2 = ((colorEnd >>> 16) & 0xFF);
        int g2 = ((colorEnd >>>  8) & 0xFF);
        int b2 = (colorEnd          & 0xFF);

        builder.posColor(x        , y         , z, r1, g1, b1, a1);
        builder.posColor(x        , y + height, z, r1, g1, b1, a1);
        builder.posColor(x + width, y + height, z, r2, g2, b2, a2);
        builder.posColor(x + width, y         , z, r2, g2, b2, a2);
    }

    public static void renderHueBarHorizontal(int x, int y, float z,
                                              int width, int height,
                                              float saturation, float value, VertexBuilder builder)
    {
        renderHueBar(x, y, z, 0, height, width / 6, 0, saturation, value, builder);
    }

    public static void renderHueBarVertical(int x, int y, float z,
                                            int width, int height,
                                            float saturation, float value, VertexBuilder builder)
    {
        y = y + height - height / 6;
        renderHueBar(x, y, z, width, 0, 0, height / 6, saturation, value, builder);
    }

    public static void renderHueBar(int x, int y, float z,
                                    int width, int height,
                                    int segmentWidth, int segmentHeight,
                                    float saturation, float value, VertexBuilder builder)
    {
        int color1 = Color.HSBtoRGB(0f   , saturation, value);
        int color2 = Color.HSBtoRGB(1f/6f, saturation, value);
        renderHueBarSegment(x, y, z, width, height, segmentWidth, segmentHeight, color1, color2, builder);
        x += segmentWidth;
        y -= segmentHeight;

        color1 = Color.HSBtoRGB(1f/6f, saturation, value);
        color2 = Color.HSBtoRGB(2f/6f, saturation, value);
        renderHueBarSegment(x, y, z, width, height, segmentWidth, segmentHeight, color1, color2, builder);
        x += segmentWidth;
        y -= segmentHeight;

        color1 = Color.HSBtoRGB(2f/6f, saturation, value);
        color2 = Color.HSBtoRGB(3f/6f, saturation, value);
        renderHueBarSegment(x, y, z, width, height, segmentWidth, segmentHeight, color1, color2, builder);
        x += segmentWidth;
        y -= segmentHeight;

        color1 = Color.HSBtoRGB(3f/6f, saturation, value);
        color2 = Color.HSBtoRGB(4f/6f, saturation, value);
        renderHueBarSegment(x, y, z, width, height, segmentWidth, segmentHeight, color1, color2, builder);
        x += segmentWidth;
        y -= segmentHeight;

        color1 = Color.HSBtoRGB(4f/6f, saturation, value);
        color2 = Color.HSBtoRGB(5f/6f, saturation, value);
        renderHueBarSegment(x, y, z, width, height, segmentWidth, segmentHeight, color1, color2, builder);
        x += segmentWidth;
        y -= segmentHeight;

        color1 = Color.HSBtoRGB(5f/6f, saturation, value);
        color2 = Color.HSBtoRGB(   1f, saturation, value);
        renderHueBarSegment(x, y, z, width, height, segmentWidth, segmentHeight, color1, color2, builder);
    }

    public static void renderHueBarSegment(int x, int y, float z,
                                           int width, int height,
                                           int segmentWidth, int segmentHeight,
                                           int color1, int color2, VertexBuilder builder)
    {
        int r1 = ((color1 >>> 16) & 0xFF);
        int g1 = ((color1 >>>  8) & 0xFF);
        int b1 = ( color1         & 0xFF);
        int r2 = ((color2 >>> 16) & 0xFF);
        int g2 = ((color2 >>>  8) & 0xFF);
        int b2 = ( color2         & 0xFF);
        int a = 255;

        builder.posColor(x                       , y + segmentHeight         , z, r1, g1, b1, a);
        builder.posColor(x + width               , y + height + segmentHeight, z, r1, g1, b1, a);
        builder.posColor(x + width + segmentWidth, y + height                , z, r2, g2, b2, a);
        builder.posColor(x + segmentWidth        , y                         , z, r2, g2, b2, a);
    }

    public static void renderHsSelector(int xStart, int yStart, float z,
                                        int width, int height, float hue, VertexBuilder builder)
    {
        int x2 = xStart + width;

        for (int y = yStart; y <= yStart + height; ++y)
        {
            float saturation = 1f - ((float) (y - yStart) / (float) height);
            int color1 = Color.HSBtoRGB(hue, saturation, 0f);
            int color2 = Color.HSBtoRGB(hue, saturation, 1f);
            int r1 = ((color1 >>> 16) & 0xFF);
            int g1 = ((color1 >>>  8) & 0xFF);
            int b1 = ( color1         & 0xFF);
            int r2 = ((color2 >>> 16) & 0xFF);
            int g2 = ((color2 >>>  8) & 0xFF);
            int b2 = ( color2         & 0xFF);
            int a = 255;

            builder.posColor(xStart, y, z, r1, g1, b1, a);
            builder.posColor(x2    , y, z, r2, g2, b2, a);
        }
    }

    public static void renderBarMarkerHorizontalBar(int x, int y, float z,
                                                    int barWidth, int barHeight,
                                                    float value, VertexBuilder builder)
    {
        x += (int) (barWidth * value);
        int s = 2;

        builder.posColor(x - s, y - s, z, Color4f.WHITE);
        builder.posColor(x    , y + s, z, Color4f.WHITE);
        builder.posColor(x    , y + s, z, Color4f.WHITE);
        builder.posColor(x + s, y - s, z, Color4f.WHITE);

        y += barHeight;

        builder.posColor(x - s, y + s, z, Color4f.WHITE);
        builder.posColor(x + s, y + s, z, Color4f.WHITE);
        builder.posColor(x    , y - s, z, Color4f.WHITE);
        builder.posColor(x    , y - s, z, Color4f.WHITE);
    }

    public static void renderBarMarkerVerticalBar(int x, int y, float z,
                                                  int barWidth, int barHeight,
                                                  float value, VertexBuilder builder)
    {
        y += (int) (barHeight * (1f - value));
        int s = 2;

        builder.posColor(x - s, y - s, z, Color4f.WHITE);
        builder.posColor(x - s, y + s, z, Color4f.WHITE);
        builder.posColor(x + s, y    , z, Color4f.WHITE);
        builder.posColor(x + s, y    , z, Color4f.WHITE);

        x += barWidth;

        builder.posColor(x + s, y - s, z, Color4f.WHITE);
        builder.posColor(x - s, y    , z, Color4f.WHITE);
        builder.posColor(x - s, y    , z, Color4f.WHITE);
        builder.posColor(x + s, y + s, z, Color4f.WHITE);
    }

    @Nullable
    protected Element getHoveredElement(int mouseX, int mouseY)
    {
        if (mouseX >= this.xHS && mouseX <= this.xHS + this.sizeHS &&
            mouseY >= this.yHS && mouseY <= this.yHS + this.sizeHS)
        {
            return Element.SV;
        }
        else if (mouseX >= this.xHFullSV && mouseX <= this.xHFullSV + this.widthHFullSV &&
                 mouseY >= this.yHS && mouseY <= this.yHS + this.sizeHS)
        {
            return Element.H_FULL_SV;
        }
        else if (mouseX >= this.xH && mouseX <= this.xH + this.widthSlider)
        {
            int h = this.heightSlider + this.gapSlider;

            if (mouseY >= this.yH && mouseY <= this.yH + h * 7 - this.gapSlider)
            {
                int relY = mouseY - this.yH;
                int index = relY / h;

                if (index < 7 && (relY % h) < this.heightSlider)
                {
                    return Element.values()[index];
                }
            }
        }

        return null;
    }

    protected static class TextChangeListener implements Consumer<String>
    {
        protected final ColorEditorHSVScreen gui;
        @Nullable protected final Element type;

        protected TextChangeListener(@Nullable Element type, ColorEditorHSVScreen gui)
        {
            this.gui = gui;
            this.type = type;
        }

        @Override
        public void accept(String newText)
        {
            int colorOld = this.gui.color;

            // Entire color code
            if (this.type == null)
            {
                this.gui.currentTextInputElement = Element.HEX;
                this.gui.setColor(Color4f.getColorFromString(newText, colorOld));
            }
            else
            {
                try
                {
                    int val = Integer.parseInt(newText);
                    float[] hsv = this.gui.getCurrentColorHSV();
                    int colorNew;

                    switch (this.type)
                    {
                        case H:
                            val = MathHelper.clamp(val, 0, 360);
                            float h = (float) val / 360f;
                            colorNew = Color.HSBtoRGB(h, hsv[1], hsv[2]);
                            break;
                        case S:
                            val = MathHelper.clamp(val, 0, 100);
                            float s = (float) val / 100f;
                            colorNew = Color.HSBtoRGB(hsv[0], s, hsv[2]);
                            break;
                        case V:
                            val = MathHelper.clamp(val, 0, 100);
                            float v = (float) val / 100f;
                            colorNew = Color.HSBtoRGB(hsv[0], hsv[1], v);
                            break;
                        case R:
                            val = MathHelper.clamp(val, 0, 255);
                            colorNew = (colorOld & 0x00FFFF) | (val << 16);
                            break;
                        case G:
                            val = MathHelper.clamp(val, 0, 255);
                            colorNew = (colorOld & 0xFF00FF) | (val <<  8);
                            break;
                        case B:
                            val = MathHelper.clamp(val, 0, 255);
                            colorNew = (colorOld & 0xFFFF00) | val;
                            break;
                        case A:
                            val = MathHelper.clamp(val, 0, 255);
                            colorNew = (colorOld & 0x00FFFFFF) | (val << 24);
                            break;
                        default:
                            return;
                    }

                    if (colorNew != colorOld)
                    {
                        this.gui.currentTextInputElement = this.type;
                        this.gui.setColor(colorNew);
                    }
                }
                catch (Exception ignore) {}
            }
        }
    }

    protected enum Element
    {
        // NOTE: The individual H, S, V, R, G, B values are used by their index in getHoveredElement()
        // So the compound/other types must come after them.
        H,
        S,
        V,
        R,
        G,
        B,
        A,
        SV,
        H_FULL_SV,
        HEX
    }
}
