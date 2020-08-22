package fi.dy.masa.malilib.gui.config;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import org.lwjgl.input.Keyboard;
import net.minecraft.client.gui.GuiScreen;
import fi.dy.masa.malilib.config.option.BlackWhiteListConfig;
import fi.dy.masa.malilib.config.option.ConfigInfo;
import fi.dy.masa.malilib.config.option.OptionListConfig;
import fi.dy.masa.malilib.config.option.StringListConfig;
import fi.dy.masa.malilib.config.value.BlackWhiteList;
import fi.dy.masa.malilib.gui.BaseListScreen;
import fi.dy.masa.malilib.gui.util.DialogHandler;
import fi.dy.masa.malilib.gui.util.GuiUtils;
import fi.dy.masa.malilib.gui.widget.list.ConfigOptionListWidget;
import fi.dy.masa.malilib.listener.EventListener;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.malilib.util.restriction.UsageRestriction.ListType;

public class BlackWhiteListEditScreen extends BaseListScreen<ConfigOptionListWidget<? extends ConfigInfo>>
{
    protected final BlackWhiteListConfig config;
    @Nullable protected final DialogHandler dialogHandler;
    @Nullable protected final EventListener saveListener;
    protected final ArrayList<ConfigInfo> configs = new ArrayList<>();
    protected final OptionListConfig<ListType> typeConfig;
    protected final StringListConfig blackListConfig;
    protected final StringListConfig whiteListConfig;

    public BlackWhiteListEditScreen(BlackWhiteListConfig config, @Nullable EventListener saveListener,
                                @Nullable DialogHandler dialogHandler, GuiScreen parent)
    {
        super(8, 30, 14, 36);

        this.config = config;
        this.saveListener = saveListener;
        this.dialogHandler = dialogHandler;

        this.title = StringUtils.translate("malilib.gui.title.black_white_list_edit", config.getDisplayName());
        this.shouldCenter = true;
        this.renderBorder = true;
        this.useTitleHierarchy = false;
        this.backgroundColor = 0xFF000000;

        this.typeConfig = new OptionListConfig<>("malilib.gui.label.black_white_list_edit.type", config.getValue().getListType());
        this.blackListConfig = new StringListConfig("malilib.gui.label.black_white_list_edit.black_list", config.getValue().getBlackList());
        this.whiteListConfig = new StringListConfig("malilib.gui.label.black_white_list_edit.white_list", config.getValue().getWhiteList());
        this.configs.add(this.typeConfig);
        this.configs.add(this.blackListConfig);
        this.configs.add(this.whiteListConfig);

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
        this.config.setValue(BlackWhiteList.of(this.typeConfig.getOptionListValue(),
                                               this.blackListConfig.getStrings(),
                                               this.whiteListConfig.getStrings()));

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

    protected List<? extends ConfigInfo> getConfigs()
    {
        return this.configs;
    }

    @Override
    protected ConfigOptionListWidget<? extends ConfigInfo> createListWidget(int listX, int listY, int listWidth, int listHeight)
    {
        return new ConfigOptionListWidget<>(listX, listY, listWidth, listHeight, this.config.getModId(), this::getConfigs, () -> 200,
                                            new ConfigWidgetContext(this::getListWidget, null, () -> this.dialogHandler));
    }
}
