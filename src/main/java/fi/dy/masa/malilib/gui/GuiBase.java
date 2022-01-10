package fi.dy.masa.malilib.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.gui.Message.MessageType;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.gui.interfaces.IMessageConsumer;
import fi.dy.masa.malilib.gui.interfaces.ITextFieldListener;
import fi.dy.masa.malilib.gui.widgets.WidgetBase;
import fi.dy.masa.malilib.gui.widgets.WidgetLabel;
import fi.dy.masa.malilib.gui.wrappers.TextFieldWrapper;
import fi.dy.masa.malilib.interfaces.IStringConsumer;
import fi.dy.masa.malilib.render.MessageRenderer;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.KeyCodes;

public abstract class GuiBase extends Screen implements IMessageConsumer, IStringConsumer
{
    public static final String TXT_AQUA = Formatting.AQUA.toString();
    public static final String TXT_BLACK = Formatting.BLACK.toString();
    public static final String TXT_BLUE = Formatting.BLUE.toString();
    public static final String TXT_GOLD = Formatting.GOLD.toString();
    public static final String TXT_GRAY = Formatting.GRAY.toString();
    public static final String TXT_GREEN = Formatting.GREEN.toString();
    public static final String TXT_RED = Formatting.RED.toString();
    public static final String TXT_WHITE = Formatting.WHITE.toString();
    public static final String TXT_YELLOW = Formatting.YELLOW.toString();

    public static final String TXT_BOLD = Formatting.BOLD.toString();
    public static final String TXT_ITALIC = Formatting.ITALIC.toString();
    public static final String TXT_RST = Formatting.RESET.toString();
    public static final String TXT_STRIKETHROUGH = Formatting.STRIKETHROUGH.toString();
    public static final String TXT_UNDERLINE = Formatting.UNDERLINE.toString();

    public static final String TXT_DARK_AQUA = Formatting.DARK_AQUA.toString();
    public static final String TXT_DARK_BLUE = Formatting.DARK_BLUE.toString();
    public static final String TXT_DARK_GRAY = Formatting.DARK_GRAY.toString();
    public static final String TXT_DARK_GREEN = Formatting.DARK_GREEN.toString();
    public static final String TXT_DARK_PURPLE = Formatting.DARK_PURPLE.toString();
    public static final String TXT_DARK_RED = Formatting.DARK_RED.toString();

    public static final String TXT_LIGHT_PURPLE = Formatting.LIGHT_PURPLE.toString();

    protected static final String BUTTON_LABEL_ADD = TXT_DARK_GREEN + "+" + TXT_RST;
    protected static final String BUTTON_LABEL_REMOVE = TXT_DARK_RED + "-" + TXT_RST;

    public static final int COLOR_WHITE          = 0xFFFFFFFF;
    public static final int TOOLTIP_BACKGROUND   = 0xB0000000;
    public static final int COLOR_HORIZONTAL_BAR = 0xFF999999;
    protected static final int LEFT         = 20;
    protected static final int TOP          = 10;
    public final MinecraftClient mc = MinecraftClient.getInstance();
    public final TextRenderer textRenderer = this.mc.textRenderer;
    public final int fontHeight = this.textRenderer.fontHeight;
    private final List<ButtonBase> buttons = new ArrayList<>();
    private final List<WidgetBase> widgets = new ArrayList<>();
    private final List<TextFieldWrapper<? extends GuiTextFieldGeneric>> textFields = new ArrayList<>();
    private final MessageRenderer messageRenderer = new MessageRenderer(0xDD000000, COLOR_HORIZONTAL_BAR);
    protected WidgetBase hoveredWidget = null;
    protected String title = "";
    protected boolean useTitleHierarchy = true;
    private int keyInputCount;
    private double mouseWheelDeltaSum;
    @Nullable
    private Screen parent;

    protected GuiBase()
    {
        super(new LiteralText(""));
    }

    public GuiBase setParent(@Nullable Screen parent)
    {
        // Don't allow nesting the GUI with itself...
        if (parent == null || parent.getClass() != this.getClass())
        {
            this.parent = parent;
        }

        return this;
    }

    @Nullable
    public Screen getParent()
    {
        return this.parent;
    }

    public String getTitleString()
    {
        return (this.useTitleHierarchy && this.parent instanceof GuiBase) ? (((GuiBase) this.parent).getTitleString() + " => " + this.title) : this.title;
    }

    @Override
    public Text getTitle()
    {
        return new LiteralText(this.getTitleString());
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    @Override
    public void removed()
    {
        this.mc.keyboard.setRepeatEvents(false);
    }

    @Override
    public boolean shouldPause()
    {
        return false;
    }

    @Override
    public void resize(MinecraftClient mc, int width, int height)
    {
        if (this.getParent() != null)
        {
            this.getParent().resize(mc, width, height);
        }

        super.resize(mc, width, height);
    }

    @Override
    public void init()
    {
        super.init();

        this.initGui();
    }

    public void initGui()
    {
        this.clearElements();
    }

    protected void closeGui(boolean showParent)
    {
        if (showParent)
        {
            this.mc.setScreen(this.parent);
        }
        else
        {
            this.onClose();
        }
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        this.drawScreenBackground(mouseX, mouseY);
        this.drawTitle(matrixStack, mouseX, mouseY, partialTicks);

        // Draw base widgets
        this.drawWidgets(mouseX, mouseY, matrixStack);
        this.drawTextFields(mouseX, mouseY, matrixStack);
        this.drawButtons(mouseX, mouseY, partialTicks, matrixStack);

        this.drawContents(matrixStack, mouseX, mouseY, partialTicks);

        this.drawButtonHoverTexts(mouseX, mouseY, partialTicks, matrixStack);
        this.drawHoveredWidget(mouseX, mouseY, matrixStack);
        this.drawGuiMessages(matrixStack);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount)
    {
        if (this.mouseWheelDeltaSum != 0.0 && Math.signum(amount) != Math.signum(this.mouseWheelDeltaSum))
        {
            this.mouseWheelDeltaSum = 0.0;
        }

        this.mouseWheelDeltaSum += amount;
        amount = (int) this.mouseWheelDeltaSum;

        if (amount != 0.0)
        {
            this.mouseWheelDeltaSum -= amount;

            if (this.onMouseScrolled((int) mouseX, (int) mouseY, amount))
            {
                return true;
            }
        }

        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton)
    {
        if (this.onMouseClicked((int) mouseX, (int) mouseY, mouseButton) == false)
        {
            return super.mouseClicked(mouseX, mouseY, mouseButton);
        }

        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int mouseButton)
    {
        if (this.onMouseReleased((int) mouseX, (int) mouseY, mouseButton) == false)
        {
            return super.mouseReleased(mouseX, mouseY, mouseButton);
        }

        return false;
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
    public boolean charTyped(char charIn, int modifiers)
    {
        // This is an ugly fix for the issue that the key press from the hotkey that
        // opens a GUI would then also get into any text fields or search bars, as the
        // charTyped() event always fires after the keyPressed() event in any case >_>
        if (this.keyInputCount <= 0)
        {
            return true;
        }

        if (this.onCharTyped(charIn, modifiers))
        {
            return true;
        }

        return super.charTyped(charIn, modifiers);
    }

    public boolean onMouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        for (ButtonBase button : this.buttons)
        {
            if (button.onMouseClicked(mouseX, mouseY, mouseButton))
            {
                // Don't call super if the button press got handled
                return true;
            }
        }

        boolean handled = false;

        for (TextFieldWrapper<?> entry : this.textFields)
        {
            if (entry.mouseClicked(mouseX, mouseY, mouseButton))
            {
                // Don't call super if the button press got handled
                handled = true;
            }
        }

        if (handled == false)
        {
            for (WidgetBase widget : this.widgets)
            {
                if (widget.isMouseOver(mouseX, mouseY) && widget.onMouseClicked(mouseX, mouseY, mouseButton))
                {
                    // Don't call super if the button press got handled
                    handled = true;
                    break;
                }
            }
        }

        return handled;
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

    public boolean onKeyTyped(int keyCode, int scanCode, int modifiers)
    {
        boolean handled = false;
        int selected = -1;

        for (int i = 0; i < this.textFields.size(); ++i)
        {
            TextFieldWrapper<?> entry = this.textFields.get(i);

            if (entry.isFocused())
            {
                if (keyCode == KeyCodes.KEY_TAB)
                {
                    entry.setFocused(false);
                    selected = i;
                }
                else
                {
                    entry.onKeyTyped(keyCode, scanCode, modifiers);
                }

                handled = keyCode != KeyCodes.KEY_ESCAPE;
                break;
            }
        }

        if (handled == false)
        {
            for (WidgetBase widget : this.widgets)
            {
                if (widget.onKeyTyped(keyCode, scanCode, modifiers))
                {
                    // Don't call super if the button press got handled
                    handled = true;
                    break;
                }
            }
        }

        if (handled == false)
        {
            if (keyCode == KeyCodes.KEY_ESCAPE)
            {
                this.closeGui(isShiftDown() == false);

                return true;
            }
        }

        if (selected >= 0)
        {
            if (isShiftDown())
            {
                selected = selected > 0 ? selected - 1 : this.textFields.size() - 1;
            }
            else
            {
                selected = (selected + 1) % this.textFields.size();
            }

            this.textFields.get(selected).setFocused(true);
        }

        return handled;
    }

    public boolean onCharTyped(char charIn, int modifiers)
    {
        boolean handled = false;

        for (TextFieldWrapper<?> entry : this.textFields)
        {
            if (entry.onCharTyped(charIn, modifiers))
            {
                handled = true;
                break;
            }
        }

        if (handled == false)
        {
            for (WidgetBase widget : this.widgets)
            {
                if (widget.onCharTyped(charIn, modifiers))
                {
                    // Don't call super if the button press got handled
                    handled = true;
                    break;
                }
            }
        }

        return handled;
    }

    @Override
    public void setString(String string)
    {
        this.messageRenderer.addMessage(3000, string);
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

    protected void drawGuiMessages(MatrixStack matrixStack)
    {
        this.messageRenderer.drawMessages(this.width / 2, this.height / 2, matrixStack);
    }

    public void bindTexture(Identifier texture)
    {
        fi.dy.masa.malilib.render.RenderUtils.bindTexture(texture);
    }

    public <T extends ButtonBase> T addButton(T button, IButtonActionListener listener)
    {
        button.setActionListener(listener);
        this.buttons.add(button);
        return button;
    }

    public <T extends GuiTextFieldGeneric> TextFieldWrapper<T> addTextField(T textField, @Nullable ITextFieldListener<T> listener)
    {
        TextFieldWrapper<T> wrapper = new TextFieldWrapper<>(textField, listener);
        this.textFields.add(wrapper);
        return wrapper;
    }

    public <T extends WidgetBase> T addWidget(T widget)
    {
        this.widgets.add(widget);
        return widget;
    }

    public WidgetLabel addLabel(int x, int y, int width, int height, int textColor, String... lines)
    {
        return this.addLabel(x, y, width, height, textColor, Arrays.asList(lines));
    }

    public WidgetLabel addLabel(int x, int y, int width, int height, int textColor, List<String> lines)
    {
        if (lines.size() > 0)
        {
            if (width == -1)
            {
                for (String line : lines)
                {
                    width = Math.max(width, this.getStringWidth(line));
                }
            }
        }

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
        this.clearTextFields();
    }

    protected void clearWidgets()
    {
        this.widgets.clear();
    }

    protected void clearButtons()
    {
        this.buttons.clear();
    }

    protected void clearTextFields()
    {
        this.textFields.clear();
    }

    protected void drawScreenBackground(int mouseX, int mouseY)
    {
        // Draw the dark background
        RenderUtils.drawRect(0, 0, this.width, this.height, TOOLTIP_BACKGROUND);
    }

    protected void drawTitle(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        this.drawString(matrixStack, this.getTitleString(), LEFT, TOP, COLOR_WHITE);
    }

    protected void drawContents(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
    }

    protected void drawButtons(int mouseX, int mouseY, float partialTicks, MatrixStack matrixStack)
    {
        for (ButtonBase button : this.buttons)
        {
            button.render(mouseX, mouseY, button.isMouseOver(), matrixStack);
        }
    }

    protected void drawTextFields(int mouseX, int mouseY, MatrixStack matrixStack)
    {
        for (TextFieldWrapper<?> entry : this.textFields)
        {
            entry.draw(mouseX, mouseY, matrixStack);
        }
    }

    protected void drawWidgets(int mouseX, int mouseY, MatrixStack matrixStack)
    {
        this.hoveredWidget = null;

        if (this.widgets.isEmpty() == false)
        {
            for (WidgetBase widget : this.widgets)
            {
                widget.render(mouseX, mouseY, false, matrixStack);

                if (widget.isMouseOver(mouseX, mouseY))
                {
                    this.hoveredWidget = widget;
                }
            }
        }
    }

    protected void drawButtonHoverTexts(int mouseX, int mouseY, float partialTicks, MatrixStack matrixStack)
    {
        for (ButtonBase button : this.buttons)
        {
            if (button.hasHoverText() && button.isMouseOver())
            {
                RenderUtils.drawHoverText(mouseX, mouseY, button.getHoverStrings(), matrixStack);
            }
        }

        RenderUtils.disableDiffuseLighting();
    }

    protected void drawHoveredWidget(int mouseX, int mouseY, MatrixStack matrixStack)
    {
        if (this.hoveredWidget != null)
        {
            this.hoveredWidget.postRenderHovered(mouseX, mouseY, false, matrixStack);
            RenderUtils.disableDiffuseLighting();
        }
    }

    public static boolean isMouseOver(int mouseX, int mouseY, int x, int y, int width, int height)
    {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

    public int getStringWidth(String text)
    {
        return this.textRenderer.getWidth(text);
    }

    public void drawString(MatrixStack matrixStack, String text, int x, int y, int color)
    {
        this.textRenderer.draw(matrixStack, text, x, y, color);
    }

    public void drawStringWithShadow(MatrixStack matrixStack, String text, int x, int y, int color)
    {
        this.textRenderer.drawWithShadow(matrixStack, text, x, y, color);
    }

    public int getMaxPrettyNameLength(List<? extends IConfigBase> configs)
    {
        int width = 0;

        for (IConfigBase config : configs)
        {
            width = Math.max(width, this.getStringWidth(config.getConfigGuiDisplayName()));
        }

        return width;
    }

    public static void openGui(Screen gui)
    {
        MinecraftClient.getInstance().setScreen(gui);
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
}
