/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.tokio;
import net.codjo.tokio.model.Scenario;
import net.codjo.tokio.model.ScenarioList;
/**
 *
 */
public interface XMLScenarioLoader {
    Scenario getScenario(String name);
    ScenarioList getScenarii();
}
