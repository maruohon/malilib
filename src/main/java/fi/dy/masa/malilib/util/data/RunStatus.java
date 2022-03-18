package fi.dy.masa.malilib.util.data;

import fi.dy.masa.malilib.util.StringUtils;

public enum RunStatus
{
    STOPPED     ("malilib.name.run_status.stopped",  "malilib.name.run_status.stopped.colored"),
    PAUSED      ("malilib.name.run_status.paused",   "malilib.name.run_status.paused.colored"),
    RUNNING     ("malilib.name.run_status.running",  "malilib.name.run_status.running.colored"),
    FINISHED    ("malilib.name.run_status.finished", "malilib.name.run_status.finished.colored"),
    ABORTED     ("malilib.name.run_status.aborted",  "malilib.name.run_status.aborted.colored");

    private final String translationKey;
    private final String coloredTranslationKey;

    RunStatus(String translationKey, String coloredTranslationKey)
    {
        this.translationKey = translationKey;
        this.coloredTranslationKey = coloredTranslationKey;
    }

    public String getDisplayName()
    {
        return StringUtils.translate(this.translationKey);
    }

    public String getColoredDisplayName()
    {
        return StringUtils.translate(this.coloredTranslationKey);
    }
}
