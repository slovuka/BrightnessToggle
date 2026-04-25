package dev.slovuka.classes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import static dev.slovuka.BrightnessToggle.MOD_ID;

public class DataClass {
    public static class Data {
        public int brightnessValue = 1000;
        public double oldGamma = 1.0;
        public boolean gammaEnabled = false;
    }

    public static DataClass.Data configData = new DataClass.Data();
    private static final File FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), MOD_ID + ".json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void load() {
        if (FILE.exists()) {
            try (FileReader reader = new FileReader(FILE)) {
                configData = GSON.fromJson(reader, DataClass.Data.class);
            } catch (Exception ignored) {}
        } else {
            save();
        }
    }

    public static void save() {
        try (FileWriter writer = new FileWriter(FILE)) {
            GSON.toJson(configData, writer);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
