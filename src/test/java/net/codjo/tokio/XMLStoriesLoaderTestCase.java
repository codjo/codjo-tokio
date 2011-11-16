/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.tokio;
/**
 *
 */
public abstract class XMLStoriesLoaderTestCase extends XMLLoaderTestCase {
    protected static final String STORIES_DIR = "src/test/resources/test/stories/";


    protected XMLStoriesLoader load(String uri) throws Exception {
        TokioConfiguration configuration = new TokioConfiguration();
        RequiredInstancesManager requiredInstancesManager = new RequiredInstancesManager();
        XMLStoriesLoader loader =
              new XMLStoriesLoader(new XMLStoryUtil(configuration,
                                                    new IncludeEntitiesManager(configuration),
                                                    new CreateEntityManager(),
                                                    requiredInstancesManager));
        loader.parse(loadDocument(uri), uri, null);
        requiredInstancesManager.finalizeDatasets();
        return loader;
    }
}
