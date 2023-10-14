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

    public void addSpecialties(Set<String> specialties) throws WrongFormatSpecialtyException {
        if (specialties.stream().anyMatch(s -> !Pattern.compile("\\d{3}").asMatchPredicate().test(s))) {
            throw new WrongFormatSpecialtyException("Specialty number should be a 3-digit code.");
        }

        specialtiesToCheckFor.addAll(specialties);
    }

    public void removeSpecialties(Set<String> specialties) throws WrongFormatSpecialtyException {
        if (specialties.stream().anyMatch(s -> !Pattern.compile("\\d{3}").asMatchPredicate().test(s))) {
            throw new WrongFormatSpecialtyException("Specialty number should be a 3-digit code.");
        }

        specialtiesToCheckFor.removeAll(specialties);
    }

    public Set<String> getSpecialties() {
        return specialtiesToCheckFor;
    }
}
