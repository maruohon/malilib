package fi.dy.masa.malilib.config.serialization;

import java.nio.file.Paths;
import java.util.HashMap;
import javax.annotation.Nullable;
import com.google.gson.JsonElement;
import fi.dy.masa.malilib.config.option.BooleanAndDoubleConfig;
import fi.dy.masa.malilib.config.option.BooleanAndFileConfig;
import fi.dy.masa.malilib.config.option.BooleanAndIntConfig;
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
import fi.dy.masa.malilib.config.option.StringConfig;
import fi.dy.masa.malilib.config.option.Vec2dConfig;
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
    public <C extends ConfigInfo> void registerSerializers(Class<C> type,
                                                           JsonConfigSerializer<C> serializer,
                                                           JsonConfigDeSerializer<C> deSerializer)
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
        this.registerSerializers(BooleanConfig.class,   JsonConfigSerializers::serializeBooleanConfig,  (c, d, n) -> JsonConfigSerializers.loadPrimitiveConfig(c::loadValue, d::getAsBoolean, d, n));
        this.registerSerializers(ColorConfig.class,     JsonConfigSerializers::serializeColorConfig,    (c, d, n) -> JsonConfigSerializers.loadPrimitiveConfig(c::loadColorValueFromString, d::getAsString, d, n));
        this.registerSerializers(DirectoryConfig.class, JsonConfigSerializers::serializeFileConfig,     (c, d, n) -> JsonConfigSerializers.loadPrimitiveConfig(c::loadValue, () -> Paths.get(d.getAsString()), d, n));
        this.registerSerializers(DoubleConfig.class,    JsonConfigSerializers::serializeDoubleConfig,   (c, d, n) -> JsonConfigSerializers.loadPrimitiveConfig(c::loadValue, d::getAsDouble, d, n));
        this.registerSerializers(FileConfig.class,      JsonConfigSerializers::serializeFileConfig,     (c, d, n) -> JsonConfigSerializers.loadPrimitiveConfig(c::loadValue, () -> Paths.get(d.getAsString()), d, n));
        this.registerSerializers(IntegerConfig.class,   JsonConfigSerializers::serializeIntegerConfig,  (c, d, n) -> JsonConfigSerializers.loadPrimitiveConfig(c::loadValue, d::getAsInt, d, n));
        this.registerSerializers(StringConfig.class,    JsonConfigSerializers::serializeStringConfig,   (c, d, n) -> JsonConfigSerializers.loadPrimitiveConfig(c::loadValue, d::getAsString, d, n));

        this.registerSerializers(BlackWhiteListConfig.class,    JsonConfigSerializers::serializeBlackWhiteListConfig,   JsonConfigSerializers::loadBlackWhiteListConfig);
        this.registerSerializers(BooleanAndDoubleConfig.class,  JsonConfigSerializers::serializeBooleanAndDoubleConfig, JsonConfigSerializers::loadBooleanAndDoubleConfig);
        this.registerSerializers(BooleanAndFileConfig.class,    JsonConfigSerializers::serializeBooleanAndFileConfig,   JsonConfigSerializers::loadBooleanAndFileConfig);
        this.registerSerializers(BooleanAndIntConfig.class,     JsonConfigSerializers::serializeBooleanAndIntConfig,    JsonConfigSerializers::loadBooleanAndIntConfig);
        this.registerSerializers(DualColorConfig.class,         JsonConfigSerializers::serializeDualColorConfig,        JsonConfigSerializers::loadDualColorConfig);
        this.registerSerializers(HotkeyConfig.class,            JsonConfigSerializers::serializeHotkeyConfig,           JsonConfigSerializers::loadHotkeyConfig);
        this.registerSerializers(HotkeyedBooleanConfig.class,   JsonConfigSerializers::serializeHotkeyedBooleanConfig,  JsonConfigSerializers::loadHotkeyedBooleanConfig);
        this.registerSerializers(OptionListConfig.class,        JsonConfigSerializers::serializeOptionListConfig,       JsonConfigSerializers::loadOptionListConfig);
        this.registerSerializers(ValueListConfig.class,         JsonConfigSerializers::serializeValueListConfig,        JsonConfigSerializers::loadValueListConfig);
        this.registerSerializers(Vec2dConfig.class,             JsonConfigSerializers::serializeVec2dConfig,            JsonConfigSerializers::loadVec2dConfig);
        this.registerSerializers(Vec2iConfig.class,             JsonConfigSerializers::serializeVec2iConfig,            JsonConfigSerializers::loadVec2iConfig);
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
