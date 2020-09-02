package fi.dy.masa.malilib.gui.widget.button;

import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.gui.GuiScreen;
import fi.dy.masa.malilib.config.option.ValueListConfig;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.config.BaseValueListEditScreen;
import fi.dy.masa.malilib.gui.util.DialogHandler;
import fi.dy.masa.malilib.gui.util.GuiUtils;
import fi.dy.masa.malilib.listener.EventListener;
import fi.dy.masa.malilib.util.StringUtils;

public abstract class BaseValueListEditButton<TYPE> extends GenericButton
{
    protected final ValueListConfig<TYPE> config;
    @Nullable protected final DialogHandler dialogHandler;
    @Nullable protected final EventListener saveListener;

    public BaseValueListEditButton(int x, int y, int width, int height, ValueListConfig<TYPE> config,
                                   @Nullable EventListener saveListener, @Nullable DialogHandler dialogHandler)
    {
        super(x, y, width, height, "");

        this.config = config;
        this.dialogHandler = dialogHandler;
        this.saveListener = saveListener;

        this.updateDisplayString();
    }

    protected abstract BaseValueListEditScreen<TYPE> createScreen(@Nullable DialogHandler dialogHandler, @Nullable GuiScreen currentScreen);

    @Override
    protected boolean onMouseClickedImpl(int mouseX, int mouseY, int mouseButton)
    {
        super.onMouseClickedImpl(mouseX, mouseY, mouseButton);

        if (this.dialogHandler != null)
        {
            this.dialogHandler.openDialog(this.createScreen(this.dialogHandler, null));
        }
        else
        {
            BaseScreen.openPopupGui(this.createScreen(null, GuiUtils.getCurrentScreen()));
        }

        return true;
    }

    @Override
    protected String generateDisplayString()
    {
        this.clearHoverStrings();

        ImmutableList<String> strings = this.config.getValuesAsString();
        int total = strings.size();
        int max = Math.min(10, total);

        this.addHoverString("malilib.gui.button.hover.entries_total", total);

        for (int i = 0; i < max; ++i)
        {
            this.addHoverString("§7" + strings.get(i));
        }

        if (total > max)
        {
            this.addHoverString("malilib.gui.button.hover.entries_more", total - max);
        }

        return StringUtils.getDisplayStringForList(strings, this.getWidth() - 10, "'", "[ ", " ]");
    }
}
