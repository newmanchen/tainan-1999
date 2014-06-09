package tn.opendata.tainan311.tn.opendata.tainan311.georeportv2.tn.opendata.tainan311.georeportv2.vo;

/**
 * Created by vincent on 2014/6/6.
 */
public class Service {
    private String service_code;
    private String service_name;
    private String description;
    private String metadata;
    private String type;
    private String keyword;
    private String group;

    public Service(){
    }

    public String getServiceCode() { return service_code; }

    public String getServiceName() { return service_name; }

    public String getDescription() { return description; }

    public boolean hasMetadata() { return "TRUE".equalsIgnoreCase(metadata); }

    public String getType() { return type; }

    public String getKeyword() { return keyword; }

    public String getGroup() { return group; }

    @Override
    public String toString() {
        return service_code + "@" + service_name;
    }
}
