package malilib.util.data;

import malilib.util.StringUtils;

public enum RunStatus
{
    STOPPED     ("malilibdev.name.run_status.stopped",  "malilibdev.name.run_status.stopped.colored"),
    PAUSED      ("malilibdev.name.run_status.paused",   "malilibdev.name.run_status.paused.colored"),
    RUNNING     ("malilibdev.name.run_status.running",  "malilibdev.name.run_status.running.colored"),
    FINISHED    ("malilibdev.name.run_status.finished", "malilibdev.name.run_status.finished.colored"),
    ABORTED     ("malilibdev.name.run_status.aborted",  "malilibdev.name.run_status.aborted.colored");

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
