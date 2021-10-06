package fi.dy.masa.malilib.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import fi.dy.masa.malilib.MaLiLibConfigs;
import fi.dy.masa.malilib.gui.config.liteloader.DialogHandler;
import fi.dy.masa.malilib.gui.icon.DefaultIcons;
import fi.dy.masa.malilib.gui.util.GuiUtils;
import fi.dy.masa.malilib.gui.util.ScreenContext;
import fi.dy.masa.malilib.gui.widget.BaseTextFieldWidget;
import fi.dy.masa.malilib.gui.widget.BaseWidget;
import fi.dy.masa.malilib.gui.widget.InteractableWidget;
import fi.dy.masa.malilib.gui.widget.LabelWidget;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;
import fi.dy.masa.malilib.input.ActionResult;
import fi.dy.masa.malilib.listener.TextChangeListener;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.render.ShapeRenderUtils;
import fi.dy.masa.malilib.render.text.StyledTextLine;
import fi.dy.masa.malilib.render.text.TextRenderer;
import fi.dy.masa.malilib.util.GameUtils;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.malilib.util.data.Vec2i;

public abstract class BaseScreen extends GuiScreen
{
    protected final Minecraft mc = GameUtils.getClient();
    protected final TextRenderer textRenderer = TextRenderer.INSTANCE;
    protected final List<Runnable> tasks = new ArrayList<>();
    private final List<InteractableWidget> widgets = new ArrayList<>();
    private String titleString = "";
    @Nullable protected StyledTextLine titleText;
    @Nullable private GuiScreen parent;
    @Nullable protected DialogHandler dialogHandler;
    @Nullable protected InteractableWidget hoveredWidget;
    @Nullable protected ScreenContext context;
    protected GenericButton closeButton;
    protected Vec2i dragStartOffset = Vec2i.ZERO;
    protected int backgroundColor = 0xB0000000;
    protected int borderColor = 0xFF999999;
    protected int customScreenScale;
    protected int x;
    protected int y;
    protected int lastMouseX = -1;
    protected int lastMouseY = -1;
    protected int screenWidth;
    protected int screenHeight;
    protected int titleColor = 0xFFFFFFFF;
    protected int titleX = 10;
    protected int titleY = 6;
    protected boolean addCloseButton = true;
    protected boolean canDragMove;
    protected boolean dragging;
    protected boolean renderBorder;
    protected boolean shouldCenter;
    protected boolean shouldRenderParent;
    protected boolean useCustomScreenScaling;
    protected boolean useTitleHierarchy = true;

    public BaseScreen()
    {
        int customScale = MaLiLibConfigs.Generic.CUSTOM_SCREEN_SCALE.getIntegerValue();
        this.useCustomScreenScaling = customScale != this.mc.gameSettings.guiScale && customScale > 0;
        this.closeButton = GenericButton.createIconOnly(DefaultIcons.CLOSE_BUTTON_9, this::closeScreenOrShowParent);
        this.closeButton.translateAndAddHoverString("malilib.hover_info.close_screen");
        this.closeButton.setPlayClickSound(false);
    }

    public int getX()
    {
        return this.x;
    }

    public int getY()
    {
        return this.y;
    }

    public int getScreenWidth()
    {
        return this.screenWidth;
    }

    public int getScreenHeight()
    {
        return this.screenHeight;
    }

    @Nullable
    public GuiScreen getParent()
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

    public BaseScreen setParent(@Nullable GuiScreen parent)
    {
        // Don't allow nesting the GUI with itself...
        if (parent == null || parent.getClass() != this.getClass())
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

    protected int getPopupGuiZLevelIncrement()
    {
        return 50;
    }

    @Override
    public void onGuiClosed()
    {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    public boolean doesGuiPauseGame()
    {
        return this.getParent() != null && this.getParent().doesGuiPauseGame();
    }

    @Override
    public void setWorldAndResolution(Minecraft mc, int width, int height)
    {
        if (this.getParent() != null)
        {
            this.getParent().setWorldAndResolution(mc, width, height);
        }

        boolean initial = this.screenWidth == this.width && this.screenHeight == this.height;

        this.updateCustomScreenScale();

        if (this.shouldUseCustomScreenScaling())
        {
            width = this.width;
            height = this.height;
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

        super.setWorldAndResolution(mc, width, height);
    }

    @Override
    public void initGui()
    {
        super.initGui();

        this.initScreen();
    }

    protected void initScreen()
    {
        this.reAddActiveWidgets();
        this.updateWidgetPositions();
        Keyboard.enableRepeatEvents(true);
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
            int x = this.x + this.screenWidth - this.closeButton.getWidth() - 2;
            int y = this.y + 2;
            this.closeButton.setPosition(x, y);
        }
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

    protected boolean shouldUseCustomScreenScaling()
    {
        return this.useCustomScreenScaling;
    }

    protected void updateCustomScreenScale()
    {
        int currentValue = MaLiLibConfigs.Generic.CUSTOM_SCREEN_SCALE.getIntegerValue();

        if (currentValue != this.customScreenScale)
        {
            boolean oldUseCustomScale = this.shouldUseCustomScreenScaling();
            this.useCustomScreenScaling = currentValue > 0 && currentValue != this.mc.gameSettings.guiScale;
            this.customScreenScale = currentValue;

            if (oldUseCustomScale || this.shouldUseCustomScreenScaling())
            {
                this.setDimensionsForScreenScale(currentValue);
            }
        }
    }

    protected void setDimensionsForScreenScale(double scaleFactor)
    {
        int width = (int) Math.ceil((double) this.mc.displayWidth / scaleFactor);
        int height = (int) Math.ceil((double) this.mc.displayHeight / scaleFactor);
        // Only set the screen size if it was originally the same as the window dimensions,
        // ie. the screen was not a smaller (popup?) screen.
        boolean setScreenSize = this.screenWidth == this.width && this.screenHeight == this.height;

        if (this.width != width || this.height != height)
        {
            this.width = width;
            this.height = height;

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

    public void setPosition(int x, int y)
    {
        this.x = x;
        this.y = y;

        this.updateWidgetPositions();
    }

    public void setScreenWidth(int screenWidth)
    {
        this.screenWidth = screenWidth;
    }

    public void setScreenHeight(int screenHeight)
    {
        this.screenHeight = screenHeight;
    }

    public void centerOnScreen()
    {
        int x;
        int y;
        GuiScreen parent = this.getParent();

        if (parent instanceof BaseScreen)
        {
            BaseScreen parentBaseScreen = (BaseScreen) this.getParent();
            x = parentBaseScreen.x + parentBaseScreen.screenWidth / 2;
            y = parentBaseScreen.y + parentBaseScreen.screenHeight / 2;
        }
        else if (parent != null)
        {
            x = parent.width / 2;
            y = parent.height / 2;
        }
        else if (GuiUtils.getCurrentScreen() != null)
        {
            GuiScreen current = GuiUtils.getCurrentScreen();
            x = current.width / 2;
            y = current.height / 2;
        }
        else
        {
            x = GuiUtils.getScaledWindowWidth() / 2;
            y = GuiUtils.getScaledWindowHeight() / 2;
        }

        x -= this.screenWidth / 2;
        y -= this.screenHeight / 2;

        this.setPosition(x, y);
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

    public ScreenContext getContext()
    {
        int mouseX = Mouse.getX() * this.width / this.mc.displayWidth;
        int mouseY = this.height - Mouse.getY() * this.height / this.mc.displayHeight - 1;
        boolean isActiveScreen = GuiUtils.getCurrentScreen() == this;
        int hoveredWidgetId = this.hoveredWidget != null ? this.hoveredWidget.getId() : -1;

        if (this.context == null ||
            this.context.matches(mouseX, mouseY, isActiveScreen, hoveredWidgetId) == false)
        {
            this.context = new ScreenContext(mouseX, mouseY, hoveredWidgetId, isActiveScreen);
        }

        return this.context;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.runTasks();

        if (this.shouldRenderParent && this.getParent() != null)
        {
            this.getParent().drawScreen(mouseX, mouseY, partialTicks);
        }

        // These are after the parent rendering, because the parent
        // can/will also both enable and disable the custom scale,
        // so it needs to be enabled here again in any case after
        // rendering the parent screen.
        if (this.shouldUseCustomScreenScaling())
        {
            RenderUtils.setupScaledScreenRendering(this.customScreenScale);
        }

        ScreenContext ctx = this.getContext();

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

        if (this.shouldUseCustomScreenScaling())
        {
            RenderUtils.setupScaledScreenRendering(RenderUtils.getVanillaScreenScale());
        }
    }

    @Override
    public void handleMouseInput() throws IOException
    {
        int mouseX = Mouse.getEventX() * this.width / this.mc.displayWidth;
        int mouseY = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
        int mouseWheelDelta = Mouse.getEventDWheel();

        boolean isActiveGui = GuiUtils.getCurrentScreen() == this;
        this.updateTopHoveredWidget(mouseX, mouseY, isActiveGui);

        if (mouseX != this.lastMouseX || mouseY != this.lastMouseY)
        {
            this.onMouseMoved(mouseX, mouseY);
            this.lastMouseX = mouseX;
            this.lastMouseY = mouseY;
        }

        if (mouseWheelDelta == 0 || this.onMouseScrolled(mouseX, mouseY, mouseWheelDelta) == false)
        {
            super.handleMouseInput();
        }

        // Update again after the input is handled
        isActiveGui = GuiUtils.getCurrentScreen() == this;
        this.updateTopHoveredWidget(mouseX, mouseY, isActiveGui);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        if (this.shouldUseCustomScreenScaling())
        {
            mouseX = Mouse.getX() * this.width / this.mc.displayWidth;
            mouseY = this.height - Mouse.getY() * this.height / this.mc.displayHeight - 1;
        }

        if (this.onMouseClicked(mouseX, mouseY, mouseButton) == false)
        {
            super.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int mouseButton)
    {
        this.dragging = false;

        if (this.shouldUseCustomScreenScaling())
        {
            mouseX = Mouse.getX() * this.width / this.mc.displayWidth;
            mouseY = this.height - Mouse.getY() * this.height / this.mc.displayHeight - 1;
        }

        if (this.onMouseReleased(mouseX, mouseY, mouseButton) == false)
        {
            super.mouseReleased(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    public void handleKeyboardInput() throws IOException
    {
        if (Keyboard.getEventKeyState() == false &&
            this.onKeyReleased(Keyboard.getEventKey(), 0, 0))
        {
            return;
        }

        super.handleKeyboardInput();
    }

    @Override
    protected void keyTyped(char charIn, int keyCode) throws IOException
    {
        if (keyCode == 0 && charIn >= ' ')
        {
            keyCode = (int) charIn + 256;
        }

        if (this.onKeyTyped(keyCode, 0, 0) == false)
        {
            super.keyTyped(charIn, keyCode);
        }

        if (charIn >= ' ')
        {
            this.onCharTyped(charIn, 0);
        }
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

        if (this.canDragMove)
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
            this.updateWidgetPositions();

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

    public boolean onKeyTyped(int keyCode, int scanCode, int modifiers)
    {
        if (keyCode == Keyboard.KEY_ESCAPE && this.dialogHandler != null)
        {
            this.dialogHandler.closeDialog();
            return true;
        }
        else if (keyCode == Keyboard.KEY_TAB &&
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

        if (keyCode == Keyboard.KEY_ESCAPE)
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

    public void bindTexture(ResourceLocation texture)
    {
        this.mc.getTextureManager().bindTexture(texture);
    }

    public BaseScreen setZ(float z)
    {
        this.zLevel = z;

        for (InteractableWidget widget : this.widgets)
        {
            widget.setZLevelBasedOnParent(z);
        }

        return this;
    }

    public BaseScreen setPopupGuiZLevelBasedOn(@Nullable GuiScreen gui)
    {
        if (gui instanceof BaseScreen)
        {
            this.setZ(((BaseScreen) gui).zLevel + this.getPopupGuiZLevelIncrement());
        }

        return this;
    }

    public <T extends InteractableWidget> T addWidget(T widget)
    {
        this.widgets.add(widget);
        widget.setTaskQueue(this::addTask);
        widget.onWidgetAdded(this.zLevel);
        return widget;
    }

    public <T extends BaseTextFieldWidget> T addTextField(T widget, TextChangeListener listener)
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
        widget.setText(lines);
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

    protected void renderScreenBackground(ScreenContext ctx)
    {
        if (this.renderBorder)
        {
            ShapeRenderUtils.renderOutlinedRectangle(this.x, this.y, this.zLevel,
                                                     this.screenWidth, this.screenHeight,
                                                     this.backgroundColor, this.borderColor);
        }
        else
        {
            ShapeRenderUtils.renderRectangle(this.x, this.y, this.zLevel,
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

            this.textRenderer.renderLine(x, y, this.zLevel + 0.00125f, this.titleColor, true, this.titleText);
        }
    }

    protected void renderCustomContents(ScreenContext ctx)
    {
    }

    protected void renderWidgets(ScreenContext ctx)
    {
        for (InteractableWidget widget : this.widgets)
        {
            widget.renderAt(widget.getX(), widget.getY(), widget.getZ(), ctx);
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

    public static boolean openScreen(@Nullable GuiScreen screen)
    {
        GameUtils.getClient().displayGuiScreen(screen);
        return true;
    }

    public static ActionResult openScreenAction(@Nullable GuiScreen screen)
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
        return openScreen(screen);
    }

    public static void applyCustomScreenScaleChange()
    {
        GuiScreen screen = GuiUtils.getCurrentScreen();

        if (screen instanceof BaseScreen)
        {
            ((BaseScreen) screen).updateCustomScreenScale();
            screen.initGui();
        }
    }

    public static boolean isShiftDown()
    {
        return isShiftKeyDown();
    }

    public static boolean isCtrlDown()
    {
        return isCtrlKeyDown();
    }

    public static boolean isAltDown()
    {
        return isAltKeyDown();
    }

    public void renderDebug(ScreenContext ctx)
    {
        if (ctx.isActiveScreen)
        {
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
