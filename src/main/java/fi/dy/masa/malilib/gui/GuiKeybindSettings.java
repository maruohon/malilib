package fi.dy.masa.malilib.gui;

import java.util.List;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.options.ConfigBase;
import fi.dy.masa.malilib.config.options.ConfigBoolean;
import fi.dy.masa.malilib.config.options.ConfigOptionList;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.ConfigButtonBoolean;
import fi.dy.masa.malilib.gui.button.ConfigButtonOptionList;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.gui.interfaces.IDialogHandler;
import fi.dy.masa.malilib.hotkeys.IKeybind;
import fi.dy.masa.malilib.hotkeys.KeyAction;
import fi.dy.masa.malilib.hotkeys.KeybindSettings;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.KeyCodes;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.resource.language.I18n;

public class GuiKeybindSettings extends GuiDialogBase
{
    protected final IKeybind keybind;
    protected final String keybindName;
    protected final ConfigOptionList cfgActivateOn  = new ConfigOptionList("Activate On", KeyAction.PRESS, "Does the keybind activate on press or release of the key combination");
    protected final ConfigOptionList cfgContext     = new ConfigOptionList("Context", KeybindSettings.Context.INGAME, "Where is the keybind usable");
    protected final ConfigBoolean cfgAllowExtra     = new ConfigBoolean("Allow Extra Keys", false, "Are extra keys allowed to be pressed to activate the keybind");
    protected final ConfigBoolean cfgOrderSensitive = new ConfigBoolean("Order Sensitive", false, "Should the keybind keys be pressed in the specific order they were defined in");
    protected final ConfigBoolean cfgExclusive      = new ConfigBoolean("Exclusive", false, "If true, then no other keybinds can have been activated before\nthe keybind in question, while some keys are being pressed");
    protected final ConfigBoolean cfgCancel         = new ConfigBoolean("Cancel further processing", false, "Cancel further (vanilla) processing when the keybind activates");
    protected final List<ConfigBase> configList;
    @Nullable protected final IDialogHandler dialogHandler;
    protected int labelWidth;
    protected int configWidth;

    public GuiKeybindSettings(IKeybind keybind, String name, @Nullable IDialogHandler dialogHandler, Screen parent)
    {
        this.minecraft = MinecraftClient.getInstance();
        this.keybind = keybind;
        this.keybindName = name;
        this.dialogHandler = dialogHandler;

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

        this.title = GuiBase.TXT_UNDERLINE + I18n.translate("malilib.gui.title.keybind_settings.advanced", this.keybindName);
        KeybindSettings settings = this.keybind.getSettings();

        this.cfgActivateOn.setOptionListValue(settings.getActivateOn());
        this.cfgContext.setOptionListValue(settings.getContext());
        this.cfgAllowExtra.setBooleanValue(settings.getAllowExtraKeys());
        this.cfgOrderSensitive.setBooleanValue(settings.isOrderSensitive());
        this.cfgExclusive.setBooleanValue(settings.isExclusive());
        this.cfgCancel.setBooleanValue(settings.shouldCancel());

        this.configList = ImmutableList.of(this.cfgActivateOn, this.cfgContext, this.cfgAllowExtra, this.cfgOrderSensitive, this.cfgExclusive, this.cfgCancel);
        this.labelWidth = GuiBase.getMaxNameLength(this.configList);
        this.configWidth = 100;

        int totalWidth = this.labelWidth + this.configWidth + 30;
        totalWidth = Math.max(totalWidth, this.minecraft.textRenderer.getStringWidth(this.title) + 20);

        this.setWidthAndHeight(totalWidth, this.configList.size() * 22 + 30);
        this.centerOnScreen();

        this.init(this.minecraft, this.dialogWidth, this.dialogHeight);
    }

    @Override
    public void init()
    {
        this.clearElements();

        Listener listener = new Listener(); // dummy

        int x = this.dialogLeft + 10;
        int y = this.dialogTop + 24;

        for (ConfigBase config : this.configList)
        {
            this.addConfig(x, y, this.labelWidth, this.configWidth, config, listener);
            y += 22;
        }
    }

    protected void addConfig(int x, int y, int labelWidth, int configWidth, ConfigBase config, Listener listener)
    {
        this.addLabel(x, y + 4, labelWidth, 10, 0xFFFFFFFF, config.getName());
        x += labelWidth + 10;

        if (config instanceof ConfigBoolean)
        {
            this.addButton(new ConfigButtonBoolean(0, x, y, configWidth, 20, (ConfigBoolean) config), listener);
        }
        else if (config instanceof ConfigOptionList)
        {
            this.addButton(new ConfigButtonOptionList(0, x, y, configWidth, 20, (ConfigOptionList) config), listener);
        }
    }

    @Override
    public void removed()
    {
        KeyAction activateOn = (KeyAction) this.cfgActivateOn.getOptionListValue();
        KeybindSettings.Context context = (KeybindSettings.Context) this.cfgContext.getOptionListValue();
        boolean allowExtraKeys = this.cfgAllowExtra.getBooleanValue();
        boolean orderSensitive = this.cfgOrderSensitive.getBooleanValue();
        boolean exclusive = this.cfgExclusive.getBooleanValue();
        boolean cancel = this.cfgCancel.getBooleanValue();

        KeybindSettings settingsNew = KeybindSettings.create(context, activateOn, allowExtraKeys, orderSensitive, exclusive, cancel);
        this.keybind.setSettings(settingsNew);

        super.removed();
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        if (this.getParent() != null)
        {
            this.getParent().render(mouseX, mouseY, partialTicks);
        }

        super.render(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void drawScreenBackground(int mouseX, int mouseY)
    {
        RenderUtils.drawOutlinedBox(this.dialogLeft, this.dialogTop, this.dialogWidth, this.dialogHeight, 0xFF000000, COLOR_HORIZONTAL_BAR);
    }

    @Override
    protected void drawTitle(int mouseX, int mouseY, float partialTicks)
    {
        this.drawString(this.textRenderer, this.title, this.dialogLeft + 10, this.dialogTop + 6, COLOR_WHITE);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        return this.onKeyTyped(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean onKeyTyped(int keyCode, int scanCode, int modifiers)
    {
        if (keyCode == KeyCodes.KEY_ESCAPE && this.dialogHandler != null)
        {
            this.dialogHandler.closeDialog();
            return true;
        }
        else
        {
            return super.onKeyTyped(keyCode, scanCode, modifiers);
        }
    }

    protected static class Listener implements IButtonActionListener<ButtonGeneric>
    {
        @Override
        public void actionPerformed(ButtonGeneric control)
        {
        }

        @Override
        public void actionPerformedWithButton(ButtonGeneric control, int mouseButton)
        {
        }
    }
}
