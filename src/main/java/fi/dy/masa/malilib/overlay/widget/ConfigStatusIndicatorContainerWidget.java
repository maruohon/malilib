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
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import fi.dy.masa.malilib.config.option.ConfigInfo;
import fi.dy.masa.malilib.config.util.ConfigUtils;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.config.ConfigTabRegistry;
import fi.dy.masa.malilib.gui.config.ConfigWidgetRegistry;
import fi.dy.masa.malilib.gui.config.indicator.ConfigStatusIndicatorGroupEditScreen;
import fi.dy.masa.malilib.gui.config.indicator.ConfigStatusWidgetFactory;
import fi.dy.masa.malilib.gui.util.GuiUtils;
import fi.dy.masa.malilib.overlay.widget.sub.BaseConfigStatusIndicatorWidget;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.render.ShapeRenderUtils;
import fi.dy.masa.malilib.render.text.TextRenderSettings;
import fi.dy.masa.malilib.util.JsonUtils;
import fi.dy.masa.malilib.util.data.ConfigOnTab;

public class ConfigStatusIndicatorContainerWidget extends InfoRendererWidget
{
    protected final Set<ConfigOnTab> configs = new HashSet<>();
    protected final List<BaseConfigStatusIndicatorWidget<?>> allWidgets = new ArrayList<>();
    protected final List<BaseConfigStatusIndicatorWidget<?>> enabledWidgets = new ArrayList<>();
    protected boolean enabledWidgetsChanged;

    public ConfigStatusIndicatorContainerWidget()
    {
        super();

        this.shouldSerialize = true;
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
                widget.setGeometryChangeListener(this::requestConditionalReLayout);
                widget.setEnabledChangeListener(this::notifyEnabledWidgetsChanged);
                widget.setHeight(this.lineHeight);
                this.allWidgets.add(widget);
                this.configs.add(config);
                this.notifyEnabledWidgetsChanged();
                this.requestUnconditionalReLayout();
            }
        }
    }

    public void removeWidget(BaseConfigStatusIndicatorWidget<?> widget)
    {
        this.allWidgets.remove(widget);

        if (widget.isEnabled())
        {
            this.notifyEnabledWidgetsChanged();
        }

        this.requestUnconditionalReLayout();
    }

    public ArrayList<BaseConfigStatusIndicatorWidget<?>> getStatusIndicatorWidgetsForEditScreen()
    {
        // return a separate, modifiable list
        return new ArrayList<>(this.allWidgets);
    }

    @Override
    public void openEditScreen()
    {
        ConfigStatusIndicatorGroupEditScreen screen = new ConfigStatusIndicatorGroupEditScreen(this);
        screen.setParent(GuiUtils.getCurrentScreen());
        BaseScreen.openScreen(screen);
    }

    public void setStatusIndicatorWidgets(List<BaseConfigStatusIndicatorWidget<?>> widgets)
    {
        this.allWidgets.clear();
        this.configs.clear();

        this.allWidgets.addAll(widgets);

        for (BaseConfigStatusIndicatorWidget<?> widget : widgets)
        {
            this.configs.add(widget.getConfigOnTab());
        }

        this.notifyEnabledWidgetsChanged();
        this.requestUnconditionalReLayout();
    }

    @Override
    public void setLineHeight(int lineHeight)
    {
        super.setLineHeight(lineHeight);

        for (BaseConfigStatusIndicatorWidget<?> widget : this.allWidgets)
        {
            widget.setHeight(this.lineHeight);
        }

        this.requestUnconditionalReLayout();
    }

    protected void notifyEnabledWidgetsChanged()
    {
        this.enabledWidgetsChanged = true;
        this.requestUnconditionalReLayout();
    }

    protected void updateEnabledWidgets()
    {
        if (this.enabledWidgetsChanged)
        {
            this.enabledWidgets.clear();

            for (BaseConfigStatusIndicatorWidget<?> widget : this.allWidgets)
            {
                if (widget.isEnabled())
                {
                    this.enabledWidgets.add(widget);
                    widget.updateState(true);
                }
            }

            this.enabledWidgetsChanged = false;
        }
    }

    @Override
    public void updateSize()
    {
        int maxLabelWidth = 0;
        int maxValueWidth = 0;
        int height = this.renderName ? this.lineHeight : 0;

        this.updateEnabledWidgets();
        
        for (BaseConfigStatusIndicatorWidget<?> widget : this.enabledWidgets)
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

    @Override
    public void updateSubWidgetPositions()
    {
        int x = this.getX();
        int y = this.getY();
        int subWidgetX = x + this.padding.getLeft();
        int subWidgetY = y + (this.renderName ? 0 : this.padding.getTop());
        int width = this.getWidth() - this.padding.getHorizontalTotal();

        this.updateEnabledWidgets();

        for (BaseConfigStatusIndicatorWidget<?> widget : this.enabledWidgets)
        {
            widget.setWidth(width);
            // Use a relative position in case scaling is involved
            widget.setPosition(subWidgetX - x, subWidgetY - y);
            subWidgetY += widget.getHeight();
        }
    }

    @Override
    public void updateState()
    {
        for (BaseConfigStatusIndicatorWidget<?> widget : this.allWidgets)
        {
            widget.updateState(false);
        }

        super.updateState();
    }

    @Override
    protected void renderOddEvenLineBackgrounds(int x, int y, float z)
    {
        BufferBuilder buffer = RenderUtils.startBuffer(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR, false);

        TextRenderSettings settings = this.getTextSettings();
        int bgColor = settings.getBackgroundColor();
        int bgColorOdd = settings.getOddRowBackgroundColor();
        int width = this.getWidth();
        int size = this.enabledWidgets.size();
        int i = 0;

        if (this.renderName && this.styledName != null)
        {
            int height = this.lineHeight + this.padding.getTop();

            if (size > 0)
            {
                BaseConfigStatusIndicatorWidget<?> widget = this.enabledWidgets.get(0);
                height += widget.getHeight();
            }
            else
            {
                height += this.padding.getBottom();
            }

            ShapeRenderUtils.renderRectangle(x, y, z, width, height, bgColor, buffer);
            y += height;
            i = 1;
        }

        for (; i < size; ++i)
        {
            BaseConfigStatusIndicatorWidget<?> widget = this.enabledWidgets.get(i);
            int height = widget.getHeight();

            if (i == 0)
            {
                height += this.padding.getTop();
            }

            if (i == size - 1)
            {
                height += this.padding.getBottom();
            }

            int color = (i & 0x1) != 0 ? bgColorOdd : bgColor;
            ShapeRenderUtils.renderRectangle(x, y, z, width, height, color, buffer);
            y += height;
        }

        RenderUtils.drawBuffer();
    }

    @Override
    protected void renderContents(int x, int y, float z)
    {
        for (BaseConfigStatusIndicatorWidget<?> widget : this.enabledWidgets)
        {
            int wx = widget.getX();
            int wy = widget.getY();
            float wz = widget.getZLevel();

            // Use a relative position in case scaling is involved
            widget.renderAt(x + wx, y + wy, z + wz);
        }
    }

    @Override
    public JsonObject toJson()
    {
        JsonObject obj = super.toJson();
        JsonArray arr = new JsonArray();

        obj.addProperty("line_height", this.lineHeight);

        for (BaseConfigStatusIndicatorWidget<?> widget : this.allWidgets)
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

        this.allWidgets.clear();

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
                    widget.setGeometryChangeListener(this::requestConditionalReLayout);
                    widget.setEnabledChangeListener(this::notifyEnabledWidgetsChanged);
                    widget.setHeight(this.lineHeight);
                    widget.updateState(true);
                    this.allWidgets.add(widget);
                }
            }
        }

        this.notifyEnabledWidgetsChanged();
        this.updateSize();
        this.requestUnconditionalReLayout();
    }
}
