package fi.dy.masa.malilib.gui.widget;

public class HoverInfoWidget extends InteractableWidget
{
    public HoverInfoWidget(int width, int height, String key, Object... args)
    {
        super(width, height);

        this.translateAndAddHoverString(key, args);
    }
}
