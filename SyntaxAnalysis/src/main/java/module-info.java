module com.example.syntaxanalysis {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;

    opens com.example.syntaxanalysis to javafx.fxml;
    exports com.example.syntaxanalysis;
}