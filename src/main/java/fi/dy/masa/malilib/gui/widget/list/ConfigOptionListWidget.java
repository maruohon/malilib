package fi.dy.masa.malilib.gui.widget.list;

import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.config.option.ConfigInfo;
import fi.dy.masa.malilib.gui.config.BaseConfigScreen;
import fi.dy.masa.malilib.gui.config.elementplacer.ConfigElementPlacer;
import fi.dy.masa.malilib.gui.config.elementplacer.ConfigTypeRegistry;
import fi.dy.masa.malilib.gui.widget.list.entry.BaseDataListEntryWidget;
import fi.dy.masa.malilib.gui.widget.util.DataListEntryWidgetFactory;

public class ConfigOptionListWidget<C extends ConfigInfo> extends DataListWidget<C>
{
    protected final BaseConfigScreen gui;

    public ConfigOptionListWidget(int x, int y, int width, int height, Supplier<List<C>> entrySupplier, BaseConfigScreen gui)
    {
        super(x, y, width, height, entrySupplier);

        this.gui = gui;

        this.setEntryWidgetFactory(new ConfigOptionWidgetFactory<>(entrySupplier, gui));
    }

    @Override
    protected void reCreateListEntryWidgets()
    {
        super.reCreateListEntryWidgets();
    }

    public static class ConfigOptionWidgetFactory<C extends ConfigInfo> implements DataListEntryWidgetFactory<C>
    {
        protected final Supplier<List<C>> entrySupplier;
        protected final BaseConfigScreen gui;

        public ConfigOptionWidgetFactory(Supplier<List<C>> entrySupplier, BaseConfigScreen gui)
        {
            this.entrySupplier = entrySupplier;
            this.gui = gui;
        }

        @Override
        @Nullable
        public BaseDataListEntryWidget<C> createWidget(int x, int y, int width, int height, int listIndex,
                                                    C config, DataListWidget<C> listWidget)
        {
            List<C> list = this.entrySupplier.get();

            if (listIndex >= 0 && listIndex < list.size())
            {
                ConfigElementPlacer<C> placer = ConfigTypeRegistry.INSTANCE.getElementPlacer(config);
                return placer.createContainerWidget(x, y, width, height, listIndex, config, this.gui);
            }

            return null;
        }
    }
}
