package fi.dy.masa.malilib.gui.interfaces;

import org.jetbrains.annotations.Nullable;

public interface ISelectionListener<T>
{
    void onSelectionChange(@Nullable T entry);
}
