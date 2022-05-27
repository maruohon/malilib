package fi.dy.masa.malilib.render.text;

import java.util.function.Supplier;
import fi.dy.masa.malilib.gui.util.BackgroundRenderer;
import fi.dy.masa.malilib.gui.util.BackgroundSettings;
import fi.dy.masa.malilib.gui.util.BorderRenderer;
import fi.dy.masa.malilib.gui.util.BorderSettings;
import fi.dy.masa.malilib.gui.util.ScreenContext;
import fi.dy.masa.malilib.render.ShapeRenderUtils;
import fi.dy.masa.malilib.util.data.EdgeInt;
import fi.dy.masa.malilib.util.data.LeftRight;

public class SingleTextLineRenderer
{
    protected final BackgroundRenderer backgroundRenderer = new BackgroundRenderer();
    protected final BorderRenderer borderRenderer = new BorderRenderer();
    protected final EdgeInt padding = new EdgeInt();
    protected final Supplier<TextRenderSettings> textSettingsSupplier;
    protected StyledTextLine originalTextLine = StyledTextLine.EMPTY;
    protected StyledTextLine clampedTextLine = StyledTextLine.EMPTY;
    protected StringListRenderer.LineClamper lineClamper;
    protected boolean hasClampedContent;
    protected int maxWidth = -1;
    protected int totalTextWidth;

    public SingleTextLineRenderer(Supplier<TextRenderSettings> textSettingsSupplier)
    {
        this.textSettingsSupplier = textSettingsSupplier;
        this.lineClamper = this::clampLineToWidth;
    }

    public SingleTextLineRenderer setMaxWidth(int maxWidth)
    {
        this.maxWidth = maxWidth;
        this.updateText();
        return this;
    }

    public SingleTextLineRenderer setLineClamper(StringListRenderer.LineClamper clamper)
    {
        this.lineClamper = clamper;
        return this;
    }

    public BackgroundRenderer getBackgroundRenderer()
    {
        return this.backgroundRenderer;
    }

    public BorderRenderer getBorderRenderer()
    {
        return this.borderRenderer;
    }

    public EdgeInt getPadding()
    {
        return this.padding;
    }

    public boolean hasClampedContent()
    {
        return this.hasClampedContent;
    }

    public int getClampedTextWidth()
    {
        return this.clampedTextLine.renderWidth;
    }

    public int getTotalTextWidth()
    {
        return this.totalTextWidth;
    }

    public int getTotalHeight()
    {
        int bw = this.borderRenderer.getHoverSettings().getActiveBorderWidth();
        return TextRenderer.INSTANCE.getLineHeight() + this.padding.getVerticalTotal() + 2 * bw;
    }

    public void setText(String translationKey, Object... args)
    {
        this.setStyledText(StyledText.translate(translationKey, args));
    }

    public void setStyledText(StyledText text)
    {
        this.setStyledTextLine(StyledTextLine.joinLines(text));
    }

    public void setStyledTextLine(StyledTextLine line)
    {
        this.originalTextLine = line;
        this.updateText();
    }

    public StyledTextLine clampLineToWidth(StyledTextLine line, int maxWidth)
    {
        return StyledTextUtils.clampStyledTextToMaxWidth(line, maxWidth, LeftRight.RIGHT, " ...");
    }

    public void updateText()
    {
        this.totalTextWidth = this.originalTextLine.renderWidth;

        if (this.maxWidth > 0 && this.originalTextLine.renderWidth > this.maxWidth)
        {
            this.clampedTextLine = this.lineClamper.clampLineToWidth(this.originalTextLine, this.maxWidth);
            this.hasClampedContent = true;
        }
        else
        {
            this.clampedTextLine = this.originalTextLine;
            this.hasClampedContent = false;
        }
    }

    public void renderAt(int x, int y, float z, boolean hovered, ScreenContext ctx)
    {
        BackgroundSettings bgSettings = this.backgroundRenderer.getActiveSettings(hovered);
        BorderSettings borderSettings = this.borderRenderer.getActiveSettings(hovered);
        BorderSettings hoveredBorderSettings = this.borderRenderer.getHoverSettings();
        int bw = hoveredBorderSettings.getActiveBorderWidth();
        int fullHeight = this.getTotalHeight();
        int bgWidth = this.totalTextWidth + this.padding.getHorizontalTotal();

        if (hoveredBorderSettings.isEnabled())
        {
            bgWidth += 2 * bw;
        }

        if (bgSettings.isEnabled() && borderSettings.isEnabled())
        {
            ShapeRenderUtils.renderOutlinedRectangle(x, y, z, bgWidth, fullHeight, bgSettings.getColor(), borderSettings.getColor());
        }
        else if (bgSettings.isEnabled())
        {
            ShapeRenderUtils.renderRectangle(x, y, z, bgWidth, fullHeight, bgSettings.getColor());
        }
        else if (borderSettings.isEnabled())
        {
            ShapeRenderUtils.renderOutline(x, y, z, bgWidth, fullHeight, bw, borderSettings.getColor());
        }

        int tx = x + this.padding.getLeft() + bw;
        int ty = y + this.padding.getTop() + bw;
        TextRenderSettings settings = this.textSettingsSupplier.get();
        StyledTextLine text = hovered ? this.originalTextLine : this.clampedTextLine;

        TextRenderer.INSTANCE.renderLine(tx, ty, z + 0.0125f, settings.textColor, settings.textShadowEnabled, text, ctx);
    }
}
