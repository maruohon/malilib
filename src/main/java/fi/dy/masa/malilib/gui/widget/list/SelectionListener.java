package fi.dy.masa.malilib.gui.widget.list;

import javax.annotation.Nullable;

public interface SelectionListener<T>
{
    void onSelectionChange(@Nullable T entry);
}
