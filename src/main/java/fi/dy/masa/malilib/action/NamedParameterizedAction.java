package fi.dy.masa.malilib.action;

import java.util.List;
import javax.annotation.Nullable;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fi.dy.masa.malilib.input.ActionResult;
import fi.dy.masa.malilib.registry.Registry;
import fi.dy.masa.malilib.render.text.StyledTextLine;
import fi.dy.masa.malilib.util.JsonUtils;
import fi.dy.masa.malilib.util.data.ModInfo;

public class NamedParameterizedAction extends NamedAction
{
    protected final ParameterizedAction action;
    protected final String argument;

    public NamedParameterizedAction(ParameterizedAction action, ModInfo mod, String name,
                                    String registryName, String translationKey, String argument)
    {
        super(mod, name, registryName, translationKey);

        this.action = action;
        this.argument = argument;
    }

    @Override
    public ActionResult execute(ActionContext ctx)
    {
        return this.action.executeWithArgument(ctx, this.argument);
    }

    @Override
    public List<StyledTextLine> getHoverInfo()
    {
        List<StyledTextLine> lines = super.getHoverInfo();

        StyledTextLine start = StyledTextLine.translate("malilib.hover_info.action.argument.colon");
        lines.add(start.append(StyledTextLine.rawWithStyle(this.argument, start.getLastStyle())));

        return lines;
    }

    @Nullable
    public JsonObject toJson()
    {
        JsonObject obj = new JsonObject();
        obj.addProperty("reg_name", this.registryName);
        obj.addProperty("arg", this.argument);
        return obj;
    }

    @Nullable
    public static NamedParameterizedAction fromJson(JsonElement el)
    {
        if (el.isJsonObject())
        {
            JsonObject obj = el.getAsJsonObject();

            if (JsonUtils.hasString(obj, "reg_name") &&
                JsonUtils.hasString(obj, "argument"))
            {
                String regName = JsonUtils.getString(obj, "reg_name");
                NamedAction baseAction = Registry.ACTION_REGISTRY.getAction(regName);

                if (baseAction instanceof NamedParameterizableAction)
                {
                    String argument = JsonUtils.getString(obj, "argument");
                    return ((NamedParameterizableAction) baseAction).parameterize(argument);
                }
            }
        }

        return null;
    }

    public static NamedParameterizedAction of(NamedParameterizableAction action, String argument)
    {
        // FIXME registry name??
        return new NamedParameterizedAction(action.action, action.getModInfo(), action.getName(),
                                            action.getRegistryName(), action.getNameTranslationKey(), argument);
    }
}
