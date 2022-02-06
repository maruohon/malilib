package fi.dy.masa.malilib.gui.widget.list.entry.action;

import javax.annotation.Nullable;
import fi.dy.masa.malilib.action.ActionRegistry;
import fi.dy.masa.malilib.action.NamedAction;
import fi.dy.masa.malilib.gui.widget.list.DataListWidget;

public class AliasActionEntryWidget extends RemovableActionEntryWidget<NamedAction>
{
    public AliasActionEntryWidget(int x, int y, int width, int height, int listIndex,
                                  int originalListIndex, @Nullable NamedAction data,
                                  @Nullable DataListWidget<NamedAction> listWidget)
    {
        super(x, y, width, height, listIndex, originalListIndex, data, listWidget);

        this.setActionRemoveFunction(ActionRegistry::removeAlias);
    }
}
