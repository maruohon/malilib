package fi.dy.masa.malilib.config.option;

public interface IConfigSlider extends IConfigValue
{
    default boolean shouldUseSlider()
    {
        return false;
    }

    default void toggleUseSlider()
    {
    }
}
