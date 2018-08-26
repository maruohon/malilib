package fi.dy.masa.malilib.gui.interfaces;

import javax.annotation.Nullable;

public interface ISelectionListener<T>
{
    void onSelectionChange(@Nullable T entry);
}
