package fi.dy.masa.malilib.gui;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.gui.Message.MessageType;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.gui.interfaces.IGuiIcon;
import fi.dy.masa.malilib.gui.interfaces.IMessageConsumer;
import fi.dy.masa.malilib.gui.interfaces.ITextFieldListener;
import fi.dy.masa.malilib.gui.widgets.WidgetBase;
import fi.dy.masa.malilib.gui.widgets.WidgetCheckBox;
import fi.dy.masa.malilib.gui.widgets.WidgetLabel;
import fi.dy.masa.malilib.gui.wrappers.ButtonWrapper;
import fi.dy.masa.malilib.gui.wrappers.TextFieldWrapper;
import fi.dy.masa.malilib.interfaces.IStringConsumer;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.KeyCodes;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.util.Window;
import net.minecraft.text.TextFormat;
import net.minecraft.util.Identifier;

public abstract class GuiBase extends Gui implements IMessageConsumer, IStringConsumer
{
    public static final String TXT_BLUE = TextFormat.BLUE.toString();
    public static final String TXT_GRAY = TextFormat.GRAY.toString();
    public static final String TXT_GREEN = TextFormat.GREEN.toString();
    public static final String TXT_GOLD = TextFormat.GOLD.toString();
    public static final String TXT_RED = TextFormat.RED.toString();
    public static final String TXT_WHITE = TextFormat.WHITE.toString();
    public static final String TXT_YELLOW = TextFormat.YELLOW.toString();

    public static final String TXT_BOLD = TextFormat.BOLD.toString();
    public static final String TXT_RST = TextFormat.RESET.toString();
    public static final String TXT_UNDERLINE = TextFormat.UNDERLINE.toString();

    public static final String TXT_DARK_GREEN = TextFormat.DARK_GREEN.toString();
    public static final String TXT_DARK_RED = TextFormat.DARK_RED.toString();

    protected static final String BUTTON_LABEL_ADD = TXT_DARK_GREEN + "+" + TXT_RST;
    protected static final String BUTTON_LABEL_REMOVE = TXT_DARK_RED + "-" + TXT_RST;

    public static final int COLOR_WHITE          = 0xFFFFFFFF;
    public static final int TOOLTIP_BACKGROUND   = 0xB0000000;
    public static final int COLOR_HORIZONTAL_BAR = 0xFF999999;
    protected static final int LEFT         = 20;
    protected static final int TOP          = 10;
    private final List<ButtonWrapper<? extends ButtonGeneric>> buttons = new ArrayList<>();
    private final List<TextFieldWrapper<? extends TextFieldWidget>> textFields = new ArrayList<>();
    private final List<WidgetBase> widgets = new ArrayList<>();
    private final List<Message> messages = new ArrayList<>();
    protected WidgetBase hoveredWidget = null;
    private MessageType nextMessageType = MessageType.INFO;
    protected String title = "";
    protected double mouseX;
    protected double mouseY;
    protected boolean useTitleHierarchy = true;
    @Nullable
    private Gui parent;

    public GuiBase setParent(@Nullable Gui parent)
    {
        // Don't allow nesting the GUI with itself...
        if (parent == null || parent.getClass() != this.getClass())
        {
            this.parent = parent;
        }

        return this;
    }

    @Nullable
    public Gui getParent()
    {
        return this.parent;
    }

    public String getTitle()
    {
        return (this.useTitleHierarchy && this.parent instanceof GuiBase) ? (((GuiBase) this.parent).getTitle() + " => " + this.title) : this.title;
    }

    @Override
    public void onClosed()
    {
        MinecraftClient.getInstance().keyboard.enableRepeatEvents(false);
    }

    @Override
    public boolean isPauseScreen()
    {
        return false;
    }

    @Override
    public void onInitialized()
    {
        super.onInitialized();

        this.clearElements();
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTicks)
    {
        this.drawScreenBackground(mouseX, mouseY);
        this.drawTitle(mouseX, mouseY, partialTicks);

        // Draw base widgets
        this.drawWidgets(mouseX, mouseY);
        this.drawTextFields(mouseX, mouseY);
        this.drawButtons(mouseX, mouseY, partialTicks);
        //super.drawScreen(mouseX, mouseY, partialTicks);

        this.drawContents(mouseX, mouseY, partialTicks);

        this.drawButtonHoverTexts(mouseX, mouseY, partialTicks);
        this.drawHoveredWidget(mouseX, mouseY);
        this.drawGuiMessages();
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY)
    {
        this.mouseX = mouseX;
        this.mouseY = mouseY;

        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(double amount)
    {
        Window window = this.client.window;
        int mouseX = (int) (this.client.mouse.getX() * (double) window.getScaledWidth() / (double) window.getWidth());
        int mouseY = (int) (this.client.mouse.getY() * (double) window.getScaledHeight() / (double) window.getHeight());

        if (amount == 0 || this.onMouseScrolled(mouseX, mouseY, (int) amount))
        {
            return super.mouseScrolled(amount);
        }

        return false;
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
        if (this.onKeyTyped(keyCode, scanCode, modifiers))
        {
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char charIn, int modifiers)
    {
        if (this.onCharTyped(charIn, modifiers))
        {
            return true;
        }

        return super.charTyped(charIn, modifiers);
    }

    public boolean onMouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        for (ButtonWrapper<?> entry : this.buttons)
        {
            if (entry.mousePressed(this.client, mouseX, mouseY, mouseButton))
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
            else
            {
                entry.getTextField().setFocused(false);
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
                }
            }
        }

        return handled;
    }

    public boolean onMouseReleased(int mouseX, int mouseY, int mouseButton)
    {
        return false;
    }

    public boolean onMouseScrolled(int mouseX, int mouseY, int mouseWheelDelta)
    {
        return false;
    }

    public boolean onKeyTyped(int keyCode, int scanCode, int modifiers)
    {
        if (keyCode == KeyCodes.KEY_ESCAPE)
        {
            if (Gui.isShiftPressed())
            {
                this.close();
            }
            else
            {
                this.client.openGui(this.parent);
            }

            return true;
        }

        boolean handled = false;
        int selected = -1;
        int i = 0;

        for (TextFieldWrapper<?> entry : this.textFields)
        {
            if (keyCode == KeyCodes.KEY_TAB && entry.getTextField().isFocused())
            {
                entry.getTextField().setFocused(false);
                selected = i;
                handled = true;
            }
            else if (entry.onKeyTyped(keyCode, scanCode, modifiers))
            {
                handled = true;
            }

            i++;
        }

        if (selected >= 0)
        {
            if (Gui.isShiftPressed())
            {
                selected = selected > 0 ? selected - 1 : this.textFields.size() - 1;
            }
            else
            {
                selected = (selected + 1) % this.textFields.size();
            }

            this.textFields.get(selected).getTextField().setFocused(true);
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
            }
        }

        return handled;
    }

    @Override
    public void setString(String string)
    {
        this.addGuiMessage(this.nextMessageType, string, 3000);
    }

    @Override
    public void addMessage(MessageType type, String messageKey)
    {
        this.addMessage(type, messageKey, new Object[0]);
    }

    @Override
    public void addMessage(MessageType type, String messageKey, Object... args)
    {
        this.addGuiMessage(type, messageKey, 5000, args);
    }

    public void addGuiMessage(MessageType type, String messageKey, int displayTimeMs, Object... args)
    {
        this.messages.add(new Message(type, displayTimeMs, 380, messageKey, args));
    }

    public void setNextMessageType(MessageType type)
    {
        this.nextMessageType = type;
    }

    protected void drawGuiMessages()
    {
        if (this.messages.isEmpty() == false)
        {
            int boxWidth = 400;
            int boxHeight = this.getMessagesHeight() + 20;
            int x = this.width / 2 - boxWidth / 2;
            int y = this.height / 2 - boxHeight / 2;

            RenderUtils.drawOutlinedBox(x, y, boxWidth, boxHeight, 0xDD000000, COLOR_HORIZONTAL_BAR);
            x += 10;
            y += 10;

            for (int i = 0; i < this.messages.size(); ++i)
            {
                Message message = this.messages.get(i);
                y = message.renderAt(x, y, 0xFFFFFFFF);

                if (message.hasExpired())
                {
                    this.messages.remove(i);
                    --i;
                }
            }
        }
    }

    protected int getMessagesHeight()
    {
        int height = 0;

        for (int i = 0; i < this.messages.size(); ++i)
        {
            height += this.messages.get(i).getMessageHeight();
        }

        return height;
    }

    public void bindTexture(Identifier texture)
    {
        this.client.getTextureManager().bindTexture(texture);
    }

    protected <T extends ButtonGeneric> ButtonWrapper<T> addButton(T button, IButtonActionListener<T> listener)
    {
        ButtonWrapper<T> entry = new ButtonWrapper<>(button, listener);
        this.buttons.add(entry);

        return entry;
    }

    protected <T extends TextFieldWidget> void addTextField(T textField, @Nullable ITextFieldListener<T> listener)
    {
        this.textFields.add(new TextFieldWrapper<>(textField, listener));
    }

    protected void addWidget(WidgetBase widget)
    {
        this.widgets.add(widget);
    }

    protected void addLabel(int x, int y, int width, int height, int textColor, String... lines)
    {
        if (lines != null && lines.length >= 1)
        {
            if (width == -1)
            {
                for (String line : lines)
                {
                    width = Math.max(width, this.fontRenderer.getStringWidth(line));
                }
            }

            WidgetLabel label = new WidgetLabel(x, y, width, height, this.zOffset, textColor, lines);
            this.addWidget(label);
        }
    }

    protected void addCheckBox(int x, int y, int width, int height, int textColor, String text,
            IGuiIcon widgetUnchecked, IGuiIcon widgetChecked, @Nullable String hoverInfo)
    {
        WidgetCheckBox checkbox = new WidgetCheckBox(x, y, this.zOffset, widgetUnchecked, widgetChecked, text, this.client, hoverInfo);
        this.addWidget(checkbox);
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
        drawRect(0, 0, this.width, this.height, TOOLTIP_BACKGROUND);
    }

    protected void drawTitle(int mouseX, int mouseY, float partialTicks)
    {
        this.client.fontRenderer.draw(this.getTitle(), LEFT, TOP, COLOR_WHITE);
    }

    protected void drawContents(int mouseX, int mouseY, float partialTicks)
    {
    }

    protected void drawButtons(int mouseX, int mouseY, float partialTicks)
    {
        for (ButtonWrapper<?> entry : this.buttons)
        {
            entry.draw(this.client, mouseX, mouseY, partialTicks);
        }
    }

    protected void drawTextFields(int mouseX, int mouseY)
    {
        for (TextFieldWrapper<?> entry : this.textFields)
        {
            entry.draw(mouseX, mouseY);
        }
    }

    protected void drawWidgets(int mouseX, int mouseY)
    {
        this.hoveredWidget = null;

        if (this.widgets.isEmpty() == false)
        {
            for (WidgetBase widget : this.widgets)
            {
                widget.render(mouseX, mouseY, false);

                if (widget.isMouseOver(mouseX, mouseY))
                {
                    this.hoveredWidget = widget;
                }
            }
        }
    }

    protected void drawButtonHoverTexts(int mouseX, int mouseY, float partialTicks)
    {
        for (ButtonWrapper<? extends ButtonGeneric> entry : this.buttons)
        {
            ButtonGeneric button = entry.getButton();

            if (button.hasHoverText() && button.isMouseOver(mouseX, mouseY))
            {
                this.drawTooltip(button.getHoverStrings(), mouseX, mouseY);
            }
        }

        GuiLighting.disable();
    }

    protected void drawHoveredWidget(int mouseX, int mouseY)
    {
        if (this.hoveredWidget != null)
        {
            this.hoveredWidget.postRenderHovered(mouseX, mouseY, false);
            GuiLighting.disable();
        }
    }

    public static boolean isMouseOver(int mouseX, int mouseY, int x, int y, int width, int height)
    {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

    public static int getTextWidth(String text)
    {
        return MinecraftClient.getInstance().fontRenderer.getStringWidth(text);
    }

    public static int getMaxNameLength(List<? extends IConfigBase> configs)
    {
        FontRenderer font = MinecraftClient.getInstance().fontRenderer;
        int width = 0;

        for (IConfigBase config : configs)
        {
            width = Math.max(width, font.getStringWidth(config.getName()));
        }

        return width;
    }
}
