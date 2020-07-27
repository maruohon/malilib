package fi.dy.masa.malilib.util.position;

import fi.dy.masa.malilib.util.PositionUtils.CoordinateType;

public interface ICoordinateValueModifier
{
    /**
     * Modifies the existing value by the given amount
     * @param type
     * @param amount
     * @return
     */
    boolean modifyValue(CoordinateType type, int amount);

    /**
     * Sets the coordinate indicated by {@code type} to the value parsed from the string {@code newValue}
     * @param type
     * @param newValue
     * @return
     */
    boolean setValueFromString(CoordinateType type, String newValue);
}
