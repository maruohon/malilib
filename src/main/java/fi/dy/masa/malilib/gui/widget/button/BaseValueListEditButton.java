package fi.dy.masa.malilib.gui.widget.button;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.gui.GuiScreen;
import fi.dy.masa.malilib.config.option.list.ValueListConfig;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.config.BaseValueListEditScreen;
import fi.dy.masa.malilib.gui.config.BaseValueListEditScreen.ValueListEditEntryWidgetFactory;
import fi.dy.masa.malilib.gui.util.GuiUtils;
import fi.dy.masa.malilib.gui.widget.DropDownListWidget.IconWidgetFactory;
import fi.dy.masa.malilib.gui.widget.list.entry.BaseValueListEditEntryWidget;
import fi.dy.masa.malilib.listener.EventListener;
import fi.dy.masa.malilib.util.StringUtils;

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

    protected BaseScreen createScreen(@Nullable GuiScreen currentScreen)
    {
        return new BaseValueListEditScreen<>(this.screenTitle, this.config, this.saveListener,
                                             this.newEntryFactory, this.widgetFactory, currentScreen);
    }

    protected void openEditScreen()
    {
        BaseScreen.openPopupScreen(this.createScreen(GuiUtils.getCurrentScreen()));
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
