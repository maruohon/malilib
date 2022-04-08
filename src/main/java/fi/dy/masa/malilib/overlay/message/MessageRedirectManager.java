package fi.dy.masa.malilib.overlay.message;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fi.dy.masa.malilib.MaLiLibConfigs;
import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.config.util.ConfigUtils;
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
        File dir = ConfigUtils.getActiveConfigDirectory();
        File backupDir = new File(dir, "config_backups");
        File saveFile = new File(dir, MaLiLibReference.MOD_ID + "_message_redirects.json");
        boolean antiDuplicate = MaLiLibConfigs.Generic.CONFIG_BACKUP_ANTI_DUPLICATE.getBooleanValue();

        this.dirty = false;

        return JsonUtils.saveToFile(dir, backupDir, saveFile, 10, antiDuplicate, this::toJson);
    }

    public void loadFromFile()
    {
        File dir = ConfigUtils.getActiveConfigDirectory();
        JsonUtils.loadFromFile(dir, MaLiLibReference.MOD_ID + "_message_redirects.json", this::fromJson);
    }
}
