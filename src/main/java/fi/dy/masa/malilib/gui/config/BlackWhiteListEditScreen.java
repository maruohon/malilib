package fi.dy.masa.malilib.gui.config;

import net.minecraft.client.gui.screen.Screen;
import fi.dy.masa.malilib.config.option.ConfigInfo;
import fi.dy.masa.malilib.config.option.OptionListConfig;
import fi.dy.masa.malilib.config.option.list.BlackWhiteListConfig;
import fi.dy.masa.malilib.config.option.list.ValueListConfig;
import fi.dy.masa.malilib.config.value.BlackWhiteList;
import fi.dy.masa.malilib.gui.widget.list.ConfigOptionListWidget;
import fi.dy.masa.malilib.listener.EventListener;
import fi.dy.masa.malilib.util.restriction.UsageRestriction.ListType;

public class BlackWhiteListEditScreen<TYPE> extends BaseConfigGroupEditScreen
{
    protected final BlackWhiteListConfig<TYPE> config;
    protected final EventListener externalSaveListener;
    protected final OptionListConfig<ListType> typeConfig;
    protected final ValueListConfig<TYPE> blackListConfig;
    protected final ValueListConfig<TYPE> whiteListConfig;

    public BlackWhiteListEditScreen(BlackWhiteListConfig<TYPE> config, EventListener saveListener, Screen parent)
    {
        super(config.getModInfo(), saveListener, parent);

        this.config = config;
        this.externalSaveListener = saveListener;
        this.setTitle("malilib.title.screen.black_white_list_edit", config.getDisplayName());

        // Initialize them to the default value so that the reset button is active when they differ from the default value,
        // and also so that the reset restores them to the default value, not the value they were at when the screen was opened
        BlackWhiteList<TYPE> bwList = config.getValue();
        this.typeConfig = new OptionListConfig<>("malilib.label.config.black_white_list_edit.type",
                                                 config.getDefaultValue().getListType(), ListType.VALUES);
        this.typeConfig.setValue(bwList.getListType());
        this.typeConfig.setCommentTranslationKey(null);

        this.blackListConfig = bwList.getBlackList().copy();
        this.blackListConfig.setCommentTranslationKey(null);
        this.whiteListConfig = bwList.getWhiteList().copy();
        this.whiteListConfig.setCommentTranslationKey(null);

        this.configs.add(this.typeConfig);
        this.configs.add(this.blackListConfig);
        this.configs.add(this.whiteListConfig);

        this.setSaveListener(this::saveConfigChanges);
    }

    protected void saveConfigChanges()
    {
        if (this.config.isLocked() == false)
        {
            BlackWhiteList<TYPE> old = this.config.getDefaultValue();
            BlackWhiteList<TYPE> list = new BlackWhiteList<>(this.typeConfig.getValue(),
                                                             this.blackListConfig,
                                                             this.whiteListConfig,
                                                             old.getToStringConverter(),
                                                             old.getFromStringConverter());
            this.config.setValue(list);
            this.externalSaveListener.onEvent();
        }
    }

    @Override
    protected ConfigOptionListWidget<? extends ConfigInfo> createListWidget()
    {
        ConfigOptionListWidget<? extends ConfigInfo> listWidget = super.createListWidget();
        listWidget.setShowInternalConfigName(false);
        return listWidget;
    }
}
