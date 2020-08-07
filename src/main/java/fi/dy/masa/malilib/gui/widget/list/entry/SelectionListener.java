package fi.dy.masa.malilib.gui.widget.list.entry;

import javax.annotation.Nullable;

public interface SelectionListener<T>
{
    void onSelectionChange(@Nullable T entry);
}
