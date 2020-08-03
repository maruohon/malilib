package fi.dy.masa.malilib.gui.widget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;
import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.config.KeybindSettingsScreen;
import fi.dy.masa.malilib.gui.interfaces.IDialogHandler;
import fi.dy.masa.malilib.gui.util.GuiUtils;
import fi.dy.masa.malilib.gui.widget.list.DataListWidget;
import fi.dy.masa.malilib.input.IKeyBind;
import fi.dy.masa.malilib.input.KeyAction;
import fi.dy.masa.malilib.input.KeyBindSettings;
import fi.dy.masa.malilib.input.KeyBindSettings.Context;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.StringUtils;

public class WidgetKeybindSettings extends WidgetBase
{
    public static final ResourceLocation TEXTURE = new ResourceLocation(MaLiLibReference.MOD_ID, "textures/gui/gui_widgets.png");

    protected final String keyBindName;
    protected final IKeyBind keyBind;
    protected final KeyBindSettings settings;
    protected final KeyBindSettings defaultSettings;
    protected final DataListWidget<?> widgetList;
    @Nullable protected final IDialogHandler dialogHandler;

    public WidgetKeybindSettings(int x, int y, int width, int height,
                                 IKeyBind keyBind, String keyBindName, DataListWidget<?> widgetList, @Nullable IDialogHandler dialogHandler)
    {
        super(x, y, width, height);

        this.keyBind = keyBind;
        this.keyBindName = keyBindName;
        this.settings = keyBind.getSettings();
        this.defaultSettings = keyBind.getDefaultSettings();
        this.widgetList = widgetList;
        this.dialogHandler = dialogHandler;
    }

    @Override
    protected boolean onMouseClickedImpl(int mouseX, int mouseY, int mouseButton)
    {
        if (mouseButton == 0)
        {
            if (this.dialogHandler != null)
            {
                this.dialogHandler.openDialog(new KeybindSettingsScreen(this.keyBind, this.keyBindName, this.dialogHandler, GuiUtils.getCurrentScreen()));
            }
            else
            {
                BaseScreen.openPopupGui(new KeybindSettingsScreen(this.keyBind, this.keyBindName, null, GuiUtils.getCurrentScreen()));
            }

            return true;
        }
        // Reset the settings to defaults on right click
        else if (mouseButton == 1)
        {
            this.keyBind.resetSettingsToDefaults();
            this.widgetList.refreshEntries();
            return true;
        }

        return false;
    }

    @Override
    public void render(int mouseX, int mouseY, boolean isActiveGui, boolean hovered)
    {
        RenderUtils.color(1f, 1f, 1f, 1f);
        this.bindTexture(TEXTURE);

        int w = 18;
        int v1 = this.settings.getActivateOn().ordinal() * w;
        int v2 = this.settings.getAllowExtraKeys() ? w : 0;
        int v3 = this.settings.isOrderSensitive() ? w : 0;
        int v4 = this.settings.isExclusive() ? w : 0;
        int v5 = this.settings.shouldCancel() ? w : 0;
        int v6 = this.settings.getAllowEmpty() ? w : 0;
        int v7 = this.settings.getContext().ordinal() * w + 54;

        int x = this.getX();
        int y = this.getY();
        int z = this.getZLevel();
        int edgeColor = this.keyBind.areSettingsModified() ? 0xFFFFBB33 : 0xFFFFFFFF;

        RenderUtils.drawRect(x    , y    , 20, 20, edgeColor, z);
        RenderUtils.drawRect(x + 1, y + 1, 18, 18, 0xFF000000, z);

        x += 1;
        y += 1;

        RenderUtils.color(1f, 1f, 1f, 1f);

        RenderUtils.drawTexturedRect(x, y,  0, v1, w, w, z);
        RenderUtils.drawTexturedRect(x, y, 18, v2, w, w, z);
        RenderUtils.drawTexturedRect(x, y, 36, v3, w, w, z);
        RenderUtils.drawTexturedRect(x, y, 54, v4, w, w, z);
        RenderUtils.drawTexturedRect(x, y, 72, v5, w, w, z);
        RenderUtils.drawTexturedRect(x, y, 90, v6, w, w, z);
        RenderUtils.drawTexturedRect(x, y,  0, v7, w, w, z);
    }

    @Override
    public void postRenderHovered(int mouseX, int mouseY, boolean isActiveGui, int hoveredWidgetId)
    {
        List<String> text = new ArrayList<>();
        String name, val, nameColor;
        boolean modified;

        text.add(BaseScreen.TXT_WHITE + BaseScreen.TXT_UNDERLINE + StringUtils.translate("malilib.gui.label.keybind_settings.title_advanced_keybind_settings"));

        name = StringUtils.translate("malilib.gui.label.keybind_settings.activate_on");
        KeyAction action = this.settings.getActivateOn();
        modified = action != this.defaultSettings.getActivateOn();
        nameColor = modified ? BaseScreen.TXT_YELLOW : BaseScreen.TXT_GRAY;
        val = BaseScreen.TXT_BLUE + action.name();
        text.add(String.format("%s%s: %s", nameColor, name, val));

        name = StringUtils.translate("malilib.gui.label.keybind_settings.context");
        Context context = this.settings.getContext();
        val = BaseScreen.TXT_BLUE + context.name();
        nameColor = context != this.defaultSettings.getContext() ? BaseScreen.TXT_YELLOW : BaseScreen.TXT_GRAY;
        text.add(String.format("%s%s: %s", nameColor, name, val));

        this.addBooleanOptionText(text, "malilib.gui.label.keybind_settings.allow_empty_keybind", this.settings.getAllowEmpty(), this.defaultSettings.getAllowEmpty());
        this.addBooleanOptionText(text, "malilib.gui.label.keybind_settings.allow_extra_keys", this.settings.getAllowExtraKeys(), this.defaultSettings.getAllowExtraKeys());
        this.addBooleanOptionText(text, "malilib.gui.label.keybind_settings.order_sensitive", this.settings.isOrderSensitive(), this.defaultSettings.isOrderSensitive());
        this.addBooleanOptionText(text, "malilib.gui.label.keybind_settings.exclusive", this.settings.isExclusive(), this.defaultSettings.isExclusive());
        this.addBooleanOptionText(text, "malilib.gui.label.keybind_settings.cancel_further", this.settings.shouldCancel(), this.defaultSettings.shouldCancel());

        text.add("");
        String[] parts = StringUtils.translate("malilib.gui.label.keybind_settings.tips").split("\\\\n");

        Collections.addAll(text, parts);

        RenderUtils.drawHoverText(mouseX + 10, mouseY, this.getZLevel(), text);
    }

    private void addBooleanOptionText(List<String> lines, String translationKey, boolean value, boolean defaultValue)
    {
        boolean modified = value != defaultValue;
        String name = StringUtils.translate(translationKey);
        String strYes = StringUtils.translate("malilib.gui.label.yes");
        String strNo = StringUtils.translate("malilib.gui.label.no");
        String valStr = value ? (BaseScreen.TXT_GREEN + strYes) : (BaseScreen.TXT_RED + strNo);
        String defaultValStr = defaultValue ? (BaseScreen.TXT_GREEN + strYes) : (BaseScreen.TXT_RED + strNo);
        String nameColor = modified ? BaseScreen.TXT_YELLOW : BaseScreen.TXT_GRAY;
        String gray = BaseScreen.TXT_GRAY;
        String def = StringUtils.translate("malilib.gui.label.keybind_settings.default");
        String defaultValueFull = modified ? String.format(" %s[%s: %s%s]", gray, def, defaultValStr, gray) : "";

        lines.add(String.format("%s%s: %s%s", nameColor, name, valStr, defaultValueFull));
    }
}
