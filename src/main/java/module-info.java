module nclan.ac.gameshopapp {

    requires javafx.controls;

    requires java.net.http;

    requires com.google.gson;

    exports nclan.ac.gameshopapp;
    exports nclan.ac.gameshopapp.app;
    exports nclan.ac.gameshopapp.database;
    exports nclan.ac.gameshopapp.enums;
    exports nclan.ac.gameshopapp.module;
    exports nclan.ac.gameshopapp.service;
    exports nclan.ac.gameshopapp.ui;
}