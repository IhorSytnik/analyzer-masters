package ua.kpi.analyzer.entities;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @author Ihor Sytnik
 */
@Getter
@Setter
@AllArgsConstructor
@JsonDeserialize(using = Work.WorkDeserializer.class)
public class Work {
    private String title;
    private String doi;
    private String eid;
    private List<String> isbn;
//    private String dcIdentifier;
    private Date date;
    private Publication publisher;

    public static class WorkDeserializer extends StdDeserializer<Work> {

        private WorkDeserializer(Class<?> vc) {
            super(vc);
        }

        public WorkDeserializer() {
            this(null);
        }

        @Override
        public Work deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JsonNode node = mapper.readTree(p);

            String title = node.get("dc:title") == null ? null : node.get("dc:title").textValue();
            String doi = node.get("prism:doi") == null ? null : node.get("prism:doi").textValue();
            String eid = node.get("eid") == null ? null : node.get("eid").textValue();
//            String dcIdentifier = node.get("dc:identifier") == null ? null : node.get("dc:identifier").textValue();

            List<String> isbn = new ArrayList<>();
            JsonNode isbnNode = node.get("prism:isbn");
            if (isbnNode != null) {
                for (var isbnEntry : isbnNode) {
                    isbn.add(isbnEntry.get("$").textValue());
                }
            }

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            Date date = null;
            try {
                date = formatter.parse(node.get("prism:coverDate").textValue());
            } catch (ParseException e) {
                formatter = new SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH);
                try {
                    date = formatter.parse(node.get("prism:coverDisplayDate").textValue());
                } catch (ParseException ex) {
                    ex.printStackTrace();
                }
            }

            Publication publisher = new ObjectMapper().readValue(node.traverse(), Publication.class);

            return new Work(title, doi, eid, isbn, /*dcIdentifier,*/ date, publisher);
        }
    }
}
