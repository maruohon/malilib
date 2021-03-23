package fi.dy.masa.malilib.overlay.widget;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.lwjgl.opengl.GL11;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import fi.dy.masa.malilib.config.option.ConfigInfo;
import fi.dy.masa.malilib.config.util.ConfigUtils;
import fi.dy.masa.malilib.gui.config.ConfigStatusWidgetFactory;
import fi.dy.masa.malilib.gui.config.ConfigTabRegistry;
import fi.dy.masa.malilib.gui.config.ConfigWidgetRegistry;
import fi.dy.masa.malilib.overlay.widget.sub.BaseConfigStatusIndicatorWidget;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.render.ShapeRenderUtils;
import fi.dy.masa.malilib.util.JsonUtils;
import fi.dy.masa.malilib.util.data.ConfigOnTab;

public class ConfigStatusIndicatorContainerWidget extends InfoRendererWidget
{
    protected final Set<ConfigOnTab> configs = new HashSet<>();
    protected final List<BaseConfigStatusIndicatorWidget<?>> widgets = new ArrayList<>();
    protected boolean needsReLayout;

    public ConfigStatusIndicatorContainerWidget()
    {
        this.shouldSerialize = true;
        this.margin.setChangeListener(this::requestReLayout);
        this.padding.setChangeListener(this::requestReLayout);
    }

    public Collection<ConfigOnTab> getConfigs()
    {
        return this.configs;
    }

    public void addWidgetForConfig(ConfigOnTab config)
    {
        if (this.configs.contains(config) == false)
        {
            ConfigStatusWidgetFactory<ConfigInfo> factory = ConfigWidgetRegistry.INSTANCE.getConfigStatusWidgetFactory(config.config);

            if (factory != null)
            {
                BaseConfigStatusIndicatorWidget<?> widget = factory.create(config.config, config);
                widget.setGeometryChangeListener(this::requestReLayout);
                widget.setHeight(this.lineHeight);
                this.widgets.add(widget);
                this.configs.add(config);
                this.requestReLayout();
            }
        }
    }

    public void removeWidget(BaseConfigStatusIndicatorWidget<?> widget)
    {
        this.widgets.remove(widget);
        this.requestReLayout();
    }

    public ArrayList<BaseConfigStatusIndicatorWidget<?>> getStatusIndicatorWidgets()
    {
        // return a separate, modifiable list
        return new ArrayList<>(this.widgets);
    }

    public void setStatusIndicatorWidgets(List<BaseConfigStatusIndicatorWidget<?>> widgets)
    {
        this.widgets.clear();
        this.configs.clear();

        this.widgets.addAll(widgets);

        for (BaseConfigStatusIndicatorWidget<?> widget : widgets)
        {
            this.configs.add(widget.getConfigOnTab());
        }

        this.requestReLayout();
    }

    @Override
    public void setLineHeight(int lineHeight)
    {
        super.setLineHeight(lineHeight);

        for (BaseConfigStatusIndicatorWidget<?> widget : this.widgets)
        {
            widget.setHeight(this.lineHeight);
        }

        this.requestReLayout();
    }

    protected void requestReLayout()
    {
        this.needsReLayout = true;
    }

    protected void reLayoutWidgets()
    {
        this.updateSize();
        this.updateWidgetPositions();
        this.notifyContainerOfChanges(true);

        this.needsReLayout = false;
    }

    @Override
    protected void onPositionChanged(int oldX, int oldY)
    {
        this.updateWidgetPositions();
    }

    @Override
    public void updateSize()
    {
        int maxLabelWidth = 0;
        int maxValueWidth = 0;
        int height = this.renderName ? this.lineHeight : 0;

        for (BaseConfigStatusIndicatorWidget<?> widget : this.widgets)
        {
            maxLabelWidth = Math.max(maxLabelWidth, widget.getLabelRenderWidth());
            maxValueWidth = Math.max(maxValueWidth, widget.getValueRenderWidth());
            height += widget.getHeight();
        }

        int width = maxLabelWidth + maxValueWidth + 10 + this.padding.getHorizontalTotal();
        height += this.padding.getVerticalTotal();

        this.setWidth(width);
        this.setHeight(height);
    }

    public void updateWidgetPositions()
    {
        int x = this.getX() + this.padding.getLeft();
        int y = this.getY() + (this.renderName ? this.lineHeight : 0) + this.padding.getTop();
        int width = this.getWidth() - this.padding.getHorizontalTotal();

        for (BaseConfigStatusIndicatorWidget<?> widget : this.widgets)
        {
            widget.setWidth(width);
            widget.setPosition(x, y);
            y += widget.getHeight();
        }
    }

    @Override
    public void updateState(Minecraft mc)
    {
        for (BaseConfigStatusIndicatorWidget<?> widget : this.widgets)
        {
            widget.updateState(false);
        }

        if (this.needsReLayout)
        {
            this.reLayoutWidgets();
        }
    }

    @Override
    protected void renderBackground(int x, int y, float z)
    {
        if (this.renderBackground)
        {
            if (this.oddEvenBackground)
            {
                this.renderOddEvenLineBackgrounds(x, y, z);
            }
            else
            {
                int width = this.getWidth();
                int height = this.getHeight();
                ShapeRenderUtils.renderRectangle(x, y, z, width, height, this.backgroundColor);
            }
        }
    }

    protected void renderOddEvenLineBackgrounds(int x, int y, float z)
    {
        BufferBuilder buffer = RenderUtils.startBuffer(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR, false);

        int width = this.getWidth();
        int size = this.widgets.size();
        int i = 0;

        if (this.renderName && this.styledName != null)
        {
            int height = this.lineHeight + this.padding.getTop();

            if (size > 0)
            {
                BaseConfigStatusIndicatorWidget<?> widget = this.widgets.get(0);
                height += widget.getHeight();
            }
            else
            {
                height += this.padding.getBottom();
            }

            ShapeRenderUtils.renderRectangle(x, y, z, width, height, this.backgroundColor, buffer);
            y += height;
            i = 1;
        }

        for (; i < size; ++i)
        {
            BaseConfigStatusIndicatorWidget<?> widget = this.widgets.get(i);
            int height = widget.getHeight();

            if (i == 0)
            {
                height += this.padding.getTop();
            }

            if (i == size - 1)
            {
                height += this.padding.getBottom();
            }

            int color = (i & 0x1) != 0 ? this.backgroundColorOdd : this.backgroundColor;
            ShapeRenderUtils.renderRectangle(x, y, z, width, height, color, buffer);
            y += height;
        }

        RenderUtils.drawBuffer();
    }

    @Override
    protected void renderContents(int x, int y, float z)
    {
        for (BaseConfigStatusIndicatorWidget<?> widget : this.widgets)
        {
            widget.render();
        }
    }

    @Override
    public JsonObject toJson()
    {
        JsonObject obj = super.toJson();
        JsonArray arr = new JsonArray();

        obj.addProperty("line_height", this.lineHeight);

        for (BaseConfigStatusIndicatorWidget<?> widget : this.widgets)
        {
            arr.add(widget.toJson());
        }

        obj.add("status_widgets", arr);

        return obj;
    }

    @Override
    public void fromJson(JsonObject obj)
    {
        super.fromJson(obj);

        this.lineHeight = JsonUtils.getIntegerOrDefault(obj, "line_height", this.lineHeight);

        this.widgets.clear();

        if (JsonUtils.hasArray(obj, "status_widgets") == false)
        {
            return;
        }

        Map<String, ConfigOnTab> configMap = ConfigUtils.getConfigIdToConfigMapFromTabs(ConfigTabRegistry.INSTANCE.getAllRegisteredConfigTabs());
        JsonArray arr = obj.get("status_widgets").getAsJsonArray();
        final int count = arr.size();

        for (int i = 0; i < count; i++)
        {
            JsonElement el = arr.get(i);

            if (el.isJsonObject())
            {
                JsonObject entryObj = el.getAsJsonObject();
                BaseConfigStatusIndicatorWidget<?> widget = BaseConfigStatusIndicatorWidget.fromJson(entryObj, configMap);

                if (widget != null)
                {
                    widget.setHeight(this.lineHeight);
                    this.widgets.add(widget);
                }
            }
        }

        this.updateSize();
        this.requestReLayout();
    }
}
