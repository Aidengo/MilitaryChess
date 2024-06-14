package hundun.militarychess.lwjgl3;

import java.io.IOException;

import com.badlogic.gdx.Gdx;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import hundun.gdxgame.corelib.base.save.AbstractSaveDataSaveTool;


public class PreferencesSaveTool extends AbstractSaveDataSaveTool<Void> {

    private final ObjectMapper objectMapper;

    public PreferencesSaveTool(String preferencesName) {
        super(preferencesName);
        this.objectMapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);
    }
    @Override
    public void lazyInitOnGameCreate() {
        this.preferences = Gdx.app.getPreferences(preferencesName);
    }

    @Override
    public void writeRootSaveData(Void saveData) {
        try {
            preferences.putString(ROOT_KEY, objectMapper.writeValueAsString(saveData));
            preferences.flush();
            Gdx.app.log(getClass().getSimpleName(), "save() done");
        } catch (Exception e) {
            Gdx.app.error(getClass().getSimpleName(), "save() error", e);
        }
    }

    @Override
    public Void readRootSaveData() {
        try {
            String date = preferences.getString(ROOT_KEY);
            Void saveData = objectMapper.readValue(date, Void.class);
            return saveData;
        } catch (IOException e) {
            Gdx.app.error(getClass().getSimpleName(), "load() error", e);
            return null;
        }
    }
}
