package fi.dy.masa.malilib.gui.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import org.lwjgl.input.Keyboard;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.gui.GuiScreen;
import fi.dy.masa.malilib.config.ConfigManager;
import fi.dy.masa.malilib.config.option.IConfigBase;
import fi.dy.masa.malilib.event.dispatch.InputEventDispatcher;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.GuiListBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.ConfigButtonKeybind;
import fi.dy.masa.malilib.gui.config.GuiConfigsBase.ConfigOptionWrapper;
import fi.dy.masa.malilib.gui.interfaces.IConfigGuiTab;
import fi.dy.masa.malilib.gui.interfaces.IConfigInfoProvider;
import fi.dy.masa.malilib.gui.interfaces.IDialogHandler;
import fi.dy.masa.malilib.gui.interfaces.IKeybindConfigGui;
import fi.dy.masa.malilib.gui.listener.ButtonPressDirtyListenerSimple;
import fi.dy.masa.malilib.gui.listener.ConfigOptionChangeListenerKeybind;
import fi.dy.masa.malilib.gui.util.GuiUtils;
import fi.dy.masa.malilib.gui.widget.WidgetConfigOption;
import fi.dy.masa.malilib.gui.widget.WidgetListConfigOptions;
import fi.dy.masa.malilib.util.StringUtils;

public abstract class GuiConfigsBase extends GuiListBase<ConfigOptionWrapper, WidgetConfigOption, WidgetListConfigOptions> implements IKeybindConfigGui
{
    protected final List<ConfigOptionChangeListenerKeybind> hotkeyChangeListeners = new ArrayList<>();
    protected final ButtonPressDirtyListenerSimple dirtyListener = new ButtonPressDirtyListenerSimple();
    protected final String modId;
    protected final List<String> initialConfigValues = new ArrayList<>();
    protected final List<IConfigGuiTab> configTabs;
    protected ConfigButtonKeybind activeKeybindButton;
    protected int configWidth = 204;
    @Nullable protected IConfigInfoProvider hoverInfoProvider;
    @Nullable protected IDialogHandler dialogHandler;

    public GuiConfigsBase(int listX, int listY, String modId, @Nullable GuiScreen parent, List<IConfigGuiTab> configTabs, String titleKey, Object... args)
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

    public abstract void setCurrentTab(IConfigGuiTab tab);

    @Nullable
    public abstract IConfigGuiTab getCurrentTab();

    protected boolean useKeybindSearch()
    {
        return this.getCurrentTab() != null && this.getCurrentTab().useKeybindSearch();
    }

    protected int getConfigWidth()
    {
        return this.getCurrentTab() != null ? this.getCurrentTab().getConfigWidth() : this.configWidth;
    }

    @Override
    public List<ConfigOptionWrapper> getConfigs()
    {
        return this.getCurrentTab() != null ? ConfigOptionWrapper.createFor(this.getCurrentTab().getConfigOptions()) : Collections.emptyList();
    }

    public GuiConfigsBase setConfigWidth(int configWidth)
    {
        this.configWidth = configWidth;
        return this;
    }

    public GuiConfigsBase setHoverInfoProvider(IConfigInfoProvider provider)
    {
        this.hoverInfoProvider = provider;
        return this;
    }

    @Nullable
    public WidgetListConfigOptions getConfigsListWidget()
    {
        return this.getListWidget();
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
    public IConfigInfoProvider getHoverInfoProvider()
    {
        return this.hoverInfoProvider;
    }

    @Override
    protected WidgetListConfigOptions createListWidget(int listX, int listY)
    {
        return new WidgetListConfigOptions(listX, listY,
                this.getBrowserWidth(), this.getBrowserHeight(), this.getConfigWidth(), this.zLevel, this.useKeybindSearch(), this);
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

        for (IConfigGuiTab tab : this.configTabs)
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

    protected int createTabButton(int x, int y, int width, IConfigGuiTab tab)
    {
        ButtonGeneric button = new ButtonGeneric(x, y, width, 20, tab.getDisplayName());
        button.setEnabled(this.getCurrentTab() != tab);
        this.addButton(button, tab.getButtonActionListener(this));

        return button.getWidth() + 2;
    }

    @Override
    public void onGuiClosed()
    {
        if (this.getListWidget().wereConfigsModified())
        {
            this.getListWidget().applyPendingModifications();
            this.onSettingsChanged();
            this.getListWidget().clearConfigsModifiedFlag();
        }

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
        if (this.activeKeybindButton != null)
        {
            this.activeKeybindButton.onKeyPressed(keyCode);
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
                GuiBase.openGui(this.getParent());
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
        if (this.activeKeybindButton != null)
        {
            this.activeKeybindButton.onClearSelection();
            this.setActiveKeybindButton(null);
            return true;
        }

        return false;
    }

    @Override
    public void clearOptions()
    {
        this.setActiveKeybindButton(null);
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
    public void setActiveKeybindButton(@Nullable ConfigButtonKeybind button)
    {
        if (this.activeKeybindButton != null)
        {
            this.activeKeybindButton.onClearSelection();
            this.updateKeybindButtons();
        }

        this.activeKeybindButton = button;

        if (this.activeKeybindButton != null)
        {
            this.activeKeybindButton.onSelected();
        }
    }

    protected void updateKeybindButtons()
    {
        for (ConfigOptionChangeListenerKeybind listener : this.hotkeyChangeListeners)
        {
            listener.updateButtons();
        }
    }

    public static class ConfigOptionWrapper
    {
        private final Type type;
        @Nullable private final IConfigBase config;
        @Nullable private final String labelPrefix;
        @Nullable private final String label;

        public ConfigOptionWrapper(IConfigBase config)
        {
            this(null, config);
        }

        public ConfigOptionWrapper(@Nullable String labelPrefix, @Nullable IConfigBase config)
        {
            this.type = Type.CONFIG;
            this.config = config;
            this.label = null;
            this.labelPrefix = labelPrefix;
        }

        public ConfigOptionWrapper(@Nullable String label)
        {
            this.type = Type.LABEL;
            this.config = null;
            this.label = label;
            this.labelPrefix = null;
        }

        public Type getType()
        {
            return this.type;
        }

        @Nullable
        public IConfigBase getConfig()
        {
            return this.config;
        }

        @Nullable
        public String getLabelPrefix()
        {
            return this.labelPrefix;
        }

        @Nullable
        public String getLabel()
        {
            return this.label;
        }

        public static List<ConfigOptionWrapper> createFor(Collection<? extends IConfigBase> configs)
        {
            ImmutableList.Builder<ConfigOptionWrapper> builder = ImmutableList.builder();

            for (IConfigBase config : configs)
            {
                builder.add(new ConfigOptionWrapper(config));
            }

            return builder.build();
        }

        public enum Type
        {
            CONFIG,
            LABEL;
        }
    }
}
