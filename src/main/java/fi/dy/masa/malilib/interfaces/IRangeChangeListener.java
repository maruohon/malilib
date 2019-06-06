package fi.dy.masa.malilib.interfaces;

public interface IRangeChangeListener
{
    void updateAll();

    void updateBetweenX(int minX, int maxX);

    void updateBetweenY(int minY, int maxY);

    void updateBetweenZ(int minZ, int maxZ);
}
