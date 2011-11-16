/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.tokio;
import junit.framework.TestCase;
/**
 * Classe supportant une TokioFixture pour les tests BD.  SI des traitements particuliers doivent être ajoutés
 * avant ou après un test, surcharger les méthodes {@link #doSetup()} et/ou {@link #doTearDown()}, les
 * méthodes {@link TokioTestCase#setUp()} et/ou {@link TokioTestCase#tearDown()} étant volontairement
 * finales.
 */
public abstract class TokioTestCase extends TestCase {
    protected TokioFixture tokioFixture;


    /**
     * .
     *
     * @see #doSetup()
     */
    @Override
    protected final void setUp() throws Exception {
        tokioFixture = new TokioFixture(getClass());
        tokioFixture.doSetUp();
        doSetup();
    }


    protected void doSetup() throws Exception {
    }


    /**
     * .
     *
     * @see #doTearDown()
     */
    @Override
    protected final void tearDown() throws Exception {
        doTearDown();
        tokioFixture.doTearDown();
    }


    protected void doTearDown() throws Exception {
    }
}
