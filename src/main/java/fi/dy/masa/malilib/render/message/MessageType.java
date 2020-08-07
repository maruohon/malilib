package fi.dy.masa.malilib.render.message;

import fi.dy.masa.malilib.util.StringUtils;

public enum MessageType
{
    INFO        ("malilib.message.formatting_code.info"),
    SUCCESS     ("malilib.message.formatting_code.success"),
    WARNING     ("malilib.message.formatting_code.warning"),
    ERROR       ("malilib.message.formatting_code.error");

    private final String translationKey;

    MessageType(String translationKey)
    {
        this.translationKey = translationKey;
    }

    public String getFormatting()
    {
        return StringUtils.translate(this.translationKey);
    }
}
