package fi.dy.masa.malilib.gui.widget.list.entry;

import fi.dy.masa.malilib.gui.widget.list.DataListWidget;
import fi.dy.masa.malilib.render.text.StyledTextLine;

public class StringListEntryWidget extends BaseDataListEntryWidget<String>
{
    public StringListEntryWidget(int x, int y, int width, int height, int listIndex, int originalListIndex,
                                 String entry, DataListWidget<String> listWidget)
    {
        super(x, y, width, height, listIndex, originalListIndex, entry, listWidget);

        this.setText(StyledTextLine.raw(entry));
        this.setNormalBackgroundColor(this.isOdd ? 0xA0101010 : 0xA0303030);
        this.setHoveredBackgroundColor(0xA0707070);
        this.setSelectedBackgroundColor(0xA0707070);
    }
}
