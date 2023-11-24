package ua.kpi.analyzer.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ua.kpi.analyzer.Processor;
import ua.kpi.analyzer.entities.Author;
import ua.kpi.analyzer.exceptions.WrongFormatSpecialtyException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * @author Ihor Sytnik
 */
@Service
public class FileProcessingService {
    @Autowired
    private Processor processor;
    @Autowired
    private Set<String> specialtiesToCheckFor;

    public void process(InputStream file, Set<String> specialties) throws IOException {
        if (specialties == null || specialties.isEmpty()) {
            processor.process(file, specialtiesToCheckFor);
            return;
        }
        if (specialties.stream().anyMatch(s -> !Pattern.compile("\\d{3}").asMatchPredicate().test(s))) {
            throw new WrongFormatSpecialtyException("Specialty number should be a 3-digit code.");
        }
        processor.process(file, specialties);
    }

    public Author getAuthor() {
        return processor.getAuthor();
    }
}
