package fi.dy.masa.malilib.util;

public enum LeftRight
{
    LEFT,
    RIGHT;

    public LeftRight getOpposite()
    {
        return this == LEFT ? RIGHT : LEFT;
    }
}
