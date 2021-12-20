package fr.sorbonne_u.treatements;

import java.util.ArrayList;
import java.util.HashMap;

public class XML {
    private HashMap<String, String> methods;
    private HashMap<String, ArrayList<String>> parametersOfOperations;
    private ArrayList<Attribute> attributes;
    private ArrayList<String> packages;
    private String ref;
    private String type;
    private String offered;

    public XML(HashMap<String, String> methods, HashMap<String, ArrayList<String>> parametersOfOperations, ArrayList<Attribute> attributes, ArrayList<String> packages) {
        this.methods = methods;
        this.parametersOfOperations = parametersOfOperations;
        this.attributes = attributes;
        this.packages = packages;
    }

    public XML() {
    }

    public HashMap<String, String> getMethods() {
        return methods;
    }

    public void setMethods(HashMap<String, String> methods) {
        this.methods = methods;
    }

    public HashMap<String, ArrayList<String>> getParametersOfOperations() {
        return parametersOfOperations;
    }

    public void setParametersOfOperations(HashMap<String, ArrayList<String>> parametersOfOperations) {
        this.parametersOfOperations = parametersOfOperations;
    }

    public ArrayList<Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(ArrayList<Attribute> attributes) {
        this.attributes = attributes;
    }

    public ArrayList<String> getPackages() {
        return packages;
    }

    public void setPackages(ArrayList<String> packages) {
        this.packages = packages;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOffered() {
        return offered;
    }

    public void setOffered(String offered) {
        this.offered = offered;
    }

    @Override
    public String toString() {
        return "XML{" +
                "methods=" + methods +
                ", parametersOfOperations=" + parametersOfOperations +
                ", attributes=" + attributes +
                ", packages=" + packages +
                ", ref='" + ref + '\'' +
                ", type='" + type + '\'' +
                ", offered='" + offered + '\'' +
                '}';
    }
}
