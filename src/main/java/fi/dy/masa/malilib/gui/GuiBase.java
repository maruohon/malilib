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
import net.minecraft.ChatFormat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.Identifier;

public abstract class GuiBase extends Screen implements IMessageConsumer, IStringConsumer
{
    public static final String TXT_BLUE = ChatFormat.BLUE.toString();
    public static final String TXT_GRAY = ChatFormat.GRAY.toString();
    public static final String TXT_GREEN = ChatFormat.GREEN.toString();
    public static final String TXT_GOLD = ChatFormat.GOLD.toString();
    public static final String TXT_RED = ChatFormat.RED.toString();
    public static final String TXT_WHITE = ChatFormat.WHITE.toString();
    public static final String TXT_YELLOW = ChatFormat.YELLOW.toString();

    public static final String TXT_BOLD = ChatFormat.BOLD.toString();
    public static final String TXT_RST = ChatFormat.RESET.toString();
    public static final String TXT_UNDERLINE = ChatFormat.UNDERLINE.toString();

    public static final String TXT_DARK_GREEN = ChatFormat.DARK_GREEN.toString();
    public static final String TXT_DARK_RED = ChatFormat.DARK_RED.toString();

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
    protected final TextRenderer textRenderer;
    protected WidgetBase hoveredWidget = null;
    private MessageType nextMessageType = MessageType.INFO;
    protected String title = "";
    protected double mouseX;
    protected double mouseY;
    protected boolean useTitleHierarchy = true;
    @Nullable
    private Screen parent;

    protected GuiBase()
    {
        super(null);

        this.textRenderer = MinecraftClient.getInstance().textRenderer;
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
    public Component getTitle()
    {
        return new TextComponent(this.getTitleString());
    }

    @Override
    public void removed()
    {
        MinecraftClient.getInstance().keyboard.enableRepeatEvents(false);
    }

    @Override
    public boolean isPauseScreen()
    {
        return false;
    }

    @Override
    public void init()
    {
        super.init();

        this.clearElements();
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks)
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
    public boolean mouseScrolled(double mouseX, double mouseY, double amount)
    {
        if (amount == 0 || this.onMouseScrolled((int) mouseX, (int) mouseY, (int) amount))
        {
            return super.mouseScrolled(mouseX, mouseY, amount);
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
            if (entry.mousePressed(this.minecraft, mouseX, mouseY, mouseButton))
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
                entry.setFocused(false);
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
            if (Screen.hasShiftDown())
            {
                this.onClose();
            }
            else
            {
                this.minecraft.openScreen(this.parent);
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
                entry.setFocused(false);
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
            if (Screen.hasShiftDown())
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
        this.minecraft.getTextureManager().bindTexture(texture);
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
                    width = Math.max(width, this.textRenderer.getStringWidth(line));
                }
            }

            WidgetLabel label = new WidgetLabel(x, y, width, height, this.blitOffset, textColor, lines);
            this.addWidget(label);
        }
    }

    protected void addCheckBox(int x, int y, int width, int height, int textColor, String text,
            IGuiIcon widgetUnchecked, IGuiIcon widgetChecked, @Nullable String hoverInfo)
    {
        WidgetCheckBox checkbox = new WidgetCheckBox(x, y, this.blitOffset, widgetUnchecked, widgetChecked, text, this.minecraft, hoverInfo);
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
        DrawableHelper.fill(0, 0, this.width, this.height, TOOLTIP_BACKGROUND);
    }

    protected void drawTitle(int mouseX, int mouseY, float partialTicks)
    {
        this.minecraft.textRenderer.draw(this.getTitleString(), LEFT, TOP, COLOR_WHITE);
    }

    protected void drawContents(int mouseX, int mouseY, float partialTicks)
    {
    }

    protected void drawButtons(int mouseX, int mouseY, float partialTicks)
    {
        for (ButtonWrapper<?> entry : this.buttons)
        {
            entry.draw(this.minecraft, mouseX, mouseY, partialTicks);
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
                this.renderTooltip(button.getHoverStrings(), mouseX, mouseY);
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
        return MinecraftClient.getInstance().textRenderer.getStringWidth(text);
    }

    public static int getMaxNameLength(List<? extends IConfigBase> configs)
    {
        TextRenderer font = MinecraftClient.getInstance().textRenderer;
        int width = 0;

        for (IConfigBase config : configs)
        {
            width = Math.max(width, font.getStringWidth(config.getName()));
        }

        return width;
    }
}
