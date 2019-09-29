package com.haiyunshan.pudding.compose.note;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;
import com.haiyunshan.pudding.dataset.BaseEntry;
import com.haiyunshan.pudding.dataset.FileStorage;
import com.haiyunshan.pudding.utils.FileHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Note extends BaseEntry {

    @SerializedName("body")
    ArrayList<BaseEntity> mBody;

    @SerializedName("source")
    String mSource; // uri格式

    Note(String id) {
        super(id);

        this.mBody = new ArrayList<>();
    }

    public String getId() {
        return mId;
    }

    public List<BaseEntity> getBody() {
        return mBody;
    }

    public void clear() {
        mBody.clear();
    }

    public void add(BaseEntity entity) {
        mBody.add(entity);
    }

    public String getSource() {
        if (mSource == null) {
            return "";
        }

        return mSource;
    }

    public void setSource(String path) {
        if (path.indexOf(':') < 0) {
            path = "file://" + path;
        }

        this.mSource = path;
    }

    public String[] getTitle() {
        String title = null;
        String subtitle = null;

        List<BaseEntity> list = this.getBody();
        for (BaseEntity item : list) {
            if (!(item instanceof ParagraphEntity)) {
                continue;
            }

            String text = ((ParagraphEntity)item).getText();
            if (text.length() > 200) {
                text = text.substring(0, 200);
            }

            String[] array = text.split("\n");
            for (String str : array) {
                str = str.trim();
                if (str.isEmpty()) {
                    continue;
                }

                if (title == null) {
                    title = str;
                } else if (subtitle == null) {
                    subtitle = str;
                }

                if (subtitle != null) {
                    break;
                }
            }

            if (subtitle != null) {
                break;
            }
        }

        return new String[] { title, subtitle };
    }

    public void save() {
        File folder = FileStorage.getNote(this.mId);
        folder.mkdirs();

        File file = new File(folder, "note.json");

        try {
            file.delete();
            file.createNewFile();

            FileOutputStream fos = new FileOutputStream(file);
            OutputStreamWriter writer = new OutputStreamWriter(fos, "utf-8");

            Gson gson = new Gson();
            gson.toJson(this, writer);

            writer.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static final boolean exist(String id) {
        File folder = FileStorage.getNote(id);
        return folder.exists();
    }

    public static final void remove(String id) throws IOException {
        File folder = FileStorage.getNote(id);
        if (folder.exists()) {
            FileHelper.forceDelete(folder);
        }
    }

    public static final Note create(String id) {
        File folder = FileStorage.getNote(id);
        if (!folder.exists()) {
            return new Note(id);
        }

        File file = new File(folder, "note.json");
        if (!file.exists()) {
            return new Note(id);
        }

        Note note = null;

        try {
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader reader = new InputStreamReader(fis, "utf-8");

            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(BaseEntity.class, new EntityDeserializer())
                    .create();
            note = gson.fromJson(reader, Note.class);

            reader.close();
            fis.close();
        } catch (Exception e) {

        }

        if (note == null) {
            note = new Note(id);
        }

        return note;
    }

    private static class EntityDeserializer implements JsonDeserializer<BaseEntity> {

        Gson mGson;

        EntityDeserializer() {
            this.mGson = new Gson();
        }

        @Override
        public BaseEntity deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            BaseEntity en = null;

            String name = jsonObject.get("type").getAsString();
            switch (name) {
                case ParagraphEntity.TYPE: {
                    en = mGson.fromJson(json, ParagraphEntity.class);
                    break;
                }
                case PictureEntity.TYPE: {
                    en = mGson.fromJson(json, PictureEntity.class);
                    break;
                }
                default: {
                    break;
                }
            }

            if (en == null) {
                throw new IllegalArgumentException("Entity not found: " + jsonObject.toString());
            }

            return en;
        }
    }
}
