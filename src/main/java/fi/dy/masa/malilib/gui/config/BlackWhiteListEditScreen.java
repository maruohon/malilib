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

public class BlackWhiteListEditScreen<TYPE, CFG extends ValueListConfig<TYPE>> extends BaseConfigGroupEditScreen
{
    protected final BlackWhiteListConfig<TYPE, CFG> config;
    protected final EventListener externalSaveListener;
    protected final OptionListConfig<ListType> typeConfig;
    protected final CFG blackListConfig;
    protected final CFG whiteListConfig;

    public BlackWhiteListEditScreen(BlackWhiteListConfig<TYPE, CFG> config, EventListener saveListener,
                                    @Nullable DialogHandler dialogHandler, GuiScreen parent)
    {
        super(config.getModId(), saveListener, dialogHandler, parent);

        this.config = config;
        this.externalSaveListener = saveListener;
        this.title = StringUtils.translate("malilib.gui.title.black_white_list_edit", config.getDisplayName());

        // Initialize them to the default value so that the reset button is active when they differ from the default value,
        // and also so that the reset restores them to the default value, not the value they were at when the screen was opened
        this.typeConfig = new OptionListConfig<>("malilib.gui.label.black_white_list_edit.type", config.getDefaultValue().getListType());
        this.typeConfig.setOptionListValue(config.getValue().getListType());

        @SuppressWarnings("unchecked")
        CFG bl = (CFG) config.getValue().getBlackList().copy();
        this.blackListConfig = bl;

        @SuppressWarnings("unchecked")
        CFG wl = (CFG) config.getValue().getWhiteList().copy();
        this.whiteListConfig = wl;

        this.configs.add(this.typeConfig);
        this.configs.add(this.blackListConfig);
        this.configs.add(this.whiteListConfig);

        this.setSaveListener(this::saveConfigChanges);
    }

    protected void saveConfigChanges()
    {
        BlackWhiteList<TYPE, CFG> old = this.config.getDefaultValue();
        BlackWhiteList<TYPE, CFG> list = new BlackWhiteList<>(this.typeConfig.getOptionListValue(),
                                                              this.blackListConfig,
                                                              this.whiteListConfig,
                                                              old.getToStringConverter(), old.getFromStringConverter());
        this.config.setValue(list);
        this.externalSaveListener.onEvent();
    }
}
