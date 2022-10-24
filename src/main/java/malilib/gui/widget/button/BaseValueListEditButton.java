package malilib.gui.widget.button;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;

import malilib.config.option.list.ValueListConfig;
import malilib.gui.BaseScreen;
import malilib.gui.config.BaseValueListEditScreen;
import malilib.gui.config.BaseValueListEditScreen.ValueListEditEntryWidgetFactory;
import malilib.gui.util.GuiUtils;
import malilib.gui.widget.DropDownListWidget.IconWidgetFactory;
import malilib.gui.widget.list.entry.BaseValueListEditEntryWidget;
import malilib.listener.EventListener;
import malilib.util.StringUtils;

public class BaseValueListEditButton<TYPE> extends GenericButton
{
    protected final ValueListConfig<TYPE> config;
    protected final String screenTitle;
    protected final Supplier<TYPE> newEntryFactory;
    protected final ValueListEditEntryWidgetFactory<TYPE> widgetFactory;
    @Nullable protected final EventListener saveListener;
    protected ImmutableList<String> valueStrings;

    public BaseValueListEditButton(int width, int height,
                                   ValueListConfig<TYPE> config,
                                   @Nullable EventListener saveListener,
                                   Supplier<TYPE> newEntryFactory,
                                   ValueListEditEntryWidgetFactory<TYPE> widgetFactory,
                                   String screenTitle)
    {
        super(width, height);

        this.config = config;
        this.saveListener = saveListener;
        this.screenTitle = screenTitle;
        this.newEntryFactory = newEntryFactory;
        this.widgetFactory = widgetFactory;
        this.valueStrings = config.getValuesAsString();

        this.setHoverStringProvider("preview", this::getHoverStrings);
        this.setActionListener(this::openEditScreen);
        this.setDisplayStringSupplier(this::getCurrentDisplayString);
    }

    public BaseValueListEditButton(int width, int height,
                                   ValueListConfig<TYPE> config,
                                   @Nullable EventListener saveListener,
                                   Supplier<TYPE> newEntryFactory,
                                   Supplier<List<TYPE>> possibleValuesSupplier,
                                   Function<TYPE, String> displayNameFactory,
                                   @Nullable IconWidgetFactory<TYPE> iconWidgetFactory,
                                   String screenTitle)
    {
        this(width, height, config, saveListener, newEntryFactory,
             (iv, cd, dv) -> new BaseValueListEditEntryWidget<>(iv, cd, dv,
                                    possibleValuesSupplier.get(), displayNameFactory, iconWidgetFactory),
             screenTitle);
    }

    @Override
    public void updateWidgetState()
    {
        this.valueStrings = this.config.getValuesAsString();
        super.updateWidgetState();
    }

    protected BaseScreen createScreen()
    {
        return new BaseValueListEditScreen<>(this.screenTitle, this.config, this.saveListener,
                                             this.newEntryFactory, this.widgetFactory);
    }

    protected void openEditScreen()
    {
        BaseScreen screen = this.createScreen();
        screen.setParent(GuiUtils.getCurrentScreen());
        BaseScreen.openPopupScreen(screen);
    }

    protected String getCurrentDisplayString()
    {
        return StringUtils.getDisplayStringForList(this.valueStrings, this.getWidth() - 10, "'", "[ ", " ]");
    }

    protected List<String> getHoverStrings()
    {
        List<String> hoverStrings = new ArrayList<>();
        int total = this.valueStrings.size();
        int max = Math.min(10, total);

        hoverStrings.add(StringUtils.translate("malilib.hover.button.config_list.total_entries", total));

        for (int i = 0; i < max; ++i)
        {
            hoverStrings.add(StringUtils.translate("malilib.hover.button.value_list_edit.entry", this.valueStrings.get(i)));
        }

        if (total > max)
        {
            hoverStrings.add(StringUtils.translate("malilib.hover.button.config_list.more_entries", total - max));
        }

        return hoverStrings;
    }
}
