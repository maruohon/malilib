package fi.dy.masa.malilib.render;

public interface RectangleRenderer
{
    /**
     * Renders something at the provided coordinates/area.
     * In malilib this is used for customizing the hover text background.
     * @param x
     * @param y
     * @param width
     * @param height
     * @param zLevel
     */
    void render(int x, int y, int width, int height, int zLevel);
}
