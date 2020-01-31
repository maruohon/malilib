package fi.dy.masa.malilib.gui.interfaces;

public interface IBackgroundRenderer
{
    /**
     * Renders a background of some kind at the provided coordinates.
     * In malilib this is used for customizing the hover text background.
     * @param x
     * @param y
     * @param width
     * @param height
     * @param zLevel
     */
    void renderBackground(int x, int y, int width, int height, int zLevel);
}
