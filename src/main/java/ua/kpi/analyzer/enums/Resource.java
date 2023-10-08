package ua.kpi.analyzer.enums;

import lombok.Getter;

import java.util.*;
import java.util.regex.Pattern;

/**
 * @author Ihor Sytnik
 */
@Getter
public enum Resource {
    SCOPUS("Scopus"),
    WEB_OF_SCIENCE("Web Of Science"),
    ORCID("Orcid"),
    RESEARCHERID("ResearcherID"),
    OTHER;

    Resource(String... names) {
        nameList.addAll(Arrays.asList(names));
    }

    private final List<String> nameList = new ArrayList<>();
    private static final Map<String, Resource> map;

    static {
        map = new HashMap<>();
        for (var v : Resource.values()) {
            for (var n : v.nameList) {
                map.put(n, v);
            }
        }
    }

    public String getCommonName() {
        return nameList.get(0);
    }

    public static Resource getSimilar(String str) {
        Pattern resourcePattern;

        for (var resEntry : map.entrySet()) {
            resourcePattern = Pattern.compile(
                    "(.+\\s*)*[^A-Za-z0-9]*" + resEntry.getKey() + "[^A-Za-z0-9]*(\\s*.+)*",
                    Pattern.CASE_INSENSITIVE);
            if (resourcePattern.asMatchPredicate().test(str)) {
                return resEntry.getValue();
            }
        }

        return OTHER;
    }

    public static Resource getByString(String str) {
        return map.get(str);
    }

}
