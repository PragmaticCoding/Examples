module ca.pragmaticcoding.examples {
    requires javafx.controls;
    requires javafx.fxml;
    requires kotlin.stdlib;


    opens ca.pragmaticcoding.examples to javafx.fxml;
    exports ca.pragmaticcoding.examples;
    exports ca.pragmaticcoding.examples.hexeditor;
}