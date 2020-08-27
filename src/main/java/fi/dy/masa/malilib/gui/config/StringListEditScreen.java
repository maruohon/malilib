package fi.dy.masa.malilib.gui.config;

import java.util.List;
import javax.annotation.Nullable;
import org.lwjgl.input.Keyboard;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.gui.GuiScreen;
import fi.dy.masa.malilib.config.option.StringListConfig;
import fi.dy.masa.malilib.gui.BaseListScreen;
import fi.dy.masa.malilib.gui.icon.BaseIcon;
import fi.dy.masa.malilib.gui.position.HorizontalAlignment;
import fi.dy.masa.malilib.gui.util.DialogHandler;
import fi.dy.masa.malilib.gui.util.GuiUtils;
import fi.dy.masa.malilib.gui.widget.SearchBarWidget;
import fi.dy.masa.malilib.gui.widget.list.DataListWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.StringListEditEntryWidget;
import fi.dy.masa.malilib.gui.widget.list.header.BaseDataListEditHeaderWidget;
import fi.dy.masa.malilib.listener.EventListener;
import fi.dy.masa.malilib.util.StringUtils;

public class StringListEditScreen extends BaseListScreen<DataListWidget<String>>
{
    protected final StringListConfig config;
    @Nullable protected final DialogHandler dialogHandler;
    @Nullable protected final EventListener saveListener;

    public StringListEditScreen(StringListConfig config, @Nullable EventListener saveListener,
                                @Nullable DialogHandler dialogHandler, GuiScreen parent)
    {
        super(8, 20, 14, 25);

        this.config = config;
        this.dialogHandler = dialogHandler;
        this.saveListener = saveListener;

        this.title = StringUtils.translate("malilib.gui.title.string_list_edit", config.getDisplayName());
        this.shouldCenter = true;
        this.renderBorder = true;
        this.useTitleHierarchy = false;
        this.backgroundColor = 0xFF000000;

        // When we have a dialog handler, then we are inside the Liteloader config menu.
        // In there we don't want to use the normal "GUI replacement and render parent first" trick.
        // The "dialog handler" stuff is used within the Liteloader config menus,
        // because there we can't change the mc.currentScreen reference to this GUI,
        // because otherwise Liteloader will freak out.
        // So instead we are using a weird wrapper "sub panel" thingy in there, and thus
        // we can NOT try to render the parent GUI here in that case, otherwise it will
        // lead to an infinite recursion loop and a StackOverflowError.
        if (this.dialogHandler == null)
        {
            this.setParent(parent);
        }
    }

    @Override
    protected void setScreenWidthAndHeight(int width, int height)
    {
        this.screenWidth = 400;
        this.screenHeight = GuiUtils.getScaledWindowHeight() - 90;
    }

    @Override
    public void onGuiClosed()
    {
        this.config.setStrings(ImmutableList.copyOf(this.getListWidget().getCurrentEntries()));

        if (this.saveListener != null)
        {
            this.saveListener.onEvent();
        }

        super.onGuiClosed();
    }

    @Override
    public boolean onKeyTyped(char typedChar, int keyCode)
    {
        if (keyCode == Keyboard.KEY_ESCAPE && this.dialogHandler != null)
        {
            this.dialogHandler.closeDialog();
            return true;
        }

        return super.onKeyTyped(typedChar, keyCode);
    }

    @Override
    protected DataListWidget<String> createListWidget(int listX, int listY, int listWidth, int listHeight)
    {
        DataListWidget<String> listWidget = new DataListWidget<>(listX, listY, listWidth, listHeight, this.config::getStrings);

        listWidget.setListEntryWidgetFixedHeight(20);
        listWidget.addSearchBar(new SearchBarWidget(listWidget.getX() + 17, listWidget.getY() + 3,
                                                    listWidget.getWidth() - 31, 14, 0, BaseIcon.SEARCH,
                                                    HorizontalAlignment.RIGHT, listWidget::onSearchBarChange));

        listWidget.setHeaderWidgetFactory((x, y, w, h, lw) -> new BaseDataListEditHeaderWidget<>(x, y, w, h, lw, "malilib.gui.button.hover.list.add_first", () -> ""));
        listWidget.setSearchBarPositioner((wgt, x, y, w) -> {
            wgt.setPosition(x + 17, y);
            wgt.setWidth(w - 17);
        });
        listWidget.setHeaderWidgetPositioner((wgt, x, y, w) -> {
            wgt.setPosition(x, y - 16);
            wgt.setWidth(15);
        });

        listWidget.setEntryWidgetFactory((wx, wy, ww, wh, li, oi, entry, lw) -> {
            List<String> defaultList = this.config.getDefaultStrings();
            String defaultValue = li < defaultList.size() ? defaultList.get(li) : "";
            return new StringListEditEntryWidget(wx, wy, ww, wh, li, oi, entry, defaultValue, lw);
        });

        return listWidget;
    }
}
