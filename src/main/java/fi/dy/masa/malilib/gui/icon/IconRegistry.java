package fi.dy.masa.malilib.gui.icon;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.google.common.collect.ImmutableList;

public class IconRegistry
{
    public static final IconRegistry INSTANCE = new IconRegistry();

    protected final List<Icon> modIcons = new ArrayList<>();
    protected final List<Icon> userIcons = new ArrayList<>();
    protected final Map<String, Icon> iconMap = new HashMap<>();
    protected ImmutableList<Icon> allIcons = ImmutableList.of();
    protected boolean dirty;
    protected boolean needsRebuild = true;

    public void registerModIcon(Icon icon)
    {
        if (this.modIcons.contains(icon) == false)
        {
            this.modIcons.add(icon);
            this.needsRebuild = true;
        }
    }

    public void registerUserIcon(Icon icon)
    {
        if (this.userIcons.contains(icon) == false)
        {
            this.userIcons.add(icon);
            this.markDirty();
        }
    }

    public void unregisterModIcon(Icon icon)
    {
        this.modIcons.remove(icon);
        this.needsRebuild = true;
    }

    public void unregisterUserIcon(Icon icon)
    {
        this.userIcons.remove(icon);
        this.markDirty();
    }

    protected void markDirty()
    {
        this.dirty = true;
        this.needsRebuild = true;
    }

    public ImmutableList<Icon> getAllIcons()
    {
        this.updateAllIconsList();
        return this.allIcons;
    }

    public Icon getIconByKey(String key)
    {
        this.updateAllIconsList();
        return this.iconMap.getOrDefault(key, DefaultIcons.EMPTY);
    }

    protected void updateAllIconsList()
    {
        if (this.needsRebuild)
        {
            List<Icon> icons = new ArrayList<>();

            icons.addAll(this.modIcons);
            icons.addAll(this.userIcons);
            icons.sort(Comparator.comparing(i -> i.getTexture().toString()));

            this.allIcons = ImmutableList.copyOf(icons);

            this.iconMap.clear();

            for (Icon icon : this.allIcons)
            {
                String key = getKeyForIcon(icon);
                this.iconMap.put(key, icon);
            }

            this.needsRebuild = false;
        }
    }

    public static String getKeyForIcon(Icon icon)
    {
        return String.format("%s_%d_%d_%d_%d", icon.getTexture().toString(),
                             icon.getU(), icon.getV(), icon.getWidth(), icon.getHeight());
    }
}
