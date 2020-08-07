package fi.dy.masa.malilib.gui.widget;

import fi.dy.masa.malilib.util.StringUtils;

public class HoverInfoWidget extends BaseWidget
{
    public HoverInfoWidget(int x, int y, int width, int height, String key, Object... args)
    {
        super(x, y, width, height);

        this.setInfoLines(key, args);
    }

    protected void setInfoLines(String key, Object... args)
    {
        String[] split = StringUtils.translate(key, args).split("\\n");
        this.addHoverStrings(split);
    }
}
