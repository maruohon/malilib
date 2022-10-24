package malilib.input;

import java.util.List;
import java.util.function.Supplier;

import malilib.util.StringUtils;
import malilib.util.data.ModInfo;

public class HotkeyCategory implements Comparable<HotkeyCategory>
{
    protected final ModInfo modInfo;
    protected final String categoryName;
    protected final Supplier<List<? extends Hotkey>> hotkeySupplier;

    public HotkeyCategory(ModInfo modInfo, String categoryName, List<? extends Hotkey> hotkeys)
    {
        this(modInfo, categoryName, () -> hotkeys);
    }

    public HotkeyCategory(ModInfo modInfo, String categoryName, Supplier<List<? extends Hotkey>> hotkeySupplier)
    {
        this.modInfo = modInfo;
        this.categoryName = categoryName;
        this.hotkeySupplier = hotkeySupplier;
    }

    public ModInfo getModInfo()
    {
        return this.modInfo;
    }

    public String getCategoryName()
    {
        return StringUtils.translate(this.categoryName);
    }

    public List<? extends Hotkey> getHotkeys()
    {
        return this.hotkeySupplier.get();
    }

    @Override
    public int compareTo(HotkeyCategory other)
    {
        int val = this.modInfo.getModId().compareTo(other.modInfo.getModId());

        if (val != 0)
        {
            return val;
        }

        return this.categoryName.compareTo(other.categoryName);
    }


    @Override
    public boolean equals(Object o)
    {
        if (this == o) { return true; }
        if (o == null || this.getClass() != o.getClass()) { return false; }

        HotkeyCategory that = (HotkeyCategory) o;

        if (!this.modInfo.equals(that.modInfo)) { return false; }
        if (!this.categoryName.equals(that.categoryName)) { return false; }
        return this.hotkeySupplier.get().equals(that.hotkeySupplier.get());
    }

    @Override
    public int hashCode()
    {
        int result = this.modInfo.hashCode();
        result = 31 * result + this.categoryName.hashCode();
        result = 31 * result + this.hotkeySupplier.get().hashCode();
        return result;
    }
}
