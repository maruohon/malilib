package fi.dy.masa.malilib.gui.widget.button;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.config.option.list.BlackWhiteListConfig;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.config.BlackWhiteListEditScreen;
import fi.dy.masa.malilib.gui.config.liteloader.DialogHandler;
import fi.dy.masa.malilib.gui.util.GuiUtils;
import fi.dy.masa.malilib.listener.EventListener;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.malilib.util.restriction.UsageRestriction.ListType;

public class BlackWhiteListEditButton extends GenericButton
{
    protected final BlackWhiteListConfig<?> config;
    protected final EventListener saveListener;
    @Nullable protected final DialogHandler dialogHandler;

    public BlackWhiteListEditButton(int width, int height, BlackWhiteListConfig<?> config,
                                    EventListener saveListener, @Nullable DialogHandler dialogHandler)
    {
        super(width, height, "");

        this.config = config;
        this.dialogHandler = dialogHandler;
        this.saveListener = saveListener;
        this.setHoverStringProvider("value_preview", this::generateHoverStrings);

        this.setActionListener(this::openEditScreen);
        this.updateDisplayString();
    }

    protected void openEditScreen()
    {
        if (this.dialogHandler != null)
        {
            this.dialogHandler.openDialog(new BlackWhiteListEditScreen<>(this.config, this.saveListener, this.dialogHandler, null));
        }
        else
        {
            BaseScreen.openPopupScreen(new BlackWhiteListEditScreen<>(this.config, this.saveListener, null, GuiUtils.getCurrentScreen()));
        }
    }

    @Override
    protected String generateDisplayString()
    {
        this.hoverInfoFactory.updateList();

        ListType type = this.config.getValue().getListType();

        if (type == ListType.NONE)
        {
            return StringUtils.translate("malilib.gui.button.black_white_list_edit.none");
        }
        else
        {
            int total = this.config.getValue().getActiveList().getValue().size();
            return StringUtils.translate("malilib.gui.button.black_white_list_edit.entries", type.getDisplayName(), total);
        }
    }

    protected List<String> generateHoverStrings()
    {
        ListType type = this.config.getValue().getListType();

        if (type != ListType.NONE)
        {
            List<String> hoverStrings = new ArrayList<>();
            List<String> list = this.config.getValue().getActiveListAsString();
            int total = list.size();
            int max = Math.min(10, total);

            hoverStrings.add(StringUtils.translate("malilib.gui.button.hover.black_white_list_edit.type", type.getDisplayName()));
            hoverStrings.add(StringUtils.translate("malilib.gui.button.hover.entries_total", total));

            for (int i = 0; i < max; ++i)
            {
                hoverStrings.add("§7" + list.get(i));
            }

            if (total > max)
            {
                hoverStrings.add(StringUtils.translate("malilib.gui.button.hover.entries_more", total - max));
            }

            return hoverStrings;
        }

        return EMPTY_STRING_LIST;
    }
}
