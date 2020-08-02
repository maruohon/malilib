package fi.dy.masa.malilib.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import fi.dy.masa.malilib.MaLiLibConfigs;
import fi.dy.masa.malilib.config.option.ConfigOption;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.gui.interfaces.ITextFieldListener;
import fi.dy.masa.malilib.gui.util.GuiUtils;
import fi.dy.masa.malilib.gui.widget.WidgetBase;
import fi.dy.masa.malilib.gui.widget.WidgetLabel;
import fi.dy.masa.malilib.gui.widget.WidgetTextFieldBase;
import fi.dy.masa.malilib.message.IMessageConsumer;
import fi.dy.masa.malilib.message.MessageType;
import fi.dy.masa.malilib.render.MessageRenderer;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.consumer.IStringConsumer;

public abstract class GuiBase extends GuiScreen implements IMessageConsumer, IStringConsumer
{
    public static final String TXT_AQUA = TextFormatting.AQUA.toString();
    public static final String TXT_BLACK = TextFormatting.BLACK.toString();
    public static final String TXT_BLUE = TextFormatting.BLUE.toString();
    public static final String TXT_GOLD = TextFormatting.GOLD.toString();
    public static final String TXT_GRAY = TextFormatting.GRAY.toString();
    public static final String TXT_GREEN = TextFormatting.GREEN.toString();
    public static final String TXT_RED = TextFormatting.RED.toString();
    public static final String TXT_WHITE = TextFormatting.WHITE.toString();
    public static final String TXT_YELLOW = TextFormatting.YELLOW.toString();

    public static final String TXT_BOLD = TextFormatting.BOLD.toString();
    public static final String TXT_ITALIC = TextFormatting.ITALIC.toString();
    public static final String TXT_RST = TextFormatting.RESET.toString();
    public static final String TXT_STRIKETHROUGH = TextFormatting.STRIKETHROUGH.toString();
    public static final String TXT_UNDERLINE = TextFormatting.UNDERLINE.toString();

    public static final String TXT_DARK_AQUA = TextFormatting.DARK_AQUA.toString();
    public static final String TXT_DARK_BLUE = TextFormatting.DARK_BLUE.toString();
    public static final String TXT_DARK_GRAY = TextFormatting.DARK_GRAY.toString();
    public static final String TXT_DARK_GREEN = TextFormatting.DARK_GREEN.toString();
    public static final String TXT_DARK_PURPLE = TextFormatting.DARK_PURPLE.toString();
    public static final String TXT_DARK_RED = TextFormatting.DARK_RED.toString();

    public static final String TXT_LIGHT_PURPLE = TextFormatting.LIGHT_PURPLE.toString();

    protected static final String BUTTON_LABEL_ADD = TXT_DARK_GREEN + "+" + TXT_RST;
    protected static final String BUTTON_LABEL_REMOVE = TXT_DARK_RED + "-" + TXT_RST;

    public static final int COLOR_WHITE          = 0xFFFFFFFF;
    public static final int TOOLTIP_BACKGROUND   = 0xB0000000;
    public static final int COLOR_HORIZONTAL_BAR = 0xFF999999;
    protected static final int LEFT         = 10;
    protected static final int TOP          = 6;
    public final Minecraft mc = Minecraft.getMinecraft();
    public final FontRenderer textRenderer = this.mc.fontRenderer;
    public final int fontHeight = this.textRenderer.FONT_HEIGHT;
    private final List<ButtonBase> buttons = new ArrayList<>();
    private final List<WidgetBase> widgets = new ArrayList<>();
    private final MessageRenderer messageRenderer;
    protected WidgetBase hoveredWidget = null;
    protected String title = "";
    protected boolean useTitleHierarchy = true;
    @Nullable
    private GuiScreen parent;

    public GuiBase()
    {
        this.messageRenderer = new MessageRenderer();
        this.messageRenderer.setBackgroundColor(0xDD000000).setBorderColor(COLOR_HORIZONTAL_BAR);
        this.messageRenderer.setCentered(true, true);
        this.messageRenderer.setZLevel(100);
    }

    public GuiBase setParent(@Nullable GuiScreen parent)
    {
        // Don't allow nesting the GUI with itself...
        if (parent == null || parent.getClass() != this.getClass())
        {
            this.parent = parent;
        }

        return this;
    }

    @Nullable
    public GuiScreen getParent()
    {
        return this.parent;
    }

    public String getTitle()
    {
        return (this.useTitleHierarchy && this.parent instanceof GuiBase) ? (((GuiBase) this.parent).getTitle() + " => " + this.title) : this.title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    protected int getPopupGuiZLevelIncrement()
    {
        return 200;
    }

    @Override
    public void onGuiClosed()
    {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    public boolean doesGuiPauseGame()
    {
        return false;
    }

    @Override
    public void initGui()
    {
        super.initGui();

        this.clearElements();
    }

    protected void closeGui(boolean showParent)
    {
        if (showParent)
        {
            this.mc.displayGuiScreen(this.parent);
        }
        else
        {
            this.mc.displayGuiScreen(null);
        }
    }

    protected WidgetBase getTopHoveredWidget(int mouseX, int mouseY, @Nullable WidgetBase highestFoundWidget)
    {
        highestFoundWidget = WidgetBase.getTopHoveredWidgetFromList(this.buttons, mouseX, mouseY, highestFoundWidget);
        highestFoundWidget = WidgetBase.getTopHoveredWidgetFromList(this.widgets, mouseX, mouseY, highestFoundWidget);
        return highestFoundWidget;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        boolean isActiveGui = GuiUtils.getCurrentScreen() == this;

        this.hoveredWidget = isActiveGui ? this.getTopHoveredWidget(mouseX, mouseY, null) : null;

        int hoveredWidgetId = isActiveGui && this.hoveredWidget != null ? this.hoveredWidget.getId() : -1;

        this.drawScreenBackground(mouseX, mouseY);
        this.drawTitle(mouseX, mouseY, partialTicks);

        // Draw base widgets
        this.drawWidgets(mouseX, mouseY, isActiveGui, hoveredWidgetId);
        //super.drawScreen(mouseX, mouseY, partialTicks);

        this.drawContents(mouseX, mouseY, partialTicks);

        this.drawHoveredWidget(mouseX, mouseY, isActiveGui, hoveredWidgetId);
        this.drawGuiMessages();

        if (MaLiLibConfigs.Debug.GUI_DEBUG.getBooleanValue() && MaLiLibConfigs.Debug.GUI_DEBUG_KEY.isHeld())
        {
            this.renderDebug(mouseX, mouseY);
        }

        WidgetBase.renderDebugTextAndClear();
    }

    @Override
    public void handleMouseInput() throws IOException
    {
        int mouseX = Mouse.getEventX() * this.width / this.mc.displayWidth;
        int mouseY = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
        int mouseWheelDelta = Mouse.getEventDWheel();

        if (mouseWheelDelta == 0 || this.onMouseScrolled(mouseX, mouseY, mouseWheelDelta) == false)
        {
            super.handleMouseInput();
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        if (this.onMouseClicked(mouseX, mouseY, mouseButton) == false)
        {
            super.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int mouseButton)
    {
        if (this.onMouseReleased(mouseX, mouseY, mouseButton) == false)
        {
            super.mouseReleased(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        if (this.onKeyTyped(typedChar, keyCode) == false)
        {
            super.keyTyped(typedChar, keyCode);
        }
    }

    public boolean onMouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        List<WidgetTextFieldBase> textFields = this.getAllTextFields();
        WidgetBase clickedWidget = null;

        for (WidgetBase widget : this.widgets)
        {
            if (widget.onMouseClicked(mouseX, mouseY, mouseButton))
            {
                clickedWidget = widget;
                break;
            }
        }

        // Clear the focus from all but the text field that was just clicked
        for (WidgetTextFieldBase tf : textFields)
        {
            if (tf != clickedWidget)
            {
                tf.setFocused(false);
            }
        }

        // A widget handled the click, abort
        if (clickedWidget != null)
        {
            return true;
        }

        for (ButtonBase button : this.buttons)
        {
            if (button.onMouseClicked(mouseX, mouseY, mouseButton))
            {
                // Don't call super if the button press got handled
                return true;
            }
        }

        return false;
    }

    public boolean onMouseReleased(int mouseX, int mouseY, int mouseButton)
    {
        for (WidgetBase widget : this.widgets)
        {
            widget.onMouseReleased(mouseX, mouseY, mouseButton);
        }

        return false;
    }

    public boolean onMouseScrolled(int mouseX, int mouseY, double mouseWheelDelta)
    {
        for (ButtonBase button : this.buttons)
        {
            if (button.onMouseScrolled(mouseX, mouseY, mouseWheelDelta))
            {
                // Don't call super if the button press got handled
                return true;
            }
        }

        for (WidgetBase widget : this.widgets)
        {
            if (widget.onMouseScrolled(mouseX, mouseY, mouseWheelDelta))
            {
                // Don't call super if the action got handled
                return true;
            }
        }

        return false;
    }

    public boolean onKeyTyped(char typedChar, int keyCode)
    {
        if (keyCode == Keyboard.KEY_TAB && changeTextFieldFocus(this.getAllTextFields(), isShiftDown()))
        {
            return true;
        }

        if (this.widgets.isEmpty() == false)
        {
            for (WidgetBase widget : this.widgets)
            {
                if (widget.onKeyTyped(typedChar, keyCode))
                {
                    // Don't call super if the button press got handled
                    return true;
                }
            }
        }

        if (keyCode == Keyboard.KEY_ESCAPE)
        {
            this.closeGui(isShiftDown() == false);
            return true;
        }

        return false;
    }

    public static boolean changeTextFieldFocus(List<WidgetTextFieldBase> textFields, boolean reverse)
    {
        final int size = textFields.size();

        if (size > 1)
        {
            int currentIndex = -1;

            for (int i = 0; i < size; ++i)
            {
                WidgetTextFieldBase textField = textFields.get(i);

                if (textField.isFocused())
                {
                    currentIndex = i;
                    textField.setFocused(false);
                    break;
                }
            }

            if (currentIndex != -1)
            {
                int newIndex = currentIndex + (reverse ? -1 : 1);

                if (newIndex >= size)
                {
                    newIndex = 0;
                }
                else if (newIndex < 0)
                {
                    newIndex = size - 1;
                }

                textFields.get(newIndex).setFocused(true);

                return true;
            }
        }

        return false;
    }

    protected List<WidgetTextFieldBase> getAllTextFields()
    {
        List<WidgetTextFieldBase> textFields = new ArrayList<>();

        if (this.widgets.isEmpty() == false)
        {
            for (WidgetBase widget : this.widgets)
            {
                textFields.addAll(widget.getAllTextFields());
            }
        }

        return textFields;
    }

    @Override
    public boolean consumeString(String string)
    {
        this.messageRenderer.addMessage(3000, string);
        return true;
    }

    @Override
    public void addMessage(MessageType type, String messageKey, Object... args)
    {
        this.addGuiMessage(type, 5000, messageKey, args);
    }

    @Override
    public void addMessage(MessageType type, int lifeTime, String messageKey, Object... args)
    {
        this.addGuiMessage(type, lifeTime, messageKey, args);
    }

    public void addGuiMessage(MessageType type, int displayTimeMs, String messageKey, Object... args)
    {
        this.messageRenderer.addMessage(type, displayTimeMs, messageKey, args);
    }

    public void setNextMessageType(MessageType type)
    {
        this.messageRenderer.setNextMessageType(type);
    }

    protected void drawGuiMessages()
    {
        this.messageRenderer.drawMessages(this.width / 2, this.height / 2);
    }

    public void bindTexture(ResourceLocation texture)
    {
        this.mc.getTextureManager().bindTexture(texture);
    }

    public GuiBase setZLevel(float zLevel)
    {
        this.zLevel = zLevel;
        int parentZLevel = (int) this.zLevel;

        for (WidgetBase widget : this.buttons)
        {
            widget.setZLevelBasedOnParent(parentZLevel);
        }

        for (WidgetBase widget : this.widgets)
        {
            widget.setZLevelBasedOnParent(parentZLevel);
        }

        this.messageRenderer.setZLevel((int) this.zLevel + 100);

        return this;
    }

    public GuiBase setPopupGuiZLevelBasedOn(@Nullable GuiScreen gui)
    {
        if (gui instanceof GuiBase)
        {
            this.setZLevel(((GuiBase) gui).zLevel + this.getPopupGuiZLevelIncrement());
        }

        return this;
    }

    public <T extends ButtonBase> T addButton(T button, IButtonActionListener listener)
    {
        button.setActionListener(listener);
        this.buttons.add(button);
        button.onWidgetAdded((int) this.zLevel);
        return button;
    }

    public <T extends WidgetBase> T addWidget(T widget)
    {
        this.widgets.add(widget);
        widget.onWidgetAdded((int) this.zLevel);
        return widget;
    }

    public <T extends WidgetTextFieldBase> T addTextField(T widget, ITextFieldListener listener)
    {
        widget.setListener(listener);
        this.addWidget(widget);
        return widget;
    }

    public WidgetLabel addLabel(int x, int y, int textColor, String... lines)
    {
        return this.addLabel(x, y, -1, -1, textColor, Arrays.asList(lines));
    }

    public WidgetLabel addLabel(int x, int y, int textColor, List<String> lines)
    {
        return this.addLabel(x, y, -1, -1, textColor, lines);
    }

    public WidgetLabel addLabel(int x, int y, int width, int height, int textColor, String... lines)
    {
        return this.addLabel(x, y, width, height, textColor, Arrays.asList(lines));
    }

    public WidgetLabel addLabel(int x, int y, int width, int height, int textColor, List<String> lines)
    {
        return this.addWidget(new WidgetLabel(x, y, width, height, textColor, lines));
    }

    protected boolean removeWidget(WidgetBase widget)
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
        this.clearButtons();
    }

    protected void clearWidgets()
    {
        this.widgets.clear();
    }

    protected void clearButtons()
    {
        this.buttons.clear();
    }

    protected void drawScreenBackground(int mouseX, int mouseY)
    {
        // Draw the dark background
        RenderUtils.drawRect(0, 0, this.width, this.height, TOOLTIP_BACKGROUND, this.zLevel);
    }

    protected void drawTitle(int mouseX, int mouseY, float partialTicks)
    {
        this.drawStringWithShadow(this.getTitle(), LEFT, TOP, COLOR_WHITE);
    }

    protected void drawContents(int mouseX, int mouseY, float partialTicks)
    {
    }

    protected void drawWidgets(int mouseX, int mouseY, boolean isActiveGui, int hoveredWidgetId)
    {
        if (this.widgets.isEmpty() == false)
        {
            for (WidgetBase widget : this.widgets)
            {
                widget.render(mouseX, mouseY, isActiveGui, hoveredWidgetId);
            }
        }

        if (this.buttons.isEmpty() == false)
        {
            for (ButtonBase button : this.buttons)
            {
                button.render(mouseX, mouseY, isActiveGui, hoveredWidgetId);
            }
        }
    }

    protected void drawHoveredWidget(int mouseX, int mouseY, boolean isActiveGui, int hoveredWidgetId)
    {
        if (this.hoveredWidget != null)
        {
            this.hoveredWidget.postRenderHovered(mouseX, mouseY, isActiveGui, hoveredWidgetId);
            RenderUtils.disableItemLighting();
        }
    }

    public int getStringWidth(String text)
    {
        return this.textRenderer.getStringWidth(text);
    }

    public void drawString(String text, int x, int y, int color)
    {
        GlStateManager.pushMatrix();
        GlStateManager.translate(0f, 0f, this.zLevel + 0.1f);

        this.textRenderer.drawString(text, x, y, color);

        GlStateManager.popMatrix();
    }

    public void drawStringWithShadow(String text, int x, int y, int color)
    {
        GlStateManager.pushMatrix();
        GlStateManager.translate(0f, 0f, this.zLevel + 0.1f);

        this.textRenderer.drawStringWithShadow(text, x, y, color);

        GlStateManager.popMatrix();
    }

    public int getMaxPrettyNameLength(List<? extends ConfigOption> configs)
    {
        int width = 0;

        for (ConfigOption config : configs)
        {
            width = Math.max(width, this.getStringWidth(config.getPrettyName()));
        }

        return width;
    }

    public static void openGui(@Nullable GuiScreen gui)
    {
        Minecraft.getMinecraft().displayGuiScreen(gui);
    }

    /**
     * Opens a popup GUI, which is meant to open on top of another GUI.
     * This will set the Z level on that GUI based on the current GUI
     * @param gui
     */
    public static void openPopupGui(GuiBase gui)
    {
        gui.setPopupGuiZLevelBasedOn(GuiUtils.getCurrentScreen());
        Minecraft.getMinecraft().displayGuiScreen(gui);
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

    public void renderDebug(int mouseX, int mouseY)
    {
        boolean renderAll = MaLiLibConfigs.Debug.GUI_DEBUG_ALL.getBooleanValue();
        boolean infoAlways = MaLiLibConfigs.Debug.GUI_DEBUG_INFO_ALWAYS.getBooleanValue();

        renderWidgetDebug(this.buttons, mouseX, mouseY, renderAll, infoAlways);
        renderWidgetDebug(this.widgets, mouseX, mouseY, renderAll, infoAlways);
    }

    public static void renderWidgetDebug(List<? extends WidgetBase> widgets, int mouseX, int mouseY, boolean renderAll, boolean infoAlways)
    {
        if (widgets.isEmpty() == false)
        {
            for (WidgetBase widget : widgets)
            {
                boolean hovered = widget.isMouseOver(mouseX, mouseY);
                widget.renderDebug(mouseX, mouseY, hovered, renderAll, infoAlways);
            }
        }
    }
}
