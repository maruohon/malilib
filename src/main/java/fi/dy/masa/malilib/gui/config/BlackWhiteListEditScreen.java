package fi.dy.masa.malilib.gui.config;

import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiScreen;
import fi.dy.masa.malilib.config.option.BlackWhiteListConfig;
import fi.dy.masa.malilib.config.option.OptionListConfig;
import fi.dy.masa.malilib.config.option.StringListConfig;
import fi.dy.masa.malilib.config.value.BlackWhiteList;
import fi.dy.masa.malilib.gui.util.DialogHandler;
import fi.dy.masa.malilib.listener.EventListener;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.malilib.util.restriction.UsageRestriction.ListType;

public class BlackWhiteListEditScreen extends BaseConfigGroupEditScreen
{
    protected final BlackWhiteListConfig config;
    protected final EventListener externalSaveListener;
    protected final OptionListConfig<ListType> typeConfig;
    protected final StringListConfig blackListConfig;
    protected final StringListConfig whiteListConfig;

    public BlackWhiteListEditScreen(BlackWhiteListConfig config, EventListener saveListener,
                                    @Nullable DialogHandler dialogHandler, GuiScreen parent)
    {
        super(config.getModId(), saveListener, dialogHandler, parent);

        this.config = config;
        this.externalSaveListener = saveListener;
        this.title = StringUtils.translate("malilib.gui.title.black_white_list_edit", config.getDisplayName());

        // Initialize them to the default value so that the reset button is active when they differ from the default value,
        // and also so that the reset restores them to the default value, not the value they were at when the screen was opened
        this.typeConfig = new OptionListConfig<>("malilib.gui.label.black_white_list_edit.type", config.getDefaultValue().getListType());
        this.blackListConfig = new StringListConfig("malilib.gui.label.black_white_list_edit.black_list", config.getDefaultValue().getBlackList());
        this.whiteListConfig = new StringListConfig("malilib.gui.label.black_white_list_edit.white_list", config.getDefaultValue().getWhiteList());

        this.typeConfig.setOptionListValue(config.getValue().getListType());
        this.blackListConfig.setStrings(config.getValue().getBlackList());
        this.whiteListConfig.setStrings(config.getValue().getWhiteList());

        this.configs.add(this.typeConfig);
        this.configs.add(this.blackListConfig);
        this.configs.add(this.whiteListConfig);

        this.setSaveListener(this::saveConfigChanges);
    }

    protected void saveConfigChanges()
    {
        this.config.setValue(BlackWhiteList.of(this.typeConfig.getOptionListValue(),
                                               this.blackListConfig.getStrings(),
                                               this.whiteListConfig.getStrings()));
        this.externalSaveListener.onEvent();
    }
}
