package fi.dy.masa.malilib.overlay.widget.sub;

import java.util.ArrayList;
import java.util.List;
import fi.dy.masa.malilib.gui.icon.DefaultIcons;
import fi.dy.masa.malilib.gui.icon.Icon;
import fi.dy.masa.malilib.gui.icon.MultiIcon;
import fi.dy.masa.malilib.gui.position.HorizontalAlignment;
import fi.dy.masa.malilib.overlay.widget.InfoRendererWidget;
import fi.dy.masa.malilib.render.text.StyledText;
import fi.dy.masa.malilib.render.text.StyledTextLine;
import fi.dy.masa.malilib.render.text.TextRenderer;
import fi.dy.masa.malilib.util.StyledTextUtils;

public class ToastWidget extends InfoRendererWidget
{
    protected final HorizontalAlignment horizontalAlignment;
    protected final List<StyledText> text = new ArrayList<>();
    protected final long fadeInDuration;
    protected final long fadeOutDuration;
    protected Icon backgroundTexture = DefaultIcons.TOAST_BACKGROUND;
    protected long lifeTimeMs = -1L;
    protected long fadeInEndTime;
    protected long fadeOutStartTime;
    protected long expireTime;
    protected int messageGap;

    public ToastWidget(int fadeInTimeMs, int fadeOutTimeMs, HorizontalAlignment horizontalAlignment)
    {
        this.horizontalAlignment = horizontalAlignment;
        this.fadeInDuration = (long) fadeInTimeMs * 1000000L;
        this.fadeOutDuration = (long) fadeOutTimeMs * 1000000L;
    }

    public void initialize(long currentTime)
    {
        this.fadeInEndTime = currentTime + this.fadeInDuration;
    }

    public void setMessageGap(int messageGap)
    {
        this.messageGap = messageGap;
    }

    public boolean hasExpired(long currentTime)
    {
        return currentTime > this.expireTime;
    }

    /**
     * Returns the relative age of this toast, in the range 0.0f - 1.0f, where 1.0f is the expiration age.
     */
    public float getRelativeAge()
    {
        long currentTime = System.nanoTime();

        if (currentTime > this.expireTime)
        {
            return 1.0f;
        }

        long remainingAge = this.expireTime - currentTime;

        return 1.0f - (float) ((double) remainingAge / (double) (this.lifeTimeMs * 1000000L));
    }

    /**
     * Adds the given text to the current text.
     * @param text the text to add to the end of the current text
     * @param lifeTimeMs the new life time of the toast. Use -1 to not update/refresh the current life time.
     */
    public void addText(StyledText text, int lifeTimeMs)
    {
        this.text.add(this.wrapTextToWidth(text));
        this.updateSize();
        this.setLifeTime(lifeTimeMs);
    }

    /**
     * Replaces the current text with the given text.
     * @param text the new text to set in the toast
     * @param lifeTimeMs the new life time of the toast. Use -1 to not update/refresh the current life time.
     */
    public void replaceText(StyledText text, int lifeTimeMs)
    {
        this.text.clear();
        this.addText(text, lifeTimeMs);
    }

    public void setBackgroundTexture(MultiIcon backgroundTexture)
    {
        this.backgroundTexture = backgroundTexture;
    }

    protected StyledText wrapTextToWidth(StyledText text)
    {
        return StyledTextUtils.wrapStyledTextToMaxWidth(text, this.maxWidth);
    }

    @Override
    public void updateSize()
    {
        int messageCount = this.text.size();
        int width = 0;
        int height = messageCount > 1 ? (messageCount - 1) * this.messageGap : 0;

        for (StyledText text : this.text)
        {
            width = Math.max(width, StyledTextLine.getRenderWidth(text.lines));
            height += text.lines.size() * this.lineHeight;
        }

        this.setWidth(width + this.padding.getHorizontalTotal());
        this.setHeight(height + this.padding.getVerticalTotal());
    }

    protected void setLifeTime(int lifeTimeMs)
    {
        if (this.lifeTimeMs < 0 && lifeTimeMs < 0)
        {
            lifeTimeMs = 5000;
        }

        if (lifeTimeMs >= 0)
        {
            this.lifeTimeMs =  lifeTimeMs;
            this.expireTime = System.nanoTime() + this.lifeTimeMs * 1000000L;
            this.fadeOutStartTime = this.expireTime - this.fadeOutDuration;
        }
    }

    public void render(long currentTime)
    {
        int offsetX = 0;

        if (currentTime < this.fadeInEndTime && this.fadeInDuration > 0)
        {
            double progress = (double) (this.fadeInEndTime - currentTime) / (double) this.fadeInDuration;
            offsetX = (int) (progress * this.getWidth());
        }
        else if (currentTime >= this.fadeOutStartTime && this.fadeOutDuration > 0)
        {
            double progress = (double) (currentTime - this.fadeOutStartTime) / (double) this.fadeOutDuration;
            offsetX = (int) (progress * this.getWidth());
        }

        int x = this.getX();
        int y = this.getY();
        float z = this.getZLevel();

        if (this.horizontalAlignment == HorizontalAlignment.RIGHT)
        {
            x += offsetX;
        }
        else if (this.horizontalAlignment == HorizontalAlignment.LEFT)
        {
            x -= offsetX;
        }

        super.renderAt(x, y, z);
    }

    @Override
    protected void renderBackground(int x, int y, float z)
    {
        this.backgroundTexture.renderFourSplicedAt(x, y, z, this.getWidth(), this.getHeight());
    }

    @Override
    protected void renderContents(int x, int y, float z)
    {
        if (this.text.isEmpty() == false)
        {
            x += this.padding.getLeft();
            y += this.padding.getTop();

            int color = this.textSettings.getTextColor();

            for (StyledText text : this.text)
            {
                TextRenderer.INSTANCE.renderText(x, y, z, color, true, text, this.lineHeight);
                y += text.lines.size() * this.lineHeight + this.messageGap;
            }
        }
    }
}
