package eu.uqasar.web.pages.qmtree.quality.objective.panels;

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


import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.CheckBoxMultipleChoice;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.ComponentFeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import wicket.contrib.tinymce.TinyMceBehavior;
import wicket.contrib.tinymce.ajax.TinyMceAjaxSubmitModifier;
import wicket.contrib.tinymce.settings.TinyMCESettings;

import com.googlecode.wicket.jquery.ui.form.button.AjaxButton;
import com.vaynberg.wicket.select2.Select2MultiChoice;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameAppender;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapBookmarkablePageLink;
import eu.uqasar.exception.model.EntityNotFoundException;
import eu.uqasar.model.meta.MetaData;
import eu.uqasar.model.meta.QModelTagData;
import eu.uqasar.model.qmtree.QMQualityObjective;
import eu.uqasar.model.qmtree.QMTreeNode;
import eu.uqasar.model.qmtree.QModel;
import eu.uqasar.model.quality.indicator.Domain;
import eu.uqasar.model.quality.indicator.Paradigm;
import eu.uqasar.model.quality.indicator.Purpose;
import eu.uqasar.model.quality.indicator.Version;
import eu.uqasar.model.role.Role;
import eu.uqasar.service.QMTreeNodeService;
import eu.uqasar.service.meta.MetaDataService;
import eu.uqasar.web.components.StringResourceModelPlaceholderDelegate;
import eu.uqasar.web.components.qmtree.TagsSelectionModal;
import eu.uqasar.web.components.util.DefaultTinyMCESettings;
import eu.uqasar.web.pages.BasePage;
import eu.uqasar.web.pages.qmtree.panels.QMBaseTreePanel;
import eu.uqasar.web.pages.qmtree.qmodels.QModelViewPage;
import eu.uqasar.web.pages.qmtree.quality.objective.QMQualityObjectiveEditPage;
import eu.uqasar.web.pages.qmtree.quality.objective.QMQualityObjectiveFormValidator;
import eu.uqasar.web.pages.qmtree.quality.objective.QMQualityObjectiveViewPage;
import eu.uqasar.web.provider.meta.MetaDataChoiceProvider;

public class QualityObjectiveEditPanel extends QMBaseTreePanel<QMQualityObjective> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6228029042856652686L;

	private TinyMceBehavior tinyMceBehavior;
	private TextArea<String> description;
	private IModel<Boolean> richEnabledModel = Model.of(Boolean.TRUE);

	//container for purpose and its attributes
	private WebMarkupContainer purposeAtt = new WebMarkupContainer("purposeAtt");

	@Inject
	private QMTreeNodeService qmodelService;

    @Inject
    @Named(MetaDataService.NAME)
    private MetaDataService metaDataService;

	private TagsSelectionModal tagsModal;

    private final Form<QMQualityObjective> form;

	private FormComponent name, lowerLimit, upperLimit, weight, targetValue;
	private FormComponent<Purpose> purpose;
	private FormComponent<Paradigm> paradigm;
	private FormComponent<Version> version;
	private CheckBoxMultipleChoice<Domain> domain;
	private ComponentFeedbackPanel feedbackName, feedbackLimits, feedbackLow;
	private AjaxButton save;
	private CheckBox chkCreateCopy;

	private final List<Class> metaDataClasses;
	
	private MetaDataChoiceProvider metaDataProvider;
	
	private Select2MultiChoice select;
	
	public QualityObjectiveEditPanel(String id, final IModel<QMQualityObjective> model, final boolean isNew) {
		super(id, model);

		QMQualityObjective indicator = model.getObject();
		
		metaDataClasses = (List<Class>)(List<?>)(MetaData.getAllClasses());
		
		form = new Form<QMQualityObjective>("form", model) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 7134281091722157566L;
			
			@Override
			protected void onSubmit() {
				
				QMQualityObjective qo = model.getObject();
				qo = checkCompleted(qo);
				qmodelService.update(qo);

				//If "create copy" check is selected
				if (chkCreateCopy.getModelObject()) {
					if (qo.getQModel() != null) {
						QMQualityObjective obj = new QMQualityObjective(qo);
						(qo.getQModel()).addChild(obj);
						
						QMTreeNode parent = qmodelService.update(qo.getQModel());
						Iterator children = parent.getChildren().iterator();
						boolean found = false;
						QMTreeNode qmn = null;
						while (children.hasNext() && !found){
							qmn = (QMTreeNode)children.next();
							if (qmn.equals(obj)){	
								found=true;
							}
						}
						
						PageParameters params = new PageParameters();
						params.add("qmodel-key", qo.getQModel().getNodeKey());		
						params.add("id", qmn.getId());
						params.add("isNew", true);
						setResponsePage(QMQualityObjectiveEditPage.class, params);
					}
				} else {
					PageParameters parameters = BasePage.appendSuccessMessage(
						QMQualityObjectiveViewPage.forQualityObjective(getModelObject()),
						new StringResourceModel("treenode.saved.message", this,
								getModel()));
					setResponsePage(QMQualityObjectiveViewPage.class, parameters);
				}
			}
		};

		//Name
		form.add(name = new TextField("name", new PropertyModel<>(model, "name")));
		form.add(feedbackName = new ComponentFeedbackPanel("feedbackName", name));
		feedbackName.setOutputMarkupId(true);
		
		//Domains
		domain = new CheckBoxMultipleChoice<Domain>("domain", new PropertyModel<List<Domain>>(indicator, "domain"), Domain.getAllDomains());
		form.add(domain);		

		// Purpose
		purpose = new DropDownChoice<Purpose>("purpose", new PropertyModel<Purpose>(indicator, "indicatorPurpose"), Purpose.getAllPurposes());
		
		// Purpose attributes
		paradigm = new DropDownChoice<Paradigm>("attContent", new PropertyModel<Paradigm>(indicator, "paradigm"), Paradigm.getAllParadigms());

		final Label paradigmLabel = new Label("attLabel", new StringResourceModel("label.objective.paradigm",this, null));

		version = new DropDownChoice<Version>("attContent", new PropertyModel<Version>(indicator, "version"), Version.getAllVersions());

		final Label versionLabel = new Label("attLabel", new StringResourceModel("label.objective.version",this, null));

		if (indicator.getIndicatorPurpose().equals(Purpose.Process)){
			purposeAtt.add(paradigm);
			purposeAtt.add(paradigmLabel);
		} else {
			purposeAtt.add(version);
			purposeAtt.add(versionLabel);
		}

		purpose.setOutputMarkupId(true);
		purpose.setOutputMarkupPlaceholderTag(true);
		purposeAtt.setOutputMarkupId(true);		
		purposeAtt.setOutputMarkupPlaceholderTag(true);		

		form.add(purposeAtt);
		form.add(purpose);

		purpose.add(new AjaxFormComponentUpdatingBehavior("onchange")
		{
			@Override
			protected void onUpdate(AjaxRequestTarget target)
			{
				if (((Purpose) getFormComponent().getConvertedInput()).equals(Purpose.Process)){
					purposeAtt.remove(versionLabel);
					purposeAtt.remove(version);
					purposeAtt.add(paradigmLabel);
					purposeAtt.add(paradigm);
				} else {
					purposeAtt.remove(paradigmLabel);
					purposeAtt.remove(paradigm);
					purposeAtt.add(versionLabel);
					purposeAtt.add(version);
				}
				if (target != null) {
					target.add(purposeAtt);
				}
			}
		});

		// Description
		description = new TextArea<String>("description",
				new PropertyModel<String>(model, "description"));
		form.add(description);
		description.add(tinyMceBehavior = new TinyMceBehavior(
				DefaultTinyMCESettings.get()));
		form.add(new AjaxCheckBox("toggle.richtext", richEnabledModel) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 390304436557732288L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				if (getModelObject()) {
					description.add(tinyMceBehavior);
				} else {
					description.remove(tinyMceBehavior);
					tinyMceBehavior = new TinyMceBehavior(
							DefaultTinyMCESettings.get());
				}
				target.add(QualityObjectiveEditPanel.this);
			}
		});

		//roles
		final CheckBoxMultipleChoice<Role> targetAudience = 
				new CheckBoxMultipleChoice<Role>("targetAudience", new PropertyModel<List<Role>>(indicator, "targetAudience"), Role.getAllRoles());
		form.add(targetAudience);

		//lower and upper limits
		form.add(new Label("limitsLabel", new StringResourceModel("label.objective.limits",this, null)));
		form.add(new Label("lowerLabel", new StringResourceModel("label.objective.lower.limit",this, null)));
		form.add(new Label("upperLabel", new StringResourceModel("label.objective.upper.limit",this, null)));

		lowerLimit = new TextField("lowerLimit", new PropertyModel<Double>(model, "lowerLimit")).setRequired(false);
		form.add(lowerLimit);
		feedbackLow = (ComponentFeedbackPanel) new ComponentFeedbackPanel("feedbackLow", lowerLimit).setOutputMarkupId(true);
		form.add(feedbackLow);
		
		if (Double.MAX_VALUE == indicator.getUpperLimit()) {
			//TODO empty field
			indicator.setUpperLimit(Double.MIN_VALUE);
		}
		
		upperLimit = new TextField("upperLimit", new PropertyModel<>(model, "upperLimit")).setRequired(false);
		form.add(upperLimit);
		feedbackLimits = (ComponentFeedbackPanel) new ComponentFeedbackPanel("feedbackLimits", upperLimit).setOutputMarkupId(true);
		form.add(feedbackLimits);
		
		//Sprint 2. Added QProject attributes
		weight = new TextField("weight", new PropertyModel<>(model, "weight")).setRequired(false);
		form.add(weight);
		targetValue = new TextField("targetValue", new PropertyModel<>(model, "targetValue")).setRequired(false);
		form.add(targetValue);

		// add  modal for adding tags
		tagsModal = newTagsSelectionModal();
		tagsModal.setOutputMarkupId(true);
		form.add(tagsModal);
		
		select = newSelect2("qModelTagData", QModelTagData.class,
            	new PropertyModel<Collection<QModelTagData>>(model.getObject(), "qModelTagData"), form);
		form.add(select);		
			
		if (isNew){
				Button cancel = new Button("cancel"){
					private static final long serialVersionUID = -6733104400111745683L;

					public void onSubmit() {
						QModel parent = qmodelService.getQModel(model.getObject().getParent().getId());
						qmodelService.removeTreeNode(model.getObject().getId());
						setResponsePage(QModelViewPage.class, QModelViewPage.forQModel(parent));
					}
				};
				cancel.setDefaultFormProcessing(false);
				form.add(cancel);
			} else {
				form.add(new BootstrapBookmarkablePageLink<QModelViewPage>(
						"cancel",
						QMQualityObjectiveViewPage.class,
						QMQualityObjectiveViewPage.forQualityObjective(model.getObject()),
						de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons.Type.Default)
						.setLabel(new StringResourceModel("button.cancel", this, null)));
			}

			form.add(save = new AjaxButton("save", new StringResourceModel("button.save",
					this, null)){
				/**
						 * 
						 */
						private static final long serialVersionUID = 1L;

				@Override 
				protected void onError( AjaxRequestTarget target, Form<?> form ){ 
					form.add(feedbackName);
					form.add(feedbackLimits);
					form.add(feedbackLow);
					target.add(form);
				} 
			});
			save.add(new TinyMceAjaxSubmitModifier());
	        
			//Create additional qo with predefined values
			chkCreateCopy = new CheckBox("checkboxCopy", Model.of(Boolean.FALSE));
			form.add(chkCreateCopy);
			form.add(new Label("checkboxCopyLabel", new StringResourceModel("label.objective.checkboxCopy",this, null)));
			
			form.setOutputMarkupId(true);
			setOutputMarkupId(true);
			form.add(new QMQualityObjectiveFormValidator(name, lowerLimit, upperLimit));
			add(form);
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(JavaScriptReferenceHeaderItem
				.forReference(TinyMCESettings.javaScriptReference()));
	}

	@Override
	protected void onConfigure() {
		super.onConfigure();
	}
	
	public QMQualityObjective checkCompleted(QMQualityObjective node) {
		if (node.getChildren().isEmpty()){
			node.setIsCompleted(false);
			//update quality model
			node.getParent().setIsCompleted(false);
			qmodelService.update(node.getParent());
		}
		return node;
	}
	
	private <T extends MetaData> Select2MultiChoice<T> newSelect2(final String id,
            Class<T> clazz, PropertyModel<Collection<T>> model, final Form<?> form) {
        
		metaDataProvider = new MetaDataChoiceProvider<>(metaDataService.getAll(clazz), clazz);
		
		Select2MultiChoice<T> select2MultiChoice = new Select2MultiChoice<>(id, model, metaDataProvider);
		
		select2MultiChoice.add(new AjaxEventBehavior("onchange") { //oninput doesntwork
			@Override
			protected void onEvent(AjaxRequestTarget target) {
				String input = getWebRequest().getRequestParameters().getParameterValue("qModelTagData").toString();
				try {
					if (input!=null && !input.equals("")){
						metaDataProvider.toChoices((Arrays.asList(input.split(","))));
					}
				} catch (EntityNotFoundException ex) {
					//SelectionModal
					tagsModal.setInputSelection(input);
					tagsModal.setTagName(ex.getMessage());
					tagsModal.setVisible(true);
					tagsModal.add(new CssClassNameAppender(""));
					tagsModal.setFadeIn(false);
					tagsModal.show(true);
					target.add(tagsModal);
				}
			}
	});
		
		final IModel<String> placeHolder = new StringResourceModelPlaceholderDelegate("placeholder.meta.input", this, null, MetaData.getLabelModel(clazz));
        select2MultiChoice.getSettings().setCloseOnSelect(false);
        select2MultiChoice.getSettings().setPlaceholder(placeHolder);
        select2MultiChoice.getSettings().setTokenSeparators(new String[]{","});
        select2MultiChoice.getSettings().setCreateSearchChoice(
                "function(term) { if (term.length > 1) { return { id: term, text: term }; } }");
        form.add(new Label("label." + id, new StringResourceModel("label.meta.known", this, null, MetaData.getLabelModel(clazz))));
        form.add(new Label("help." + id, new StringResourceModel("help.meta.input", this, null, MetaData.getLabelModel(clazz))));
        return select2MultiChoice;
    }

	/**
	 * newTagsSelectionModal create a modal window with a dropdown to select the type of metadata to add.
	 * @return TagsSelectionModal
	 */
	private TagsSelectionModal newTagsSelectionModal() {
		final TagsSelectionModal modal = new TagsSelectionModal("tagsModal", metaDataClasses){
			
			@Override
			public boolean onConfirmed(AjaxRequestTarget target) {
				boolean confirmed = false;
				try {
					if (this.getTypeSelected()!=null && !this.getTypeSelected().equals("")){
						Class<?> clazz = Class.forName("eu.uqasar.model.meta."+this.getTypeSelected());

						MetaData created = metaDataService.getByMetaDataOrCreate(clazz, this.getTagName());
						QModelTagData qmtg = (QModelTagData) metaDataService.getByQMTagData(created.getId());
						
						String input = this.getInputSelection();
						Collection<QModelTagData> ch;
						Set<QModelTagData> set = new HashSet<QModelTagData>();
						if (input!=null){
							input = input.replaceAll(this.getTagName(),String.valueOf(qmtg.getId()));
							ch = metaDataProvider.toChoices((Arrays.asList(input.split(","))));
							Iterator it = ch.iterator();
							while (it.hasNext()){
								set.add((QModelTagData)it.next());
							}
						} 
						
						select.setModelObject(set);
						target.add(select);
						confirmed = true;
						this.setInputSelection("");
						this.setTypeSelected("");
						this.setTagName("");
					}
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return confirmed;
			}
		};
		
		modal.setHeaderVisible(false);
		modal.setUseCloseHandler(true);
		return modal;
		}
}
