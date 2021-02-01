package fi.dy.masa.malilib.gui.widget;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;
import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.config.value.OptionListConfigValue;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.config.KeybindSettingsScreen;
import fi.dy.masa.malilib.gui.config.liteloader.DialogHandler;
import fi.dy.masa.malilib.gui.util.GuiUtils;
import fi.dy.masa.malilib.input.CancelCondition;
import fi.dy.masa.malilib.input.KeyBind;
import fi.dy.masa.malilib.input.KeyBindSettings;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.StringUtils;

public class KeybindSettingsWidget extends BaseWidget
{
    public static final ResourceLocation TEXTURE = new ResourceLocation(MaLiLibReference.MOD_ID, "textures/gui/gui_widgets.png");

    protected final String keyBindName;
    protected final KeyBind keyBind;
    @Nullable protected final DialogHandler dialogHandler;

    public KeybindSettingsWidget(int x, int y, int width, int height, KeyBind keyBind,
                                 String keyBindName, @Nullable DialogHandler dialogHandler)
    {
        super(x, y, width, height);

        this.keyBind = keyBind;
        this.keyBindName = keyBindName;
        this.dialogHandler = dialogHandler;
        this.hoverInfoFactory.setStringListProvider("hover_info", this::rebuildHoverStrings);
    }

    @Override
    protected boolean onMouseClicked(int mouseX, int mouseY, int mouseButton)
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
            this.updateHoverStrings();
            return true;
        }

        return false;
    }

    @Override
    public void renderAt(int x, int y, float z, int mouseX, int mouseY, boolean isActiveGui, boolean hovered)
    {
        RenderUtils.color(1f, 1f, 1f, 1f);
        this.bindTexture(TEXTURE);

        KeyBindSettings settings = this.keyBind.getSettings();

        int w = 18;
        int vStart = 166;
        int v1 = vStart + (settings.getActivateOn().getIconIndex() * w);
        int v2 = vStart + (settings.getAllowExtraKeys() ? w : 0);
        int v3 = vStart + (settings.isOrderSensitive() ? w : 0);
        int v4 = vStart + (settings.isExclusive() ? w : 0);
        int v5 = vStart + (settings.shouldCancel() != CancelCondition.NEVER ? w : 0); // TODO add separate icons for ON_SUCCESS and ON_FAILURE
        int v6 = vStart + (settings.getAllowEmpty() ? w : 0);
        int v7 = 202 + settings.getContext().getIconIndex() * w;

        int edgeColor = this.keyBind.areSettingsModified() ? 0xFFFFBB33 : 0xFFFFFFFF;

        RenderUtils.renderRectangle(x    , y    , 20, 20, edgeColor, z);
        RenderUtils.renderRectangle(x + 1, y + 1, 18, 18, 0xFF000000, z);

        x += 1;
        y += 1;

        RenderUtils.color(1f, 1f, 1f, 1f);

        RenderUtils.renderTexturedRectangle(x, y,  0, v1, w, w, z);
        RenderUtils.renderTexturedRectangle(x, y, 18, v2, w, w, z);
        RenderUtils.renderTexturedRectangle(x, y, 36, v3, w, w, z);
        RenderUtils.renderTexturedRectangle(x, y, 54, v4, w, w, z);
        RenderUtils.renderTexturedRectangle(x, y, 72, v5, w, w, z);
        RenderUtils.renderTexturedRectangle(x, y, 90, v6, w, w, z);
        RenderUtils.renderTexturedRectangle(x, y, 90, v7, w, w, z);
    }

    protected List<String> rebuildHoverStrings()
    {
        List<String> lines = new ArrayList<>();
        KeyBindSettings settings = this.keyBind.getSettings();
        KeyBindSettings defaultSettings = this.keyBind.getDefaultSettings();

        lines.add(BaseScreen.TXT_WHITE + StringUtils.translate("malilib.gui.label.keybind_settings.title_advanced_keybind_settings"));

        this.addOptionText(lines, "malilib.gui.label.keybind_settings.activate_on", settings.getActivateOn(), defaultSettings.getActivateOn(), this::getDisplayString);
        this.addOptionText(lines, "malilib.gui.label.keybind_settings.context", settings.getContext(), defaultSettings.getContext(), this::getDisplayString);
        this.addOptionText(lines, "malilib.gui.label.keybind_settings.cancel_further", settings.shouldCancel(), defaultSettings.shouldCancel(), this::getDisplayString);

        this.addOptionText(lines, "malilib.gui.label.keybind_settings.allow_empty_keybind", settings.getAllowEmpty(), defaultSettings.getAllowEmpty(), this::getDisplayString);
        this.addOptionText(lines, "malilib.gui.label.keybind_settings.allow_extra_keys", settings.getAllowExtraKeys(), defaultSettings.getAllowExtraKeys(), this::getDisplayString);
        this.addOptionText(lines, "malilib.gui.label.keybind_settings.order_sensitive", settings.isOrderSensitive(), defaultSettings.isOrderSensitive(), this::getDisplayString);
        this.addOptionText(lines, "malilib.gui.label.keybind_settings.exclusive", settings.isExclusive(), defaultSettings.isExclusive(), this::getDisplayString);
        this.addOptionText(lines, "malilib.gui.label.keybind_settings.first_only", settings.getFirstOnly(), defaultSettings.getFirstOnly(), this::getDisplayString);
        this.addOptionText(lines, "malilib.gui.label.keybind_settings.priority", settings.getPriority(), defaultSettings.getPriority(), this::getDisplayString);

        lines.add("");
        StringUtils.addTranslatedLines(lines, "malilib.gui.label.keybind_settings.tips");

        return lines;
    }

    protected <T> void addOptionText(List<String> lines, String translationKey, T value, T defaultValue, Function<T, String> displayValueFunction)
    {
        boolean modified = value.equals(defaultValue) == false;
        String gray = BaseScreen.TXT_GRAY;
        String nameColor = modified ? BaseScreen.TXT_YELLOW : gray;

        String name = StringUtils.translate(translationKey);
        String valStr = displayValueFunction.apply(value);
        String defValStr = displayValueFunction.apply(defaultValue);
        String def = StringUtils.translate("malilib.gui.label.keybind_settings.default");
        String defFull = modified ? String.format(" %s[%s: %s%s]", gray, def, defValStr, gray) : "";

        lines.add(String.format("%s%s: %s%s", nameColor, name, valStr, defFull));
    }

    protected String getDisplayString(boolean value)
    {
        String strYes = StringUtils.translate("malilib.label.yes");
        String strNo = StringUtils.translate("malilib.label.no");
        return value ? (BaseScreen.TXT_GREEN + strYes) : (BaseScreen.TXT_RED + strNo);
    }

    protected String getDisplayString(OptionListConfigValue value)
    {
        return BaseScreen.TXT_BLUE + value.getDisplayName();
    }

    protected String getDisplayString(int value)
    {
        return BaseScreen.TXT_AQUA + value;
    }
}
