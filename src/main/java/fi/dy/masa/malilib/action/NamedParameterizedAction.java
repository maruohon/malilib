package fi.dy.masa.malilib.action;

import java.util.List;
import javax.annotation.Nullable;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fi.dy.masa.malilib.render.text.StyledTextLine;
import fi.dy.masa.malilib.util.JsonUtils;
import fi.dy.masa.malilib.util.data.ModInfo;

public class NamedParameterizedAction extends NamedAction
{
    protected final BaseParameterizedAction parameterizedAction;

    public NamedParameterizedAction(ModInfo mod, String name, String registryName,
                                    String translationKey, BaseParameterizedAction action)
    {
        super(mod, name, registryName, translationKey, action);

        this.parameterizedAction = action;
        this.needsArguments = true;
    }

    @Override
    public AliasAction createAlias(String aliasName, @Nullable String argument)
    {
        if (this.needsArguments && argument != null)
        {
            NamedAction action = new NamedParameterizedAction(this.mod, this.name, this.registryName,
                                                              this.translationKey,
                                                              this.parameterizedAction.parameterize(argument));
            return new AliasAction(aliasName, action);
        }

        return super.createAlias(aliasName, argument);
    }

    @Override
    public List<StyledTextLine> getHoverInfo()
    {
        List<StyledTextLine> lines = super.getHoverInfo();
        String arg = this.parameterizedAction.getArgument();

        if (arg != null)
        {
            StyledTextLine start = StyledTextLine.translate("malilib.hover_info.action.argument.colon");
            lines.add(start.append(StyledTextLine.rawWithStyle(arg, start.getLastStyle())));
        }

        return lines;
    }

    @Nullable
    @Override
    public JsonObject toJson()
    {
        @Nullable String argument = this.parameterizedAction.getArgument();

        if (argument != null)
        {
            JsonObject obj = new JsonObject();
            obj.addProperty("argument", argument);
            return obj;
        }

        return null;
    }

    @Override
    public NamedParameterizedAction fromJson(JsonElement el)
    {
        if (el.isJsonObject())
        {
            JsonObject obj = el.getAsJsonObject();

            if (JsonUtils.hasString(obj, "argument"))
            {
                String argument = JsonUtils.getString(obj, "argument");

                return new NamedParameterizedAction(this.mod, this.name, this.registryName,
                                                    this.translationKey,
                                                    this.parameterizedAction.parameterize(argument));
            }
        }

        return this;
    }

    public static NamedAction register(ModInfo modInfo, String name, ParameterizedAction action)
    {
        NamedAction namedAction = new NamedParameterizedAction(modInfo, name, createRegistryNameFor(modInfo, name),
                                                               createTranslationKeyFor(modInfo, name),
                                                               BaseParameterizedAction.of(action));
        namedAction.setCommentIfTranslationExists(modInfo.getModId(), name);
        ActionRegistry.INSTANCE.registerAction(namedAction);
        return namedAction;
    }
}
