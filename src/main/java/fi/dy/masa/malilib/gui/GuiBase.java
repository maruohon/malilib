package fi.dy.masa.malilib.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import fi.dy.masa.malilib.gui.Message.MessageType;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonHoverText;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.gui.interfaces.IMessageConsumer;
import fi.dy.masa.malilib.gui.interfaces.ITextFieldListener;
import fi.dy.masa.malilib.gui.widgets.WidgetInfo;
import fi.dy.masa.malilib.gui.wrappers.ButtonWrapper;
import fi.dy.masa.malilib.gui.wrappers.TextFieldWrapper;
import fi.dy.masa.malilib.interfaces.IStringConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

public abstract class GuiBase extends GuiScreen implements IMessageConsumer, IStringConsumer
{
    public static final String TXT_BLUE = TextFormatting.BLUE.toString();
    public static final String TXT_GRAY = TextFormatting.GRAY.toString();
    public static final String TXT_GREEN = TextFormatting.GREEN.toString();
    public static final String TXT_GOLD = TextFormatting.GOLD.toString();
    public static final String TXT_RED = TextFormatting.RED.toString();
    public static final String TXT_WHITE = TextFormatting.WHITE.toString();
    public static final String TXT_BOLD = TextFormatting.BOLD.toString();
    public static final String TXT_RST = TextFormatting.RESET.toString();

    public static final String TXT_DARK_GREEN = TextFormatting.DARK_GREEN.toString();
    public static final String TXT_DARK_RED = TextFormatting.DARK_RED.toString();

    protected static final String BUTTON_LABEL_ADD = TextFormatting.DARK_GREEN + "+" + TextFormatting.RESET;
    protected static final String BUTTON_LABEL_REMOVE = TextFormatting.DARK_RED + "-" + TextFormatting.RESET;

    public static final int COLOR_WHITE          = 0xFFFFFFFF;
    public static final int TOOLTIP_BACKGROUND   = 0xB0000000;
    public static final int COLOR_HORIZONTAL_BAR = 0xFF999999;
    protected static final int LEFT         = 20;
    protected static final int TOP          = 10;
    private final List<ButtonWrapper<? extends ButtonBase>> buttons = new ArrayList<>();
    private final List<TextFieldWrapper<? extends GuiTextField>> textFields = new ArrayList<>();
    private final List<GuiLabel> labels = new ArrayList<>();
    private final List<WidgetInfo> infoWidgets = new ArrayList<>();
    private final List<Message> messages = new ArrayList<>();
    private MessageType nextMessageType = MessageType.INFO;
    protected String title = "";
    @Nullable
    private GuiBase parent;

    public GuiBase()
    {
    }

    public GuiBase setParent(@Nullable GuiBase parent)
    {
        // Don't allow nesting the GUI with itself...
        if (parent == null || parent.getClass() != this.getClass())
        {
            this.parent = parent;
        }

        return this;
    }

    @Nullable
    public GuiBase getParent()
    {
        return this.parent;
    }

    protected String getTitle()
    {
        return this.parent != null ? this.parent.getTitle() + " => " + this.title : this.title;
    }

    public Minecraft getMinecraft()
    {
        return this.mc;
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

        this.clearLabels();
        this.clearButtons();
        this.clearTextFields();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawPanel(mouseX, mouseY, partialTicks);

        this.drawContents(mouseX, mouseY, partialTicks);

        super.drawScreen(mouseX, mouseY, partialTicks);

        this.drawGuiMessages();

        this.drawButtonHoverTexts(mouseX, mouseY, partialTicks);

        if (this.infoWidgets.isEmpty() == false)
        {
            for (WidgetInfo widget : this.infoWidgets)
            {
                widget.render(mouseX, mouseY, false);
            }
        }
    }

    public void drawContents(int mouseX, int mouseY, float partialTicks)
    {
    }

    @Override
    protected void actionPerformed(GuiButton control)
    {
        if (control.id == 0)
        {
            this.close();
        }
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
        for (ButtonWrapper<?> entry : this.buttons)
        {
            if (entry.mousePressed(this.mc, mouseX, mouseY, mouseButton))
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

    public boolean onKeyTyped(char typedChar, int keyCode)
    {
        if (keyCode == Keyboard.KEY_ESCAPE)
        {
            if (GuiScreen.isShiftKeyDown())
            {
                this.mc.displayGuiScreen(null);
            }
            else
            {
                this.mc.displayGuiScreen(this.parent);
            }

            return true;
        }

        boolean handled = false;
        int selected = -1;
        int i = 0;

        for (TextFieldWrapper<?> entry : this.textFields)
        {
            if (keyCode == Keyboard.KEY_TAB && entry.getTextField().isFocused())
            {
                entry.getTextField().setFocused(false);
                selected = i;
                handled = true;
            }
            else if (entry.keyTyped(typedChar, keyCode))
            {
                handled = true;
            }

            i++;
        }

        if (selected >= 0)
        {
            if (GuiScreen.isShiftKeyDown())
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

    protected void addInfoWidget(WidgetInfo widget)
    {
        this.infoWidgets.add(widget);
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

    public void bindTexture(ResourceLocation texture)
    {
        this.mc.getTextureManager().bindTexture(texture);
    }

    protected <T extends ButtonBase> ButtonWrapper<T> addButton(T button, IButtonActionListener<T> listener)
    {
        ButtonWrapper<T> entry = new ButtonWrapper<>(button, listener);
        this.buttons.add(entry);

        return entry;
    }

    protected <T extends GuiTextField> void addTextField(T textField, @Nullable ITextFieldListener<T> listener)
    {
        this.textFields.add(new TextFieldWrapper<>(textField, listener));
    }

    protected void addLabel(int id, int x, int y, int width, int height, int colour, String... lines)
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

            GuiLabel label = new GuiLabel(this.mc.fontRenderer, id, x, y, width, height, colour);

            for (String line : lines)
            {
                label.addLine(line);
            }

            this.labels.add(label);
        }
    }

    protected void clearLabels()
    {
        this.labels.clear();
    }

    protected void clearButtons()
    {
        this.buttons.clear();
    }

    protected void clearTextFields()
    {
        this.textFields.clear();
    }

    public void close()
    {
        this.mc.displayGuiScreen(null);
    }

    protected void drawPanel(int mouseX, int mouseY, float partialTicks)
    {
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        GlStateManager.pushMatrix();

        // Draw the dark background
        drawRect(0, 0, this.width, this.height, TOOLTIP_BACKGROUND);

        this.draw(mouseX, mouseY, partialTicks);

        GlStateManager.popMatrix();
    }

    protected void drawButtons(int mouseX, int mouseY, float partialTicks)
    {
        for (ButtonWrapper<?> entry : this.buttons)
        {
            entry.draw(this.mc, mouseX, mouseY, partialTicks);
        }
    }

    protected void drawButtonHoverTexts(int mouseX, int mouseY, float partialTicks)
    {
        for (ButtonWrapper<? extends ButtonBase> entry : this.buttons)
        {
            ButtonBase button = entry.getButton();

            if ((button instanceof ButtonHoverText) && button.isMouseOver())
            {
                this.drawHoveringText(((ButtonHoverText) button).getHoverStrings(), mouseX, mouseY);
            }
        }
    }

    protected void drawTextFields()
    {
        for (TextFieldWrapper<?> entry : this.textFields)
        {
            entry.draw();
        }
    }

    protected void drawLabels(int mouseX, int mouseY, float partialTicks)
    {
        for (GuiLabel label : this.labels)
        {
            label.drawLabel(this.mc, mouseX, mouseY);
        }
    }

    /**
     * Draw the panel and chrome
     * 
     * @param mouseX
     * @param mouseY
     * @param partialTicks
     */
    protected void draw(int mouseX, int mouseY, float partialTicks)
    {
        // Draw panel title
        this.mc.fontRenderer.drawString(this.getTitle(), LEFT, TOP, COLOR_WHITE);

        // Offset by scroll
        GlStateManager.pushMatrix();

        // Draw panel contents
        this.drawLabels(mouseX, mouseY, partialTicks);
        this.drawTextFields();
        this.drawButtons(mouseX, mouseY, partialTicks);

        GlStateManager.clear(GL11.GL_DEPTH_BUFFER_BIT);

        // Restore transform
        GlStateManager.popMatrix();
    }

    public static boolean isMouseOver(int mouseX, int mouseY, int x, int y, int width, int height)
    {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

    public static int getTextWidth(String text)
    {
        return Minecraft.getMinecraft().fontRenderer.getStringWidth(text);
    }
}
