package malilib.gui;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

import malilib.gui.tab.ScreenTab;
import malilib.gui.widget.list.BaseListWidget;

public class BaseMultiListScreen extends BaseTabbedScreen
{
    protected final ArrayList<BaseListWidget> listWidgets = new ArrayList<>();

    public BaseMultiListScreen(String screenId, List<? extends ScreenTab> screenTabs, @Nullable ScreenTab defaultTab)
    {
        super(screenId, screenTabs, defaultTab);

        this.shouldCreateTabButtons = screenTabs.isEmpty() == false;
        this.addPostInitListener(this::refreshListWidgets);
        this.addPreScreenCloseListener(this::closeListWidgets);
    }

    @Override
    protected void clearElements()
    {
        super.clearElements();
        this.listWidgets.clear();
    }

    protected void addListWidget(BaseListWidget widget)
    {
        widget.setTaskQueue(this::addTask);
        widget.setZ(this.z + 10);
        widget.initListWidget();

        this.listWidgets.add(widget);
        this.addWidget(widget);
    }

    protected void refreshListWidgets()
    {
        for (BaseListWidget listWidget : this.listWidgets)
        {
            listWidget.refreshEntries();
        }
    }

    protected void closeListWidgets()
    {
        for (BaseListWidget listWidget : this.listWidgets)
        {
            listWidget.onScreenClosed();
        }
    }
}
