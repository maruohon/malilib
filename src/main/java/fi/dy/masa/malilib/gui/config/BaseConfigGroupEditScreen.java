package fi.dy.masa.malilib.gui.config;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiScreen;
import fi.dy.masa.malilib.config.ConfigInfo;
import fi.dy.masa.malilib.gui.BaseListScreen;
import fi.dy.masa.malilib.gui.util.DialogHandler;
import fi.dy.masa.malilib.gui.util.GuiUtils;
import fi.dy.masa.malilib.gui.widget.list.ConfigOptionListWidget;
import fi.dy.masa.malilib.listener.EventListener;

public class BaseConfigGroupEditScreen extends BaseListScreen<ConfigOptionListWidget<? extends ConfigInfo>>
{
    protected final ArrayList<ConfigInfo> configs = new ArrayList<>();
    protected final String modId;
    @Nullable protected EventListener saveListener;
    @Nullable protected KeybindEditingScreen keyBindEditingScreen;
    protected int elementsWidth = 200;

    public BaseConfigGroupEditScreen(String modId, @Nullable EventListener saveListener,
                                     @Nullable DialogHandler dialogHandler, GuiScreen parent)
    {
        super(8, 30, 14, 36);

        this.modId = modId;
        this.saveListener = saveListener;
        this.dialogHandler = dialogHandler;

        this.shouldCenter = true;
        this.renderBorder = true;
        this.useTitleHierarchy = false;
        this.backgroundColor = 0xFF000000;

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
        if (this.saveListener != null)
        {
            this.saveListener.onEvent();
        }

        super.onGuiClosed();
    }

    public void setSaveListener(@Nullable EventListener saveListener)
    {
        this.saveListener = saveListener;
    }

    public void setConfigs(List<? extends ConfigInfo> configs)
    {
        this.configs.clear();
        this.configs.addAll(configs);
    }

    protected List<? extends ConfigInfo> getConfigs()
    {
        return this.configs;
    }

    protected int getElementsWidth()
    {
        return this.elementsWidth;
    }

    @Nullable
    protected KeybindEditingScreen getKeybindEditingScreen()
    {
        return this.keyBindEditingScreen;
    }

    @Override
    protected ConfigOptionListWidget<? extends ConfigInfo> createListWidget(int listX, int listY, int listWidth, int listHeight)
    {
        ConfigWidgetContext ctx = new ConfigWidgetContext(this::getListWidget, this.getKeybindEditingScreen(), () -> this.dialogHandler, 0);
        return ConfigOptionListWidget.createWithExpandedGroups(listX, listY, listWidth, listHeight,
                                                               this::getElementsWidth, this.modId, this::getConfigs, ctx);
    }
}
