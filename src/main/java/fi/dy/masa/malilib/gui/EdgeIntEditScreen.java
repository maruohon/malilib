package fi.dy.masa.malilib.gui;

import javax.annotation.Nullable;
import fi.dy.masa.malilib.gui.position.EdgeInt;
import fi.dy.masa.malilib.gui.widget.EdgeIntEditWidget;
import fi.dy.masa.malilib.util.StringUtils;

public class EdgeIntEditScreen extends BaseScreen
{
    protected final EdgeIntEditWidget editWidget;

    public EdgeIntEditScreen(EdgeInt value, boolean isColor, String titleKey, @Nullable String centerText)
    {
        this.useTitleHierarchy = false;
        this.title = StringUtils.translate(titleKey);

        this.setScreenWidthAndHeight(320, 130);
        this.centerOnScreen();

        this.editWidget = new EdgeIntEditWidget(0, 0, 300, 100, value, isColor, centerText);
    }

    @Override
    protected void initScreen()
    {
        super.initScreen();

        this.editWidget.setPosition(this.x + 10, this.y + 26);
        this.addWidget(this.editWidget);
    }
}
