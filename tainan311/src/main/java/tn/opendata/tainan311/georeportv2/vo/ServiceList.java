package tn.opendata.tainan311.georeportv2.vo;

import java.util.Collections;
import java.util.List;

/**
 * Created by vincent on 2014/6/9.
 */
public class ServiceList {
    private List<Services> services;

    private static class Services {
        private List<Service> services;

        public List<Service> getServices() {
            return services;
        }

        @Override
        public String toString() {
            return String.valueOf(services.size());
        }
    }

    public List<Service> getServices() {
        return Collections.unmodifiableList(services.get(0).getServices());
    }

    @Override
    public String toString() {
        return services.get(0).getServices().toString();
    }
}
