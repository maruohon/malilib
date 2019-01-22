package fi.dy.masa.malilib.gui.widgets;

import java.util.List;
import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.gui.GuiConfigsBase.ConfigOptionWrapper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.FontRenderer;

public class WidgetListConfigOptions extends WidgetListConfigOptionsBase<ConfigOptionWrapper, WidgetConfigOption>
{
    protected final GuiConfigsBase parent;

    public WidgetListConfigOptions(int x, int y, int width, int height, int configWidth, GuiConfigsBase parent)
    {
        super(x, y, width, height, configWidth);

        this.parent = parent;
    }

    @Override
    public void refreshEntries()
    {
        this.listContents.clear();
        this.listContents.addAll(this.parent.getConfigs());
        this.maxLabelWidth = getMaxNameLengthWrapped(this.listContents);

        this.reCreateListEntryWidgets();
    }

    @Override
    protected WidgetConfigOption createListEntryWidget(int x, int y, int listIndex, boolean isOdd, ConfigOptionWrapper wrapper)
    {
        return new WidgetConfigOption(x, y, this.browserEntryWidth, this.browserEntryHeight, this.zOffset,
                this.maxLabelWidth, this.configWidth, wrapper, this.parent, this.client, this);
    }

    public static int getMaxNameLengthWrapped(List<ConfigOptionWrapper> wrappers)
    {
        FontRenderer font = MinecraftClient.getInstance().fontRenderer;
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
}
