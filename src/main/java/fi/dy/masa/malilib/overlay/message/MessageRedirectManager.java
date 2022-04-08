package fi.dy.masa.malilib.overlay.message;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.config.util.ConfigUtils;
import fi.dy.masa.malilib.util.BackupUtils;
import fi.dy.masa.malilib.util.data.json.JsonUtils;

public class MessageRedirectManager
{
    protected final Map<String, MessageRedirect> messageRedirects = new HashMap<>();
    protected boolean dirty;

    public static class MessageRedirect
    {
        protected final String translationKey;
        protected final MessageOutput output;

        public MessageRedirect(String translationKey, MessageOutput output)
        {
            this.translationKey = translationKey;
            this.output = output;
        }

        public String getMessageTranslationKey()
        {
            return this.translationKey;
        }

        public MessageOutput getOutput()
        {
            return this.output;
        }
    }

    public MessageOutput getRedirectedMessageOutput(String translationKey, MessageOutput originalOutput)
    {
        MessageRedirect redirect = this.messageRedirects.get(translationKey);

        if (redirect != null)
        {
            return redirect.output;
        }

        return originalOutput;
    }

    public void addRedirect(String translationKey, MessageRedirect redirect)
    {
        this.messageRedirects.put(translationKey, redirect);
        this.dirty = true;
    }

    public void removeRedirect(String translationKey)
    {
        this.messageRedirects.remove(translationKey);
        this.dirty = true;
    }

    public List<MessageRedirect> getAllRedirects()
    {
        List<MessageRedirect> list = new ArrayList<>(this.messageRedirects.values());
        list.sort(Comparator.comparing(MessageRedirect::getMessageTranslationKey));
        return list;
    }

    protected JsonObject toJson()
    {
        JsonObject obj = new JsonObject();

        for (MessageRedirect redirect : this.getAllRedirects())
        {
            obj.addProperty(redirect.getMessageTranslationKey(), redirect.getOutput().getName());
        }

        return obj;
    }

    protected void fromJson(JsonElement el)
    {
        if (el.isJsonObject() == false)
        {
            return;
        }

        this.messageRedirects.clear();

        JsonObject obj = el.getAsJsonObject();

        for (Map.Entry<String, JsonElement> entry : obj.entrySet())
        {
            MessageOutput output = MessageOutput.getByName(entry.getValue().getAsString());

            if (output != null)
            {
                String translationKey = entry.getKey();
                this.messageRedirects.put(translationKey, new MessageRedirect(translationKey, output));
            }
        }
    }

    public boolean saveToFileIfDirty()
    {
        if (this.dirty)
        {
            return this.saveToFile();
        }

        return false;
    }

    public boolean saveToFile()
    {
        Path configDir = ConfigUtils.getActiveConfigDirectoryPath();
        File saveFile = configDir.resolve(MaLiLibReference.MOD_ID).resolve("message_redirects.json").toFile();
        File backupDir = configDir.resolve("backups").resolve(MaLiLibReference.MOD_ID).toFile();

        if (BackupUtils.createRegularBackup(saveFile, backupDir) &&
            JsonUtils.writeJsonToFile(this.toJson(), saveFile))
        {
            this.dirty = false;
            return true;
        }

        return false;
    }

    public void loadFromFile()
    {
        Path configDir = ConfigUtils.getActiveConfigDirectoryPath();
        File saveFile = configDir.resolve(MaLiLibReference.MOD_ID).resolve("message_redirects.json").toFile();
        JsonUtils.loadFromFile(saveFile, this::fromJson);
    }
}
