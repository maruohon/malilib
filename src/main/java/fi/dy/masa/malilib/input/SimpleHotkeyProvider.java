package fi.dy.masa.malilib.input;

import java.util.List;
import java.util.function.Supplier;
import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.util.data.ModInfo;

public class SimpleHotkeyProvider implements HotkeyProvider
{
    protected final ModInfo modInfo;
    protected final String categoryName;
    protected final Supplier<List<? extends Hotkey>> hotkeySupplier;

    public SimpleHotkeyProvider(ModInfo modInfo, String categoryName, Supplier<List<? extends Hotkey>> hotkeySupplier)
    {
        this.modInfo = modInfo;
        this.categoryName = categoryName;
        this.hotkeySupplier = hotkeySupplier;
    }

    @Override
    public List<? extends Hotkey> getAllHotkeys()
    {
        return this.hotkeySupplier.get();
    }

    @Override
    public List<HotkeyCategory> getHotkeysByCategories()
    {
        return ImmutableList.of(new HotkeyCategory(this.modInfo, this.categoryName , this.getAllHotkeys()));
    }
}
