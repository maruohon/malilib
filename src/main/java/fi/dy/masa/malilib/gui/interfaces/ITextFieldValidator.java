package fi.dy.masa.malilib.gui.interfaces;

public interface ITextFieldValidator
{
    /**
     * Checks if the given string is a valid input for this input type.
     * @param text
     * @return
     */
    boolean isValidInput(String text);
}
