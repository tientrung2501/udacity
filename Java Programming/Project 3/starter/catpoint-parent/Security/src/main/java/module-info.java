module com.udacity.catpoint.security {
    opens com.udacity.catpoint.security.data to com.google.gson;
    requires miglayout;
    requires com.google.gson;
    requires com.google.common;
    requires java.sql;
    requires com.udacity.catpoint.image;
    requires java.desktop;
    requires java.prefs;
}