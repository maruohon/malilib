package fi.dy.masa.malilib.gui.widget;

public class HoverInfoWidget extends BaseWidget
{
    public HoverInfoWidget(int x, int y, int width, int height, String key, Object... args)
    {
        super(x, y, width, height);

        this.addHoverString(key, args);
    }
}
