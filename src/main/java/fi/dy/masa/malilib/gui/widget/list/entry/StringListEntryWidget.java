package fi.dy.masa.malilib.gui.widget.list.entry;

import fi.dy.masa.malilib.render.text.StyledTextLine;

public class StringListEntryWidget extends BaseDataListEntryWidget<String>
{
    public StringListEntryWidget(String data, DataListEntryWidgetData constructData)
    {
        super(data, constructData);

        this.setText(StyledTextLine.raw(data));
        this.getBackgroundRenderer().getNormalSettings().setColor(this.isOdd ? 0xA0101010 : 0xA0303030);
        this.getBackgroundRenderer().getHoverSettings().setColor(0xA0707070);
        this.setSelectedBackgroundColor(0xA0707070);
    }
}
