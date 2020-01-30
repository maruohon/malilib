package fi.dy.masa.malilib.gui.interfaces;

public interface ITextRenderer
{
    /**
     * Renders the given string at the given location using the provided color.
     * Whether or not the text is line broken in any way depends on the implementation.
     * @param x
     * @param y
     * @param color
     * @param text
     */
    void renderText(int x, int y, int color, String text);
}
