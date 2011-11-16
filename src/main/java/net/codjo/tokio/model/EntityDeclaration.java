package net.codjo.tokio.model;
import java.util.HashMap;
import java.util.Map;

public class EntityDeclaration {
    private final Map<String, Parameter> parameters = new HashMap<String, Parameter>();
    private String id;
    private String name;
    private Location location = new Location();


    public EntityDeclaration(String name) {
        this.name = name;
    }


    public EntityDeclaration(String name, String id) {
        this.name = name;
        this.id = id;
    }


    public String getId() {
        return id;
    }


    public void setId(String id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public Location getLocation() {
        return location;
    }


    public void setLocation(Location location) {
        this.location = location;
    }


    public Map<String, Parameter> getParameters() {
        return parameters;
    }


    public void addParameter(String parameterName, Parameter parameter) {
        parameters.put(parameterName, parameter);
    }
}
