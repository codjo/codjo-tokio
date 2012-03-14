/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.tokio;
import java.io.File;
/**
 *
 */
public abstract class XMLCasesLoaderTestCase extends XMLLoaderTestCase {
    protected static final String CASES_DIR = "src/test/resources/test/cases/";


    protected XMLCasesLoader load(String pathname) throws Exception {
        return loadImpl(new File(pathname).toURI().toString());
    }


    private XMLCasesLoader loadImpl(String uri) throws Exception {
        TokioConfiguration configuration = new TokioConfiguration();
        IncludeEntitiesManager includeEntitiesManager = new IncludeEntitiesManager(configuration);
        RequiredInstancesManager requiredInstancesManager = new RequiredInstancesManager();
        XMLCasesLoader loader = new XMLCasesLoader(
              includeEntitiesManager,
              requiredInstancesManager,
              new XMLStoryUtil(configuration,
                               includeEntitiesManager,
                               new CreateEntityManager(),
                               requiredInstancesManager));
        loader.parse(loadDocument(uri), uri, null);
        requiredInstancesManager.finalizeDatasets();
        return loader;
    }
}
