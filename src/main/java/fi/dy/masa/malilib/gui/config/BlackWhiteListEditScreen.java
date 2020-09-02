package fi.dy.masa.malilib.gui.config;

import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiScreen;
import fi.dy.masa.malilib.config.option.BlackWhiteListConfig;
import fi.dy.masa.malilib.config.option.OptionListConfig;
import fi.dy.masa.malilib.config.option.ValueListConfig;
import fi.dy.masa.malilib.config.value.BlackWhiteList;
import fi.dy.masa.malilib.gui.util.DialogHandler;
import fi.dy.masa.malilib.listener.EventListener;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.malilib.util.restriction.UsageRestriction.ListType;

public class BlackWhiteListEditScreen<TYPE> extends BaseConfigGroupEditScreen
{
    protected final BlackWhiteListConfig<TYPE> config;
    protected final EventListener externalSaveListener;
    protected final OptionListConfig<ListType> typeConfig;
    protected final ValueListConfig<TYPE> blackListConfig;
    protected final ValueListConfig<TYPE> whiteListConfig;

    public BlackWhiteListEditScreen(BlackWhiteListConfig<TYPE> config, EventListener saveListener,
                                    @Nullable DialogHandler dialogHandler, GuiScreen parent)
    {
        super(config.getModId(), saveListener, dialogHandler, parent);

        this.config = config;
        this.externalSaveListener = saveListener;
        this.title = StringUtils.translate("malilib.gui.title.black_white_list_edit", config.getDisplayName());

        // Initialize them to the default value so that the reset button is active when they differ from the default value,
        // and also so that the reset restores them to the default value, not the value they were at when the screen was opened
        this.typeConfig = new OptionListConfig<>("malilib.gui.label.black_white_list_edit.type", config.getDefaultValue().getListType());
        BlackWhiteList<TYPE> list = config.getDefaultValue();
        this.blackListConfig = new ValueListConfig<>("malilib.gui.label.black_white_list_edit.black_list", list.getBlackList(), list.getToStringConverter(), list.getFromStringConverter());
        this.whiteListConfig = new ValueListConfig<>("malilib.gui.label.black_white_list_edit.white_list", list.getWhiteList(), list.getToStringConverter(), list.getFromStringConverter());

        this.typeConfig.setOptionListValue(config.getValue().getListType());
        this.blackListConfig.setValues(config.getValue().getBlackList());
        this.whiteListConfig.setValues(config.getValue().getWhiteList());

        this.configs.add(this.typeConfig);
        this.configs.add(this.blackListConfig);
        this.configs.add(this.whiteListConfig);

        this.setSaveListener(this::saveConfigChanges);
    }

    protected void saveConfigChanges()
    {
        BlackWhiteList<TYPE> old = this.config.getDefaultValue();
        BlackWhiteList<TYPE> list = new BlackWhiteList<>(this.typeConfig.getOptionListValue(),
                                                         this.blackListConfig.getValues(),
                                                         this.whiteListConfig.getValues(),
                                                         old.getToStringConverter(), old.getFromStringConverter());
        this.config.setValue(list);
        this.externalSaveListener.onEvent();
    }
}
