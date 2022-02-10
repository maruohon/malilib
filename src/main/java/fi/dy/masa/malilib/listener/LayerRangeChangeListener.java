package fi.dy.masa.malilib.listener;

public interface LayerRangeChangeListener
{
    /**
     * This method is called when the range changes such that a global/entire update should be performed
     */
    void updateAll();

    /**
     * This method is called when the range changes such that an update
     * only between the given X coordinate range is sufficient
     */
    void updateBetweenX(int minX, int maxX);

    /**
     * This method is called when the range changes such that an update
     * only between the given Y coordinate range is sufficient
     */
    void updateBetweenY(int minY, int maxY);

    /**
     * This method is called when the range changes such that an update
     * only between the given Z coordinate range is sufficient
     */
    void updateBetweenZ(int minZ, int maxZ);
}
