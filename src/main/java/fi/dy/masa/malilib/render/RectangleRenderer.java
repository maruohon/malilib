package fi.dy.masa.malilib.render;

public interface RectangleRenderer
{
    /**
     * Renders something at the provided coordinates/area.
     * In malilib this is used for customizing the hover text background.
     * @param x
     * @param y
     * @param z
     * @param width
     * @param height
     */
    void render(int x, int y, float z, int width, int height);
}
