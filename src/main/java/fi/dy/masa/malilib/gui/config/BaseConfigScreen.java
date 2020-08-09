package fi.dy.masa.malilib.gui.config;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import org.lwjgl.input.Keyboard;
import net.minecraft.client.gui.GuiScreen;
import fi.dy.masa.malilib.config.ConfigManager;
import fi.dy.masa.malilib.config.option.ConfigInfo;
import fi.dy.masa.malilib.event.dispatch.KeyBindManager;
import fi.dy.masa.malilib.gui.BaseListScreen;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.button.GenericButton;
import fi.dy.masa.malilib.gui.button.KeyBindConfigButton;
import fi.dy.masa.malilib.gui.util.DialogHandler;
import fi.dy.masa.malilib.gui.util.GuiUtils;
import fi.dy.masa.malilib.gui.widget.list.ConfigOptionListWidget;
import fi.dy.masa.malilib.util.StringUtils;

public abstract class BaseConfigScreen extends BaseListScreen<ConfigOptionListWidget<? extends ConfigInfo>> implements KeybindEditingScreen
{
    protected final List<ConfigTab> configTabs;
    protected final String modId;
    @Nullable protected KeyBindConfigButton activeKeyBindButton;
    @Nullable protected ConfigInfoProvider hoverInfoProvider;
    @Nullable protected DialogHandler dialogHandler;
    protected int configElementsWidth = 120;

    public BaseConfigScreen(int listX, int listY, String modId, @Nullable GuiScreen parent, List<ConfigTab> configTabs, String titleKey, Object... args)
    {
        super(listX, listY);

        this.modId = modId;
        this.title = StringUtils.translate(titleKey, args);
        this.configTabs = configTabs;
        this.setParent(parent);
    }

    @Override
    protected int getListWidth()
    {
        return this.width - 20;
    }

    @Override
    protected int getListHeight()
    {
        return this.height - 80;
    }

    public abstract void setCurrentTab(ConfigTab tab);

    @Nullable
    public abstract ConfigTab getCurrentTab();

    public boolean useKeyBindSearch()
    {
        return this.getCurrentTab() != null && this.getCurrentTab().useKeyBindSearch();
    }

    public int getConfigElementsWidth()
    {
        int overriddenWidth = this.getListWidget().getElementWidth();

        if (overriddenWidth != -1)
        {
            return overriddenWidth;
        }

        return this.getCurrentTab() != null ? this.getCurrentTab().getConfigWidth() : this.configElementsWidth;
    }

    @Override
    public List<? extends ConfigInfo> getConfigs()
    {
        return this.getCurrentTab() != null ? this.getCurrentTab().getConfigsForDisplay() : Collections.emptyList();
    }

    /**
     * Sets the requested config elements width for this screen.
     * Use -1 to indicate automatic/default width decided by the widgets.
     * @param configElementsWidth
     * @return
     */
    public BaseConfigScreen setConfigElementsWidth(int configElementsWidth)
    {
        this.configElementsWidth = configElementsWidth;
        return this;
    }

    public BaseConfigScreen setHoverInfoProvider(ConfigInfoProvider provider)
    {
        this.hoverInfoProvider = provider;
        return this;
    }

    public void reCreateConfigWidgets()
    {
        super.reCreateListWidget();
    }

    @Override
    @Nullable
    public DialogHandler getDialogHandler()
    {
        return this.dialogHandler;
    }

    public void setDialogHandler(@Nullable DialogHandler handler)
    {
        this.dialogHandler = handler;
    }

    @Override
    public String getModId()
    {
        return this.modId;
    }

    @Override
    @Nullable
    public ConfigInfoProvider getHoverInfoProvider()
    {
        return this.hoverInfoProvider;
    }

    @Override
    protected ConfigOptionListWidget<? extends ConfigInfo> createListWidget(int listX, int listY, int listWidth, int listHeight)
    {
        return new ConfigOptionListWidget<>(listX, listY, listWidth, listHeight, this::getConfigs, this);
    }

    @Override
    public void initGui()
    {
        super.initGui();

        this.clearOptions();
        this.createTabButtons();

        Keyboard.enableRepeatEvents(true);
    }

    protected void createTabButtons()
    {
        int x = 10;
        int y = 26;
        int rows = 1;

        for (ConfigTab tab : this.configTabs)
        {
            int width = this.getStringWidth(tab.getDisplayName()) + 10;

            if (x >= this.width - width - 10)
            {
                x = 10;
                y += 22;
                rows++;
            }

            x += this.createTabButton(x, y, width, tab);
        }

        if (rows > 1)
        {
            this.updateListPosition(this.getListX(), 50 + (rows - 1) * 22);
        }
    }

    protected int createTabButton(int x, int y, int width, ConfigTab tab)
    {
        GenericButton button = new GenericButton(x, y, width, 20, tab.getDisplayName());
        button.setEnabled(this.getCurrentTab() != tab);
        this.addButton(button, tab.getButtonActionListener(this));

        return button.getWidth() + 2;
    }

    @Override
    public void onGuiClosed()
    {
        super.onGuiClosed();

        if (ConfigManager.INSTANCE.saveConfigsIfChanged(this.modId))
        {
            this.onSettingsChanged();
        }

        Keyboard.enableRepeatEvents(false);
    }

    protected void onSettingsChanged()
    {
        KeyBindManager.INSTANCE.updateUsedKeys();
    }

    @Override
    public boolean onKeyTyped(char typedChar, int keyCode)
    {
        if (this.activeKeyBindButton != null)
        {
            this.activeKeyBindButton.onKeyPressed(keyCode);
            return true;
        }
        else
        {
            if (this.getListWidget().onKeyTyped(typedChar, keyCode))
            {
                return true;
            }

            if (keyCode == Keyboard.KEY_ESCAPE && this.getParent() != GuiUtils.getCurrentScreen())
            {
                BaseScreen.openGui(this.getParent());
                return true;
            }

            return super.onKeyTyped(typedChar, keyCode);
        }
    }

    @Override
    public boolean onMouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (super.onMouseClicked(mouseX, mouseY, mouseButton))
        {
            return true;
        }

        // When clicking on not-a-button, clear the selection
        if (this.activeKeyBindButton != null)
        {
            this.setActiveKeyBindButton(null);
            return true;
        }

        return false;
    }

    @Override
    public void clearOptions()
    {
        this.setActiveKeyBindButton(null);
    }

    @Override
    public void setActiveKeyBindButton(@Nullable KeyBindConfigButton button)
    {
        if (this.activeKeyBindButton != null)
        {
            this.activeKeyBindButton.onClearSelection();
        }

        this.activeKeyBindButton = button;

        if (this.activeKeyBindButton != null)
        {
            this.activeKeyBindButton.onSelected();
        }
    }
}
