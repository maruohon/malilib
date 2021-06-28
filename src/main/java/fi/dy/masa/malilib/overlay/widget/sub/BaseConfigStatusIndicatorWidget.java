package fi.dy.masa.malilib.overlay.widget.sub;

import java.util.Map;
import javax.annotation.Nullable;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.config.option.ConfigInfo;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.config.ConfigStatusWidgetRegistry;
import fi.dy.masa.malilib.gui.config.indicator.BaseConfigStatusIndicatorEditScreen;
import fi.dy.masa.malilib.gui.config.indicator.ConfigStatusWidgetFactory;
import fi.dy.masa.malilib.gui.util.GuiUtils;
import fi.dy.masa.malilib.gui.util.ScreenContext;
import fi.dy.masa.malilib.overlay.widget.BaseOverlayWidget;
import fi.dy.masa.malilib.render.text.MultiLineTextRenderSettings;
import fi.dy.masa.malilib.render.text.StyledTextLine;
import fi.dy.masa.malilib.util.JsonUtils;
import fi.dy.masa.malilib.util.data.ConfigOnTab;

public abstract class BaseConfigStatusIndicatorWidget<C extends ConfigInfo> extends BaseOverlayWidget
{
    protected final C config;
    protected final ConfigOnTab configOnTab;
    protected final MultiLineTextRenderSettings textSettings = new MultiLineTextRenderSettings();
    protected final String widgetTypeId;
    protected String name = "?";
    protected StyledTextLine styledName;
    @Nullable protected StyledTextLine valueDisplayText;
    protected boolean nameShadow = true;
    protected boolean valueShadow = true;
    protected int nameColor = 0xFFFFFFFF;
    protected int valueColor = 0xFF00FFFF;
    protected int valueRenderWidth;

    public BaseConfigStatusIndicatorWidget(C config, ConfigOnTab configOnTab, String widgetTypeId)
    {
        super();

        this.config = config;
        this.configOnTab = configOnTab;
        this.widgetTypeId = widgetTypeId;

        this.getTextSettings().setOddEvenBackgroundEnabled(true);
        this.setHeight(this.getLineHeight());
        this.setName(config.getDisplayName());
    }

    @Override
    public String getWidgetTypeId()
    {
        return this.widgetTypeId;
    }

    @Override
    public MultiLineTextRenderSettings getTextSettings()
    {
        return this.textSettings;
    }

    public ConfigOnTab getConfigOnTab()
    {
        return this.configOnTab;
    }

    public StyledTextLine getStyledName()
    {
        return this.styledName;
    }

    public String getName()
    {
        return this.name;
    }

    public void setName(String name)
    {
        this.name = name;
        this.styledName = StyledTextLine.of(name);
        this.geometryResizeNotifier.notifyContainerOfChanges();
    }

    public int getNameColor()
    {
        return this.nameColor;
    }

    public void setNameColor(int nameColor)
    {
        this.nameColor = nameColor;
    }

    public int getValueColor()
    {
        return this.valueColor;
    }

    public void setValueColor(int valueColor)
    {
        this.valueColor = valueColor;
    }

    public boolean getUseNameShadow()
    {
        return this.nameShadow;
    }

    public void setUseNameShadow(boolean nameShadow)
    {
        this.nameShadow = nameShadow;
    }

    public boolean getUseValueShadow()
    {
        return this.valueShadow;
    }

    public void setUseValueShadow(boolean valueShadow)
    {
        this.valueShadow = valueShadow;
    }

    public int getLabelRenderWidth()
    {
        return this.styledName.renderWidth;
    }

    public int getValueRenderWidth()
    {
        return this.valueRenderWidth;
    }

    public void openEditScreen()
    {
        BaseConfigStatusIndicatorEditScreen<?> screen = new BaseConfigStatusIndicatorEditScreen<>(this, GuiUtils.getCurrentScreen());
        BaseScreen.openScreen(screen);
    }

    public abstract void updateState(boolean force);

    @Override
    public void renderAt(int x, int y, float z, ScreenContext ctx)
    {
        int ty = y + this.getHeight() / 2 - this.getFontHeight() / 2;

        this.renderNameText(x, ty, z, ctx);
        this.renderValueDisplayText(x, ty, z, ctx);
    }

    protected void renderNameText(int x, int textY, float z, ScreenContext ctx)
    {
        this.renderTextLine(x, textY, z, this.nameColor, this.nameShadow, ctx, this.styledName);
    }

    protected void renderValueDisplayText(int x, int textY, float z, ScreenContext ctx)
    {
        if (this.valueDisplayText != null)
        {
            this.renderTextLine(x + this.getWidth() - this.valueDisplayText.renderWidth, textY, z,
                                this.valueColor, this.valueShadow, ctx, this.valueDisplayText);
        }
    }

    @Override
    public JsonObject toJson()
    {
        JsonObject obj = super.toJson();

        obj.addProperty("config_path", this.configOnTab.getConfigPath());
        obj.addProperty("name", this.name);
        obj.addProperty("name_color", this.nameColor);
        obj.addProperty("name_shadow", this.nameShadow);
        obj.addProperty("value_color", this.valueColor);
        obj.addProperty("value_shadow", this.valueShadow);

        return obj;
    }

    @Override
    public void fromJson(JsonObject obj)
    {
        super.fromJson(obj);

        if (JsonUtils.hasString(obj, "name"))
        {
            this.setName(obj.get("name").getAsString());
        }

        this.nameColor = JsonUtils.getIntegerOrDefault(obj, "name_color", 0xFFFFFFFF);
        this.valueColor = JsonUtils.getIntegerOrDefault(obj, "value_color", 0xFF00FFFF);
        this.nameShadow = JsonUtils.getBooleanOrDefault(obj, "name_shadow", true);
        this.valueShadow = JsonUtils.getBooleanOrDefault(obj, "value_shadow", true);
    }

    @Nullable
    public static <C extends  ConfigInfo> BaseConfigStatusIndicatorWidget<?> fromJson(JsonElement el, Map<String, ConfigOnTab> configMap)
    {
        if (el.isJsonObject() == false)
        {
            return null;
        }

        JsonObject obj = el.getAsJsonObject();

        if (JsonUtils.hasString(obj, "type") &&
            JsonUtils.hasString(obj, "config_path"))
        {
            String configPath = obj.get("config_path").getAsString();
            ConfigOnTab configOnTab = configMap.get(configPath);

            if (configOnTab != null)
            {
                String type = obj.get("type").getAsString();

                try
                {
                    @SuppressWarnings("unchecked")
                    ConfigStatusWidgetFactory<C> factory = (ConfigStatusWidgetFactory<C>) ConfigStatusWidgetRegistry.INSTANCE.getConfigStatusWidgetFactory(type);

                    if (factory != null)
                    {
                        @SuppressWarnings("unchecked")
                        BaseConfigStatusIndicatorWidget<?> widget = factory.create((C) configOnTab.config, configOnTab);
                        widget.fromJson(obj);
                        widget.updateState(true);
                        return widget;
                    }
                }
                catch (Exception e)
                {
                    MaLiLib.LOGGER.error("Failed to create a config status indicator widget of type '{}' for config '{}'",
                                         type, configPath, e);
                }
            }
        }

        return null;
    }
}
