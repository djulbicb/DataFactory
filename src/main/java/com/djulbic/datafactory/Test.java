package com.djulbic.datafactory;

import com.djulbic.datafactory.util.Utils;
import org.markdown4j.Markdown4jProcessor;

import java.io.File;
import java.io.IOException;

public class Test {
    public static void main(String[] args) throws IOException {
        String content = Utils.readLineByLineJava8(new File("README.md").getAbsolutePath());
        String html = new Markdown4jProcessor().process(content);
        System.out.println(html);
    }
}
