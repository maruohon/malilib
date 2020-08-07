package fi.dy.masa.malilib.input;

import java.util.List;
import fi.dy.masa.malilib.util.StringUtils;

public class KeyBindCategory implements Comparable<KeyBindCategory>
{
    private final String modId;
    private final String modName;
    private final String categoryName;
    private final List<? extends Hotkey> hotkeys;

    public KeyBindCategory(String modId, String modName, String categoryName, List<? extends Hotkey> hotkeys)
    {
        this.modId = modId;
        this.modName = modName;
        this.categoryName = categoryName;
        this.hotkeys = hotkeys;
    }

    public String getModId()
    {
        return this.modId;
    }

    public String getModName()
    {
        return this.modName;
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
    public int compareTo(KeyBindCategory other)
    {
        int val = this.modId.compareTo(other.modId);

        if (val != 0)
        {
            return val;
        }

        return this.categoryName.compareTo(other.categoryName);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        KeyBindCategory other = (KeyBindCategory) obj;
        if (categoryName == null)
        {
            if (other.categoryName != null)
                return false;
        }
        else if (!categoryName.equals(other.categoryName))
            return false;
        if (modId == null)
        {
            if (other.modId != null)
                return false;
        }
        else if (!modId.equals(other.modId))
            return false;
        return true;
    }
}
