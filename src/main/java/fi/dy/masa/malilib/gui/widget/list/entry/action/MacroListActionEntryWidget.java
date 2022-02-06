package fi.dy.masa.malilib.gui.widget.list.entry.action;

import javax.annotation.Nullable;
import fi.dy.masa.malilib.action.NamedAction;
import fi.dy.masa.malilib.gui.widget.list.DataListWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.BaseOrderableListEditEntryWidget;
import fi.dy.masa.malilib.render.text.StyledTextLine;
import fi.dy.masa.malilib.util.StyledTextUtils;
import fi.dy.masa.malilib.util.data.LeftRight;

public class MacroListActionEntryWidget extends BaseOrderableListEditEntryWidget<NamedAction>
{
    public MacroListActionEntryWidget(int x, int y, int width, int height, int listIndex,
                                      int originalListIndex, @Nullable NamedAction data,
                                      @Nullable DataListWidget<NamedAction> listWidget)
    {
        super(x, y, width, height, listIndex, originalListIndex, data, listWidget);

        this.useAddButton = false;

        this.getBorderRenderer().getHoverSettings().setBorderWidthAndColor(1, 0xFF00FF60);
        this.getBackgroundRenderer().getHoverSettings().setEnabled(false);

        StyledTextLine fullName = data.getWidgetDisplayName();
        this.setText(StyledTextUtils.clampStyledTextToMaxWidth(fullName, width - 16, LeftRight.RIGHT, " ..."));

        this.getHoverInfoFactory().setTextLineProvider("action_info", data::getHoverInfo);
    }
}
