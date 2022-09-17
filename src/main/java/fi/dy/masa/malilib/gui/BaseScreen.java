package fi.dy.masa.malilib.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import fi.dy.masa.malilib.MaLiLibConfigs;
import fi.dy.masa.malilib.gui.icon.DefaultIcons;
import fi.dy.masa.malilib.gui.util.GuiUtils;
import fi.dy.masa.malilib.gui.util.ScreenContext;
import fi.dy.masa.malilib.gui.widget.BaseTextFieldWidget;
import fi.dy.masa.malilib.gui.widget.BaseWidget;
import fi.dy.masa.malilib.gui.widget.InteractableWidget;
import fi.dy.masa.malilib.gui.widget.LabelWidget;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;
import fi.dy.masa.malilib.input.ActionResult;
import fi.dy.masa.malilib.input.Keys;
import fi.dy.masa.malilib.listener.EventListener;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.render.ShapeRenderUtils;
import fi.dy.masa.malilib.render.text.StyledTextLine;
import fi.dy.masa.malilib.render.text.TextRenderer;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.malilib.util.game.wrap.GameUtils;
import fi.dy.masa.malilib.util.position.Vec2i;

public abstract class BaseScreen extends Screen
{
    protected final MinecraftClient mc = GameUtils.getClient();
    protected final TextRenderer textRenderer = TextRenderer.INSTANCE;
    protected final List<Runnable> tasks = new ArrayList<>();
    private final List<InteractableWidget> widgets = new ArrayList<>();

    private int keyInputCount;
    private String titleString = "";

    @Nullable protected EventListener screenCloseListener;
    @Nullable protected StyledTextLine titleText;
    @Nullable private Screen parent;
    @Nullable protected InteractableWidget hoveredWidget;
    @Nullable protected ScreenContext context;
    protected GenericButton closeButton;
    protected Vec2i dragStartOffset = Vec2i.ZERO;
    protected int backgroundColor = 0xB0000000;
    protected int borderColor = 0xFF999999;
    protected int customScreenScale;
    protected int x;
    protected int y;
    protected float z;
    protected int screenWidth;
    protected int screenHeight;
    protected int oldWidth;
    protected int oldHeight;
    protected int titleColor = 0xFFFFFFFF;
    protected int titleX = 10;
    protected int titleY = 6;
    protected boolean addCloseButton = true;
    protected boolean canDragMove;
    protected boolean dragging;
    protected boolean isPopup;
    protected boolean renderBorder;
    protected boolean shouldCenter;
    protected boolean shouldRenderParent;
    protected boolean useCustomScreenScaling;
    protected boolean useTitleHierarchy = true;

    public BaseScreen()
    {
        super(Text.of(""));

        int customScale = MaLiLibConfigs.Generic.CUSTOM_SCREEN_SCALE.getIntegerValue();
        this.useCustomScreenScaling = customScale != this.getVanillaGuiScale() && customScale > 0;
        this.closeButton = GenericButton.create(DefaultIcons.CLOSE_BUTTON_9, this::closeScreenOrShowParent);
        this.closeButton.translateAndAddHoverString("malilib.hover.misc.close_screen");
        this.closeButton.setPlayClickSound(false);
    }

    @Override
    public void init()
    {
        super.init();
        this.initScreen();
    }

    @Override
    public void removed()
    {
        super.removed();
        this.onScreenClosed();
    }

    // TODO 1.13+ port - does this init hierarchy work?
    @Override
    protected void clearAndInit()
    {
        this.onScreenResolutionSet(this.width, this.height);

        if (this.useCustomScreenScaling)
        {
            this.width = this.getTotalWidth();
            this.height = this.getTotalHeight();
        }

        if (this.getParent() != null)
        {
            this.getParent().init(this.mc, this.width, this.height);
        }

        super.clearAndInit();
    }

    @Override
    public boolean shouldPause()
    {
        return this.getParent() != null && this.getParent().shouldPause();
    }

    protected void initScreen()
    {
        this.reAddActiveWidgets();
        this.updateWidgetPositions();
        this.mc.keyboard.setRepeatEvents(true);
    }

    protected void onScreenClosed()
    {
        this.mc.keyboard.setRepeatEvents(false);

        if (this.screenCloseListener != null)
        {
            this.screenCloseListener.onEvent();
        }
    }

    @Override
    public void resize(MinecraftClient client, int width, int height)
    {
        this.oldWidth = width;
        this.oldHeight = height;

        super.resize(client, width, height);
    }

    protected void onScreenResolutionSet(int width, int height)
    {
        boolean initial = this.isFullScreen() || this.screenWidth == 0 || this.screenHeight == 0;

        this.updateCustomScreenScale();

        if (this.useCustomScreenScaling)
        {
            width = this.getTotalWidth();
            height = this.getTotalHeight();
        }

        // Don't override custom screen sizes when the window is resized or whatever,
        // which calls this method again.
        if (initial)
        {
            this.setScreenWidthAndHeight(width, height);
        }

        if (this.shouldCenter)
        {
            this.centerOnScreen();
        }

        this.oldWidth = 0;
        this.oldHeight = 0;
    }

    protected boolean isFullScreen()
    {
        return !this.isPopup && (this.screenWidth != this.getTotalWidth() || this.screenHeight != this.getTotalHeight());
    }

    protected int getVanillaGuiScale()
    {
        return this.mc.options.getGuiScale().getValue();
    }

    protected void updateCustomScreenScale()
    {
        int currentValue = MaLiLibConfigs.Generic.CUSTOM_SCREEN_SCALE.getIntegerValue();

        if (currentValue != this.customScreenScale)
        {
            boolean oldUseCustomScale = this.useCustomScreenScaling;
            this.useCustomScreenScaling = currentValue > 0 && currentValue != this.getVanillaGuiScale();
            this.customScreenScale = currentValue;

            if ((oldUseCustomScale || this.useCustomScreenScaling) && currentValue > 0)
            {
                this.setWidthAndHeightForScale(currentValue);
            }
        }
    }

    protected void setWidthAndHeightForScale(double scaleFactor)
    {
        int width = (int) Math.ceil((double) GuiUtils.getDisplayWidth() / scaleFactor);
        int height = (int) Math.ceil((double) GuiUtils.getDisplayHeight() / scaleFactor);

        if (this.getTotalWidth() != width || this.getTotalHeight() != height)
        {
            // Only set the screen size if it was originally the same as the window dimensions,
            // ie. the screen was not a smaller (popup?) screen.
            boolean setScreenSize = this.isFullScreen();

            this.width = width;
            this.height = height;

            // Only set the screen size if it was originally the same as the window dimensions,
            // ie. the screen was not a smaller (popup?) screen.
            if (setScreenSize)
            {
                this.setScreenWidthAndHeight(width, height);
            }
        }
    }

    protected void setScreenWidthAndHeight(int width, int height)
    {
        this.screenWidth = width;
        this.screenHeight = height;
    }

    public void setScreenWidth(int screenWidth)
    {
        this.screenWidth = screenWidth;
    }

    public void setScreenHeight(int screenHeight)
    {
        this.screenHeight = screenHeight;
    }

    public int getTotalWidth()
    {
        if (this.oldWidth != 0) {
            return this.oldWidth;
        }

        return this.width;
    }

    public int getTotalHeight()
    {
        if (this.oldHeight != 0) {
            return this.oldHeight;
        }

        return this.height;
    }

    public void centerOnScreen()
    {
        int x;
        int y;
        Screen parent = this.getParent();
        Screen current = GuiUtils.getCurrentScreen();

        if (parent instanceof BaseScreen)
        {
            BaseScreen parentBaseScreen = (BaseScreen) parent;
            x = parentBaseScreen.x + parentBaseScreen.getScreenWidth() / 2;
            y = parentBaseScreen.y + parentBaseScreen.getScreenHeight() / 2;
        }
        else if (parent != null)
        {
            x = parent.width / 2;
            y = parent.height / 2;
        }
        else if (current != null)
        {
            x = current.width / 2;
            y = current.height / 2;
        }
        else
        {
            x = GuiUtils.getScaledWindowWidth() / 2;
            y = GuiUtils.getScaledWindowHeight() / 2;
        }

        x -= this.getScreenWidth() / 2;
        y -= this.getScreenHeight() / 2;

        this.setPosition(x, y);
    }

    protected void reAddActiveWidgets()
    {
        this.clearElements();

        if (this.addCloseButton && this.closeButton != null)
        {
            this.addWidget(this.closeButton);
        }
    }

    protected void updateWidgetPositions()
    {
        if (this.closeButton != null)
        {
            int x = this.getRight() - this.closeButton.getWidth() - 2;
            this.closeButton.setPosition(x, this.y + 2);
        }
    }

    public int getX()
    {
        return this.x;
    }

    public int getY()
    {
        return this.y;
    }

    public int getRight()
    {
        return this.x + this.screenWidth;
    }

    public int getBottom()
    {
        return this.y + this.screenHeight;
    }

    public int getScreenWidth()
    {
        return this.screenWidth;
    }

    public int getScreenHeight()
    {
        return this.screenHeight;
    }

    public void setPosition(int x, int y)
    {
        int oldX = this.x;
        int oldY = this.y;

        this.x = x;
        this.y = y;

        if (this.x != oldX || this.y != oldY)
        {
            this.updateWidgetPositions();
        }
    }

    @Override
    public Text getTitle()
    {
        return Text.of(this.getTitleString());
    }

    @Nullable
    public Screen getParent()
    {
        return this.parent;
    }

    public String getTitleString()
    {
        if (this.useTitleHierarchy && this.parent instanceof BaseScreen)
        {
            return ((BaseScreen) this.parent).getTitleString() + " => " + this.titleString;
        }

        return this.titleString;
    }

    public void setTitle(@Nullable String titleKey, Object... args)
    {
        if (org.apache.commons.lang3.StringUtils.isBlank(titleKey))
        {
            this.titleString = "";
            this.titleText = null;
        }
        else
        {
            this.titleString = StringUtils.translate(titleKey, args);
            this.titleText = StyledTextLine.of(this.getTitleString());
        }
    }

    public BaseScreen setParent(@Nullable Screen parent)
    {
        // Don't allow nesting the GUI with itself...
        if (parent != this)
        {
            this.parent = parent;
        }

        return this;
    }

    public void setRenderBorder(boolean renderBorder)
    {
        this.renderBorder = renderBorder;
    }

    public void setShouldRenderParent(boolean render)
    {
        this.shouldRenderParent = render;
    }

    public void setCanDragMove(boolean canDragMove)
    {
        this.canDragMove = canDragMove;
    }

    public void setIsPopup(boolean isPopup)
    {
        this.isPopup = isPopup;
    }

    protected int getPopupGuiZLevelIncrement()
    {
        return 50;
    }

    /**
     * Shows the parent screen (if one is set), unless the shift key is held,
     * in which case the screen is closed entirely.
     */
    protected void closeScreenOrShowParent()
    {
        if (isShiftDown())
        {
            this.closeScreen();
        }
        else
        {
            this.openParentScreen();
        }
    }

    protected void closeScreen()
    {
        openScreen(null);
    }

    protected void openParentScreen()
    {
        openScreen(this.parent);
    }

    protected InteractableWidget getTopHoveredWidget(int mouseX, int mouseY,
                                                     @Nullable InteractableWidget highestFoundWidget)
    {
        return InteractableWidget.getTopHoveredWidgetFromList(this.widgets, mouseX, mouseY, highestFoundWidget);
    }

    protected void updateTopHoveredWidget(int mouseX, int mouseY, boolean isActiveScreen)
    {
        this.hoveredWidget = isActiveScreen ? this.getTopHoveredWidget(mouseX, mouseY, null) : null;
    }

    public ScreenContext getContext(MatrixStack matrices)
    {
        int mouseX = GuiUtils.getMouseScreenX(this.getTotalWidth());
        int mouseY = GuiUtils.getMouseScreenY(this.getTotalHeight());
        boolean isActiveScreen = GuiUtils.getCurrentScreen() == this;
        int hoveredWidgetId = this.hoveredWidget != null ? this.hoveredWidget.getId() : -1;

        //if (this.context == null ||
        //    this.context.matches(mouseX, mouseY, isActiveScreen, hoveredWidgetId) == false)
        {
            this.context = new ScreenContext(mouseX, mouseY, hoveredWidgetId, isActiveScreen, matrices);
        }

        return this.context;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float partialTicks)
    {
        this.runTasks();

        if (this.shouldRenderParent && this.getParent() != null)
        {
            this.getParent().render(matrices, mouseX, mouseY, partialTicks);
        }

        RenderUtils.color(1f, 1f, 1f, 1f);
        RenderUtils.setupBlend();

        // These are after the parent rendering, because the parent
        // can/will also both enable and disable the custom scale,
        // so it needs to be enabled here again in any case after
        // rendering the parent screen.
        if (this.useCustomScreenScaling)
        {
            RenderUtils.setupScaledScreenRendering(this.customScreenScale);
        }

        ScreenContext ctx = this.getContext(matrices);

        // Update again after the input is handled
        // TODO 1.13+ port is this enough here? It was before and after "raw mouse input handling" in 1.12.2
        boolean isActiveGui = GuiUtils.getCurrentScreen() == this;
        this.updateTopHoveredWidget(mouseX, mouseY, isActiveGui);

        this.renderScreenBackground(ctx);
        this.renderScreenTitle(ctx);

        // Draw base widgets
        this.renderWidgets(ctx);
        //super.drawScreen(mouseX, mouseY, partialTicks);

        this.renderCustomContents(ctx);

        this.renderHoveredWidget(ctx);

        if (MaLiLibConfigs.Debug.GUI_DEBUG.getBooleanValue() &&
            MaLiLibConfigs.Debug.GUI_DEBUG_KEY.isHeld())
        {
            this.renderDebug(ctx);
        }

        BaseWidget.renderDebugTextAndClear(ctx);

        if (this.useCustomScreenScaling)
        {
            RenderUtils.setupScaledScreenRendering(RenderUtils.getVanillaScreenScale());
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton)
    {
        if (this.useCustomScreenScaling)
        {
            mouseX = GuiUtils.getMouseScreenX(this.getTotalWidth());
            mouseY = GuiUtils.getMouseScreenY(this.getTotalHeight());
        }

        if (this.onMouseClicked((int) mouseX, (int) mouseY, mouseButton))
        {
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int mouseButton)
    {
        this.dragging = false;

        if (this.useCustomScreenScaling)
        {
            mouseX = GuiUtils.getMouseScreenX(this.getTotalWidth());
            mouseY = GuiUtils.getMouseScreenY(this.getTotalHeight());
        }

        if (this.onMouseReleased((int) mouseX, (int) mouseY, mouseButton))
        {
            return true;
        }

        return super.mouseReleased(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount)
    {
        if (this.onMouseScrolled((int) mouseX, (int) mouseY, amount))
        {
            return true;
        }

        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY)
    {
        if (this.onMouseMoved((int) mouseX, (int) mouseY))
        {
            return true;
        }

        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        this.keyInputCount++;

        if (this.onKeyTyped(keyCode, scanCode, modifiers))
        {
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers)
    {
        // This is an ugly fix for the issue that the key press from the hotkey that
        // opens a Screen would then also get into any text fields or search bars, as the
        // charTyped() event always fires after the keyPressed() event in any case >_>
        if (this.keyInputCount++ <= 0)
        {
            return true;
        }

        if (this.onCharTyped(chr, modifiers))
        {
            return true;
        }

        return super.charTyped(chr, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers)
    {
        if (this.onKeyReleased(keyCode, scanCode, modifiers))
        {
            return true;
        }

        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    public boolean onMouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        List<BaseTextFieldWidget> textFields = this.getAllTextFields();
        InteractableWidget clickedWidget = null;

        // Clear the focus from all text fields
        for (BaseTextFieldWidget tf : textFields)
        {
            tf.setFocused(false);
        }

        if (this.hoveredWidget != null &&
            this.hoveredWidget.tryMouseClick(mouseX, mouseY, mouseButton))
        {
            clickedWidget = this.hoveredWidget;
        }
        else
        {
            for (InteractableWidget widget : this.widgets)
            {
                if (widget.tryMouseClick(mouseX, mouseY, mouseButton))
                {
                    clickedWidget = widget;
                    break;
                }
            }
        }

        // Only call super if the click wasn't handled
        if (clickedWidget != null)
        {
            return true;
        }

        if (this.canDragMove && this.isMouseOver(mouseX, mouseY))
        {
            this.dragStartOffset = new Vec2i(mouseX - this.getX(),  mouseY - this.getY());
            this.dragging = true;
            return true;
        }

        return false;
    }

    public boolean onMouseReleased(int mouseX, int mouseY, int mouseButton)
    {
        for (InteractableWidget widget : this.widgets)
        {
            widget.onMouseReleased(mouseX, mouseY, mouseButton);
        }

        return false;
    }

    public boolean onMouseScrolled(int mouseX, int mouseY, double mouseWheelDelta)
    {
        if (this.hoveredWidget != null &&
            this.hoveredWidget.tryMouseScroll(mouseX, mouseY, mouseWheelDelta))
        {
            return true;
        }

        for (InteractableWidget widget : this.widgets)
        {
            if (widget.tryMouseScroll(mouseX, mouseY, mouseWheelDelta))
            {
                // Don't call super if the action got handled
                return true;
            }
        }

        return false;
    }

    public boolean onMouseMoved(int mouseX, int mouseY)
    {
        if (this.dragging)
        {
            int x = mouseX - this.dragStartOffset.x;
            int y = mouseY - this.dragStartOffset.y;

            this.setPosition(x, y);

            return true;
        }

        if (this.hoveredWidget != null && this.hoveredWidget.onMouseMoved(mouseX, mouseY))
        {
            return true;
        }

        for (InteractableWidget widget : this.widgets)
        {
            if (widget.onMouseMoved(mouseX, mouseY))
            {
                return true;
            }
        }

        return false;
    }

    public boolean isMouseOver(int mouseX, int mouseY)
    {
        return mouseX >= this.x && mouseX <= this.x + this.screenWidth &&
               mouseY >= this.y && mouseY <= this.y + this.screenHeight;
    }

    public boolean onKeyTyped(int keyCode, int scanCode, int modifiers)
    {
        if (keyCode == Keys.KEY_TAB &&
            GuiUtils.changeTextFieldFocus(this.getAllTextFields(), isShiftDown()))
        {
            return true;
        }

        for (InteractableWidget widget : this.widgets)
        {
            if (widget.onKeyTyped(keyCode, scanCode, modifiers))
            {
                // Don't call super if the button press got handled
                return true;
            }
        }

        if (keyCode == Keys.KEY_ESCAPE)
        {
            this.closeScreenOrShowParent();
            return true;
        }

        return false;
    }

    public boolean onKeyReleased(int keyCode, int scanCode, int modifiers)
    {
        return false;
    }

    public boolean onCharTyped(char charIn, int modifiers)
    {
        for (InteractableWidget widget : this.widgets)
        {
            if (widget.onCharTyped(charIn, modifiers))
            {
                // Don't call super if the button press got handled
                return true;
            }
        }

        return false;
    }

    protected List<BaseTextFieldWidget> getAllTextFields()
    {
        List<BaseTextFieldWidget> textFields = new ArrayList<>();

        for (InteractableWidget widget : this.widgets)
        {
            textFields.addAll(widget.getAllTextFields());
        }

        return textFields;
    }

    public void bindTexture(Identifier texture)
    {
        RenderUtils.bindTexture(texture);
    }

    public BaseScreen setZ(float z)
    {
        this.z = z;

        for (InteractableWidget widget : this.widgets)
        {
            widget.setZLevelBasedOnParent(z);
        }

        return this;
    }

    public BaseScreen setPopupGuiZLevelBasedOn(@Nullable Screen gui)
    {
        if (gui instanceof BaseScreen)
        {
            this.setZ(((BaseScreen) gui).z + this.getPopupGuiZLevelIncrement());
        }

        return this;
    }

    public <T extends InteractableWidget> T addWidget(T widget)
    {
        this.widgets.add(widget);
        widget.setTaskQueue(this::addTask);
        widget.onWidgetAdded(this.z);
        return widget;
    }

    public <T extends BaseTextFieldWidget> T addTextField(T widget, Consumer<String> listener)
    {
        widget.setListener(listener);
        this.addWidget(widget);
        return widget;
    }

    public LabelWidget addLabel(int x, int y, int textColor, String... lines)
    {
        return this.addLabel(x, y, -1, -1, textColor, Arrays.asList(lines));
    }

    public LabelWidget addLabel(int x, int y, int textColor, List<String> lines)
    {
        return this.addLabel(x, y, -1, -1, textColor, lines);
    }

    public LabelWidget addLabel(int x, int y, int width, int height, int textColor, String... lines)
    {
        return this.addLabel(x, y, width, height, textColor, Arrays.asList(lines));
    }

    public LabelWidget addLabel(int x, int y, int width, int height, int textColor, List<String> lines)
    {
        LabelWidget widget = this.addWidget(new LabelWidget(x, y, width, height, textColor));
        widget.setLabelText(lines);
        return widget;
    }

    protected boolean removeWidget(InteractableWidget widget)
    {
        if (widget != null && this.widgets.contains(widget))
        {
            this.widgets.remove(widget);
            return true;
        }

        return false;
    }

    protected void clearElements()
    {
        this.clearWidgets();
    }

    protected void clearWidgets()
    {
        this.widgets.clear();
    }

    protected void addTask(Runnable task)
    {
        this.tasks.add(task);
    }

    protected void runTasks()
    {
        if (this.tasks.isEmpty() == false)
        {
            for (Runnable task : this.tasks)
            {
                task.run();
            }

            this.tasks.clear();
        }
    }

    public void updateWidgetStates()
    {
        for (InteractableWidget widget : this.widgets)
        {
            widget.updateWidgetState();
        }
    }

    protected void renderScreenBackground(ScreenContext ctx)
    {
        if (this.renderBorder)
        {
            ShapeRenderUtils.renderOutlinedRectangle(this.x, this.y, this.z,
                                                     this.screenWidth, this.screenHeight,
                                                     this.backgroundColor, this.borderColor);
        }
        else
        {
            ShapeRenderUtils.renderRectangle(this.x, this.y, this.z,
                                             this.screenWidth, this.screenHeight,
                                             this.backgroundColor);
        }
    }

    protected void renderScreenTitle(ScreenContext ctx)
    {
        if (this.titleText != null)
        {
            int x = this.x + this.titleX;
            int y = this.y + this.titleY;

            this.textRenderer.renderLine(x, y, this.z + 0.125f, this.titleColor, true, this.titleText, ctx);
        }
    }

    protected void renderCustomContents(ScreenContext ctx)
    {
    }

    protected void renderWidgets(ScreenContext ctx)
    {
        for (InteractableWidget widget : this.widgets)
        {
            widget.render(ctx);
        }
    }

    protected void renderHoveredWidget(ScreenContext ctx)
    {
        if (this.hoveredWidget != null)
        {
            this.hoveredWidget.postRenderHovered(ctx);
            RenderUtils.disableItemLighting();
        }
    }

    public int getStringWidth(String text)
    {
        return StringUtils.getStringWidth(text);
    }

    public static boolean openScreen(@Nullable Screen screen)
    {
        GameUtils.getClient().setScreen(screen);
        return true;
    }

    public static boolean openScreenWithParent(@Nullable BaseScreen screen)
    {
        screen.setParent(GuiUtils.getCurrentScreen());
        GameUtils.getClient().setScreen(screen);
        return true;
    }

    public static ActionResult openScreenAction(@Nullable Screen screen)
    {
        openScreen(screen);
        return ActionResult.SUCCESS;
    }

    /**
     * Opens a popup screen, which is meant to open on top of another screen.
     * This will set the Z level of that screen based on the current screen,
     * and the screen will also be marked as draggable.
     */
    public static boolean openPopupScreen(BaseScreen screen)
    {
        return openPopupScreen(screen, true);
    }

    /**
     * Opens a popup screen, which is meant to open on top of another screen.
     * This will set the Z level of that screen based on the current screen,
     * and the screen will also be marked as draggable.
     */
    public static boolean openPopupScreen(BaseScreen screen, boolean shouldRenderParent)
    {
        screen.setPopupGuiZLevelBasedOn(GuiUtils.getCurrentScreen());
        screen.setShouldRenderParent(shouldRenderParent);
        screen.setCanDragMove(true);
        screen.setIsPopup(true);
        return openScreen(screen);
    }

    public static void applyCustomScreenScaleChange()
    {
        Screen screen = GuiUtils.getCurrentScreen();

        if (screen instanceof BaseScreen)
        {
            ((BaseScreen) screen).updateCustomScreenScale();
            ((BaseScreen) screen).initScreen();
        }
    }

    public static boolean isShiftDown()
    {
        return hasShiftDown();
    }

    public static boolean isCtrlDown()
    {
        return hasControlDown();
    }

    public static boolean isAltDown()
    {
        return hasAltDown();
    }

    public static boolean isKeyComboCtrlA(int keyCode)
    {
        return isSelectAll(keyCode);
    }

    public static boolean isKeyComboCtrlC(int keyCode)
    {
        return isCopy(keyCode);
    }

    public static boolean isKeyComboCtrlX(int keyCode)
    {
        return isCut(keyCode);
    }

    public static boolean isKeyComboCtrlV(int keyCode)
    {
        return isPaste(keyCode);
    }

    public static void setStringToClipboard(String str)
    {
        MinecraftClient.getInstance().keyboard.setClipboard(str);
    }

    public static String getStringFromClipboard()
    {
        return MinecraftClient.getInstance().keyboard.getClipboard();
    }

    public void renderDebug(ScreenContext ctx)
    {
        if (ctx.isActiveScreen)
        {
            String str = String.format("%s @ x: %d, y: %d, w: %d, h: %d",
                                       this.getClass().getName(),
                                       this.x, this.y, this.screenWidth, this.screenHeight);
            StyledTextLine line = StyledTextLine.of(str);

            int x = this.x + 1;
            int y = this.y + 1;
            float z = this.z + 20;
            int w = line.renderWidth + 4;

            // if this is a popup-screen or other screen that does not extend
            // to the bottom of the display, then render the info bar below the
            // screen area, to not obstruct other widgets.
            if (this.y + this.screenHeight + 14 < this.getTotalHeight())
            {
                x = this.x;
                y = this.y + this.screenHeight + 1;
            }
            else if (this.y >= 15)
            {
                x = this.x;
                y = this.y - 15;
            }
            else if (ctx.mouseY < this.getTotalHeight() / 2)
            {
                y = this.y + this.screenHeight - 15;
            }

            ShapeRenderUtils.renderOutlinedRectangle(x, y, z, w, 14, 0xFF000000, 0xFFA0A0A0);
            this.textRenderer.renderLine(x + 2, y + 3, z + 0.00125f, 0xFF00FFFF, true, line, ctx);

            renderWidgetDebug(this.widgets, ctx);
        }
    }

    public static void renderWidgetDebug(List<? extends InteractableWidget> widgets, ScreenContext ctx)
    {
        for (InteractableWidget widget : widgets)
        {
            boolean hovered = widget.isMouseOver(ctx.mouseX, ctx.mouseY);
            widget.renderDebug(hovered, ctx);
        }
    }
}
