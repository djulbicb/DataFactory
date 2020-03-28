package com.djulbic.datafactory.controllers;

import com.djulbic.datafactory.model.DbConnection;
import com.djulbic.datafactory.model.ExecuteRequestDTO;
import com.djulbic.datafactory.model.ExecuteRequestPreset;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import resources.FileUtility;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Component
public class ConnectionManager {

    @Autowired
    Environment environment;

    public void saveDatabaseConnectionPreset(DbConnection dbConnection) throws IOException, ParseException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        List<DbConnection> connections = getDatabaseConnectionPresets();
        connections.add(dbConnection);

        String storagePath = environment.getProperty("STORAGE_PATH");
        String dbConnections = environment.getProperty("DB_CONNECTIONS");
        String json = gson.toJson(connections);

        FileWriter writer = new FileWriter(storagePath + dbConnections);
        writer.write(json);
        writer.close();
    }

    public List<DbConnection> getDatabaseConnectionPresets() throws IOException, ParseException {
        String storagePath = environment.getProperty("STORAGE_PATH");
        String dbConnections = environment.getProperty("DB_CONNECTIONS");
        String filePath = storagePath + dbConnections;
        File file = new File(filePath);

        ArrayList<DbConnection> list = new ArrayList<>();

        if (file.exists()){
            JSONParser parser = new JSONParser();
            FileReader fileReader = new FileReader(filePath);

            FileUtility utility = new FileUtility();
            String content = readLineByLineJava8(filePath);
            if (content.isEmpty()){
                return list;
            }

            List<DbConnection> connections = new ObjectMapper().readValue(content, new TypeReference<List<DbConnection>>() {});
            list.addAll(connections);

        } else{
            File f = new File(storagePath + dbConnections);
            f.createNewFile();
        }

        return list;
    }

    private static String readLineByLineJava8(String filePath)
    {
        StringBuilder contentBuilder = new StringBuilder();
        try (Stream<String> stream = Files.lines( Paths.get(filePath), StandardCharsets.UTF_8))
        {
            stream.forEach(s -> contentBuilder.append(s).append("\n"));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return contentBuilder.toString();
    }

    public List<ExecuteRequestPreset> getDatabaseRequestConfigPreset(ExecuteRequestDTO request) throws IOException {
        ArrayList<ExecuteRequestPreset> list = new ArrayList<>();
        File file = getPresetsFile(request);

        if (file.exists()){
            JSONParser parser = new JSONParser();
            String content = readLineByLineJava8(file.getAbsolutePath());
            if (content.isEmpty()){
                return list;
            }

            List<ExecuteRequestPreset> connections = new ObjectMapper().readValue(content, new TypeReference<List<ExecuteRequestPreset>>() {});
            list.addAll(connections);
        } else{
            file.createNewFile();
        }

        return list;
    }

    public void addDatabaseRequestConfigPreset(ExecuteRequestPreset preset) throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        List<ExecuteRequestPreset> presets = getDatabaseRequestConfigPreset(preset.getRequest());
        presets.add(preset);

        String json = gson.toJson(presets);

        File presetsFile = getPresetsFile(preset.getRequest());
        FileWriter writer = new FileWriter(presetsFile);
        writer.write(json);
        writer.close();
    }

    public void removeDatabaseRequestConfigPreset(ExecuteRequestPreset preset) throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        List<ExecuteRequestPreset> presets = getDatabaseRequestConfigPreset(preset.getRequest());
        presets.remove(preset);

        String json = gson.toJson(presets);

        File presetsFile = getPresetsFile(preset.getRequest());
        FileWriter writer = new FileWriter(presetsFile);
        writer.write(json);
        writer.close();
    }

    public List<String> getDatabaseRequestConfigPresetAsStringList(ExecuteRequestDTO request) throws IOException {
        List<ExecuteRequestPreset> presets = getDatabaseRequestConfigPreset(request);
        List<String> presetsName = new ArrayList<>();
        for (ExecuteRequestPreset preset : presets) {
            presetsName.add(preset.getPresetName());
        }
        return presetsName;
    }

    public File getPresetsFile(ExecuteRequestDTO request) throws IOException {
        return getPresetsFile(request.getConfig().getDatabaseName());
    }

    public File getPresetsFile(String dbName) throws IOException {
        String storagePath = environment.getProperty("STORAGE_PATH");
        String presetPath = environment.getProperty("PRESETS_FOLDER");
        File file = new File(storagePath + presetPath + dbName + ".json");
        return file;
    }

    public ExecuteRequestPreset getDatabaseRequestConfigPresetByPresetName(ExecuteRequestPreset request) throws IOException {
        List<ExecuteRequestPreset> presets = getDatabaseRequestConfigPreset(request.getRequest());
        for (ExecuteRequestPreset preset : presets) {
            if (preset.equals(request)){
                return preset;
            }
        }
        throw new IllegalArgumentException(String.format("Cant find preset %s for database %s.", request.getPresetName(), request.getRequest().getConfig().getDatabaseName()));
    }
}
