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
import fi.dy.masa.malilib.gui.config.liteloader.DialogHandler;
import fi.dy.masa.malilib.gui.util.GuiUtils;
import fi.dy.masa.malilib.gui.widget.DropDownListWidget.IconWidgetFactory;
import fi.dy.masa.malilib.gui.widget.list.entry.BaseValueListEditEntryWidget;
import fi.dy.masa.malilib.listener.EventListener;
import fi.dy.masa.malilib.util.StringUtils;

public class BaseValueListEditButton<TYPE> extends GenericButton
{
    protected final ValueListConfig<TYPE> config;
    @Nullable protected final DialogHandler dialogHandler;
    @Nullable protected final EventListener saveListener;
    protected final String screenTitle;
    protected final Supplier<TYPE> newEntryFactory;
    protected final ValueListEditEntryWidgetFactory<TYPE> widgetFactory;
    protected final List<String> hoverStrings = new ArrayList<>();

    public BaseValueListEditButton(int x, int y, int width, int height, ValueListConfig<TYPE> config,
                                   @Nullable EventListener saveListener, @Nullable DialogHandler dialogHandler,
                                   String screenTitle, Supplier<TYPE> newEntryFactory,
                                   ValueListEditEntryWidgetFactory<TYPE> widgetFactory)
    {
        super(x, y, width, height, "");

        this.config = config;
        this.dialogHandler = dialogHandler;
        this.saveListener = saveListener;
        this.screenTitle = screenTitle;
        this.newEntryFactory = newEntryFactory;
        this.widgetFactory = widgetFactory;

        this.setHoverStringProvider("preview", () -> this.hoverStrings);
        this.setActionListener((btn, mbtn) -> this.openEditScreen());
        this.updateDisplayString();
    }

    public BaseValueListEditButton(int x, int y, int width, int height, ValueListConfig<TYPE> config,
                                   @Nullable EventListener saveListener, @Nullable DialogHandler dialogHandler,
                                   String screenTitle, Supplier<TYPE> newEntryFactory,
                                   Supplier<List<TYPE>> possibleValuesSupplier,
                                   Function<TYPE, String> displayNameFactory,
                                   @Nullable IconWidgetFactory<TYPE> iconWidgetFactory)
    {
        this(x, y, width, height, config, saveListener, dialogHandler, screenTitle, newEntryFactory,
             (wx, wy, ww, wh, li, oi, iv, dv, lw) ->
                 new BaseValueListEditEntryWidget<>(wx, wy, ww, wh, li, oi, iv, dv, lw,
                                                    possibleValuesSupplier.get(), displayNameFactory, iconWidgetFactory));
    }

    protected BaseScreen createScreen(@Nullable DialogHandler dialogHandler, @Nullable GuiScreen currentScreen)
    {
        return new BaseValueListEditScreen<>(this.config, this.saveListener, dialogHandler, currentScreen,
                                             this.screenTitle, this.newEntryFactory, this.widgetFactory);
    }

    protected void openEditScreen()
    {
        if (this.dialogHandler != null)
        {
            this.dialogHandler.openDialog(this.createScreen(this.dialogHandler, null));
        }
        else
        {
            BaseScreen.openPopupScreen(this.createScreen(null, GuiUtils.getCurrentScreen()));
        }
    }

    @Override
    protected String generateDisplayString()
    {
        ImmutableList<String> valueStrings = this.config.getValuesAsString();
        this.updateHoverStrings(valueStrings);

        return StringUtils.getDisplayStringForList(valueStrings, this.getWidth() - 10, "'", "[ ", " ]");
    }

    protected void updateHoverStrings(ImmutableList<String> valueStrings)
    {
        List<String> hoverStrings = this.hoverStrings;
        int total = valueStrings.size();
        int max = Math.min(10, total);

        hoverStrings.clear();
        hoverStrings.add(StringUtils.translate("malilib.gui.button.hover.entries_total", total));

        for (int i = 0; i < max; ++i)
        {
            hoverStrings.add("§7" + valueStrings.get(i));
        }

        if (total > max)
        {
            hoverStrings.add(StringUtils.translate("malilib.gui.button.hover.entries_more", total - max));
        }

        this.updateHoverStrings();
    }
}
