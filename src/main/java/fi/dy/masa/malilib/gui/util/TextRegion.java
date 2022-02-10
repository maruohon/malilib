package fi.dy.masa.malilib.gui.util;

import fi.dy.masa.malilib.config.value.HorizontalAlignment;
import fi.dy.masa.malilib.render.text.StyledTextLine;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.malilib.util.data.LeftRight;

public class TextRegion
{
    protected int maxWidth = 4;
    protected int endIndex;
    protected int startIndex;
    protected String text;
    protected StyledTextLine styledText;

    public TextRegion()
    {
        this.setText("");
    }

    public void setMaxWidth(int maxWidth)
    {
        this.maxWidth = maxWidth;
    }

    public int getStartIndex()
    {
        return this.startIndex;
    }

    public int getEndIndex()
    {
        return this.endIndex;
    }

    public int getRegionLength()
    {
        return this.endIndex - this.startIndex + 1;
    }

    public String getText()
    {
        return this.text;
    }

    public StyledTextLine getStyledText()
    {
        return this.styledText;
    }

    protected void setText(String text)
    {
        this.text = text;
        this.styledText = StyledTextLine.raw(text);
    }

    /**
     * Updates the currently visible string, using the current start index and left alignment
     * @param fullText
     */
    public void update(String fullText)
    {
        this.alignToIndexAt(fullText, this.startIndex, HorizontalAlignment.LEFT);
    }

    /**
     * Clips and aligns the provided full string so that the given index position of it
     * aligns on the given alignment point within the visible region/max string render width.
     * @param fullText
     * @param index
     * @param alignment
     */
    public void alignToIndexAt(String fullText, int index, HorizontalAlignment alignment)
    {
        int fullTextWidth = StringUtils.getStringWidth(fullText);
        int fullTextLength = fullText.length();

        // The entire text can fit, just use it as-is
        if (fullTextWidth <= this.maxWidth)
        {
            this.startIndex = 0;
            this.endIndex = fullTextLength - 1;
            this.setText(fullText);
            //System.out.printf("align: i: %d, a: %s => FULL\n", index, alignment);
            return;
        }

        String visibleText;
        int startIndex, endIndex;

        // Align the index to the center of the region.
        // Clip the text from the index point to both the left and the right,
        // clip those to half of the maximum length, and then join them together in the middle.
        if (alignment == HorizontalAlignment.CENTER)
        {
            String subStringLeft  = fullText.substring(0, Math.min(index, fullTextLength));
            String subStringRight = fullText.substring(Math.min(index, fullTextLength), fullTextLength);

            // First allow half of the max width for the left part, then use the remaining width for the right side
            int width = this.maxWidth / 2;
            String clippedLeft  = StringUtils.clampTextToRenderLength(subStringLeft , width, LeftRight.LEFT, "");
            width = this.maxWidth - StringUtils.getStringWidth(clippedLeft);
            String clippedRight = StringUtils.clampTextToRenderLength(subStringRight, width, LeftRight.RIGHT, "");

            // If the left clipped string fit completely, then left align the entire string
            if (clippedLeft.length() >= subStringLeft.length())
            {
                visibleText = StringUtils.clampTextToRenderLength(fullText, this.maxWidth, LeftRight.RIGHT, "");
                startIndex = 0;
                endIndex = visibleText.length() - 1;
            }
            // Join the clipped left and right parts in the middle
            else
            {
                visibleText = clippedLeft + clippedRight;
                startIndex = Math.max(0, index - 1 - (clippedLeft.length() - 1));
                endIndex = startIndex + (visibleText.length() - 1);
            }
        }
        else
        {
            int subStringLength;

            // Clip the text from the left, so it gets aligned on the right
            if (alignment == HorizontalAlignment.RIGHT)
            {
                startIndex = 0;
                endIndex = Math.min(index, fullTextLength - 1);

                // Get the text from the beginning up to the alignment point
                String subString = fullText.substring(startIndex, endIndex + 1);
                subStringLength = subString.length();
                visibleText = StringUtils.clampTextToRenderLength(subString, this.maxWidth, LeftRight.LEFT, "");
            }
            // Clip the text from the right, so it gets aligned on the left
            else // if (alignment == HorizontalAlignment.LEFT)
            {
                startIndex = Math.min(index, fullTextLength - 1);
                endIndex = fullTextLength - 1;

                // Get the text from the alignment point to the end
                String subString = fullText.substring(startIndex);
                subStringLength = subString.length();
                visibleText = StringUtils.clampTextToRenderLength(subString, this.maxWidth, LeftRight.RIGHT, "");
            }

            int visibleTextLength = visibleText.length();

            // The entire string did not fit to the visible area, update the start and end indices
            if (visibleTextLength < subStringLength)
            {
                if (alignment == HorizontalAlignment.RIGHT)
                {
                    startIndex = endIndex - (visibleTextLength - 1);
                }
                else
                {
                    endIndex = startIndex + (visibleTextLength - 1);
                }
            }
        }

        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.setText(visibleText);
        //System.out.printf("align: i: %d, a: %s => s: %s, e: %d, t: %s\n", index, alignment, this.startIndex, this.endIndex, this.text);
    }

    public boolean isIndexVisibleWithCurrentStart(int index, String fullText)
    {
        int fullTextLength = fullText.length();

        if (index < this.startIndex || index < 0)
        {
            return false;
        }

        return StringUtils.getStringWidth(fullText.substring(this.startIndex, Math.min(index + 1, fullTextLength))) <= this.maxWidth;
    }
}
