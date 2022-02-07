package fi.dy.masa.malilib.gui.config;

import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.gui.GuiScreen;
import fi.dy.masa.malilib.config.option.list.ValueListConfig;
import fi.dy.masa.malilib.gui.BaseListScreen;
import fi.dy.masa.malilib.gui.icon.DefaultIcons;
import fi.dy.masa.malilib.gui.position.HorizontalAlignment;
import fi.dy.masa.malilib.gui.config.liteloader.DialogHandler;
import fi.dy.masa.malilib.gui.util.GuiUtils;
import fi.dy.masa.malilib.gui.widget.SearchBarWidget;
import fi.dy.masa.malilib.gui.widget.list.DataListWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.BaseOrderableListEditEntryWidget;
import fi.dy.masa.malilib.gui.widget.list.header.BaseDataListEditHeaderWidget;
import fi.dy.masa.malilib.listener.EventListener;

public class BaseValueListEditScreen<TYPE> extends BaseListScreen<DataListWidget<TYPE>>
{
    protected final ValueListConfig<TYPE> config;
    protected final Supplier<TYPE> newEntrySupplier;
    protected final ValueListEditEntryWidgetFactory<TYPE> widgetFactory;
    @Nullable protected final EventListener saveListener;

    public BaseValueListEditScreen(ValueListConfig<TYPE> config, @Nullable EventListener saveListener,
                                   @Nullable DialogHandler dialogHandler, GuiScreen parent, String title,
                                   Supplier<TYPE> newEntrySupplier, ValueListEditEntryWidgetFactory<TYPE> widgetFactory)
    {
        super(8, 20, 14, 25);

        this.config = config;
        this.dialogHandler = dialogHandler;
        this.saveListener = saveListener;
        this.newEntrySupplier = newEntrySupplier;
        this.widgetFactory = widgetFactory;

        this.shouldCenter = true;
        this.renderBorder = true;
        this.useTitleHierarchy = false;
        this.backgroundColor = 0xFF000000;
        this.setTitle(title);

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
        this.screenHeight = GuiUtils.getScaledWindowHeight() - 60;
    }

    @Override
    public void onGuiClosed()
    {
        this.config.setValues(ImmutableList.copyOf(this.getListWidget().getCurrentContents()));

        if (this.saveListener != null)
        {
            this.saveListener.onEvent();
        }

        super.onGuiClosed();
    }

    @Override
    protected DataListWidget<TYPE> createListWidget(int listX, int listY, int listWidth, int listHeight)
    {
        DataListWidget<TYPE> listWidget = new DataListWidget<>(listX, listY, listWidth, listHeight, this.config::getValue);

        listWidget.setListEntryWidgetFixedHeight(20);
        listWidget.setSearchBar(new SearchBarWidget(listWidget.getX() + 17, listWidget.getY() + 3,
                                                    listWidget.getWidth() - 31, 14, 0, DefaultIcons.SEARCH,
                                                    HorizontalAlignment.RIGHT,
                                                    listWidget::onSearchBarChange,
                                                    listWidget::refreshFilteredEntries));

        listWidget.setHeaderWidgetFactory((lw) -> new BaseDataListEditHeaderWidget<>(lw, "malilib.gui.button.hover.list.add_first", this.newEntrySupplier));
        listWidget.setSearchBarPositioner((wgt, x, y, w) -> {
            wgt.setPosition(x + 17, y);
            wgt.setWidth(w - 17);
        });
        listWidget.setHeaderWidgetPositioner((wgt, x, y, w) -> {
            wgt.setPosition(x, y - 14);
            wgt.setWidth(15);
        });

        listWidget.setEntryWidgetFactory((wx, wy, ww, wh, li, oi, entry, lw) -> {
            List<TYPE> defaultList = this.config.getDefaultValue();
            TYPE defaultValue = li < defaultList.size() ? defaultList.get(li) : this.newEntrySupplier.get();
            return this.widgetFactory.create(wx, wy, ww, wh, li, oi, entry, defaultValue, lw);
        });

        return listWidget;
    }

    public interface ValueListEditEntryWidgetFactory<DATATYPE>
    {
        BaseOrderableListEditEntryWidget<DATATYPE> create(int x, int y, int width, int height, int listIndex,
                                                          int originalListIndex, DATATYPE initialValue,
                                                          DATATYPE defaultValue, DataListWidget<DATATYPE> listWidget);
    }
}
