package fi.dy.masa.malilib.overlay.widget.sub;

import java.util.Map;
import javax.annotation.Nullable;
import com.google.gson.JsonObject;
import fi.dy.masa.malilib.config.option.ConfigInfo;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.config.BaseConfigStatusIndicatorEditScreen;
import fi.dy.masa.malilib.gui.config.ConfigStatusWidgetFactory;
import fi.dy.masa.malilib.gui.config.ConfigWidgetRegistry;
import fi.dy.masa.malilib.gui.util.GuiUtils;
import fi.dy.masa.malilib.overlay.widget.InfoRendererWidget;
import fi.dy.masa.malilib.render.text.StyledTextLine;
import fi.dy.masa.malilib.util.JsonUtils;
import fi.dy.masa.malilib.util.data.ConfigOnTab;

public abstract class BaseConfigStatusIndicatorWidget<C extends ConfigInfo> extends InfoRendererWidget
{
    protected final C config;
    protected final ConfigOnTab configOnTab;
    protected StyledTextLine styledName;
    @Nullable protected StyledTextLine valueDisplayText;
    protected boolean nameShadow = true;
    protected boolean valueShadow = true;
    protected int nameColor = 0xFFFFFFFF;
    protected int valueColor = 0xFF00FFFF;
    protected int valueRenderWidth;

    public BaseConfigStatusIndicatorWidget(C config, ConfigOnTab configOnTab)
    {
        this.config = config;
        this.configOnTab = configOnTab;
        this.getTextSettings().setUseOddEvenBackground(true);

        this.setHeight(this.lineHeight);
        this.setName(config.getDisplayName());
    }

    public ConfigOnTab getConfigOnTab()
    {
        return this.configOnTab;
    }

    public StyledTextLine getStyledName()
    {
        return this.styledName;
    }

    @Override
    public void setName(String name)
    {
        super.setName(name);
        this.styledName = StyledTextLine.of(name);
        this.notifyContainerOfChanges(true);
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
        BaseConfigStatusIndicatorEditScreen<?, ?> screen = new BaseConfigStatusIndicatorEditScreen<>(this, GuiUtils.getCurrentScreen());
        BaseScreen.openScreen(screen);
    }

    public abstract void updateState(boolean force);

    @Override
    protected void renderContents(int x, int y, float z)
    {
        int ty = y + this.getHeight() / 2 - this.fontHeight / 2;
        this.renderTextLine(x, ty, z, this.nameColor, this.nameShadow, this.styledName);

        if (this.valueDisplayText != null)
        {
            this.renderTextLine(this.getRight() - this.valueDisplayText.renderWidth, ty, z,
                                this.valueColor, this.valueShadow, this.valueDisplayText);
        }
    }

    @Override
    public JsonObject toJson()
    {
        JsonObject obj = new JsonObject();

        obj.addProperty("type", this.getClass().getName());
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
        if (JsonUtils.hasString(obj, "name"))
        {
            this.setName(obj.get("name").getAsString());
        }

        if (JsonUtils.hasInteger(obj, "name_color"))
        {
            this.nameColor = obj.get("name_color").getAsInt();
        }

        if (JsonUtils.hasInteger(obj, "value_color"))
        {
            this.valueColor = obj.get("value_color").getAsInt();
        }

        this.nameShadow = JsonUtils.getBooleanOrDefault(obj, "name_shadow", true);
        this.valueShadow = JsonUtils.getBooleanOrDefault(obj, "value_shadow", true);
    }

    @Nullable
    public static <C extends  ConfigInfo> BaseConfigStatusIndicatorWidget<?> fromJson(JsonObject obj, Map<String, ConfigOnTab> configMap)
    {
        if (JsonUtils.hasString(obj, "type") &&
            JsonUtils.hasString(obj, "config_path"))
        {
            String configPath = obj.get("config_path").getAsString();
            ConfigOnTab configOnTab = configMap.get(configPath);

            if (configOnTab != null)
            {
                String type = obj.get("type").getAsString();
                @SuppressWarnings("unchecked")
                ConfigStatusWidgetFactory<C> factory = (ConfigStatusWidgetFactory<C>) ConfigWidgetRegistry.INSTANCE.getConfigStatusWidgetFactory(type);

                if (factory != null)
                {
                    @SuppressWarnings("unchecked")
                    BaseConfigStatusIndicatorWidget<?> widget = factory.create((C) configOnTab.config, configOnTab);
                    widget.fromJson(obj);
                    widget.updateState(true);
                    return widget;
                }
            }
        }

        return null;
    }
}
