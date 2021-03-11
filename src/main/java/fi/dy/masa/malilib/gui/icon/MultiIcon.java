package fi.dy.masa.malilib.gui.icon;

public interface MultiIcon extends Icon
{
    /**
     * Get the U coordinate for the given icon variant
     * @param variantIndex
     * @return
     */
    int getVariantU(int variantIndex);

    /**
     * Get the V coordinate for the given icon variant
     * @param variantIndex
     * @return
     */
    int getVariantV(int variantIndex);

    /**
     * Renders the icon at the given location, using the given icon variant index.
     * The variant index is basically an offset from the base UV location.
     * The implementation can define where and how the position is offset
     * from the base location.
     * @param x
     * @param y
     * @param z
     * @param variantIndex
     */
    void renderAt(int x, int y, float z, int variantIndex);

    /**
     * Renders the icon at the given location, using an icon variant chosen
     * by the given enabled and hover status.
     * @param x
     * @param y
     * @param z
     * @param enabled
     * @param hovered
     */
    default void renderAt(int x, int y, float z, boolean enabled, boolean hovered)
    {
        int variantIndex = this.getVariantIndex(enabled, hovered);
        this.renderAt(x, y, z, variantIndex);
    }

    /**
     * Returns the icon variant index to use for the given status of the icon.
     * By default a disabled icon is at index 0, an enabled, non-hovered icon
     * is at index 1 and an enabled, hovered icon is at index 1.
     * Thus the hover status has no effect for disabled icons.
     * @param enabled
     * @param hovered
     * @return
     */
    default int getVariantIndex(boolean enabled, boolean hovered)
    {
        if (enabled == false)
        {
            return 0;
        }

        return hovered ? 2 : 1;
    }
}
