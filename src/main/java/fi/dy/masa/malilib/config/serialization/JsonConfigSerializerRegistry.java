package fi.dy.masa.malilib.config.serialization;

import java.io.File;
import java.util.HashMap;
import javax.annotation.Nullable;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import fi.dy.masa.malilib.config.option.BooleanConfig;
import fi.dy.masa.malilib.config.option.ColorConfig;
import fi.dy.masa.malilib.config.option.ConfigInfo;
import fi.dy.masa.malilib.config.option.DirectoryConfig;
import fi.dy.masa.malilib.config.option.DoubleConfig;
import fi.dy.masa.malilib.config.option.DualColorConfig;
import fi.dy.masa.malilib.config.option.FileConfig;
import fi.dy.masa.malilib.config.option.HotkeyConfig;
import fi.dy.masa.malilib.config.option.HotkeyedBooleanConfig;
import fi.dy.masa.malilib.config.option.IntegerConfig;
import fi.dy.masa.malilib.config.option.OptionListConfig;
import fi.dy.masa.malilib.config.option.OptionalDirectoryConfig;
import fi.dy.masa.malilib.config.option.StringConfig;
import fi.dy.masa.malilib.config.option.Vec2iConfig;
import fi.dy.masa.malilib.config.option.list.BlackWhiteListConfig;
import fi.dy.masa.malilib.config.option.list.ValueListConfig;

public class JsonConfigSerializerRegistry
{
    private final HashMap<Class<? extends ConfigInfo>, JsonConfigSerializer<?>> serializers = new HashMap<>();
    private final HashMap<Class<? extends ConfigInfo>, JsonConfigDeSerializer<?>> deSerializers = new HashMap<>();

    public JsonConfigSerializerRegistry()
    {
        this.registerDefaultSerializers();
    }

    /**
     * Registers a config option serializer and deserializer
     */
    public <C extends ConfigInfo>
    void registerSerializers(Class<C> type, JsonConfigSerializer<C> serializer, JsonConfigDeSerializer<C> deSerializer)
    {
        this.serializers.put(type, serializer);
        this.deSerializers.put(type, deSerializer);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public <C extends ConfigInfo> JsonConfigSerializer<C> getSerializer(ConfigInfo config)
    {
        JsonConfigSerializer<C> serializer = (JsonConfigSerializer<C>) this.serializers.get(config.getClass());

        if (serializer == null)
        {
            Class<?> clazz = config.getClass().getSuperclass();

            while (clazz != null && ConfigInfo.class.isAssignableFrom(clazz))
            {
                serializer = (JsonConfigSerializer<C>) this.serializers.get(clazz);

                if (serializer != null)
                {
                    return serializer;
                }

                clazz = clazz.getSuperclass();
            }
        }

        return serializer;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public <C extends ConfigInfo> JsonConfigDeSerializer<C> getDeSerializer(ConfigInfo config)
    {
        JsonConfigDeSerializer<C> deSerializer = (JsonConfigDeSerializer<C>) this.deSerializers.get(config.getClass());

        if (deSerializer == null)
        {
            Class<?> clazz = config.getClass().getSuperclass();

            while (clazz != null && ConfigInfo.class.isAssignableFrom(clazz))
            {
                deSerializer = (JsonConfigDeSerializer<C>) this.deSerializers.get(clazz);

                if (deSerializer != null)
                {
                    return deSerializer;
                }

                clazz = clazz.getSuperclass();
            }
        }

        return deSerializer;
    }

    protected void registerDefaultSerializers()
    {
        this.registerSerializers(BooleanConfig.class,   (c) -> new JsonPrimitive(c.getBooleanValue()),  (c, d, n) -> JsonConfigSerializers.loadGenericConfig(c::loadValueFromConfig,        d::getAsBoolean, d, n));
        this.registerSerializers(ColorConfig.class,     (c) -> new JsonPrimitive(c.getStringValue()),   (c, d, n) -> JsonConfigSerializers.loadGenericConfig(c::loadColorValueFromString,   d::getAsString, d, n));
        this.registerSerializers(DirectoryConfig.class, (c) -> new JsonPrimitive(c.getStringValue()),   (c, d, n) -> JsonConfigSerializers.loadGenericConfig(c::loadValueFromConfig,        () -> new File(d.getAsString()), d, n));
        this.registerSerializers(DoubleConfig.class,    (c) -> new JsonPrimitive(c.getDoubleValue()),   (c, d, n) -> JsonConfigSerializers.loadGenericConfig(c::loadValueFromConfig,        d::getAsDouble, d, n));
        this.registerSerializers(FileConfig.class,      (c) -> new JsonPrimitive(c.getStringValue()),   (c, d, n) -> JsonConfigSerializers.loadGenericConfig(c::loadValueFromConfig,        () -> new File(d.getAsString()), d, n));
        this.registerSerializers(HotkeyConfig.class,    (c) -> c.getKeyBind().getAsJsonElement(),       HotkeyConfig::loadHotkeyValueFromConfig);
        this.registerSerializers(IntegerConfig.class,   (c) -> new JsonPrimitive(c.getIntegerValue()),  (c, d, n) -> JsonConfigSerializers.loadGenericConfig(c::loadValueFromConfig,        d::getAsInt, d, n));
        this.registerSerializers(StringConfig.class,    (c) -> new JsonPrimitive(c.getValue()),         (c, d, n) -> JsonConfigSerializers.loadGenericConfig(c::loadValueFromConfig,        d::getAsString, d, n));

        this.registerSerializers(BlackWhiteListConfig.class,    JsonConfigSerializers::saveBlackWhiteListConfig,    JsonConfigSerializers::loadBlackWhiteListConfig);
        this.registerSerializers(DualColorConfig.class,         JsonConfigSerializers::saveDualColorConfig,         JsonConfigSerializers::loadDualColorConfig);
        this.registerSerializers(HotkeyedBooleanConfig.class,   JsonConfigSerializers::saveHotkeyedBooleanConfig,   JsonConfigSerializers::loadHotkeyedBooleanConfig);
        this.registerSerializers(OptionalDirectoryConfig.class, JsonConfigSerializers::saveOptionalDirectoryConfig, JsonConfigSerializers::loadOptionalDirectoryConfig);
        this.registerSerializers(OptionListConfig.class,        JsonConfigSerializers::saveOptionListConfig,        JsonConfigSerializers::loadOptionListConfig);
        this.registerSerializers(ValueListConfig.class,         JsonConfigSerializers::saveValueListConfig,         JsonConfigSerializers::loadValueListConfig);
        this.registerSerializers(Vec2iConfig.class,             JsonConfigSerializers::saveVec2iConfig,             JsonConfigSerializers::loadVec2iConfig);
    }

    public interface JsonConfigSerializer<C extends ConfigInfo>
    {
        JsonElement serializeConfigValue(C config);
    }

    public interface JsonConfigDeSerializer<C extends ConfigInfo>
    {
        void deSerializeConfigValue(C config, JsonElement data, String configName);
    }
}
