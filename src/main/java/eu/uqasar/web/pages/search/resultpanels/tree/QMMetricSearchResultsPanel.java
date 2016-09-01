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


import eu.uqasar.model.qmtree.QMMetric;
import eu.uqasar.web.pages.qmtree.metric.QMMetricEditPage;
import eu.uqasar.web.pages.qmtree.qmodels.QModelViewPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.Model;

/**
 *
 *
 */
public class QMMetricSearchResultsPanel extends AbstractQModelTreeNodeSearchResultPanel<QMMetric> {

    public QMMetricSearchResultsPanel(String id, String searchTerm) {
        super(id, searchTerm, QMMetric.class);
    }

    @Override
    protected void populateDataViewItem(Item<QMMetric> item) {
        final QMMetric entity = item.getModelObject();
        BookmarkablePageLink<QMMetricEditPage> link = new BookmarkablePageLink<>("link",
                QMMetricEditPage.class, QMMetricEditPage.forMetric(entity));
        link.add(new Label("name", Model.of(entity.getName()
                + " (" + entity.getNodeKey() + ")")).setRenderBodyOnly(true));
        item.add(link);
        
        BookmarkablePageLink<QModelViewPage> QMlink = new BookmarkablePageLink<>("qmodel",
        		QModelViewPage.class, QModelViewPage.forQModel(entity.getQModel()));
        QMlink.add(new Label("qmKey", Model.of(entity.getQModel().getNodeKey())));
        item.add(QMlink);
        
        
        item.add(new Label("value", Model.of(entity.getTargetValue())));
    }
}
