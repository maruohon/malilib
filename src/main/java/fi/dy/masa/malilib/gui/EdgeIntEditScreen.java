package fi.dy.masa.malilib.gui;

import javax.annotation.Nullable;
import fi.dy.masa.malilib.gui.position.EdgeInt;
import fi.dy.masa.malilib.gui.widget.EdgeIntEditWidget;

public class EdgeIntEditScreen extends BaseScreen
{
    protected final EdgeIntEditWidget editWidget;

    public EdgeIntEditScreen(EdgeInt value, boolean isColor, String titleKey, @Nullable String centerText)
    {
        this.useTitleHierarchy = false;
        this.setTitle(titleKey);

        this.editWidget = new EdgeIntEditWidget(0, 0, 300, 100, value, isColor, centerText);

        this.setScreenWidthAndHeight(320, 130);
        this.centerOnScreen();
    }

    @Override
    protected void reAddActiveWidgets()
    {
        super.reAddActiveWidgets();

        this.addWidget(this.editWidget);
    }

    @Override
    protected void updateWidgetPositions()
    {
        super.updateWidgetPositions();

        this.editWidget.setPosition(this.x + 10, this.y + 26);
    }
}
