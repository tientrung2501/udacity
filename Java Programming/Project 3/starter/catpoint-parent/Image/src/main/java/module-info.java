module com.udacity.catpoint.image {
    exports com.udacity.catpoint.image.service;
    requires software.amazon.awssdk.core;
    requires software.amazon.awssdk.services.rekognition;
    requires software.amazon.awssdk.regions;
    requires software.amazon.awssdk.auth;
    requires org.slf4j;
    requires java.desktop;
}