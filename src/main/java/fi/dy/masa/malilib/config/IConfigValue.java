package fi.dy.masa.malilib.config;

public interface IConfigValue extends IConfigBase, IStringRepresentable
{
    void onValueChanged();

    void setValueChangeCallback(IConfigValueChangeCallback callback);
}
