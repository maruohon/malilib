package fi.dy.masa.malilib.gui.widgets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import fi.dy.masa.malilib.gui.interfaces.ITextRenderer;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.StringUtils;

public class WidgetLabel extends WidgetBackground
{
    protected final List<String> labels = new ArrayList<>();
    protected boolean centered;
    protected boolean useTextShadow = true;
    protected boolean visible = true;
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

        if (width < 0)
        {
            width = 0;

            for (String line : this.labels)
            {
                width = Math.max(width, this.getStringWidth(line));
            }

            this.setWidth(width);
        }

        if (height < 0)
        {
            this.setHeight((this.fontHeight + 1) * this.labels.size() - 2);
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
            this.drawBackground();

            int x = this.getX() + this.textOffsetX + this.borderWidth;
            int y = this.getY() + this.textOffsetY + this.borderWidth;
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
}
