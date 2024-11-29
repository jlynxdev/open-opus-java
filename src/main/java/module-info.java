module dev.jlynx.openopusjava {
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;
    requires java.net.http;
    requires org.slf4j;

    exports dev.jlynx.openopusjava;
    exports dev.jlynx.openopusjava.request;
    exports dev.jlynx.openopusjava.response.body;
    exports dev.jlynx.openopusjava.response.subtype;
    exports dev.jlynx.openopusjava.exception;

    opens dev.jlynx.openopusjava.response.subtype;
    opens dev.jlynx.openopusjava.response.body;
}