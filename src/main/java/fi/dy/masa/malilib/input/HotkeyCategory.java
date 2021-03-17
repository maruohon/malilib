package fi.dy.masa.malilib.input;

import java.util.List;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.malilib.util.data.ModInfo;

public class HotkeyCategory implements Comparable<HotkeyCategory>
{
    private final ModInfo modInfo;
    private final String categoryName;
    private final List<? extends Hotkey> hotkeys;

    public HotkeyCategory(ModInfo modInfo, String categoryName, List<? extends Hotkey> hotkeys)
    {
        this.modInfo = modInfo;
        this.categoryName = categoryName;
        this.hotkeys = hotkeys;
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
        return this.hotkeys;
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
        return this.hotkeys.equals(that.hotkeys);
    }

    @Override
    public int hashCode()
    {
        int result = this.modInfo.hashCode();
        result = 31 * result + this.categoryName.hashCode();
        result = 31 * result + this.hotkeys.hashCode();
        return result;
    }
}
