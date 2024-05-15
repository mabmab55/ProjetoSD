module com.mabmab.sistemasdistribuidos {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires org.json;


    opens com.mabmab.sistemasdistribuidos to javafx.fxml;
    exports com.mabmab.sistemasdistribuidos;
}