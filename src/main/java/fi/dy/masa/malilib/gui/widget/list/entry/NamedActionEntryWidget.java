package fi.dy.masa.malilib.gui.widget.list.entry;

import javax.annotation.Nullable;
import fi.dy.masa.malilib.action.NamedAction;
import fi.dy.masa.malilib.gui.widget.ScreenContext;
import fi.dy.masa.malilib.gui.widget.list.DataListWidget;
import fi.dy.masa.malilib.render.text.StyledTextLine;
import fi.dy.masa.malilib.util.StyledTextUtils;
import fi.dy.masa.malilib.util.data.LeftRight;

public class NamedActionEntryWidget extends BaseOrderableListEditEntryWidget<NamedAction>
{
    protected final StyledTextLine nameText;

    public NamedActionEntryWidget(int x, int y, int width, int height, int listIndex,
                                              int originalListIndex, @Nullable NamedAction data,
                                              @Nullable DataListWidget<NamedAction> listWidget)
    {
        super(x, y, width, height, listIndex, originalListIndex, data, listWidget);

        this.canReOrder = false;
        this.useAddButton = false;
        this.useRemoveButton = false;
        this.useMoveButtons = false;

        this.renderHoverBackground = false;
        this.setHoveredBorderWidth(1);
        this.setHoveredBorderColor(0xFF00FF60);

        StyledTextLine fullName = StyledTextLine.of(data.getWidgetDisplayName());
        this.nameText = StyledTextUtils.clampStyledTextToMaxWidth(fullName, width - 16, LeftRight.RIGHT, " ...");

        this.getHoverInfoFactory().setStringListProvider("action_info", data::getHoverInfo);
    }

    @Override
    public void renderAt(int x, int y, float z, ScreenContext ctx)
    {
        super.renderAt(x, y, z, ctx);

        int ty = y + this.getHeight() / 2 - this.fontHeight / 2;
        this.renderTextLine(x + 4, ty, z + 0.1f, 0xFFFFFFFF, true, ctx, this.nameText);
    }
}
