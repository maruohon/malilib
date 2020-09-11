package fi.dy.masa.malilib.gui.widget;

import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import org.lwjgl.input.Keyboard;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.math.MathHelper;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.position.HorizontalAlignment;
import fi.dy.masa.malilib.gui.util.GuiUtils;
import fi.dy.masa.malilib.gui.util.TextRegion;
import fi.dy.masa.malilib.gui.widget.util.TextFieldValidator;
import fi.dy.masa.malilib.listener.TextChangeListener;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.render.message.MessageRenderer;
import fi.dy.masa.malilib.render.message.MessageType;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.malilib.util.data.LeftRight;

public class BaseTextFieldWidget extends BackgroundWidget
{
    public static final Pattern PATTERN_HEX_COLOR_6_8 = Pattern.compile("^(#|0x)([0-9a-fA-F]{6}|[0-9a-fA-F]{8})$");
    public static final Pattern PATTERN_HEX_COLOR_8 = Pattern.compile("^(#|0x)[0-9a-fA-F]{8}$");

    public static final TextFieldValidator VALIDATOR_HEX_COLOR_6_8     = (str) -> PATTERN_HEX_COLOR_6_8.matcher(str).matches();
    public static final TextFieldValidator VALIDATOR_HEX_COLOR_8       = (str) -> PATTERN_HEX_COLOR_8.matcher(str).matches();
    public static final TextFieldValidator VALIDATOR_DOUBLE            = new DoubleTextFieldWidget.DoubleValidator(Double.MIN_VALUE, Double.MAX_VALUE);
    public static final TextFieldValidator VALIDATOR_DOUBLE_POSITIVE   = new DoubleTextFieldWidget.DoubleValidator(+0.0, Double.MAX_VALUE);
    public static final TextFieldValidator VALIDATOR_INTEGER           = new IntegerTextFieldWidget.IntValidator(Integer.MIN_VALUE, Integer.MAX_VALUE);
    public static final TextFieldValidator VALIDATOR_INTEGER_POSITIVE  = new IntegerTextFieldWidget.IntValidator(1, Integer.MAX_VALUE);

    protected final TextRegion visibleText = new TextRegion();
    protected MessageRenderer messageRenderer = new MessageRenderer();
    private String text = "";
    protected String lastNotifiedText = "";
    @Nullable protected IInputCharacterValidator inputValidator;
    @Nullable protected TextFieldValidator textValidator;
    @Nullable protected TextChangeListener listener;
    protected int currentlyVisibleTextStartIndex;
    protected int colorError = 0xFFE04040;
    protected int colorFocused = 0xFFD0D0D0;
    protected int colorUnfocused = 0xFFA0A0A0;
    protected int colorWarning;
    protected int cursorPosition;
    protected int selectionStartPosition = -1;
    protected boolean isFocused;
    protected boolean isValidInput = true;
    protected boolean updateListenerAlways;
    protected boolean updateListenerFromTextSet;
    protected boolean visibleTextNeedsUpdate;

    public BaseTextFieldWidget(int x, int y, int width, int height)
    {
        this(x, y, width, height, "");
    }

    public BaseTextFieldWidget(int x, int y, int width, int height, String text)
    {
        super(x, y, width, height);

        this.lastNotifiedText = text;

        this.setBackgroundColor(0xFF000000);
        this.setBackgroundColorHovered(this.backgroundColor);
        this.setBackgroundEnabled(true);
        this.setRenderHoverBackground(false);
        this.setBorderColor(this.colorUnfocused);
        this.setBorderWidth(1);
        this.setPaddingX(3);
        this.updateTextFieldSize();
        this.setText(text);
    }

    @Override
    protected void onSizeChanged()
    {
        super.onSizeChanged();

        this.updateTextFieldSize();
    }

    @Override
    protected void onPositionOrSizeChanged(int oldX, int oldY)
    {
        super.onPositionOrSizeChanged(oldX, oldY);

        this.updateTextFieldSize();
    }

    protected void updateTextFieldSize()
    {
        this.visibleText.setMaxWidth(this.getMaxTextWidth());
        this.messageRenderer.setWidth(Math.max(this.getWidth(), 220));
    }

    @Override
    public BaseWidget setZLevel(float zLevel)
    {
        this.messageRenderer.setZLevel(zLevel + 10);
        return super.setZLevel(zLevel);
    }

    public String getText()
    {
        return this.text;
    }

    @Override
    public List<BaseTextFieldWidget> getAllTextFields()
    {
        return ImmutableList.of(this);
    }

    public BaseTextFieldWidget setColorError(int color)
    {
        this.colorError = color;
        return this;
    }

    public BaseTextFieldWidget setColorFocused(int color)
    {
        this.colorFocused = color;
        return this;
    }

    public BaseTextFieldWidget setColorUnfocused(int color)
    {
        this.colorUnfocused = color;
        return this;
    }

    public BaseTextFieldWidget setColorWarning(int color)
    {
        this.colorWarning = color;
        return this;
    }

    public BaseTextFieldWidget setText(String newText)
    {
        if (this.updateListenerFromTextSet == false)
        {
            // Set the cached string first to avoid a notification here
            this.lastNotifiedText = newText;
        }

        this.setTextInternal(newText, this.updateListenerFromTextSet);
        this.setCursorToEnd();

        return this;
    }

    protected BaseTextFieldWidget setTextInternal(String newText)
    {
        return this.setTextInternal(newText, this.updateListenerAlways);
    }

    protected BaseTextFieldWidget setTextInternal(String newText, boolean updateListener)
    {
        this.text = newText != null ? newText : "";
        this.isValidInput = this.isValidText(this.text);
        this.setVisibleTextNeedsUpdate();

        if (updateListener)
        {
            this.notifyListenerIfNeeded();
        }

        this.addErrorMessage();

        return this;
    }

    protected void addErrorMessage()
    {
        if (this.isValidInput)
        {
            this.messageRenderer.clearMessages();
        }
        else if (this.textValidator != null && this.text.isEmpty() == false)
        {
            String message = this.textValidator.getErrorMessage(this.text);

            if (message != null)
            {
                this.messageRenderer.addMessage(MessageType.ERROR, 3000, message);
                this.updateMessageRendererPosition();
            }
        }
    }

    protected void updateMessageRendererPosition()
    {
        int x = this.getX();
        int y = this.getY();
        int height = this.getHeight();
        int messagesWidth = this.messageRenderer.getWidth();
        int messagesHeight = this.messageRenderer.getHeight();
        int scaledWidth = GuiUtils.getScaledWindowWidth();

        if (x + messagesWidth > scaledWidth)
        {
            this.messageRenderer.setX(scaledWidth - messagesWidth - 2);
        }
        else
        {
            this.messageRenderer.setX(x);
        }

        if (y + height + messagesHeight > GuiUtils.getScaledWindowHeight())
        {
            this.messageRenderer.setY(y - messagesHeight);
        }
        else
        {
            this.messageRenderer.setY(y + height);
        }
    }

    public boolean isFocused()
    {
        return this.isFocused;
    }

    public BaseTextFieldWidget setFocused(boolean isFocused)
    {
        boolean wasFocused = this.isFocused;

        this.isFocused = isFocused;
        this.setBorderColor(isFocused ? this.colorFocused : this.colorUnfocused);

        if (wasFocused && this.isFocused == false)
        {
            this.notifyListenerIfNeeded();
        }

        if (this.isFocused)
        {
            Keyboard.enableRepeatEvents(true);
        }

        return this;
    }

    public BaseTextFieldWidget setInputValidator(@Nullable IInputCharacterValidator inputValidator)
    {
        this.inputValidator = inputValidator;
        return this;
    }

    public BaseTextFieldWidget setTextValidator(@Nullable TextFieldValidator validator)
    {
        this.textValidator = validator;
        return this;
    }

    /**
     * Set the text change listener to use, if any.
     * <br><br>
     * <b>Note:</b> By default the listener is only notified when Enter is pressed,
     * or the text field loses focus.
     * If the listener should be notified on every change (characters written or removed etc.),
     * then call {@link BaseTextFieldWidget#setUpdateListenerAlways(boolean)}
     * @param listener
     * @return
     */
    public BaseTextFieldWidget setListener(@Nullable TextChangeListener listener)
    {
        this.listener = listener;
        return this;
    }

    /**
     * Set whether or not to update the listener on every text change (from typing) or not.
     * If this is set to false, then the listener is only notified
     * when Enter is pressed or the text field loses focus.
     * @param updateAlways
     * @return
     */
    public BaseTextFieldWidget setUpdateListenerAlways(boolean updateAlways)
    {
        this.updateListenerAlways = updateAlways;
        return this;
    }

    /**
     * Set whether or not to update the listener from calls to the setText() method, such
     * as initializing the text or changing it from right mouse button clearing etc.
     * @param update
     * @return
     */
    public BaseTextFieldWidget setUpdateListenerFromTextSet(boolean update)
    {
        this.updateListenerFromTextSet = update;
        return this;
    }

    protected boolean isValidText(String text)
    {
        return this.textValidator == null || this.textValidator.isValidInput(text);
    }

    protected void notifyListenerIfNeeded()
    {
        if (this.listener != null &&
            this.text.equals(this.lastNotifiedText) == false &&
            this.isValidText(this.text))
        {
            this.listener.onTextChange(this.text);
            this.lastNotifiedText = this.text;
        }
    }

    public int getCursorPosition()
    {
        return this.cursorPosition;
    }

    protected void setVisibleTextNeedsUpdate()
    {
        this.visibleTextNeedsUpdate = true;
    }

    protected void moveCursor(LeftRight direction, boolean selectText)
    {
        int newPos;

        // Nudging the position left or right while there was a selection,
        // remove the selection and move the cursor to the appropriate end of the selection
        if (this.selectionStartPosition != -1 && selectText == false)
        {
            int p1 = this.cursorPosition;
            int p2 = this.selectionStartPosition;
            newPos = direction == LeftRight.LEFT ? Math.min(p1, p2) : Math.max(p1, p2);
            this.selectionStartPosition = -1;
        }
        else
        {
            newPos = this.cursorPosition + (direction == LeftRight.LEFT ? -1 : 1);
        }

        this.setCursorPosition(newPos, selectText);
    }

    protected BaseTextFieldWidget setCursorPosition(int position, boolean selectText)
    {
        int oldPosition = this.cursorPosition;

        if (selectText == false)
        {
            this.selectionStartPosition = -1;
        }
        else if (this.selectionStartPosition < 0)
        {
            this.selectionStartPosition = oldPosition;
        }

        // The cursor can go after the last character, so not length - 1 here
        this.cursorPosition = MathHelper.clamp(position, 0, this.text.length());

        if (this.cursorPosition <= this.visibleText.getStartIndex() ||
            this.visibleText.isIndexVisibleWithCurrentStart(this.cursorPosition, this.text) == false)
        {
            this.setVisibleTextNeedsUpdate();
        }

        this.updateVisibleTextIfNeeded();

        return this;
    }

    protected void moveCursorToEndOfWord(LeftRight direction, boolean allowNonAlphaNumeric, boolean selectText)
    {
        int end = this.text.length();

        if (end == 0)
        {
            return;
        }

        int inc = direction == LeftRight.LEFT ? -1 : 1;
        int pos = Math.min(this.cursorPosition, end - 1);
        int next = pos + inc;
        Function<Character, Boolean> isWord = allowNonAlphaNumeric ? (c) -> c != ' ' : this::isAlphanumeric;
        StringBuilder sb = new StringBuilder(this.text);

        // When going left, always change the position by at least one, so that the cursor can leave
        // the word it's currently on.
        if (direction == LeftRight.LEFT)
        {
            pos = next;
            next += inc;
        }

        // First skip past all the space/non-word characters the cursor is currently on
        while (next >= 0 && next <= end && isWord.apply(sb.charAt(pos)) == false)
        {
            pos = next;
            next += inc;
        }

        next = pos + inc;

        // When going left, leave the cursor at the start index of the word
        if (direction == LeftRight.LEFT)
        {
            while (next >= 0 && isWord.apply(sb.charAt(next)))
            {
                pos = next;
                --next;
            }
        }
        // When going right, leave the cursor at the position following the word
        else
        {
            while (next <= end && isWord.apply(sb.charAt(pos)))
            {
                pos = next;
                next += inc;
            }
        }

        this.setCursorPosition(pos, selectText);
    }

    protected boolean isAlphanumeric(char c)
    {
        return Character.isAlphabetic(c);
        //return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9');
    }

    public BaseTextFieldWidget setCursorPosition(int position)
    {
        return this.setCursorPosition(position, false);
    }

    public BaseTextFieldWidget setCursorToStart()
    {
        return this.setCursorPosition(0, false);
    }

    public BaseTextFieldWidget setCursorToEnd()
    {
        return this.setCursorPosition(this.text.length(), false);
    }

    protected BaseTextFieldWidget setCursorToBeginning(boolean selectText)
    {
        return this.setCursorPosition(0, selectText);
    }

    protected BaseTextFieldWidget setCursorToEnd(boolean selectText)
    {
        return this.setCursorPosition(this.text.length(), selectText);
    }

    protected void updateVisibleTextIfNeeded()
    {
        if (this.visibleTextNeedsUpdate)
        {
            // Cursor was moved to outside of the previously visible text region
            if (this.cursorPosition <= this.visibleText.getStartIndex() ||
                this.visibleText.isIndexVisibleWithCurrentStart(this.cursorPosition, this.text) == false)
            {
                this.visibleText.alignToIndexAt(this.text, this.cursorPosition, HorizontalAlignment.CENTER);
            }
            else
            {
                this.visibleText.update(this.text);
            }

            this.visibleTextNeedsUpdate = false;
        }
    }

    protected int getClickedTextIndex(int mouseX, int mouseY)
    {
        int relX = mouseX - (this.getX() + this.getTextStartRelativeX());

        if (relX <= 0)
        {
            return 0;
        }

        String visibleText = this.visibleText.getText();
        String textLeftOfCursor = StringUtils.clampTextToRenderLength(visibleText, relX, LeftRight.RIGHT, "");
        int textLeftLength = textLeftOfCursor.length();
        int index = textLeftLength + this.visibleText.getStartIndex();

        // Set the click position to the right of the clicked character,
        // if the click position is on the right half of the character.
        if (visibleText.length() > textLeftLength)
        {
            int xPosInChar = relX - this.getStringWidth(textLeftOfCursor);
            int charWidth = this.getStringWidth(visibleText.substring(textLeftLength, textLeftLength + 1));

            if (xPosInChar >= charWidth / 2)
            {
                index += 1;
            }
        }

        return index;
    }

    protected int getTextStartRelativeX()
    {
        return this.borderWidth + this.paddingX;
    }

    /**
     * Returns the maximum render width for text
     * @return
     */
    protected int getMaxTextWidth()
    {
        return this.getWidth() - this.borderWidth * 2 - this.paddingX * 2;
    }

    protected boolean isUsableCharacter(char typedChar, int modifiers)
    {
        if (typedChar != 167 && ChatAllowedCharacters.isAllowedCharacter(typedChar) == false)
        {
            return false;
        }

        return this.inputValidator == null ||
               this.inputValidator.canWriteCharacter(this.cursorPosition, this.text, typedChar, modifiers);
    }

    protected void writeCharacter(char typedChar, int modifiers)
    {
        this.insertText(String.valueOf(typedChar));
    }

    protected void insertText(String text)
    {
        if (this.selectionStartPosition >= 0)
        {
            this.deleteSelectedText();
        }

        int oldLength = this.text.length();
        StringBuilder sb = new StringBuilder(oldLength + text.length());

        if (this.cursorPosition > 0)
        {
            sb.append(this.text, 0, this.cursorPosition);
        }

        sb.append(text);

        if (this.cursorPosition < oldLength)
        {
            sb.append(this.text.substring(this.cursorPosition));
        }

        this.setTextInternal(sb.toString());
        this.setCursorPosition(this.cursorPosition + text.length(), false);
        this.updateVisibleTextIfNeeded();
    }

    protected boolean deleteCharacterAtCursor(boolean forward)
    {
        int oldLength = this.text.length();
        int index = forward ? this.cursorPosition : this.cursorPosition - 1;

        if (index >= 0 && index < oldLength)
        {
            StringBuilder sb = new StringBuilder(this.text);
            int num = Character.charCount(sb.codePointAt(index));

            sb.delete(index, index + num);
            this.setTextInternal(sb.toString());

            // Move the cursor position back when using backspace to delete characters
            if (forward == false)
            {
                this.setCursorPosition(this.cursorPosition - 1, false);
            }

            this.updateVisibleTextIfNeeded();

            return true;
        }

        return false;
    }

    protected boolean deleteSelectedText()
    {
        int start = Math.min(this.cursorPosition, this.selectionStartPosition);
        int end   = Math.max(this.cursorPosition, this.selectionStartPosition);
        int textLen = this.text.length();

        if (start < 0 || end < 0 || start >= textLen || end > textLen)
        {
            return false;
        }

        // Remove the selection
        this.selectionStartPosition = -1;

        StringBuilder sb = new StringBuilder(this.text);
        sb.delete(start, end);
        this.setTextInternal(sb.toString());
        this.setCursorPosition(start, false);

        this.updateVisibleTextIfNeeded();

        return true;
    }

    protected boolean deleteSelectionOrCharacter(boolean forward)
    {
        if (this.text.length() == 0)
        {
            return false;
        }

        if (this.selectionStartPosition >= 0)
        {
            return this.deleteSelectedText();
        }
        else
        {
            return this.deleteCharacterAtCursor(forward);
        }
    }

    @Nullable
    protected String getSelectedText()
    {
        if (this.selectionStartPosition >= 0)
        {
            int start = Math.min(this.cursorPosition, this.selectionStartPosition);
            int end   = Math.max(this.cursorPosition, this.selectionStartPosition);
            int textLen = this.text.length();

            if (start >= 0 && end >= 0 && start < textLen && end <= textLen)
            {
                return this.text.substring(start, end);
            }
        }

        return null;
    }

    protected boolean cutSelectedText()
    {
        String selectedText = this.getSelectedText();

        if (selectedText != null)
        {
            GuiScreen.setClipboardString(selectedText);
            this.deleteSelectedText();
            return true;
        }

        return false;
    }

    protected boolean copySelectedText()
    {
        String selectedText = this.getSelectedText();

        if (selectedText != null)
        {
            GuiScreen.setClipboardString(selectedText);
            return true;
        }

        return false;
    }

    protected void pasteText()
    {
        if (this.selectionStartPosition >= 0)
        {
            this.deleteSelectedText();
        }

        this.insertText(GuiScreen.getClipboardString());
    }

    @Override
    public boolean getShouldReceiveOutsideClicks()
    {
        return true;
    }

    @Override
    protected boolean onMouseClickedImpl(int mouseX, int mouseY, int mouseButton)
    {
        boolean isMouseOver = this.isMouseOver(mouseX, mouseY);

        if (isMouseOver)
        {
            if (this.isFocused() == false)
            {
                this.setFocused(true);
            }

            // Clear the text field on right click
            if (mouseButton == 1)
            {
                this.setText("");
            }
            else
            {
                int clickedIndex = this.getClickedTextIndex(mouseX, mouseY);
                boolean selectText = BaseScreen.isShiftDown();
                this.setCursorPosition(clickedIndex, selectText);
            }

            return true;
        }
        // Remove focus
        else if (this.isFocused())
        {
            this.setFocused(false);
        }

        return false;
    }

    @Override
    public boolean onMouseScrolledImpl(int mouseX, int mouseY, double mouseWheelDelta)
    {
        // Allow selecting text or moving the cursor by scrolling
        if (this.isFocused() && this.text.length() > 0)
        {
            boolean selectText = BaseScreen.isShiftDown();
            this.setCursorPosition(this.cursorPosition + (mouseWheelDelta < 0 ? 1 : -1), selectText);
            return true;
        }

        return false;
    }

    @Override
    public void onMouseReleasedImpl(int mouseX, int mouseY, int mouseButton)
    {
    }

    @Override
    protected boolean onKeyTypedImpl(char typedChar, int keyCode)
    {
        if (this.isFocused())
        {
            boolean selectText = BaseScreen.isShiftDown();

            if (keyCode == Keyboard.KEY_BACK)
            {
                if (BaseScreen.isCtrlDown())
                {
                    this.selectionStartPosition = -1;
                    this.moveCursorToEndOfWord(LeftRight.LEFT, BaseScreen.isAltDown(), true);
                }

                this.deleteSelectionOrCharacter(false);
            }
            else if (keyCode == Keyboard.KEY_DELETE)
            {
                if (BaseScreen.isCtrlDown())
                {
                    this.selectionStartPosition = -1;
                    this.moveCursorToEndOfWord(LeftRight.RIGHT, BaseScreen.isAltDown(), true);
                }

                this.deleteSelectionOrCharacter(true);
            }
            else if (keyCode == Keyboard.KEY_RETURN || keyCode == Keyboard.KEY_TAB)
            {
                if (keyCode == Keyboard.KEY_RETURN)
                {
                    this.notifyListenerIfNeeded();
                }

                return keyCode != Keyboard.KEY_TAB; // Don't cancel on tab input, to allow the container to handle focus change
            }
            else if (keyCode == Keyboard.KEY_LEFT)
            {
                if (BaseScreen.isCtrlDown())
                {
                    this.moveCursorToEndOfWord(LeftRight.LEFT, BaseScreen.isAltDown(), selectText);
                }
                else
                {
                    this.moveCursor(LeftRight.LEFT, selectText);
                }
            }
            else if (keyCode == Keyboard.KEY_RIGHT)
            {
                if (BaseScreen.isCtrlDown())
                {
                    this.moveCursorToEndOfWord(LeftRight.RIGHT, BaseScreen.isAltDown(), selectText);
                }
                else
                {
                    this.moveCursor(LeftRight.RIGHT, selectText);
                }
            }
            else if (keyCode == Keyboard.KEY_HOME)
            {
                this.setCursorToBeginning(selectText);
            }
            else if (keyCode == Keyboard.KEY_END)
            {
                this.setCursorToEnd(selectText);
            }
            else if (GuiScreen.isKeyComboCtrlA(keyCode))
            {
                this.setCursorToBeginning(false);
                this.setCursorToEnd(true);
            }
            else if (GuiScreen.isKeyComboCtrlX(keyCode))
            {
                this.cutSelectedText();
            }
            else if (GuiScreen.isKeyComboCtrlC(keyCode))
            {
                this.copySelectedText();
            }
            else if (GuiScreen.isKeyComboCtrlV(keyCode))
            {
                this.pasteText();
            }
            else if (this.isUsableCharacter(typedChar, keyCode))
            {
                this.writeCharacter(typedChar, keyCode);
            }
            else
            {
                return false;
            }

            return true;
        }

        return false;
    }

    protected void renderCursor(int x, int y, float z, int color)
    {
        int relIndex = this.cursorPosition - this.visibleText.getStartIndex();
        color = this.selectionStartPosition != -1 ? 0xFF00D0FF : color;

        if (relIndex >= 0)
        {
            String visibleText = this.visibleText.getText();

            // The cursor is at the end of the text, use an underscore cursor
            if (this.cursorPosition == this.text.length() && this.selectionStartPosition == -1)
            {
                int offX = this.getStringWidth(visibleText);
                this.drawString(x + offX, y + this.getCenteredTextOffsetY(), z, color, "_");
            }
            else
            {
                relIndex = Math.min(relIndex, visibleText.length());
                // Use a different cursor color in selection mode
                int colorTr = (color & 0x00FFFFFF) | 0x50000000;
                int cursorExtraHeight = 2;

                int offX = this.getStringWidth(visibleText.substring(0, relIndex));
                int offY = (this.getHeight() - (this.fontHeight + cursorExtraHeight * 2)) / 2;
                int y1 = y + offY;
                int y2 = y1 + cursorExtraHeight;
                int y3 = y2 + this.fontHeight;

                RenderUtils.renderVerticalLine(x + offX, y1, cursorExtraHeight    , color  , z + 0.1f);
                RenderUtils.renderVerticalLine(x + offX, y2, this.fontHeight      , colorTr, z + 0.1f);
                RenderUtils.renderVerticalLine(x + offX, y3, cursorExtraHeight + 1, color  , z + 0.1f);
            }
        }
    }

    protected void renderTextSegment(int x, int y, float z, int startIndex, int textLength, int textColor, int backgroundColor)
    {
    }

    protected void renderVisibleText(int x, int y, float z, int textColor)
    {
        String visibleText = this.visibleText.getText();

        // A selection exists
        if (this.selectionStartPosition >= 0)
        {
            int selStart = Math.min(this.cursorPosition, this.selectionStartPosition);
            int selEnd   = Math.max(this.cursorPosition, this.selectionStartPosition);
            int start = this.visibleText.getStartIndex();
            int end = this.visibleText.getEndIndex();
            int visLen = visibleText.length();

            // There is at least a part of a selected region visible
            if (selStart <= end && selEnd >= start)
            {
                // Non-selected text at the start
                if (selStart > start)
                {
                    String str = visibleText.substring(0, selStart - start);
                    this.drawString(x, y, z, textColor, str);
                    x += this.getStringWidth(str);
                }

                int p1 = Math.max(0     , selStart - start);
                int p2 = Math.min(visLen, selEnd - start);
                String str = visibleText.substring(p1, p2);
                int selWidth = this.getStringWidth(str);

                RenderUtils.renderRectangle(x, y - 2, selWidth, this.fontHeight + 3, textColor, z);
                this.drawString(x, y, z, 0xFF000000, str);
                x += selWidth;

                // Non-selected text at the start
                if (selEnd <= end)
                {
                    str = visibleText.substring(selEnd - start, visLen);
                    this.drawString(x, y, z, textColor, str);
                }

                return;
            }
        }

        // No selection
        this.drawString(x, y, z, textColor, visibleText);
    }

    @Override
    public void renderAt(int x, int y, float z, int mouseX, int mouseY, boolean isActiveGui, boolean hovered)
    {
        super.renderAt(x, y, z, mouseX, mouseY, isActiveGui, hovered);

        int color;

        if (this.isValidInput)
        {
            color = this.isFocused() ? this.colorFocused : this.colorUnfocused;
        }
        else
        {
            color = this.colorError;
        }

        x += this.getTextStartRelativeX();

        if (this.text.isEmpty() == false)
        {
            int bw = this.borderEnabled ? this.borderWidth * 2 : 0;
            // The font is usually 1 pixel "too high", as in it's touching the top, but not the bottom
            int offset = Math.max((int) Math.ceil((this.getHeight() - bw - this.fontHeight) / 2.0) + 1, 0);

            if ((offset & 0x1) == 1)
            {
                offset += 1;
            }

            this.renderVisibleText(x, y + offset, z + 0.1f, color);
        }

        if (this.isFocused())
        {
            this.renderCursor(x, y, z + 0.1f, color);
        }

        int messagesHeightPre = this.messageRenderer.getHeight();
        int diffX = x - this.getX();
        int diffY = y - this.getY();
        this.messageRenderer.drawMessages(this.messageRenderer.getX() + diffX, this.messageRenderer.getY() + diffY, z + 0.1f);

        // Update the position when old messages are removed
        if (this.messageRenderer.getHeight() != messagesHeightPre)
        {
            this.updateMessageRendererPosition();
        }

        RenderUtils.color(1f, 1f, 1f, 1f);
    }

    public interface IInputCharacterValidator
    {
        boolean canWriteCharacter(int index, String currentText, char typedChar, int modifiers);
    }
}
