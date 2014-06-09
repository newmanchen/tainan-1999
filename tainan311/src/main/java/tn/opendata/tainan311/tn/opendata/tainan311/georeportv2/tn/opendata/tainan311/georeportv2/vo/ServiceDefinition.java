package tn.opendata.tainan311.tn.opendata.tainan311.georeportv2.tn.opendata.tainan311.georeportv2.vo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by vincent on 2014/6/6.
 */
public class ServiceDefinition {

    private String service_code;
    private ArrayList<Attribute> attributes;

    private static class Attribute {
        private String variable;
        private String code;
        private String datatype;
        private String required;
        private String datatype_description;
        private String order;
        private String description;
        private ArrayList<KeyName> values;

        public Attribute() {
        }
    }

    private static class KeyName {
        private String key;
        private String name;
    }

    public ServiceDefinition() {

    }

    public String getServiceCode() { return service_code; }

    public List<Attribute> getAttribute() { return attributes; }

    @Override
    public String toString() {
        return service_code + "@";
    }
}
