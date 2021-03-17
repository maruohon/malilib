package fi.dy.masa.malilib.overlay.widget;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fi.dy.masa.malilib.config.option.ConfigInfo;
import fi.dy.masa.malilib.config.util.ConfigUtils;
import fi.dy.masa.malilib.gui.config.ConfigStatusWidgetFactory;
import fi.dy.masa.malilib.gui.config.ConfigTabRegistry;
import fi.dy.masa.malilib.gui.config.ConfigWidgetRegistry;
import fi.dy.masa.malilib.message.InfoRendererWidget;
import fi.dy.masa.malilib.util.JsonUtils;
import fi.dy.masa.malilib.util.data.ConfigOnTab;

public class ConfigStatusIndicatorContainerWidget extends InfoRendererWidget
{
    protected final Set<ConfigOnTab> configs = new HashSet<>();
    protected final List<BaseConfigStatusIndicatorWidget<?>> widgets = new ArrayList<>();

    public ConfigStatusIndicatorContainerWidget()
    {
        this.shouldSerialize = true;
    }

    public Collection<ConfigOnTab> getConfigs()
    {
        return this.configs;
    }

    public void addWidgetForConfig(ConfigOnTab config)
    {
        if (this.configs.contains(config) == false)
        {
            ConfigStatusWidgetFactory<ConfigInfo> factory = ConfigWidgetRegistry.INSTANCE.getConfigStatusWidgetFactory(config.config);

            if (factory != null)
            {
                BaseConfigStatusIndicatorWidget<?> widget = factory.create(config.config, config);
                this.widgets.add(widget);
                this.configs.add(config);
            }
        }
    }

    public void removeWidget(BaseConfigStatusIndicatorWidget<?> widget)
    {
        this.widgets.remove(widget);
    }

    public List<BaseConfigStatusIndicatorWidget<?>> getStatusIndicatorWidgets()
    {
        return new ArrayList<>(this.widgets);
    }

    public void setStatusIndicatorWidgets(List<BaseConfigStatusIndicatorWidget<?>> widgets)
    {
        this.widgets.clear();
        this.widgets.addAll(widgets);
        this.configs.clear();

        for (BaseConfigStatusIndicatorWidget<?> widget : widgets)
        {
            this.configs.add(widget.getConfigOnTab());
        }
    }

    @Override
    public void renderAt(int x, int y, float z)
    {
        
    }

    @Override
    public JsonObject toJson()
    {
        JsonObject obj = super.toJson();
        JsonArray arr = new JsonArray();

        for (BaseConfigStatusIndicatorWidget<?> widget : this.widgets)
        {
            arr.add(widget.toJson());
        }

        obj.add("status_widgets", arr);

        return obj;
    }

    @Override
    public void fromJson(JsonObject obj)
    {
        super.fromJson(obj);

        this.widgets.clear();

        if (JsonUtils.hasArray(obj, "status_widgets") == false)
        {
            return;
        }

        Map<String, ConfigOnTab> configMap = ConfigUtils.getConfigIdToConfigMapFromTabs(ConfigTabRegistry.INSTANCE.getAllRegisteredConfigTabs());
        JsonArray arr = obj.get("status_widgets").getAsJsonArray();
        final int count = arr.size();

        for (int i = 0; i < count; i++)
        {
            JsonElement el = arr.get(i);

            if (el.isJsonObject())
            {
                JsonObject entryObj = el.getAsJsonObject();
                BaseConfigStatusIndicatorWidget<?> widget = BaseConfigStatusIndicatorWidget.fromJson(entryObj, configMap);

                if (widget != null)
                {
                    this.widgets.add(widget);
                }
            }
        }
    }
}
