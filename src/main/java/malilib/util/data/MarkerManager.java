package malilib.util.data;

import java.util.ArrayList;
import java.util.function.Function;
import javax.annotation.Nullable;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import malilib.util.data.json.JsonUtils;

public class MarkerManager<T>
{
    protected final ArrayList<T> markers = new ArrayList<>();
    protected final Function<T, JsonElement> jsonSerializer;
    protected final Function<JsonElement, T> jsonDeserializer;

    public MarkerManager(Function<T, JsonElement> jsonSerializer, Function<JsonElement, T> jsonDeserializer)
    {
        this.jsonSerializer = jsonSerializer;
        this.jsonDeserializer = jsonDeserializer;
    }

    public void addMarker(T marker)
    {
        this.markers.add(marker);
    }

    public void removeMarker(T marker)
    {
        this.markers.remove(marker);
    }

    /**
     * Checks if the given marker exists.
     * If the given marker is null, then the widget must not have any markers to "match".
     */
    public boolean matchesMarker(@Nullable T marker)
    {
        return marker != null ? this.markers.contains(marker) : this.markers.isEmpty();
    }

    protected void readMarkerFromJsonElement(JsonElement el)
    {
        T obj = this.jsonDeserializer.apply(el);

        if (obj != null)
        {
            this.markers.add(obj);
        }
    }

    public void writeToJsonIfHasData(JsonObject obj, String keyName)
    {
        if (this.markers.isEmpty() == false)
        {
            obj.add(keyName, this.toJson());
        }
    }

    public JsonArray toJson()
    {
        JsonArray arr = new JsonArray();

        for (T marker : this.markers)
        {
            arr.add(this.jsonSerializer.apply(marker));
        }

        return arr;
    }

    public void fromJson(JsonElement el)
    {
        this.markers.clear();

        if (el.isJsonArray())
        {
            JsonArray arr = el.getAsJsonArray();
            JsonUtils.readArrayElements(arr, this::readMarkerFromJsonElement);
        }
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) { return true; }
        if (o == null || this.getClass() != o.getClass()) { return false; }

        MarkerManager<?> that = (MarkerManager<?>) o;

        return this.markers.equals(that.markers);
    }

    @Override
    public int hashCode()
    {
        return this.markers.hashCode();
    }
}
