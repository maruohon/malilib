package fi.dy.masa.malilib.overlay.message;

import java.util.List;
import fi.dy.masa.malilib.render.text.StyledText;
import fi.dy.masa.malilib.render.text.StyledTextLine;
import fi.dy.masa.malilib.render.text.TextRenderer;
import fi.dy.masa.malilib.util.StyledTextUtils;

public class Message
{
    public static final int INFO = 0xFFFFFFFF;
    public static final int SUCCESS = 0xFF55FF55;
    public static final int WARNING = 0xFFFFAA00;
    public static final int ERROR = 0xFFFF5555;

    protected final List<StyledTextLine> messageLines;
    protected final int defaultTextColor;
    protected final int width;
    protected final long expireTime;
    protected final long fadeDuration;
    protected final long fadeTime;

    public Message(int defaultTextColor, int displayTimeMs, String translationKey, Object... args)
    {
        this(defaultTextColor, displayTimeMs, 1000, 300, translationKey, args);
    }

    public Message(int defaultTextColor, int displayTimeMs, int maxLineWidth, String translationKey, Object... args)
    {
        this(defaultTextColor, displayTimeMs, 1000, maxLineWidth, translationKey, args);
    }

    public Message(int defaultTextColor, int displayTimeMs, int fadeTimeMs, int maxLineWidth, String translationKey, Object... args)
    {
        this.defaultTextColor = defaultTextColor;
        this.expireTime = System.nanoTime() + (long) displayTimeMs * 1000000L;
        this.fadeDuration = Math.min((long) fadeTimeMs * 1000000L, (long) displayTimeMs * 1000000L / 2L);
        this.fadeTime = this.expireTime - this.fadeDuration;

        this.messageLines = StyledTextUtils.wrapStyledTextToMaxWidth(StyledText.translatedOf(translationKey, args).lines, maxLineWidth);

        int width = 0;

        for (StyledTextLine line : this.messageLines)
        {
            width = Math.max(width, line.renderWidth);
        }

        this.width = width;
    }

    public boolean hasExpired(long currentTime)
    {
        return currentTime >= this.expireTime;
    }

    protected boolean isFading(long currentTime)
    {
        return currentTime >= this.fadeTime;
    }

    public int getWidth()
    {
        return this.width;
    }

    public int getLineCount()
    {
        return this.messageLines.size();
    }

    /**
     * Renders the lines for this message
     * @return the y coordinate of the next message
     */
    public void renderAt(int x, int y, float z, int lineHeight, long currentTime)
    {
        TextRenderer.INSTANCE.startBuffers();

        for (StyledTextLine line : this.messageLines)
        {
            if (this.isFading(currentTime))
            {
                int alphaInt = (this.defaultTextColor & 0xFF000000) >>> 24;
                double fadeProgress = 1.0 - (double) (currentTime - this.fadeTime) / (double) this.fadeDuration;
                float alpha = (float) alphaInt * (float) fadeProgress / 255.0f;
                TextRenderer.INSTANCE.renderLineToBuffer(x, y, z, this.defaultTextColor, alpha, true, line);
            }
            else
            {
                TextRenderer.INSTANCE.renderLineToBuffer(x, y, z, this.defaultTextColor, true, line);
            }

            y += lineHeight;
        }

        TextRenderer.INSTANCE.renderBuffers();
    }
}
