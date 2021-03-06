package eu.uqasar.web.pages.search.resultpanels.tree;

/*
 * #%L
 * U-QASAR
 * %%
 * Copyright (C) 2012 - 2015 U-QASAR Consortium
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import javax.inject.Inject;

import eu.uqasar.model.AbstractEntity;
import eu.uqasar.service.AbstractService;
import eu.uqasar.service.tree.ProjectSearchService;
import eu.uqasar.web.pages.search.resultpanels.AbstractSearchResultsPanel;

/**
 *
 * @param <T>
 */
public abstract class AbstractProjectSearchResultPanel<T extends AbstractEntity> extends AbstractSearchResultsPanel<T> {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Inject
    ProjectSearchService service;
    
    public AbstractProjectSearchResultPanel(String id, String searchTerm, Class<T> clazz) {
        super(id, searchTerm, clazz);
    }

    @Override
    protected <S extends AbstractService<T>> S getService() {
        return (S)service;
    }
}
