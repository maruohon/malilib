package fi.dy.masa.malilib.gui.widgets;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.gui.GuiConfigsBase.ConfigOptionWrapper;
import fi.dy.masa.malilib.gui.LeftRight;
import fi.dy.masa.malilib.gui.MaLiLibIcons;
import fi.dy.masa.malilib.util.AlphaNumComparator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

public class WidgetListConfigOptions extends WidgetListConfigOptionsBase<ConfigOptionWrapper, WidgetConfigOption>
{
    protected final GuiConfigsBase parent;

    public WidgetListConfigOptions(int x, int y, int width, int height, int configWidth, float zLevel, GuiConfigsBase parent)
    {
        super(x, y, width, height, configWidth);

        this.parent = parent;
        this.widgetSearchBar = new WidgetSearchBar(x + 2, y + 4, width - 14, 14, zLevel, 0, MaLiLibIcons.SEARCH, LeftRight.LEFT, Minecraft.getMinecraft());
        this.browserEntriesOffsetY = this.widgetSearchBar.getHeight() + 3;
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
    protected boolean entryMatchesFilter(ConfigOptionWrapper entry, String filterText)
    {
        IConfigBase config = entry.getConfig();
        return config == null || config.getName().toLowerCase().indexOf(filterText) != -1;
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
