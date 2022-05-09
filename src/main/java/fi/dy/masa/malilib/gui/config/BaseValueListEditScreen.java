package fi.dy.masa.malilib.gui.config;

import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.gui.screen.Screen;
import fi.dy.masa.malilib.config.option.list.ValueListConfig;
import fi.dy.masa.malilib.config.value.HorizontalAlignment;
import fi.dy.masa.malilib.gui.BaseListScreen;
import fi.dy.masa.malilib.gui.icon.DefaultIcons;
import fi.dy.masa.malilib.gui.util.GuiUtils;
import fi.dy.masa.malilib.gui.widget.list.DataListWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.BaseOrderableListEditEntryWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.DataListEntryWidgetData;
import fi.dy.masa.malilib.gui.widget.list.header.BaseDataListEditHeaderWidget;
import fi.dy.masa.malilib.gui.widget.list.search.SearchBarWidget;
import fi.dy.masa.malilib.listener.EventListener;

public class BaseValueListEditScreen<TYPE> extends BaseListScreen<DataListWidget<TYPE>>
{
    protected final ValueListConfig<TYPE> config;
    protected final Supplier<TYPE> newEntrySupplier;
    protected final ValueListEditEntryWidgetFactory<TYPE> widgetFactory;
    @Nullable protected final EventListener saveListener;

    public BaseValueListEditScreen(String title,
                                   ValueListConfig<TYPE> config,
                                   @Nullable EventListener saveListener,
                                   Supplier<TYPE> newEntrySupplier,
                                   ValueListEditEntryWidgetFactory<TYPE> widgetFactory,
                                   Screen parent)
    {
        super(8, 20, 14, 25);

        this.config = config;
        this.saveListener = saveListener;
        this.newEntrySupplier = newEntrySupplier;
        this.widgetFactory = widgetFactory;

        this.shouldCenter = true;
        this.renderBorder = true;
        this.useTitleHierarchy = false;
        this.backgroundColor = 0xFF000000;
        this.screenWidth = 400;
        this.screenHeight = GuiUtils.getScaledWindowHeight() - 60;

        this.setTitle(title);
        this.setParent(parent);
    }

    @Override
    protected void onScreenClosed()
    {
        if (this.config.isLocked() == false)
        {
            this.config.setValue(ImmutableList.copyOf(this.getListWidget().getNonFilteredDataList()));

            if (this.saveListener != null)
            {
                this.saveListener.onEvent();
            }
        }

        super.onScreenClosed();
    }

    @Override
    protected DataListWidget<TYPE> createListWidget()
    {
        DataListWidget<TYPE> listWidget = new DataListWidget<>(this.config::getValue, false);

        SearchBarWidget searchBar = new SearchBarWidget(10, 14, listWidget::onSearchBarTextChanged,
                                                        listWidget::refreshFilteredEntries, DefaultIcons.SEARCH);
        searchBar.setToggleButtonAlignment(HorizontalAlignment.RIGHT);

        listWidget.setListEntryWidgetFixedHeight(20);
        listWidget.setSearchBar(searchBar);
        listWidget.getBorderRenderer().getNormalSettings().setBorderWidth(0);

        listWidget.setHeaderWidgetFactory((lw) -> new BaseDataListEditHeaderWidget<>(lw, "malilib.hover.button.list.add_first", this.newEntrySupplier));
        listWidget.setSearchBarPositioner((wgt, x, y, w) -> {
            wgt.setPosition(x + 17, y);
            wgt.setWidth(w - 17);
        });
        listWidget.setHeaderWidgetPositioner((wgt, x, y, w) -> {
            wgt.setPosition(x, y - 14);
            wgt.setWidth(15);
        });

        listWidget.setDataListEntryWidgetFactory((data, constructData) -> {
            List<TYPE> defaultList = this.config.getDefaultValue();
            int index = constructData.listIndex;
            TYPE defaultValue = index < defaultList.size() ? defaultList.get(index) : this.newEntrySupplier.get();
            return this.widgetFactory.create(data, constructData, defaultValue);
        });

        return listWidget;
    }

    public interface ValueListEditEntryWidgetFactory<DATATYPE>
    {
        BaseOrderableListEditEntryWidget<DATATYPE> create(DATATYPE initialValue,
                                                          DataListEntryWidgetData constructData,
                                                          DATATYPE defaultValue);
    }
}
