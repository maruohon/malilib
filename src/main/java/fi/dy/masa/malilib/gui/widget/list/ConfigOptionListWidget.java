package fi.dy.masa.malilib.gui.widget.list;

import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.config.option.ConfigInfo;
import fi.dy.masa.malilib.gui.config.BaseConfigScreen;
import fi.dy.masa.malilib.gui.config.ConfigOptionWidgetFactory;
import fi.dy.masa.malilib.gui.config.ConfigTypeRegistry;
import fi.dy.masa.malilib.gui.widget.list.entry.config.BaseConfigOptionWidget;
import fi.dy.masa.malilib.gui.widget.util.DataListEntryWidgetFactory;

public class ConfigOptionListWidget<C extends ConfigInfo> extends DataListWidget<C>
{
    protected final BaseConfigScreen gui;

    public ConfigOptionListWidget(int x, int y, int width, int height, Supplier<List<C>> entrySupplier, BaseConfigScreen gui)
    {
        super(x, y, width, height, entrySupplier);

        this.gui = gui;

        this.setEntryWidgetFactory(new ConfigOptionListEntryWidgetFactory<>(entrySupplier, gui));
        this.setEntryRefreshListener(gui);
    }

    @Override
    protected void reCreateListEntryWidgets()
    {
        super.reCreateListEntryWidgets();
    }

    public static class ConfigOptionListEntryWidgetFactory<C extends ConfigInfo> implements DataListEntryWidgetFactory<C>
    {
        protected final Supplier<List<C>> entrySupplier;
        protected final BaseConfigScreen gui;

        public ConfigOptionListEntryWidgetFactory(Supplier<List<C>> entrySupplier, BaseConfigScreen gui)
        {
            this.entrySupplier = entrySupplier;
            this.gui = gui;
        }

        @Override
        @Nullable
        public BaseConfigOptionWidget<C> createWidget(int x, int y, int width, int height, int listIndex, 
                                                      C config, DataListWidget<C> listWidget)
        {
            ConfigOptionWidgetFactory<C> factory = ConfigTypeRegistry.INSTANCE.getWidgetFactory(config);
            return factory.create(x, y, width, height, listIndex, config, this.gui);
        }
    }
}
