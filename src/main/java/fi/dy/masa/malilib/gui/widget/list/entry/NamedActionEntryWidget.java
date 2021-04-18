package fi.dy.masa.malilib.gui.widget.list.entry;

import javax.annotation.Nullable;
import fi.dy.masa.malilib.action.NamedAction;
import fi.dy.masa.malilib.gui.widget.list.DataListWidget;
import fi.dy.masa.malilib.render.text.StyledTextLine;
import fi.dy.masa.malilib.util.StyledTextUtils;
import fi.dy.masa.malilib.util.data.LeftRight;

public class NamedActionEntryWidget extends BaseDataListEntryWidget<NamedAction>
{
    protected final StyledTextLine nameText;

    public NamedActionEntryWidget(int x, int y, int width, int height, int listIndex,
                                              int originalListIndex, @Nullable NamedAction data,
                                              @Nullable DataListWidget<? extends NamedAction> listWidget)
    {
        super(x, y, width, height, listIndex, originalListIndex, data, listWidget);

        this.renderHoverBackground = false;
        this.setHoveredBorderWidth(1);
        this.setHoveredBorderColor(0xFFFF6000);

        String name = data.getName();
        String modName = data.getMod().getModName();
        StyledTextLine fullName = StyledTextLine.translatedOf("malilib.label.named_action_entry_widget.name",
                                                              name, modName);
        this.nameText = StyledTextUtils.clampStyledTextToMaxWidth(fullName, width - 16, LeftRight.RIGHT, " ...");

        this.getHoverInfoFactory().setStringListProvider("action_info", data::getHoverInfo);
    }

    @Override
    public void renderAt(int x, int y, float z, int mouseX, int mouseY, boolean isActiveGui, boolean hovered)
    {
        super.renderAt(x, y, z, mouseX, mouseY, isActiveGui, hovered);

        int ty = y + this.getHeight() / 2 - this.fontHeight / 2;
        this.renderTextLine(x + 4, ty, z + 0.1f, 0xFFFFFFFF, true, this.nameText);
    }
}
