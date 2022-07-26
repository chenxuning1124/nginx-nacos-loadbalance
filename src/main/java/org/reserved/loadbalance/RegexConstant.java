package org.reserved.loadbalance;

/**
 * @author chenxuning
 */
public interface RegexConstant {
    String PROPERTY_BEAN_NAME="propertyUtil";

    String UPSTREAM_NAME = "#[upstream_name]#";
    String UPSTREAM_REGEX = "upstream\\s*" + UPSTREAM_NAME + "\\s*\\{[^}]*\\}";

    String PLACEHOLDER_SERVER = "#[upstream_server_name]#";
    String UPSTREAM_FORMAT = "upstream " + UPSTREAM_NAME + " {\n" + PLACEHOLDER_SERVER + "}";
}
