package fi.dy.masa.malilib.gui.widgets;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.ConfigType;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.gui.GuiConfigsBase.ConfigOptionWrapper;
import fi.dy.masa.malilib.gui.LeftRight;
import fi.dy.masa.malilib.gui.MaLiLibIcons;
import fi.dy.masa.malilib.hotkeys.IHotkey;
import fi.dy.masa.malilib.hotkeys.IKeybind;
import fi.dy.masa.malilib.util.AlphaNumComparator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

public class WidgetListConfigOptions extends WidgetListConfigOptionsBase<ConfigOptionWrapper, WidgetConfigOption>
{
    protected final GuiConfigsBase parent;
    protected final WidgetSearchBarConfigs widgetSearchConfigs;

    public WidgetListConfigOptions(int x, int y, int width, int height, int configWidth, float zLevel, boolean useKeybindSearch, GuiConfigsBase parent)
    {
        super(x, y, width, height, configWidth);

        this.parent = parent;

        if (useKeybindSearch)
        {
            this.widgetSearchConfigs = new WidgetSearchBarConfigs(x + 2, y + 4, width - 14, 20, zLevel, 0, MaLiLibIcons.SEARCH, LeftRight.LEFT, Minecraft.getMinecraft());
            this.widgetSearchBar = this.widgetSearchConfigs;
            this.browserEntriesOffsetY = 23;
        }
        else
        {
            this.widgetSearchConfigs = null;
            this.widgetSearchBar = new WidgetSearchBar(x + 2, y + 4, width - 14, 14, zLevel, 0, MaLiLibIcons.SEARCH, LeftRight.LEFT, Minecraft.getMinecraft());
            this.browserEntriesOffsetY = 17;
        }
    }

    @Override
    protected Collection<ConfigOptionWrapper> getAllEntries()
    {
        return this.parent.getConfigs();
    }

    @Override
    protected void reCreateListEntryWidgets()
    {
        this.maxLabelWidth = getMaxNameLengthWrapped(this.listContents);
        super.reCreateListEntryWidgets();
    }

    @Override
    protected List<String> getEntryStringsForFilter(ConfigOptionWrapper entry)
    {
        IConfigBase config = entry.getConfig();

        if (config != null)
        {
            return ImmutableList.of(config.getName().toLowerCase());
        }

        return Collections.emptyList();
    }

    @Override
    protected void addFilteredContents(Collection<ConfigOptionWrapper> entries)
    {
        if (this.widgetSearchConfigs != null)
        {
            String filterText = this.widgetSearchConfigs.getFilter();
            IKeybind filterKeys = this.widgetSearchConfigs.getKeybind();

            for (ConfigOptionWrapper entry : entries)
            {
                if ((filterText.isEmpty() || this.entryMatchesFilter(entry, filterText)) &&
                    (entry.getConfig().getType() != ConfigType.HOTKEY ||
                     filterKeys.getKeys().size() == 0 ||
                     ((IHotkey) entry.getConfig()).getKeybind().overlaps(filterKeys)))
                {
                    this.listContents.add(entry);
                }
            }
        }
        else
        {
            super.addFilteredContents(entries);
        }
    }

    @Override
    protected Comparator<ConfigOptionWrapper> getComparator()
    {
        return new ConfigComparator();
    }

    @Override
    protected WidgetConfigOption createListEntryWidget(int x, int y, int listIndex, boolean isOdd, ConfigOptionWrapper wrapper)
    {
        return new WidgetConfigOption(x, y, this.browserEntryWidth, this.browserEntryHeight, this.zLevel,
                this.maxLabelWidth, this.configWidth, wrapper, listIndex, this.parent, this.mc, this);
    }

    public static int getMaxNameLengthWrapped(List<ConfigOptionWrapper> wrappers)
    {
        FontRenderer font = Minecraft.getMinecraft().fontRenderer;
        int width = 0;

        for (ConfigOptionWrapper wrapper : wrappers)
        {
            if (wrapper.getType() == ConfigOptionWrapper.Type.CONFIG)
            {
                width = Math.max(width, font.getStringWidth(wrapper.getConfig().getName()));
            }
        }

        return width;
    }

    protected static class ConfigComparator extends AlphaNumComparator implements Comparator<ConfigOptionWrapper>
    {
        @Override
        public int compare(ConfigOptionWrapper config1, ConfigOptionWrapper config2)
        {
            return this.compare(config1.getConfig().getName(), config2.getConfig().getName());
        }
    }
}
