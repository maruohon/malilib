package fi.dy.masa.malilib.gui.widget.button;

import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.option.StringListConfig;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.config.StringListEditScreen;
import fi.dy.masa.malilib.gui.util.DialogHandler;
import fi.dy.masa.malilib.gui.util.GuiUtils;
import fi.dy.masa.malilib.listener.EventListener;
import fi.dy.masa.malilib.util.StringUtils;

public class StringListEditButton extends GenericButton
{
    protected final StringListConfig config;
    @Nullable protected final DialogHandler dialogHandler;
    @Nullable protected final EventListener saveListener;

    public StringListEditButton(int x, int y, int width, int height, StringListConfig config,
                                @Nullable EventListener saveListener, @Nullable DialogHandler dialogHandler)
    {
        super(x, y, width, height, "");

        this.config = config;
        this.dialogHandler = dialogHandler;
        this.saveListener = saveListener;

        this.updateDisplayString();
    }

    @Override
    protected boolean onMouseClickedImpl(int mouseX, int mouseY, int mouseButton)
    {
        super.onMouseClickedImpl(mouseX, mouseY, mouseButton);

        if (this.dialogHandler != null)
        {
            this.dialogHandler.openDialog(new StringListEditScreen(this.config, this.saveListener, this.dialogHandler, null));
        }
        else
        {
            BaseScreen.openPopupGui(new StringListEditScreen(this.config, this.saveListener, null, GuiUtils.getCurrentScreen()));
        }

        return true;
    }

    @Override
    protected String generateDisplayString()
    {
        this.clearHoverStrings();

        ImmutableList<String> list = this.config.getStrings();
        int total = list.size();
        int max = Math.min(10, total);

        this.addHoverString("malilib.gui.button.hover.entries_total", total);

        for (int i = 0; i < max; ++i)
        {
            this.addHoverString("§7" + list.get(i));
        }

        if (total > max)
        {
            this.addHoverString("malilib.gui.button.hover.entries_more", total - max);
        }

        return StringUtils.getDisplayStringForList(this.config.getStrings(), this.getWidth() - 10, "'", "[ ", " ]");
    }
}
