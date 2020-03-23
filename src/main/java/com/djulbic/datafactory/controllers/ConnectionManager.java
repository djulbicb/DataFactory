package com.djulbic.datafactory.controllers;

import com.djulbic.datafactory.model.DbConnection;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeCreator;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import resources.FileUtility;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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

    public void saveConnection(DbConnection dbConnection) throws IOException, ParseException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        List<DbConnection> connections = getConnections();
        connections.add(dbConnection);

        String storagePath = environment.getProperty("STORAGE_PATH");
        String dbConnections = environment.getProperty("DB_CONNECTIONS");
        String json = gson.toJson(connections);

        FileWriter writer = new FileWriter(storagePath + dbConnections);
        writer.write(json);
        writer.close();
    }

    public List<DbConnection> getConnections() throws IOException, ParseException {
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

            List<DbConnection> users = new ObjectMapper().readValue(content, new TypeReference<List<DbConnection>>() {});
            return users;

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
}
