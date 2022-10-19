package malilib.overlay.widget.sub;

import com.google.gson.JsonObject;
import malilib.MaLiLibReference;
import malilib.config.option.BooleanConfig;
import malilib.config.option.ConfigInfo;
import malilib.util.data.ConfigOnTab;

public class PlaceholderConfigStatusIndicatorWidget extends BaseConfigStatusIndicatorWidget<ConfigInfo>
{
    public static final BooleanConfig DUMMY_CONFIG = new BooleanConfig("?", false);

    protected JsonObject data = new JsonObject();

    public PlaceholderConfigStatusIndicatorWidget(ConfigInfo config, ConfigOnTab configOnTab)
    {
        this(config, configOnTab, MaLiLibReference.MOD_ID + ":csi_value_placeholder");
    }

    public PlaceholderConfigStatusIndicatorWidget(ConfigInfo config, ConfigOnTab configOnTab, String widgetTypeId)
    {
        super(config, configOnTab, widgetTypeId);
    }

    @Override
    public void updateState(boolean force)
    {
    }

    @Override
    public void fromJson(JsonObject obj)
    {
        this.data = obj;
    }

    @Override
    public JsonObject toJson()
    {
        return this.data;
    }
}
