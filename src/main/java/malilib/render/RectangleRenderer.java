package malilib.render;

public interface RectangleRenderer
{
    /**
     * Renders something at the provided coordinates/area.
     * In malilib this is used for customizing the hover text background.
     */
    void render(int x, int y, float z, int width, int height);
}
