package fi.dy.masa.malilib.gui.widgets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import fi.dy.masa.malilib.gui.interfaces.ITextRenderer;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.StringUtils;

public class WidgetLabel extends WidgetBase
{
    protected final List<String> labels = new ArrayList<>();
    protected boolean backgroundEnabled;
    protected boolean centered;
    protected boolean useTextShadow = true;
    protected boolean visible = true;
    protected int backgroundColor;
    protected int borderColorBR;
    protected int borderColorUL;
    protected int borderSize;
    protected int textOffsetX;
    protected int textOffsetY;
    protected int textColor;

    public WidgetLabel(int x, int y, int width, int height, int textColor, String... text)
    {
        this(x, y, width, height, textColor, Arrays.asList(text));
    }

    public WidgetLabel(int x, int y, int width, int height, int textColor, List<String> lines)
    {
        super(x, y, width, height);

        this.textColor = textColor;

        for (String str : lines)
        {
            this.addLine(str);
        }
    }

    public WidgetLabel addLine(String translationKey, Object... args)
    {
        this.labels.add(StringUtils.translate(translationKey, args));
        return this;
    }

    public WidgetLabel setText(String translationKey, Object... args)
    {
        this.labels.clear();
        return this.addLine(translationKey, args);
    }

    public WidgetLabel setVisible(boolean visible)
    {
        this.visible = visible;
        return this;
    }

    public WidgetLabel setCentered(boolean centered)
    {
        this.centered = centered;
        return this;
    }

    public WidgetLabel setUseTextShadow(boolean useShadow)
    {
        this.useTextShadow = useShadow;
        return this;
    }

    public WidgetLabel setTextColor(int color)
    {
        this.textColor = color;
        return this;
    }

    public WidgetLabel setBackgroundProperties(int borderSize, int backgroundColor, int borderULColor, int borderBRColor)
    {
        this.borderSize = borderSize;
        this.backgroundColor = backgroundColor;
        this.borderColorUL = borderULColor;
        this.borderColorBR = borderBRColor;
        this.backgroundEnabled = true;
        return this;
    }

    public WidgetLabel setTextOffsetX(int offsetX)
    {
        this.textOffsetX = offsetX;
        return this;
    }

    public WidgetLabel setTextOffsetY(int offsetY)
    {
        this.textOffsetY = offsetY;
        return this;
    }

    public WidgetLabel setTextOffsetXY(int offset)
    {
        this.textOffsetX = offset;
        this.textOffsetY = offset;
        return this;
    }

    @Override
    public void render(int mouseX, int mouseY, boolean selected)
    {
        if (this.visible)
        {
            RenderUtils.color(1f, 1f, 1f, 1f);
            RenderUtils.setupBlend();
            this.drawLabelBackground();

            int x = this.x + this.textOffsetX + this.borderSize;
            int y = this.y + this.textOffsetY + this.borderSize;
            int fontHeight = this.fontHeight;
            ITextRenderer renderer = this.getTextRenderer(this.useTextShadow, this.centered);

            for (int i = 0; i < this.labels.size(); ++i)
            {
                String text = this.labels.get(i);
                renderer.renderText(x, y, this.textColor, text);
                y += fontHeight + 1;
            }

            RenderUtils.color(1f, 1f, 1f, 1f);
        }
    }

    protected void drawLabelBackground()
    {
        if (this.backgroundEnabled)
        {
            int x = this.x;
            int y = this.y;
            int w = this.width;
            int h = this.height;
            int bs = this.borderSize;

            RenderUtils.drawRect(x + bs, y + bs, w - bs * 2 + 1, h - bs * 2 + 1, this.backgroundColor);

            RenderUtils.drawHorizontalLine(x, y    , w, this.borderColorUL);
            RenderUtils.drawHorizontalLine(x, y + h, w, this.borderColorBR);
            RenderUtils.drawVerticalLine(x    , y, h    , this.borderColorUL);
            RenderUtils.drawVerticalLine(x + w, y, h + 1, this.borderColorBR);
        }
    }
}
