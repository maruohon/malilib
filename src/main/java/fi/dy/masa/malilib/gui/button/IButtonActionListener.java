package fi.dy.masa.malilib.gui.button;

public interface IButtonActionListener<T extends ButtonBase>
{
    void actionPerformed(T control);

    void actionPerformedWithButton(T control, int mouseButton);
}
