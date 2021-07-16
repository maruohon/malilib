package fi.dy.masa.malilib.gui.widget.list.entry;

import javax.annotation.Nullable;
import fi.dy.masa.malilib.action.NamedAction;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.widget.list.DataListWidget;

public class ActionPromptNamedActionEntryWidget extends NamedActionEntryWidget
{
    public ActionPromptNamedActionEntryWidget(int x, int y, int width, int height, int listIndex,
                                              int originalListIndex, @Nullable NamedAction data,
                                              @Nullable DataListWidget<NamedAction> listWidget)
    {
        super(x, y, width, height, listIndex, originalListIndex, data, listWidget);
    }

    @Override
    protected boolean onMouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        // Close the current screen first, in case the action opens another screen
        BaseScreen.openScreen(null);
        this.data.execute();
        return true;
    }
}
