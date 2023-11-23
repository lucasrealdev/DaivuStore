module com.example.sprintjava {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;
    requires mysql.connector.java;
    requires jbcrypt;
    requires com.auth0.jwt;
    requires javax.mail;
    requires google.api.client;
    requires com.google.api.client.auth;
    requires com.google.api.client;
    requires com.google.api.client.json.jackson2;
    requires com.google.api.client.extensions.java6.auth;
    requires com.google.api.client.extensions.jetty.auth;
    requires jdk.httpserver;
    requires com.google.gson;

    opens com.example.sprintjava to javafx.fxml;
    exports com.example.sprintjava;
}