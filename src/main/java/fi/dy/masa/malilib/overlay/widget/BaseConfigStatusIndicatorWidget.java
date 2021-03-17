package fi.dy.masa.malilib.overlay.widget;

import java.util.Map;
import javax.annotation.Nullable;
import com.google.gson.JsonObject;
import fi.dy.masa.malilib.config.option.ConfigInfo;
import fi.dy.masa.malilib.gui.config.ConfigStatusWidgetFactory;
import fi.dy.masa.malilib.gui.config.ConfigWidgetRegistry;
import fi.dy.masa.malilib.gui.widget.ContainerWidget;
import fi.dy.masa.malilib.render.text.StyledTextLine;
import fi.dy.masa.malilib.util.JsonUtils;
import fi.dy.masa.malilib.util.data.ConfigOnTab;

public abstract class BaseConfigStatusIndicatorWidget<C extends ConfigInfo> extends ContainerWidget
{
    protected final C config;
    protected final ConfigOnTab configOnTab;
    protected String name;
    protected StyledTextLine styledName;
    protected boolean nameShadow = true;
    protected boolean valueShadow = true;
    protected int nameColor = 0xFFFFFFFF;
    protected int valueColor = 0xFF00FFFF;

    public BaseConfigStatusIndicatorWidget(C config, ConfigOnTab configOnTab)
    {
        super(0, 0, -1, -1);

        this.config = config;
        this.configOnTab = configOnTab;

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

    public String getName()
    {
        return this.name;
    }

    public void setName(String name)
    {
        this.name = name;
        this.styledName = StyledTextLine.of(name);
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

    @Override
    public void renderAt(int x, int y, float z, int mouseX, int mouseY, boolean isActiveGui, boolean hovered)
    {
        int ty = y + this.getHeight() / 2 - this.fontHeight / 2;
        this.renderTextLine(x + 2, ty, z, this.nameColor, this.nameShadow, this.styledName);
    }

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

    public void fromJson(JsonObject obj)
    {
        if (JsonUtils.hasString(obj, "name"))
        {
            this.name = obj.get("name").getAsString();
        }

        if (JsonUtils.hasInteger(obj, "name_color"))
        {
            this.nameColor = obj.get("name_color").getAsInt();
        }

        if (JsonUtils.hasInteger(obj, "value_color"))
        {
            this.valueColor = obj.get("value_color").getAsInt();
        }

        if (JsonUtils.hasBoolean(obj, "name_shadow"))
        {
            this.nameShadow = obj.get("name_shadow").getAsBoolean();
        }

        if (JsonUtils.hasBoolean(obj, "value_shadow"))
        {
            this.valueShadow = obj.get("value_shadow").getAsBoolean();
        }
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
                    return widget;
                }
            }
        }

        return null;
    }
}
