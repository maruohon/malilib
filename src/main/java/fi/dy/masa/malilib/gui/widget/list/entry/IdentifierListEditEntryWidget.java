package fi.dy.masa.malilib.gui.widget.list.entry;

import net.minecraft.util.ResourceLocation;
import fi.dy.masa.malilib.gui.widget.list.DataListWidget;

public class IdentifierListEditEntryWidget extends BaseStringListEditEntryWidget<ResourceLocation>
{
    public IdentifierListEditEntryWidget(int x, int y, int width, int height, int listIndex, int originalListIndex,
                                         ResourceLocation initialValue, ResourceLocation defaultValue,
                                         DataListWidget<ResourceLocation> listWidget)
    {
        super(x, y, width, height, listIndex, originalListIndex, initialValue, defaultValue,
              ResourceLocation::toString, ResourceLocation::new, listWidget);
    }

    @Override
    protected ResourceLocation getNewDataEntry()
    {
        return new ResourceLocation("minecraft:foo");
    }
}
