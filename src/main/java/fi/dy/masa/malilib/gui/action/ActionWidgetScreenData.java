package fi.dy.masa.malilib.gui.action;

import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fi.dy.masa.malilib.util.JsonUtils;

public class ActionWidgetScreenData
{
    public final ImmutableList<BaseActionExecutionWidget> widgets;
    public final boolean closeScreenOnExecute;
    public final boolean closeScreenOnKeyRelease;

    public ActionWidgetScreenData(ImmutableList<BaseActionExecutionWidget> widgets,
                                  boolean closeScreenOnExecute, boolean closeScreenOnKeyRelease)
    {
        this.widgets = widgets;
        this.closeScreenOnExecute = closeScreenOnExecute;
        this.closeScreenOnKeyRelease = closeScreenOnKeyRelease;
    }

    public JsonObject toJson()
    {
        JsonObject obj = new JsonObject();
        JsonArray arr = new JsonArray();

        for (BaseActionExecutionWidget widget : this.widgets)
        {
            arr.add(widget.toJson());
        }

        obj.addProperty("close_on_execute", this.closeScreenOnExecute);
        obj.addProperty("close_on_key_release", this.closeScreenOnKeyRelease);
        obj.add("widgets", arr);

        return obj;
    }

    @Nullable
    public static ActionWidgetScreenData fromJson(JsonElement el)
    {
        if (el.isJsonObject() == false)
        {
            return null;
        }

        JsonObject obj = el.getAsJsonObject();
        boolean closeScreenOnExecute = JsonUtils.getBooleanOrDefault(obj, "close_on_execute", false);
        boolean closeScreenOnKeyRelease = JsonUtils.getBooleanOrDefault(obj, "close_on_key_release", false);
        ImmutableList.Builder<BaseActionExecutionWidget> builder = ImmutableList.builder();

        if (JsonUtils.hasArray(obj, "widgets"))
        {
            JsonArray arr = obj.get("widgets").getAsJsonArray();
            final int size = arr.size();

            for (int i = 0; i < size; ++i)
            {
                JsonElement ae = arr.get(i);

                if (ae.isJsonObject())
                {
                    BaseActionExecutionWidget widget = BaseActionExecutionWidget.createFromJson(ae.getAsJsonObject());

                    if (widget != null)
                    {
                        builder.add(widget);
                    }
                }
            }
        }

        return new ActionWidgetScreenData(builder.build(), closeScreenOnExecute, closeScreenOnKeyRelease);
    }

    public static ActionWidgetScreenData createEmpty()
    {
        return new ActionWidgetScreenData(ImmutableList.of(), false, false);
    }
}
