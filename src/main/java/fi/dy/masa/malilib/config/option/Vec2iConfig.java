package fi.dy.masa.malilib.config.option;

import javax.annotation.Nullable;
import org.apache.commons.lang3.tuple.Pair;
import fi.dy.masa.malilib.util.position.Vec2i;

public class Vec2iConfig extends BaseGenericConfig<Vec2i>
{
    @Nullable protected Pair<String, String> labels;
    @Nullable protected Pair<String, String> hoverTexts;

    public Vec2iConfig(String name, Vec2i defaultValue)
    {
        this(name, defaultValue, name);
    }

    public Vec2iConfig(String name, Vec2i defaultValue, String comment)
    {
        super(name, defaultValue, comment);
    }

    @Nullable
    public Pair<String, String> getLabels()
    {
        return this.labels;
    }

    public void setLabels(@Nullable Pair<String, String> labels)
    {
        this.labels = labels;
    }

    @Nullable
    public Pair<String, String> getHoverTexts()
    {
        return this.hoverTexts;
    }

    public void setHoverTexts(@Nullable Pair<String, String> hoverTexts)
    {
        this.hoverTexts = hoverTexts;
    }
}
