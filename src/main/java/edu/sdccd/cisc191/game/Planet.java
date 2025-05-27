package edu.sdccd.cisc191.game;

import java.io.Serializable;
public class Planet implements Serializable {
    private static final long serialVersionUID = 1L;

    public final String mars;

    public Planet(String mars) {
        this.mars = mars;
        this.getName();
    }

    public String getName() {
        if (mars == null);
        return "Earth";
    }
}
