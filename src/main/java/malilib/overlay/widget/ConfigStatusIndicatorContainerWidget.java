package malilib.overlay.widget;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;

import malilib.MaLiLibReference;
import malilib.config.option.ConfigInfo;
import malilib.config.option.HotkeyConfig;
import malilib.config.util.ConfigUtils;
import malilib.gui.BaseScreen;
import malilib.gui.config.ConfigTab;
import malilib.gui.config.indicator.ConfigStatusIndicatorGroupEditScreen;
import malilib.gui.config.indicator.ConfigStatusWidgetFactory;
import malilib.gui.util.ScreenContext;
import malilib.gui.widget.list.entry.BaseInfoRendererWidgetEntryWidget;
import malilib.input.ActionResult;
import malilib.input.Hotkey;
import malilib.input.HotkeyProvider;
import malilib.input.KeyAction;
import malilib.input.KeyBind;
import malilib.input.SimpleHotkeyProvider;
import malilib.overlay.widget.sub.BaseConfigStatusIndicatorWidget;
import malilib.registry.Registry;
import malilib.render.RenderUtils;
import malilib.render.ShapeRenderUtils;
import malilib.render.text.MultiLineTextRenderSettings;
import malilib.util.data.ConfigOnTab;
import malilib.util.data.EdgeInt;
import malilib.util.data.json.JsonUtils;

public class ConfigStatusIndicatorContainerWidget extends InfoRendererWidget
{
    protected final Set<ConfigOnTab> configs = new HashSet<>();
    protected final List<BaseConfigStatusIndicatorWidget<?>> allWidgets = new ArrayList<>();
    protected final List<BaseConfigStatusIndicatorWidget<?>> enabledWidgets = new ArrayList<>();
    protected final HotkeyConfig hotkey = new HotkeyConfig("csiToggleKey", "");
    protected boolean enabledWidgetsChanged;

    public ConfigStatusIndicatorContainerWidget()
    {
        super();

        this.hotkey.getKeyBind().setCallback(this::toggleIndicatorGroupEnabled);
        this.hotkey.setModInfo(MaLiLibReference.MOD_INFO);
        this.shouldSerialize = true;
    }

    @Override
    public String getWidgetTypeId()
    {
        return MaLiLibReference.MOD_ID + ":csi_container";
    }

    public Collection<ConfigOnTab> getConfigs()
    {
        return this.configs;
    }

    public HotkeyConfig getHotkey()
    {
        return this.hotkey;
    }

    public void addWidgetForConfig(ConfigOnTab config)
    {
        if (this.configs.contains(config) == false)
        {
            ConfigStatusWidgetFactory<ConfigInfo> factory = Registry.CONFIG_STATUS_WIDGET.getConfigStatusWidgetFactory(config.getConfig());

            if (factory != null)
            {
                BaseConfigStatusIndicatorWidget<?> widget = factory.create(config.getConfig(), config);
                widget.setGeometryChangeListener(this::requestConditionalReLayout);
                widget.setEnabledChangeListener(this::notifyEnabledWidgetsChanged);
                widget.setHeight(this.getLineHeight());
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
        BaseScreen.openScreenWithParent(new ConfigStatusIndicatorGroupEditScreen(this));
    }

    @Override
    public void initListEntryWidget(BaseInfoRendererWidgetEntryWidget widget)
    {
        widget.setCanConfigure(true);
        widget.setCanRemove(true);
        widget.setCanToggle(true);
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
            widget.setHeight(this.getLineHeight());
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

    protected ActionResult toggleIndicatorGroupEnabled(KeyAction action, KeyBind key)
    {
        this.toggleEnabled();
        return ActionResult.SUCCESS;
    }

    @Override
    public void updateSize()
    {
        int maxLabelWidth = 0;
        int maxValueWidth = 0;
        int height = this.renderName ? this.getLineHeight() : 0;

        this.updateEnabledWidgets();
        
        for (BaseConfigStatusIndicatorWidget<?> widget : this.enabledWidgets)
        {
            maxLabelWidth = Math.max(maxLabelWidth, widget.getLabelRenderWidth());
            maxValueWidth = Math.max(maxValueWidth, widget.getValueRenderWidth());
            height += widget.getHeight();
        }

        EdgeInt padding = this.padding;
        int width = maxLabelWidth + maxValueWidth + 10 + padding.getHorizontalTotal();
        height += padding.getVerticalTotal();

        this.setSizeNoUpdate(width, height);
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
    protected void renderOddEvenTextLineBackgrounds(int x, int y, float z, ScreenContext ctx)
    {
        BufferBuilder buffer = RenderUtils.startBuffer(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR, false);

        MultiLineTextRenderSettings settings = this.getTextSettings();
        int bgColor = settings.getBackgroundColor();
        int bgColorOdd = settings.getOddRowBackgroundColor();
        int width = this.getWidth();
        int size = this.enabledWidgets.size();
        int i = 0;

        if (this.renderName && this.styledName != null)
        {
            int height = this.getLineHeight() + this.padding.getTop();

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

        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderUtils.drawBuffer();
    }

    @Override
    protected void renderContents(int x, int y, float z, ScreenContext ctx)
    {
        for (BaseConfigStatusIndicatorWidget<?> widget : this.enabledWidgets)
        {
            int wx = widget.getX();
            int wy = widget.getY();
            float wz = widget.getZ();

            // Use a relative position in case scaling is involved
            widget.renderAt(x + wx, y + wy, z + wz, ctx);
        }
    }

    @Override
    public JsonObject toJson()
    {
        JsonObject obj = super.toJson();
        JsonArray arr = new JsonArray();

        obj.add("hotkey", this.hotkey.getKeyBind().getAsJsonElement());

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

        if (obj.has("hotkey"))
        {
            this.hotkey.getKeyBind().setValueFromJsonElement(obj.get("hotkey"), "");
        }

        this.allWidgets.clear();

        List<? extends ConfigTab> tabs = Registry.CONFIG_TAB.getAllRegisteredConfigTabs();
        Map<String, ConfigOnTab> configMap = ConfigUtils.getConfigIdToConfigMapFromTabs(tabs);
        JsonUtils.getArrayElementsIfExists(obj, "status_widgets", (e) -> this.readAndAddWidget(e, configMap));

        this.notifyEnabledWidgetsChanged();
        this.updateSize();
        this.requestUnconditionalReLayout();
    }

    protected void readAndAddWidget(JsonElement el, Map<String, ConfigOnTab> configMap)
    {
        BaseConfigStatusIndicatorWidget<?> widget = BaseConfigStatusIndicatorWidget.fromJson(el, configMap);

        if (widget != null)
        {
            widget.setGeometryChangeListener(this::requestConditionalReLayout);
            widget.setEnabledChangeListener(this::notifyEnabledWidgetsChanged);
            widget.setHeight(this.getLineHeight());
            widget.updateState(true);
            this.allWidgets.add(widget);
        }
    }

    public static List<? extends Hotkey> getToggleHotkeys()
    {
        List<ConfigStatusIndicatorContainerWidget> widgets = Registry.INFO_WIDGET_MANAGER.getAllWidgetsExtendingType(ConfigStatusIndicatorContainerWidget.class);
        ArrayList<Hotkey> hotkeys = new ArrayList<>();

        for (ConfigStatusIndicatorContainerWidget widget : widgets)
        {
            hotkeys.add(widget.getHotkey());
        }

        return hotkeys;
    }

    /**
     * Convenience method for malilib to register the C.S.I. toggle hotkeys.
     * This should not be useful for or used by other mods.
     */
    public static HotkeyProvider getHotkeyProvider()
    {
        return new SimpleHotkeyProvider(MaLiLibReference.MOD_INFO,
                                        "malilib.hotkeys.category.csi_toggles",
                                        ConfigStatusIndicatorContainerWidget::getToggleHotkeys);
    }
}
