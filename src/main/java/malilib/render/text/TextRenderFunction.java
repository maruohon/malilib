package malilib.render.text;

public interface TextRenderFunction
{
    /**
     * Renders the given string at the given location using the provided color.
     * Whether or not the text is line broken in any way depends on the implementation.
     */
    void renderText(int x, int y, float z, int color, String text);
}
