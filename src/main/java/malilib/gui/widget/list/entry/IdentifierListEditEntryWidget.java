package malilib.gui.widget.list.entry;

import net.minecraft.util.ResourceLocation;

public class IdentifierListEditEntryWidget extends BaseStringListEditEntryWidget<ResourceLocation>
{
    public IdentifierListEditEntryWidget(ResourceLocation data,
                                         DataListEntryWidgetData constructData,
                                         ResourceLocation defaultValue)
    {
        super(data, constructData, defaultValue, ResourceLocation::toString, ResourceLocation::new);
        this.newEntryFactory = () -> new ResourceLocation("minecraft:foo");
    }
}
