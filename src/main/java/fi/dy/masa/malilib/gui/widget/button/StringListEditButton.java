package fi.dy.masa.malilib.gui.widget.button;

import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiScreen;
import fi.dy.masa.malilib.config.option.StringListConfig;
import fi.dy.masa.malilib.gui.config.BaseValueListEditScreen;
import fi.dy.masa.malilib.gui.util.DialogHandler;
import fi.dy.masa.malilib.gui.widget.list.entry.StringListEditEntryWidget;
import fi.dy.masa.malilib.listener.EventListener;
import fi.dy.masa.malilib.util.StringUtils;

public class StringListEditButton extends BaseValueListEditButton<String>
{
    public StringListEditButton(int x, int y, int width, int height, StringListConfig config,
                                @Nullable EventListener saveListener, @Nullable DialogHandler dialogHandler)
    {
        super(x, y, width, height, config, saveListener, dialogHandler);
    }

    @Override
    protected BaseValueListEditScreen<String> createScreen(@Nullable DialogHandler dialogHandler, @Nullable GuiScreen currentScreen)
    {
        String title = StringUtils.translate("malilib.gui.title.string_list_edit", this.config.getDisplayName());

        return new BaseValueListEditScreen<>(this.config, this.saveListener, dialogHandler, currentScreen,
                                             title, () -> "", StringListEditEntryWidget::new);
    }
}
