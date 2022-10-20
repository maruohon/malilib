package malilib.gui.widget;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;
import malilib.gui.BaseScreen;
import malilib.gui.icon.Icon;
import malilib.gui.util.ScreenContext;
import malilib.listener.EventListener;
import malilib.render.TextRenderUtils;
import malilib.render.text.OrderedStringListFactory;
import malilib.render.text.StyledText;
import malilib.render.text.StyledTextLine;
import malilib.util.StringUtils;
import malilib.util.position.Vec2i;

public abstract class InteractableWidget extends BackgroundWidget
{
    protected OrderedStringListFactory hoverInfoFactory = new OrderedStringListFactory();
    @Nullable protected BooleanSupplier enabledStatusSupplier;
    @Nullable protected EventListener clickListener;
    @Nullable protected BaseWidget hoverInfoWidget;
    @Nullable protected ImmutableList<StyledTextLine> hoverHelp;
    @Nullable protected HoverChecker renderHoverChecker;
    @Nullable protected Consumer<Runnable> taskQueue;
    protected boolean canInteract = true;
    protected boolean enabled = true;
    protected boolean enabledLast = true;
    protected boolean hoverInfoRequiresShift;
    protected boolean shouldReceiveOutsideClicks;
    protected boolean shouldReceiveOutsideScrolls;

    public InteractableWidget(int width, int height)
    {
        this(0, 0, width, height);
    }

    public InteractableWidget(int x, int y, int width, int height)
    {
        super(x, y, width, height);

        this.hoverInfoFactory.setDynamic(true);
    }

    @Override
    public boolean canInteract()
    {
        return this.canInteract;
    }

    public List<BaseTextFieldWidget> getAllTextFields()
    {
        return Collections.emptyList();
    }

    public void setTaskQueue(@Nullable Consumer<Runnable> taskQueue)
    {
        this.taskQueue = taskQueue;
    }

    public void setRenderHoverChecker(@Nullable HoverChecker checker)
    {
        this.renderHoverChecker = checker;
    }

    public void setCanInteract(boolean canInteract)
    {
        this.canInteract = canInteract;
    }

    public void setClickListener(@Nullable EventListener listener)
    {
        this.clickListener = listener;
    }

    public void setShouldReceiveOutsideClicks(boolean shouldReceiveOutsideClicks)
    {
        this.shouldReceiveOutsideClicks = shouldReceiveOutsideClicks;
    }

    public void setShouldReceiveOutsideScrolls(boolean shouldReceiveOutsideScrolls)
    {
        this.shouldReceiveOutsideScrolls = shouldReceiveOutsideScrolls;
    }

    public void setHoverInfoRequiresShift(boolean requireShift)
    {
        this.hoverInfoRequiresShift = requireShift;
    }

    public boolean getShouldReceiveOutsideClicks()
    {
        return this.shouldReceiveOutsideClicks;
    }

    public boolean getShouldReceiveOutsideScrolls()
    {
        return this.shouldReceiveOutsideScrolls;
    }

    protected int getTextColorForRender(boolean hovered)
    {
        return this.getTextSettings().getEffectiveTextColor(hovered);
    }

    public void setHoverInfoWidget(@Nullable BaseWidget hoverInfoWidget)
    {
        this.hoverInfoWidget = hoverInfoWidget;
    }

    /**
     * Schedules a task to run after any widget iterations are finished, to not cause CMEs
     * if the task needs to modify the data list or the widget list in some way.
     */
    protected void scheduleTask(Runnable task)
    {
        if (this.taskQueue != null)
        {
            this.taskQueue.accept(task);
        }
    }

    /**
     * Sets a supplier that provides the enabled status for the button.
     * An existing enabled status supplier overrides the
     * enabled field's value in the isEnabled() getter method.
     */
    public void setEnabledStatusSupplier(@Nullable BooleanSupplier enabledStatusSupplier)
    {
        this.enabledStatusSupplier = enabledStatusSupplier;
    }

    protected void onEnabledStateChanged(boolean isEnabled)
    {
    }

    public void setEnabled(boolean enabled)
    {
        boolean wasEnabled = this.enabled;
        this.enabled = enabled;

        if (enabled != wasEnabled)
        {
            this.onEnabledStateChanged(enabled);
            this.enabledLast = enabled;
        }
    }

    public boolean isEnabled()
    {
        boolean enabled = this.enabledStatusSupplier != null ? this.enabledStatusSupplier.getAsBoolean() : this.enabled;

        if (enabled != this.enabledLast)
        {
            this.onEnabledStateChanged(enabled);
            this.enabledLast = enabled;
        }

        return enabled;
    }

    public boolean isMouseOver(int mouseX, int mouseY)
    {
        int x = this.getX();
        int y = this.getY();

        return mouseX >= x && mouseX < x + this.getWidth() &&
               mouseY >= y && mouseY < y + this.getHeight();
    }

    public boolean tryMouseClick(int mouseX, int mouseY, int mouseButton)
    {
        if (this.getShouldReceiveOutsideClicks() || this.isMouseOver(mouseX, mouseY))
        {
            return this.onMouseClicked(mouseX, mouseY, mouseButton);
        }

        return false;
    }

    protected boolean onMouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (this.clickListener != null)
        {
            this.clickListener.onEvent();
            return true;
        }

        return false;
    }

    public void onMouseReleased(int mouseX, int mouseY, int mouseButton)
    {
    }

    public boolean tryMouseScroll(int mouseX, int mouseY, double mouseWheelDelta)
    {
        if (this.getShouldReceiveOutsideScrolls() || this.isMouseOver(mouseX, mouseY))
        {
            return this.onMouseScrolled(mouseX, mouseY, mouseWheelDelta);
        }

        return false;
    }

    protected boolean onMouseScrolled(int mouseX, int mouseY, double mouseWheelDelta)
    {
        return false;
    }

    public boolean onMouseMoved(int mouseX, int mouseY)
    {
        return false;
    }

    public boolean onKeyTyped(int keyCode, int scanCode, int modifiers)
    {
        return false;
    }

    public boolean onCharTyped(char charIn, int modifiers)
    {
        return false;
    }

    /**
     * Returns true if this widget can be hovered (for hover info etc.) at the given point
     */
    public boolean canHoverAt(int mouseX, int mouseY, int mouseButton)
    {
        return true;
    }

    public boolean hasHoverText()
    {
        return this.hoverInfoFactory.hasNoStrings() == false;
    }

    public ImmutableList<StyledTextLine> getHoverHelp()
    {
        if (this.hoverHelp == null)
        {
            this.hoverHelp = StyledText.translate("malilibdev.hover.misc.hold_shift_for_info").lines;
        }

        return this.hoverHelp;
    }

    public ImmutableList<StyledTextLine> getHoverText()
    {
        if (this.hoverInfoRequiresShift &&
            BaseScreen.isShiftDown() == false &&
            this.hoverInfoFactory.getStyledLines().isEmpty() == false)
        {
            return this.getHoverHelp();
        }

        return this.hoverInfoFactory.getStyledLines();
    }

    public OrderedStringListFactory getHoverInfoFactory()
    {
        return this.hoverInfoFactory;
    }

    public void setHoverInfoFactory(OrderedStringListFactory hoverInfoFactory)
    {
        this.hoverInfoFactory = hoverInfoFactory;
    }

    public void updateHoverStrings()
    {
        this.hoverInfoFactory.updateList();
    }

    /**
     * <b>Note:</b> The strings should be localized already.
     */
    public void addHoverStrings(String... hoverStrings)
    {
        this.hoverInfoFactory.addStrings(Arrays.asList(hoverStrings));
    }

    public void translateAndAddHoverString(String translationKey, Object... args)
    {
        this.hoverInfoFactory.addStrings(StringUtils.translate(translationKey, args));
    }

    /**
     * Adds the provided hover string supplier, by using the provided key.<br>
     * The key can be used to remove this string provider later.<br>
     * <b>Note:</b> The returned strings should be localized already.
     */
    public void setHoverStringProvider(String key, Supplier<List<String>> supplier)
    {
        this.hoverInfoFactory.setStringListProvider(key, supplier);
    }

    @Override
    public boolean isHoveredForRender(ScreenContext ctx)
    {
        if (this.renderHoverChecker != null)
        {
            return this.renderHoverChecker.isHovered(ctx);
        }

        return ctx.hoveredWidgetId == this.getId();/* ||
               (ctx.isActiveScreen && this.isMouseOver(ctx.mouseX, ctx.mouseY));*/
    }

    public boolean shouldRenderHoverInfo(ScreenContext ctx)
    {
        return this.getId() == ctx.hoveredWidgetId &&
               this.isHoveredForRender(ctx) &&
               this.canHoverAt(ctx.mouseX, ctx.mouseY, 0);
    }

    protected void renderIcon(int x, int y, float z, boolean enabled, boolean hovered, ScreenContext ctx)
    {
        Icon icon = this.getIcon();

        if (icon != null)
        {
            int usableWidth = this.getWidth() - this.padding.getHorizontalTotal();
            int usableHeight = this.getHeight() - this.padding.getVerticalTotal();
            x = this.getIconPositionX(x, usableWidth, icon.getWidth());
            y = this.getIconPositionY(y, usableHeight, icon.getHeight());

            icon.renderAt(x, y, z + 0.0125f, enabled, hovered);
        }
    }

    @Override
    public void renderAt(int x, int y, float z, ScreenContext ctx)
    {
        this.renderWidgetBackgroundAndBorder(x, y, z, ctx);

        boolean hovered = this.isHoveredForRender(ctx);
        int color = this.getTextColorForRender(hovered);

        this.renderIcon(x, y, z, true, false, ctx);
        this.renderText(x, y, z, color, ctx);
    }

    public void postRenderHovered(ScreenContext ctx)
    {
        if (this.shouldRenderHoverInfo(ctx))
        {
            if (this.hoverInfoWidget != null)
            {
                renderHoverInfoWidget(this.hoverInfoWidget, this.getZ() + 50f, ctx);
            }
            else if (this.hasHoverText())
            {
                TextRenderUtils.renderStyledHoverText(ctx.mouseX, ctx.mouseY, this.getZ() + 50f, this.getHoverText());
            }
        }
    }

    @Override
    public void renderDebug(int x, int y, float z, boolean hovered, ScreenContext ctx)
    {
        super.renderDebug(x, y, z, hovered, ctx);

        if (hovered && this.hoverInfoWidget != null && this.shouldRenderHoverInfo(ctx))
        {
            Vec2i pos = getHoverInfoWidgetRenderPosition(this.hoverInfoWidget, ctx);
            this.hoverInfoWidget.renderDebug(pos.x, pos.y, z, true, ctx);
        }
    }

    @Nullable
    public InteractableWidget getTopHoveredWidget(int mouseX, int mouseY, @Nullable InteractableWidget highestFoundWidget)
    {
        if (this.canInteract() && this.isMouseOver(mouseX, mouseY) &&
            (highestFoundWidget == null || this.getZ() > highestFoundWidget.getZ()))
        {
            return this;
        }

        return highestFoundWidget;
    }

    @Nullable
    public static InteractableWidget getTopHoveredWidgetFromList(List<? extends InteractableWidget> widgets, int mouseX, int mouseY, @Nullable InteractableWidget highestFoundWidget)
    {
        if (widgets.isEmpty() == false)
        {
            for (InteractableWidget widget : widgets)
            {
                highestFoundWidget = widget.getTopHoveredWidget(mouseX, mouseY, highestFoundWidget);
            }
        }

        return highestFoundWidget;
    }

    public static void renderHoverInfoWidget(BaseWidget widget, float z, ScreenContext ctx)
    {
        Vec2i pos = getHoverInfoWidgetRenderPosition(widget, ctx);
        widget.renderAt(pos.x, pos.y, z, ctx);
    }

    public static Vec2i getHoverInfoWidgetRenderPosition(BaseWidget widget, ScreenContext ctx)
    {
        int w = widget.getWidth();
        int h = widget.getHeight();
        return TextRenderUtils.getScreenClampedHoverTextStartPosition(ctx.mouseX, ctx.mouseY, w, h);
    }

    public interface HoverChecker
    {
        boolean isHovered(ScreenContext ctx);
    }
}
