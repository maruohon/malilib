package fi.dy.masa.malilib.action;

import java.util.List;
import javax.annotation.Nullable;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fi.dy.masa.malilib.input.ActionResult;
import fi.dy.masa.malilib.render.text.StyledTextLine;
import fi.dy.masa.malilib.util.JsonUtils;

public class ParameterizedNamedAction extends NamedAction
{
    protected final ParameterizableNamedAction baseAction;
    protected final String argument;

    public ParameterizedNamedAction(String name,
                                    String registryName,
                                    ParameterizableNamedAction baseAction,
                                    String argument)
    {
        super(name, registryName, baseAction.getNameTranslationKey(), baseAction.getModInfo());

        this.baseAction = baseAction;
        this.argument = argument;
    }

    @Override
    public ActionResult execute(ActionContext ctx)
    {
        return this.baseAction.action.executeWithArgument(ctx, this.argument);
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
        obj.addProperty("name", this.name);
        obj.addProperty("reg_name", this.baseAction.getRegistryName());
        obj.addProperty("arg", this.argument);
        return obj;
    }

    @Nullable
    public static ParameterizedNamedAction fromJson(ActionRegistry registry, JsonElement el)
    {
        if (el.isJsonObject())
        {
            JsonObject obj = el.getAsJsonObject();

            if (JsonUtils.hasString(obj, "name") &&
                JsonUtils.hasString(obj, "reg_name") &&
                JsonUtils.hasString(obj, "arg"))
            {
                String regName = JsonUtils.getString(obj, "reg_name");
                NamedAction baseAction = registry.getAction(regName);

                if (baseAction instanceof ParameterizableNamedAction)
                {
                    String name = JsonUtils.getString(obj, "name");
                    String argument = JsonUtils.getString(obj, "arg");
                    return of(name, (ParameterizableNamedAction) baseAction, argument);
                }
            }
        }

        return null;
    }

    public static ParameterizedNamedAction of(String name, ParameterizableNamedAction baseAction, String argument)
    {
        String registryName = "<parameterized>:" + baseAction.getRegistryName() + ":" + argument;
        return new ParameterizedNamedAction(name, registryName, baseAction, argument);
    }
}
