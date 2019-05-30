package fi.dy.masa.malilib.gui.widgets;

import java.util.ArrayList;
import java.util.List;
import fi.dy.masa.malilib.render.RenderUtils;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;

public class WidgetLabel extends WidgetBase
{
    protected final List<String> labels = new ArrayList<>();
    protected final int textColor;
    protected boolean visible = true;
    protected boolean centered;
    protected boolean backgroundEnabled;
    protected int backgroundColor;
    protected int borderULColor;
    protected int borderBRColor;
    protected int borderSize;

    public WidgetLabel(int x, int y, int width, int height, int textColor, String... text)
    {
        super(x, y, width, height);

        this.textColor = textColor;

        for (String str : text)
        {
            this.addLine(str);
        }
    }

    public void addLine(String key, Object... args)
    {
        this.labels.add(I18n.format(key, args));
    }

    public void setCentered(boolean centered)
    {
        this.centered = centered;
    }

    public void setBackgroundProperties(int borderSize, int backgroundColor, int borderULColor, int borderBRColor)
    {
        this.borderSize = borderSize;
        this.backgroundColor = backgroundColor;
        this.borderULColor = borderULColor;
        this.borderBRColor = borderBRColor;
        this.backgroundEnabled = true;
    }

    @Override
    public void render(int mouseX, int mouseY, boolean selected)
    {
        if (this.visible)
        {
            GlStateManager.enableBlend();
            GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            this.drawLabelBackground();

            int fontHeight = this.textRenderer.FONT_HEIGHT;
            int yCenter = this.y + this.height / 2 + this.borderSize / 2;
            int yTextStart = yCenter - 1 - this.labels.size() * fontHeight / 2;

            for (int i = 0; i < this.labels.size(); ++i)
            {
                String text = this.labels.get(i);

                if (this.centered)
                {
                    RenderUtils.drawCenteredString(this.textRenderer, text, this.x + this.width / 2, yTextStart + i * fontHeight, this.textColor);
                }
                else
                {
                    this.drawStringWithShadow(text, this.x, yTextStart + i * fontHeight, this.textColor);
                }
            }
        }
    }

    protected void drawLabelBackground()
    {
        if (this.backgroundEnabled)
        {
            int bgWidth = this.width + this.borderSize * 2;
            int bgHeight = this.height + this.borderSize * 2;
            int xStart = this.x - this.borderSize;
            int yStart = this.y - this.borderSize;

            Gui.drawRect(xStart, yStart, xStart + bgWidth, yStart + bgHeight, this.backgroundColor);

            RenderUtils.drawHorizontalLine(xStart, yStart           , bgWidth, this.borderULColor);
            RenderUtils.drawHorizontalLine(xStart, yStart + bgHeight, bgWidth, this.borderBRColor);
            RenderUtils.drawVerticalLine(xStart          , yStart, bgHeight, this.borderULColor);
            RenderUtils.drawVerticalLine(xStart + bgWidth, yStart, bgHeight, this.borderBRColor);
        }
    }
}
