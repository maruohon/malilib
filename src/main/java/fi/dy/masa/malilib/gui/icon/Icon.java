package fi.dy.masa.malilib.gui.icon;

import net.minecraft.util.ResourceLocation;

public interface Icon
{
    /**
     * @return the width of this icon
     */
    int getWidth();

    /**
     * @return the height of this icon
     */
    int getHeight();

    /**
     * @return the texture U-coordinate (x-coordinate) of this icon
     */
    int getU();

    /**
     * @return the texture V-coordinate (y-coordinate) of this icon
     */
    int getV();

    /**
     * @return the identifier/location of the texture used for this icon
     */
    ResourceLocation getTexture();

    /**
     * Renders this icon at the given position
     * @param x
     * @param y
     * @param z
     */
    void renderAt(int x, int y, float z);
}
