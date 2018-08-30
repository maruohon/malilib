package fi.dy.masa.malilib.gui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nullable;
import org.lwjgl.input.Keyboard;
import fi.dy.masa.malilib.config.ConfigManager;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.IConfigValue;
import fi.dy.masa.malilib.config.gui.ButtonPressDirtyListenerSimple;
import fi.dy.masa.malilib.config.gui.ConfigOptionChangeListenerKeybind;
import fi.dy.masa.malilib.event.InputEventHandler;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ConfigButtonKeybind;
import fi.dy.masa.malilib.gui.interfaces.IKeybindConfigGui;
import fi.dy.masa.malilib.gui.widgets.WidgetConfigOption;
import fi.dy.masa.malilib.gui.widgets.WidgetListConfigOptions;
import fi.dy.masa.malilib.reference.Reference;
import net.minecraft.client.gui.GuiScreen;

public abstract class GuiConfigsBase extends GuiListBase<IConfigValue, WidgetConfigOption, WidgetListConfigOptions> implements IKeybindConfigGui
{
    @Nullable
    protected final GuiScreen parent;
    protected final List<ConfigOptionChangeListenerKeybind> hotkeyChangeListeners = new ArrayList<>();
    protected final ButtonPressDirtyListenerSimple<ButtonBase> dirtyListener = new ButtonPressDirtyListenerSimple<>();
    protected final String modId;
    protected final List<String> initialConfigValues = new ArrayList<>();
    protected boolean configsDirty;

    protected ConfigButtonKeybind activeKeybindButton;
    protected int maxTextfieldTextLength = 256;

    public GuiConfigsBase(int x, int y, @Nullable GuiScreen parent)
    {
        super(x, y);

        this.modId = Reference.MOD_ID;
        this.parent = parent;
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

    protected int getConfigWidth()
    {
        return 204;
    }

    @Override
    protected WidgetListConfigOptions createListWidget(int listX, int listY)
    {
        return new WidgetListConfigOptions(listX, listY, this.getBrowserWidth(), this.getBrowserHeight(), this.getConfigWidth(), this);
    }

    @Override
    public void initGui()
    {
        super.initGui();

        Keyboard.enableRepeatEvents(true);
    }

    @Override
    public void onGuiClosed()
    {
        if (this.widget.wereConfigsModified())
        {
            this.widget.applyPendingModifications();
            this.onSettingsChanged();
        }

        Keyboard.enableRepeatEvents(false);
    }

    protected void onSettingsChanged()
    {
        ConfigManager.getInstance().onConfigsChanged(this.modId);

        if (this.hotkeyChangeListeners.size() > 0)
        {
            InputEventHandler.getInstance().updateUsedKeys();
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
            if (this.widget.onKeyTyped(typedChar, keyCode))
            {
                return true;
            }

            if (keyCode == Keyboard.KEY_ESCAPE)
            {
                this.mc.displayGuiScreen(this.parent);
                return true;
            }

            return false;
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
    public ButtonPressDirtyListenerSimple<ButtonBase> getButtonPressListener()
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

    public int getMaxLabelWidth(Collection<IConfigValue> entries)
    {
        int maxWidth = 0;

        for (IConfigBase entry : entries)
        {
            maxWidth = Math.max(maxWidth, this.mc.fontRenderer.getStringWidth(entry.getName()));
        }

        return maxWidth;
    }
}
