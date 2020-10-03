package com.djulb.datafactory.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class PrepareJarForDocker {
    public static void main(String[] args) throws IOException {
        writeDockerfile(args);
        copyFiles();
    }

    private static void copyFiles() throws IOException {
        File sourceReadme = new File("README.md");
        File destReadme = new File("src/main/resources/README.md");
        Files.copy(sourceReadme.toPath(), destReadme.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    private static void writeDockerfile(String[] args) throws IOException {
        StringBuilder sb = new StringBuilder();

        String projectName = args[0];
        String version = args[1];

        File file = new File("development/DockerfileTemplate");
        FileReader reader = new FileReader(file.getAbsoluteFile());

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line+"\n");
            }
        } catch (Exception e) {

        }

        String content = sb.toString().replaceAll("\\$\\{fileName\\}", projectName+ "-" + version);

        File dockerfile = new File("target/dockerfile/Dockerfile");
        dockerfile.getParentFile().mkdirs();
        FileWriter fw = new FileWriter(dockerfile);
        fw.write(content);
        fw.close();
    }
}
