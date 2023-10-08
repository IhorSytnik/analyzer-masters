package ua.kpi.analyzer.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.kpi.analyzer.exceptions.WrongFormatSpecialtyException;

import java.util.Set;
import java.util.regex.Pattern;

/**
 * @author Ihor Sytnik
 */
@Service
public class SpecialtiesService {

    @Autowired
    private Set<String> specialtiesToCheckFor;

    public boolean addSpecialty(String specialty) throws WrongFormatSpecialtyException {
        if (!Pattern.compile("\\d{3}").asMatchPredicate().test(specialty)) {
            throw new WrongFormatSpecialtyException("Specialty number should be a 3-digit code.");
        }

        return specialtiesToCheckFor.add(specialty);
    }

    public boolean removeSpecialty(String specialty) throws WrongFormatSpecialtyException {
        if (!Pattern.compile("\\d{3}").asMatchPredicate().test(specialty)) {
            throw new WrongFormatSpecialtyException("Specialty number should be a 3-digit code.");
        }

        return specialtiesToCheckFor.remove(specialty);
    }

    public Set<String> getSpecialties() {
        return specialtiesToCheckFor;
    }
}
