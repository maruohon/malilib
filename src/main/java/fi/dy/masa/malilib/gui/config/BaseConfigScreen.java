package fi.dy.masa.malilib.gui.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import org.lwjgl.input.Keyboard;
import net.minecraft.client.gui.GuiScreen;
import fi.dy.masa.malilib.config.ConfigManager;
import fi.dy.masa.malilib.config.option.ConfigInfo;
import fi.dy.masa.malilib.event.dispatch.InputEventDispatcher;
import fi.dy.masa.malilib.gui.BaseListScreen;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.ConfigButtonKeyBind;
import fi.dy.masa.malilib.gui.interfaces.IDialogHandler;
import fi.dy.masa.malilib.gui.interfaces.IKeybindConfigGui;
import fi.dy.masa.malilib.gui.listener.ButtonPressDirtyListenerSimple;
import fi.dy.masa.malilib.gui.listener.ConfigOptionChangeListenerKeybind;
import fi.dy.masa.malilib.gui.util.GuiUtils;
import fi.dy.masa.malilib.gui.widget.list.ConfigOptionListWidget;
import fi.dy.masa.malilib.util.StringUtils;

public abstract class BaseConfigScreen extends BaseListScreen<ConfigOptionListWidget<?>> implements IKeybindConfigGui
{
    protected final List<ConfigOptionChangeListenerKeybind> hotkeyChangeListeners = new ArrayList<>();
    protected final ButtonPressDirtyListenerSimple dirtyListener = new ButtonPressDirtyListenerSimple();
    protected final String modId;
    protected final List<String> initialConfigValues = new ArrayList<>();
    protected final List<ConfigTab> configTabs;
    protected ConfigButtonKeyBind activeKeyBindButton;
    protected int configWidth = 204;
    @Nullable protected ConfigInfoProvider hoverInfoProvider;
    @Nullable protected IDialogHandler dialogHandler;

    public BaseConfigScreen(int listX, int listY, String modId, @Nullable GuiScreen parent, List<ConfigTab> configTabs, String titleKey, Object... args)
    {
        super(listX, listY);

        this.modId = modId;
        this.title = StringUtils.translate(titleKey, args);
        this.configTabs = configTabs;
        this.setParent(parent);
    }

    @Override
    protected int getBrowserWidth()
    {
        return this.width - 20;
    }

    @Override
    protected int getBrowserHeight()
    {
        return this.height - 80;
    }

    public abstract void setCurrentTab(ConfigTab tab);

    @Nullable
    public abstract ConfigTab getCurrentTab();

    protected boolean useKeybindSearch()
    {
        return this.getCurrentTab() != null && this.getCurrentTab().useKeyBindSearch();
    }

    protected int getConfigWidth()
    {
        return this.getCurrentTab() != null ? this.getCurrentTab().getConfigWidth() : this.configWidth;
    }

    @Override
    public List<? extends ConfigInfo> getConfigs()
    {
        return this.getCurrentTab() != null ? this.getCurrentTab().getConfigOptions() : Collections.emptyList();
    }

    public BaseConfigScreen setConfigWidth(int configWidth)
    {
        this.configWidth = configWidth;
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
    public IDialogHandler getDialogHandler()
    {
        return this.dialogHandler;
    }

    public void setDialogHandler(@Nullable IDialogHandler handler)
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
    protected ConfigOptionListWidget<?> createListWidget(int listX, int listY)
    {
        return new ConfigOptionListWidget<>(listX, listY,
                this.getBrowserWidth(), this.getBrowserHeight(), this::getConfigs, this);
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

        this.updateListPosition(this.getListX(), 50 + (rows - 1) * 22);
    }

    protected int createTabButton(int x, int y, int width, ConfigTab tab)
    {
        ButtonGeneric button = new ButtonGeneric(x, y, width, 20, tab.getDisplayName());
        button.setEnabled(this.getCurrentTab() != tab);
        this.addButton(button, tab.getButtonActionListener(this));

        return button.getWidth() + 2;
    }

    @Override
    public void onGuiClosed()
    {
        /*
        if (this.getListWidget().wereConfigsModified())
        {
            this.getListWidget().applyPendingModifications();
            this.onSettingsChanged();
            this.getListWidget().clearConfigsModifiedFlag();
        }
        */

        Keyboard.enableRepeatEvents(false);
    }

    protected void onSettingsChanged()
    {
        ConfigManager.INSTANCE.onConfigsChanged(this.modId);

        if (this.hotkeyChangeListeners.size() > 0)
        {
            InputEventDispatcher.getKeyBindManager().updateUsedKeys();
        }
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
            this.activeKeyBindButton.onClearSelection();
            this.setActiveKeyBindButton(null);
            return true;
        }

        return false;
    }

    @Override
    public void clearOptions()
    {
        this.setActiveKeyBindButton(null);
        this.hotkeyChangeListeners.clear();
    }

    @Override
    public void addKeybindChangeListener(ConfigOptionChangeListenerKeybind listener)
    {
        this.hotkeyChangeListeners.add(listener);
    }

    @Override
    public ButtonPressDirtyListenerSimple getButtonPressListener()
    {
        return this.dirtyListener;
    }

    @Override
    public void setActiveKeyBindButton(@Nullable ConfigButtonKeyBind button)
    {
        if (this.activeKeyBindButton != null)
        {
            this.activeKeyBindButton.onClearSelection();
            this.updateKeybindButtons();
        }

        this.activeKeyBindButton = button;

        if (this.activeKeyBindButton != null)
        {
            this.activeKeyBindButton.onSelected();
        }
    }

    protected void updateKeybindButtons()
    {
        for (ConfigOptionChangeListenerKeybind listener : this.hotkeyChangeListeners)
        {
            listener.updateButtons();
        }
    }
}
