/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.tokio.model;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
/**
 * DOCUMENT ME!
 *
 * @author $Author: crego $
 * @version $Revision: 1.9 $
 */
public class ScenarioList {
    private Map<String, Scenario> scenarii = new LinkedHashMap<String, Scenario>();


    public ScenarioList() {
    }


    public ScenarioList(Scenario[] sc) {
        for (Scenario aSc : sc) {
            addScenario(aSc);
        }
    }


    public ScenarioList(Scenario sc) {
        addScenario(sc);
    }


    public Map<String, Scenario> getScenarii() {
        return Collections.unmodifiableMap(scenarii);
    }


    public Scenario getScenario(String name) {
        Scenario sc = scenarii.get(name);
        if (sc == null) {
            throw new java.util.NoSuchElementException("Scenario inconnu " + name);
        }
        return sc;
    }


    public void addScenario(Scenario sc) {
        scenarii.put(sc.getName(), sc);
    }


    public void clear() {
        scenarii.clear();
    }


    public void removeScenario(String name) {
        Scenario sc = scenarii.remove(name);
        if (sc != null) {
            sc.clear();
        }
    }


    public Iterator<Scenario> scenarii() {
        return getScenarii().values().iterator();
    }


    public long getScenarioCount() {
        return getScenarii().values().size();
    }


    public Scenario[] toArray() {
        return scenarii.values().toArray(new Scenario[scenarii.size()]);
    }
}
