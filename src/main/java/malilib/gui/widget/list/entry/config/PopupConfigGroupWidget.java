package malilib.gui.widget.list.entry.config;

import java.util.List;

import malilib.config.group.PopupConfigGroup;
import malilib.gui.BaseScreen;
import malilib.gui.config.BaseConfigGroupEditScreen;
import malilib.gui.config.ConfigWidgetContext;
import malilib.gui.util.GuiUtils;
import malilib.gui.widget.button.GenericButton;
import malilib.gui.widget.list.entry.DataListEntryWidgetData;
import malilib.render.text.StyledTextLine;

public class PopupConfigGroupWidget extends BaseConfigWidget<PopupConfigGroup>
{
    protected final PopupConfigGroup config;
    protected final GenericButton groupOpenButton;

    public PopupConfigGroupWidget(PopupConfigGroup config,
                                  DataListEntryWidgetData constructData,
                                  ConfigWidgetContext ctx)
    {
        super(config, constructData, ctx);

        this.config = config;
        this.groupOpenButton = GenericButton.create("malilib.button.config.popup_group.show_configs",
                                                    this::openConfigGroupEditScreen);
        this.groupOpenButton.getHoverInfoFactory().setTextLineProvider("config_list", this::getContainedConfigsHoverInfo);
        this.groupOpenButton.getHoverInfoFactory().setDynamic(false);

        this.getHoverInfoFactory().setTextLineProvider("config_list", this::getContainedConfigsHoverInfo);
        this.getHoverInfoFactory().setDynamic(false);
    }

    @Override
    public void reAddSubWidgets()
    {
        super.reAddSubWidgets();
        this.addWidget(this.groupOpenButton);
    }

    @Override
    public void updateSubWidgetPositions()
    {
        super.updateSubWidgetPositions();
        this.groupOpenButton.setPosition(this.getElementsStartPosition(), this.getY());
    }

    protected void openConfigGroupEditScreen()
    {
        BaseConfigGroupEditScreen screen = new BaseConfigGroupEditScreen(this.config.getModInfo(), null);
        screen.setScreenWidth(Math.max(520, GuiUtils.getScaledWindowWidth() - 80));
        screen.setConfigs(this.config.getConfigs());
        screen.setTitle(this.config.getDisplayName());
        screen.setParent(GuiUtils.getCurrentScreen());
        BaseScreen.openPopupScreen(screen);
    }

    protected List<StyledTextLine> getContainedConfigsHoverInfo()
    {
        return ExpandableConfigGroupWidget.getContainedConfigsHoverInfo(this.config.getConfigs());
    }
}
