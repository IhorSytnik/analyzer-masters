package ua.kpi.analyzer.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.kpi.analyzer.Processor;
import ua.kpi.analyzer.entities.Author;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * @author Ihor Sytnik
 */
@Service
public class FileProcessingService {
    @Autowired
    private Processor processor;

    public void process(String filename) throws IOException, InterruptedException {
        processor.process(new FileInputStream(filename));
    }

    public Author getAuthor() {
        return processor.getAuthor();
    }
}
