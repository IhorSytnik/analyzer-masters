package ua.kpi.analyzer.views.sectors;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.MultiFileReceiver;
import com.vaadin.flow.component.upload.Receiver;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.annotation.Autowired;
import ua.kpi.analyzer.Processor;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

@SpringComponent
public class UploadArea extends VerticalLayout {

    private final Upload uploadField;
    private final Span errorField;
    @Autowired
    private ScanResults scanResults;
    @Autowired
    private Processor processor;

    public UploadArea() {
        uploadField = new Upload(createFileReceiver());
        uploadField.getStyle().set("background-color", "#ffffff");
        uploadField.setAcceptedFileTypes(".docx");
        uploadField.setMaxFiles(1);
        uploadField.addSucceededListener(event -> {
            scanResults.setVisibleToResults(false);
            scanResults.setVisibleToProcessing(true);
        });

        errorField = new Span();
        errorField.setVisible(false);
        errorField.getStyle().set("color", "red");

        uploadField.addFailedListener(e -> showErrorMessage(e.getReason().getMessage()));
        uploadField.addFileRejectedListener(e -> showErrorMessage(e.getErrorMessage()));

        add(uploadField, errorField);
    }

    public Upload getUploadField() {
        return uploadField;
    }

    public void hideErrorField() {
        errorField.setVisible(false);
    }

    private Receiver createFileReceiver() {
        return (MultiFileReceiver) (filename, mimeType) -> {
            PipedOutputStream out = new PipedOutputStream();

            try {
                PipedInputStream in = new PipedInputStream(out);
                new Thread(() -> {
                    try {
                        processor.process(in);

                        scanResults.initializeResults(processor.getAuthor());
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            return out;
        };
    }

    private void showErrorMessage(String message) {
        errorField.setVisible(true);
        errorField.setText(message);
    }

    class MyPiped extends PipedInputStream {

        public MyPiped(PipedOutputStream src) throws IOException {
            super(src);
        }


    }
}
