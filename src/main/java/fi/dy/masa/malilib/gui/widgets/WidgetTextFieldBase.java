package fi.dy.masa.malilib.gui.widgets;

import java.util.function.Function;
import javax.annotation.Nullable;
import org.lwjgl.input.Keyboard;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.math.MathHelper;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.util.TextRegion;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.HorizontalAlignment;
import fi.dy.masa.malilib.util.LeftRight;
import fi.dy.masa.malilib.util.StringUtils;

public class WidgetTextFieldBase extends WidgetBackground
{
    protected final TextRegion visibleText = new TextRegion();
    private String text = "";
    @Nullable protected IInputCharacterValidator inputValidator;
    protected int currentlyVisibleTextStartIndex;
    protected int colorError;
    protected int colorFocused = 0xFFC0C0C0;
    protected int colorUnfocused = 0xFFA0A0A0;
    protected int colorWarning;
    protected int cursorPosition;
    protected int selectionStartPosition = -1;
    protected boolean isFocused;
    protected boolean visibleTextNeedsUpdate;

    public WidgetTextFieldBase(int x, int y, int width, int height)
    {
        this(x, y, width, height, "");
    }

    public WidgetTextFieldBase(int x, int y, int width, int height, String text)
    {
        super(x, y, width, height);

        this.setBackgroundColor(0xFF000000);
        this.setBackgroundEnabled(true);
        this.setPaddingX(4);

        this.visibleText.setMaxWidth(this.getMaxTextWidth());
        this.setText(text);
    }

    @Override
    public void setWidth(int width)
    {
        super.setWidth(width);

        this.visibleText.setMaxWidth(this.getMaxTextWidth());
    }

    public String getText()
    {
        return this.text;
    }

    public WidgetTextFieldBase setText(String newText)
    {
        this.setTextInternal(newText);
        this.setCursorToEnd();
        return this;
    }

    protected WidgetTextFieldBase setTextInternal(String newText)
    {
        this.text = newText != null ? newText : "";
        this.setVisibleTextNeedsUpdate();
        return this;
    }

    public boolean isFocused()
    {
        return this.isFocused;
    }

    public WidgetTextFieldBase setFocused(boolean isFocused)
    {
        this.isFocused = isFocused;
        this.setBorderColor(isFocused ? this.colorFocused : this.colorUnfocused);
        Keyboard.enableRepeatEvents(this.isFocused);
        return this;
    }

    public WidgetTextFieldBase setInputValidator(@Nullable IInputCharacterValidator inputValidator)
    {
        this.inputValidator = inputValidator;
        return this;
    }

    public int getCursorPosition()
    {
        return this.cursorPosition;
    }

    protected void setVisibleTextNeedsUpdate()
    {
        this.visibleTextNeedsUpdate = true;
    }

    protected WidgetTextFieldBase setCursorPosition(int position, boolean selectText)
    {
        int oldPosition = this.cursorPosition;

        if (selectText == false)
        {
            int oldSelection = this.selectionStartPosition;
            this.selectionStartPosition = -1;

            // Don't change the cursor position when removing a selection region
            if (oldSelection >= 0)
            {
                return this;
            }
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

    public WidgetTextFieldBase setCursorToBeginning()
    {
        return this.setCursorPosition(0, false);
    }

    public WidgetTextFieldBase setCursorToEnd()
    {
        return this.setCursorPosition(this.text.length(), false);
    }

    protected WidgetTextFieldBase setCursorToBeginning(boolean selectText)
    {
        return this.setCursorPosition(0, selectText);
    }

    protected WidgetTextFieldBase setCursorToEnd(boolean selectText)
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

        String textLeftOfCursor = StringUtils.clampTextToRenderLength(this.visibleText.getText(), relX, LeftRight.RIGHT, "");
        return textLeftOfCursor.length() + this.visibleText.getStartIndex();
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

        if (this.inputValidator != null && this.inputValidator.canWriteCharacter(this.cursorPosition, this.text, typedChar, modifiers) == false)
        {
            return false;
        }

        return true;
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
            sb.append(this.text.substring(0, this.cursorPosition));
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

            int clickedIndex = this.getClickedTextIndex(mouseX, mouseY);
            boolean selectText = GuiBase.isShiftDown();
            this.setCursorPosition(clickedIndex, selectText);

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
            boolean selectText = GuiBase.isShiftDown();
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
            boolean selectText = GuiBase.isShiftDown();

            if (keyCode == Keyboard.KEY_BACK)
            {
                this.deleteSelectionOrCharacter(false);
            }
            else if (keyCode == Keyboard.KEY_DELETE)
            {
                this.deleteSelectionOrCharacter(true);
            }
            else if (keyCode == Keyboard.KEY_RETURN || keyCode == Keyboard.KEY_TAB)
            {
                // TODO call the listener
                return keyCode != Keyboard.KEY_TAB; // Don't cancel on tab input, to allow the container to handle focus change
            }
            else if (keyCode == Keyboard.KEY_LEFT)
            {
                if (GuiBase.isCtrlDown())
                {
                    this.moveCursorToEndOfWord(LeftRight.LEFT, GuiBase.isAltDown() == false, selectText);
                }
                else
                {
                    this.setCursorPosition(this.cursorPosition - 1, selectText);
                }
            }
            else if (keyCode == Keyboard.KEY_RIGHT)
            {
                if (GuiBase.isCtrlDown())
                {
                    this.moveCursorToEndOfWord(LeftRight.RIGHT, GuiBase.isAltDown() == false, selectText);
                }
                else
                {
                    this.setCursorPosition(this.cursorPosition + 1, selectText);
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

    protected void renderCursor(int x, int y, int color)
    {
        int relIndex = this.cursorPosition - this.visibleText.getStartIndex();
        color = this.selectionStartPosition != -1 ? 0xFF00D0FF : color;

        if (relIndex >= 0)
        {
            String visibleText = this.visibleText.getText();

            // The cursor is at the end of the text, use an underscore cursor
            if (this.cursorPosition == this.text.length())
            {
                int offX = this.getStringWidth(visibleText);
                this.drawString(x + offX, y + this.getCenteredTextOffsetY(), color, "_");
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

                RenderUtils.drawVerticalLine(x + offX, y1, cursorExtraHeight    , color  , this.getZLevel() + 0.1f);
                RenderUtils.drawVerticalLine(x + offX, y2, this.fontHeight      , colorTr, this.getZLevel() + 0.1f);
                RenderUtils.drawVerticalLine(x + offX, y3, cursorExtraHeight + 1, color  , this.getZLevel() + 0.1f);
            }
        }
    }

    protected void renderTextSegment(int x, int y, int startIndex, int textLength, int textColor, int backgroundColor)
    {
    }

    protected void renderVisibleText(int x, int y, int textColor)
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
                    this.drawString(x, y, textColor, str);
                    x += this.getStringWidth(str);
                }

                int p1 = Math.max(0     , selStart - start);
                int p2 = Math.min(visLen, selEnd - start);
                String str = visibleText.substring(p1, p2);
                int selWidth = this.getStringWidth(str);

                RenderUtils.drawRect(x, y - 2, selWidth, this.fontHeight + 3, textColor, this.getZLevel());
                this.drawString(x, y, 0xFF000000, str);
                x += selWidth;

                // Non-selected text at the start
                if (selEnd < end)
                {
                    str = visibleText.substring(selEnd - start, visLen);
                    this.drawString(x, y, textColor, str);
                }

                return;
            }
        }

        // No selection
        this.drawString(x, y, textColor, visibleText);
    }

    @Override
    public void render(int mouseX, int mouseY, boolean selected)
    {
        this.renderBorder();
        this.renderBackgroundOnly();

        int color = this.isFocused() ? this.colorFocused : this.colorUnfocused;
        int x = this.getX() + this.getTextStartRelativeX();
        int y = this.getY();

        if (this.text.isEmpty() == false)
        {
            this.renderVisibleText(x, y + this.getCenteredTextOffsetY(), color);
        }

        if (this.isFocused())
        {
            this.renderCursor(x, y, color);
        }
    }

    public interface IInputCharacterValidator
    {
        boolean canWriteCharacter(int index, String currentText, char typedChar, int modifiers);
    }
}
