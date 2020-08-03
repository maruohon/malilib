package fi.dy.masa.malilib.gui.widget;

public class WidgetListConfigOptions// extends WidgetListConfigOptionsBase<ConfigOptionWrapper>
{
    /*
    protected final BaseConfigScreen parent;
    protected final WidgetSearchBarConfigs widgetSearchConfigs;

    public WidgetListConfigOptions(int x, int y, int width, int height, int configWidth, float zLevel, boolean useKeybindSearch, BaseConfigScreen parent)
    {
        super(x, y, width, height, configWidth);

        this.parent = parent;

        if (useKeybindSearch)
        {
            this.widgetSearchConfigs = new WidgetSearchBarConfigs(x + 2, y + 1, width - 14, 20, 0, BaseGuiIcon.SEARCH, HorizontalAlignment.LEFT);
            this.addSearchBarWidget(this.widgetSearchConfigs);
        }
        else
        {
            this.widgetSearchConfigs = null;
            this.addSearchBarWidget(new WidgetSearchBar(x + 2, y + 4, width - 14, 14, 0, BaseGuiIcon.SEARCH, HorizontalAlignment.LEFT));
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
        this.maxLabelWidth = this.getMaxNameLengthWrapped(this.listContents);
        super.reCreateListEntryWidgets();
    }

    @Override
    protected List<String> getEntryStringsForFilter(ConfigOptionWrapper entry)
    {
        ConfigOption config = entry.getConfig();

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
            IKeyBind filterKeys = this.widgetSearchConfigs.getKeybind();

            for (ConfigOptionWrapper entry : entries)
            {
                if (entry.getType() == ConfigOptionWrapper.Type.CONFIG &&
                    (filterText.isEmpty() || this.entryMatchesFilter(entry, filterText)) &&
                    (entry.getConfig().getType() != ConfigType.HOTKEY ||
                     filterKeys.getKeys().size() == 0 ||
                     ((IHotkey) entry.getConfig()).getKeyBind().overlaps(filterKeys)))
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
        return new WidgetConfigOption(x, y, this.entryWidgetWidth, this.entryWidgetFixedHeight,
                                      this.maxLabelWidth, this.configWidth, wrapper, listIndex, this.parent, this);
    }

    public int getMaxNameLengthWrapped(List<ConfigOptionWrapper> wrappers)
    {
        int width = 0;

        for (ConfigOptionWrapper wrapper : wrappers)
        {
            if (wrapper.getType() == ConfigOptionWrapper.Type.CONFIG)
            {
                width = Math.max(width, this.getStringWidth(wrapper.getConfig().getName()));
            }
        }

        return width;
    }

    protected static class ConfigComparator extends AlphaNumComparator implements Comparator<ConfigOptionWrapper>
    {
        @Override
        public int compare(ConfigOptionWrapper config1, ConfigOptionWrapper config2)
        {
            if (config1.getType() != ConfigOptionWrapper.Type.CONFIG)
            {
                return 1;
            }

            if (config2.getType() != ConfigOptionWrapper.Type.CONFIG)
            {
                return -1;
            }

            return this.compare(config1.getConfig().getName(), config2.getConfig().getName());
        }
    }
    */
}
