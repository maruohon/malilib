package fi.dy.masa.malilib.gui.widget;

public class HoverInfoWidget extends InteractableWidget
{
    public HoverInfoWidget(int x, int y, int width, int height, String key, Object... args)
    {
        super(x, y, width, height);

        this.translateAndAddHoverString(key, args);
    }
}
