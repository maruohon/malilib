package fi.dy.masa.malilib.gui.widget.button;

import java.util.List;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.config.option.BlackWhiteListConfig;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.config.BlackWhiteListEditScreen;
import fi.dy.masa.malilib.gui.util.DialogHandler;
import fi.dy.masa.malilib.gui.util.GuiUtils;
import fi.dy.masa.malilib.listener.EventListener;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.malilib.util.restriction.UsageRestriction.ListType;

public class BlackWhiteListEditButton extends GenericButton
{
    protected final BlackWhiteListConfig<?, ?> config;
    protected final EventListener saveListener;
    @Nullable protected final DialogHandler dialogHandler;

    public BlackWhiteListEditButton(int x, int y, int width, int height, BlackWhiteListConfig<?, ?> config,
                                    EventListener saveListener, @Nullable DialogHandler dialogHandler)
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
            this.dialogHandler.openDialog(new BlackWhiteListEditScreen<>(this.config, this.saveListener, this.dialogHandler, null));
        }
        else
        {
            BaseScreen.openPopupGui(new BlackWhiteListEditScreen<>(this.config, this.saveListener, null, GuiUtils.getCurrentScreen()));
        }

        return true;
    }

    @Override
    protected String generateDisplayString()
    {
        this.clearHoverStrings();

        ListType type = this.config.getValue().getListType();

        if (type == ListType.NONE)
        {
            return StringUtils.translate("malilib.gui.button.black_white_list_edit.none");
        }
        else
        {
            List<String> list = this.config.getValue().getActiveListAsString();
            int total = list.size();
            int max = Math.min(10, total);

            this.addHoverString("malilib.gui.button.hover.black_white_list_edit.type", type.getDisplayName());
            this.addHoverString("malilib.gui.button.hover.entries_total", total);

            for (int i = 0; i < max; ++i)
            {
                this.addHoverString("§7" + list.get(i));
            }

            if (total > max)
            {
                this.addHoverString("malilib.gui.button.hover.entries_more", total - max);
            }

            return StringUtils.translate("malilib.gui.button.black_white_list_edit.entries", type.getDisplayName(), total);
        }
    }
}
