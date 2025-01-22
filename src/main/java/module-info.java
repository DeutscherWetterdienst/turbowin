module turbowin {
    requires transitive java.desktop;
    requires transitive java.logging;
    requires com.fazecast.jSerialComm;
    requires jakarta.activation;
    requires jakarta.mail;

    exports turbowin;
}
