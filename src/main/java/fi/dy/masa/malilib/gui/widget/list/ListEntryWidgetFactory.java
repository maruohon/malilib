package fi.dy.masa.malilib.gui.widget.list;

import java.util.function.Consumer;
import fi.dy.masa.malilib.gui.widget.list.entry.BaseListEntryWidget;

public interface ListEntryWidgetFactory
{
    /**
     * @return The total number of unique widgets that the list widget will/can contain.
     * This is used for calculating the scroll bar size and other related things.
     * This does not need to correspond with the size of the backing data list,
     * if there is a custom factory (what this interface defines) being used.
     * An example of a use case where the list sizes may be different, is having some
     * kind of category titles/headers mixed with the data widgets.
     */
    int getTotalListWidgetCount();

    /**
     * Creates the entry widgets starting from the visible/scroll index {@code startIndex},
     * creating as many widgets as can fit to the given space (usually height) {@code usableSpace}.
     * @param startX The starting X position
     * @param startY The starting Y position
     * @param usableSpace The usable space, usually the height of the list entry widget area
     * @param startIndex the starting index of the visible widgets
     * @param widgetConsumer the consumer that adds the widgets to the backing list widget
     */
    void createEntryWidgets(int startX, int startY, int usableSpace,
                            int startIndex, Consumer<BaseListEntryWidget> widgetConsumer);
}
