package fi.dy.masa.malilib.gui.widgets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.mojang.blaze3d.platform.GlStateManager;
import fi.dy.masa.malilib.render.RenderUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.resource.language.I18n;

public class WidgetLabel extends WidgetBase
{
    protected final TextRenderer fontRenderer;
    protected final List<String> labels;
    protected final int textColor;
    protected boolean visible = true;
    protected boolean centered;
    protected boolean backgroundEnabled;
    protected int backgroundColor;
    protected int borderULColor;
    protected int borderBRColor;
    protected int borderSize;

    public WidgetLabel(int x, int y, int width, int height, float zLevel, int textColor, String... text)
    {
        super(x, y, width, height, zLevel);

        this.textColor = textColor;
        this.fontRenderer = MinecraftClient.getInstance().textRenderer;
        this.labels = new ArrayList<>();
        this.labels.addAll(Arrays.asList(text));
    }

    public void addLine(String key, Object... args)
    {
        this.labels.add(I18n.translate(key, args));
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
            RenderUtils.setupBlend();
            this.drawLabelBackground();

            int fontHeight = this.fontRenderer.fontHeight;
            int yCenter = this.y + this.height / 2 + this.borderSize / 2;
            int yTextStart = yCenter - this.labels.size() * fontHeight / 2;

            for (int i = 0; i < this.labels.size(); ++i)
            {
                String text = this.labels.get(i);

                if (this.centered)
                {
                    RenderUtils.drawCenteredString(this.fontRenderer, text, this.x + this.width / 2, yTextStart + i * fontHeight, this.textColor);
                }
                else
                {
                    RenderUtils.drawString(this.fontRenderer, text, this.x, yTextStart + i * fontHeight, this.textColor);
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

            DrawableHelper.fill(xStart, yStart, xStart + bgWidth, yStart + bgHeight, this.backgroundColor);

            RenderUtils.drawHorizontalLine(xStart, xStart + bgWidth, yStart, this.borderULColor);
            RenderUtils.drawHorizontalLine(xStart, xStart + bgWidth, yStart + bgHeight, this.borderBRColor);
            RenderUtils.drawVerticalLine(xStart, yStart, yStart + bgHeight, this.borderULColor);
            RenderUtils.drawVerticalLine(xStart + bgWidth, yStart, yStart + bgHeight, this.borderBRColor);
        }
    }
}
