package fi.dy.masa.malilib.action;

import java.util.function.Function;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fi.dy.masa.malilib.util.JsonUtils;

public class ActionType<T extends NamedAction>
{
    public static final ActionType<NamedAction>                SIMPLE          = new ActionType<>("simple",          ActionGroup.SIMPLE,          NamedAction.class,                NamedAction::baseActionFromJson);
    public static final ActionType<AliasAction>                ALIAS           = new ActionType<>("alias",           ActionGroup.ALIAS,           AliasAction.class,                AliasAction::aliasActionFromJson);
    public static final ActionType<MacroAction>                MACRO           = new ActionType<>("macro",           ActionGroup.MACRO,           MacroAction.class,                MacroAction::macroActionFromJson);
    public static final ActionType<ParameterizableNamedAction> PARAMETERIZABLE = new ActionType<>("parameterizable", ActionGroup.PARAMETERIZABLE, ParameterizableNamedAction.class, ParameterizableNamedAction::parameterizableActionFromJson);
    public static final ActionType<ParameterizedNamedAction>   PARAMETERIZED   = new ActionType<>("parameterized",   ActionGroup.PARAMETERIZED,   ParameterizedNamedAction.class,   ParameterizedNamedAction::parameterizedActionFromJson);

    public static final ImmutableList<ActionType<?>> VALUES = ImmutableList.of(SIMPLE, ALIAS, MACRO, PARAMETERIZABLE, PARAMETERIZED);

    protected final String id;
    protected final ActionGroup group;
    protected final Function<JsonObject, T> fromJsonFunction;
    protected final Class<T> clazz;

    public ActionType(String id, ActionGroup group, Class<T> clazz, Function<JsonObject, T> fromJsonFunction)
    {
        this.id = id;
        this.group = group;
        this.fromJsonFunction = fromJsonFunction;
        this.clazz = clazz;
    }

    public String getId()
    {
        return this.id;
    }

    public ActionGroup getGroup()
    {
        return this.group;
    }

    @Nullable
    public T loadFromJson(JsonObject obj)
    {
        NamedAction action = this.fromJsonFunction.apply(obj);

        if (action != null)
        {
            action = action.loadFromJson(obj);

            if (this.clazz.isAssignableFrom(action.getClass()))
            {
                return this.clazz.cast(action);
            }
        }

        return null;
    }

    @Nullable
    public static ActionType<?> fromId(String id)
    {
        for (ActionType<?> type : VALUES)
        {
            if (type.id.equals(id))
            {
                return type;
            }
        }

        return null;
    }

    @Nullable
    public static NamedAction loadActionFromJson(JsonElement el)
    {
        if (el.isJsonObject())
        {
            JsonObject obj = el.getAsJsonObject();

            if (JsonUtils.hasString(obj, "type"))
            {
                ActionType<?> type = fromId(JsonUtils.getString(obj, "type"));

                if (type != null)
                {
                    return type.loadFromJson(obj);
                }
            }
        }

        return null;
    }
}
