package fi.dy.masa.malilib.gui.widget.util;

import javax.annotation.Nullable;

public interface TextFieldValidator
{
    /**
     * Checks if the given string is a valid input for this input type.
     * @param text
     * @return
     */
    boolean isValidInput(String text);

    /**
     * Returns an optional error message to be displayed when
     * the given input is not a valid value.
     * @return The optional localized error message to be displayed.
     */
    @Nullable default String getErrorMessage(String text)
    {
        return null;
    }
}
