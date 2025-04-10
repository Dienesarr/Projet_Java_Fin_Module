module com.example.examen_javal3 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.java;
    requires org.jfree.jfreechart;
    requires itextpdf;
    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;
    // requires de.jensd.fx.glyphs.fontawesome;

    opens com.example.examen_javal3 to javafx.fxml;
    exports com.example.examen_javal3;
    opens com.example.examen_javal3.Model to javafx.base;
    exports com.example.examen_javal3.Controller;
    opens com.example.examen_javal3.Controller to javafx.fxml;
}
