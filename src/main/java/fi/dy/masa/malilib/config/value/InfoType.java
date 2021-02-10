package fi.dy.masa.malilib.config.value;

import com.google.common.collect.ImmutableList;

public class InfoType extends BaseOptionListConfigValue
{
    public static final InfoType MESSAGE_OVERLAY = new InfoType("message",        "malilib.label.info_type.message");
    public static final InfoType CUSTOM_HOTBAR   = new InfoType("custom_hotbar",  "malilib.label.info_type.hotbar.custom");
    public static final InfoType VANILLA_HOTBAR  = new InfoType("vanilla_hotbar", "malilib.label.info_type.hotbar.vanilla");
    public static final InfoType CHAT            = new InfoType("chat",           "malilib.label.info_type.chat");
    public static final InfoType NONE            = new InfoType("none",           "malilib.label.info_type.none");

    public static final ImmutableList<InfoType> VALUES = ImmutableList.of(MESSAGE_OVERLAY, CUSTOM_HOTBAR, VANILLA_HOTBAR, CHAT, NONE);

    private InfoType(String name, String translationKey)
    {
        super(name, translationKey);
    }
}
