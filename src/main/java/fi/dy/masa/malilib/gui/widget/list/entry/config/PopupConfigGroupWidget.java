package fi.dy.masa.malilib.gui.widget.list.entry.config;

import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.config.BaseConfigGroupEditScreen;
import fi.dy.masa.malilib.gui.config.ConfigWidgetContext;
import fi.dy.masa.malilib.gui.config.PopupConfigGroup;
import fi.dy.masa.malilib.gui.util.GuiUtils;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;

public class PopupConfigGroupWidget extends BaseConfigWidget<PopupConfigGroup>
{
    protected final PopupConfigGroup config;
    protected final GenericButton groupOpenButton;

    public PopupConfigGroupWidget(int x, int y, int width, int height, int listIndex, int originalListIndex,
                                  PopupConfigGroup config, ConfigWidgetContext ctx)
    {
        super(x, y, width, height, listIndex, originalListIndex, config, ctx);

        this.config = config;

        this.groupOpenButton = GenericButton.simple("malilib.gui.button.label.show_configs",
                                                    this::openConfigGroupEditScreen);
    }

    @Override
    public void reAddSubWidgets()
    {
        super.reAddSubWidgets();

        this.addWidget(this.groupOpenButton);
    }

    @Override
    public void updateSubWidgetsToGeometryChanges()
    {
        super.updateSubWidgetsToGeometryChanges();

        int x = this.getElementsStartPosition();
        int y = this.getY();
        this.groupOpenButton.setPosition(x, y);
    }

    protected void openConfigGroupEditScreen()
    {
        BaseConfigGroupEditScreen screen = new BaseConfigGroupEditScreen(this.config.getModInfo(),
                                                                         null, null, GuiUtils.getCurrentScreen());
        screen.setConfigs(this.config.getConfigs());
        screen.setTitle(this.config.getDisplayName());
        BaseScreen.openPopupScreen(screen);
    }
}
