package fi.dy.masa.malilib.config.value;

import com.google.common.collect.ImmutableList;

public class InfoType extends BaseOptionListConfigValue
{
    public static final InfoType MESSAGE_OVERLAY = new InfoType("message", "malilib.label.info_type.message");
    public static final InfoType HOTBAR          = new InfoType("hotbar",  "malilib.label.info_type.hotbar");
    public static final InfoType CHAT            = new InfoType("chat",    "malilib.label.info_type.chat");
    public static final InfoType NONE            = new InfoType("none",    "malilib.label.info_type.none");

    public static final ImmutableList<InfoType> VALUES = ImmutableList.of(MESSAGE_OVERLAY, HOTBAR, CHAT, NONE);

    private InfoType(String name, String translationKey)
    {
        super(name, translationKey);
    }
}
