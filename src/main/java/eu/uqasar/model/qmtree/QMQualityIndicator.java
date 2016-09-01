package eu.uqasar.model.qmtree;

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


import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonTypeInfo.As;
import org.codehaus.jackson.annotate.JsonTypeInfo.Id;
import org.hibernate.search.annotations.Indexed;

import de.agilecoders.wicket.core.markup.html.bootstrap.image.IconType;
import eu.uqasar.model.lifecycle.LifeCycleStage;
import eu.uqasar.model.lifecycle.RupStage;
import eu.uqasar.model.quality.indicator.Paradigm;
import eu.uqasar.model.quality.indicator.Purpose;
import eu.uqasar.model.quality.indicator.Version;
import eu.uqasar.model.role.Role;

@Entity
@XmlRootElement
@Indexed
@XmlType(propOrder = {"name","description","indicatorPurpose", "version",
		"paradigm", "lifeCycleStage", "rupStage", "targetAudience","upperLimit", "lowerLimit", "targetValue", "weight", "children"})
@JsonTypeInfo(use = Id.CLASS, include = As.PROPERTY, property = "@class")
public class QMQualityIndicator extends QMBaseIndicator {


	/**
	 * 
	 */
	private static final long serialVersionUID = 7941535859451021850L;

	public static final IconType ICON = new IconType("dashboard");

	@JsonCreator
	public QMQualityIndicator(
			@JsonProperty("name") final String name,
			@JsonProperty("children") final List<QMMetric> children,
			@JsonProperty("description") final String description,
			@JsonProperty("indicatorPurpose") final Purpose indicatorPurpose,
			@JsonProperty("lifeCycleStage") final LifeCycleStage lifeCycleStage,
			@JsonProperty("lowerLimit") final float lowerLimit,
			@JsonProperty("paradigm") final Paradigm paradigm,
			@JsonProperty("rupStage") final RupStage rupStage,
			@JsonProperty("targetAudience") final List<Role> targetAudience,
			@JsonProperty("targetValue") final float targetValue,
			@JsonProperty("upperLimit") final float upperLimit,
			@JsonProperty("version") final Version version,
			@JsonProperty("weight") final float weight) {
		
		this.setName(name);
		this.setDescription(description);
		this.setIndicatorPurpose(indicatorPurpose);
		this.setLifeCycleStage(lifeCycleStage);
		this.setLowerLimit((double) lowerLimit);
		this.setParadigm(paradigm);
		this.setRupStage(rupStage);
		this.setTargetAudience(targetAudience);
		this.setTargetValue(targetValue);
		this.setUpperLimit((double) upperLimit);
		this.setVersion(version);
		this.setWeight(weight);
		
		Iterator<QMMetric> it = children.iterator();
		List<QMTreeNode> nodes = new LinkedList<>();
		while (it.hasNext()){
			QMMetric met = it.next();
			nodes.add(met);
		}
		this.setChildren(nodes);
		if (this.getChildren().size()>0){
			this.setCompleted(true);
		}
	}
	
	public QMQualityIndicator() {
		this.setCompleted(false);
	}
	
	public QMQualityIndicator(final String name, final QMQualityObjective parent) {
		super(parent);
		this.setName(name);
		this.setIndicatorPurpose(parent.getIndicatorPurpose());
		this.setParadigm(parent.getParadigm());
		this.setVersion(parent.getVersion());
		this.setCompleted(false);
	}
	
	public QMQualityIndicator(final QMQualityIndicator copy) {
		super(copy.getParent());
		this.setDescription(copy.getDescription());
		this.setIndicatorPurpose(copy.getIndicatorPurpose());
		this.setCompleted(false);
		//TODO AUTOGENERATED? this.setKey("0");
		this.setLifeCycleStage(copy.getLifeCycleStage());
		this.setLowerLimit(copy.getLowerLimit());
		this.setName("Copy of " + copy.getName());
		this.setParadigm(copy.getParadigm());
		this.setRupStage(copy.getRupStage());
		this.setTargetAudience(copy.getTargetAudience());
		this.setTargetValue(copy.getTargetValue());
		this.setUpperLimit(copy.getUpperLimit());
		this.setVersion(copy.getVersion());
		this.setWeight(copy.getWeight());
		this.setQModelTagData(copy.getQModelTagData());
	}
	
	@JsonIgnore
	@Override
	public IconType getIconType() {
		return ICON;
	}

	@JsonIgnore
	public QMQualityObjective getQMQualityObjective() {
		return (QMQualityObjective) getParent();
	}

	@JsonIgnore
	@Override
	public Class<QMMetric> getChildType() {
		return QMMetric.class;
	}

	@XmlElement(name="lifeCycleStage")
	public LifeCycleStage getLifeCycleStage() {
		return super.getLifeCycleStage();
	}
	
	@XmlElement(name="rupStage")
	public RupStage getRupStage() {
		return super.getRupStage();
	}

	@XmlElementWrapper(name="audience")
	@XmlElement(name="targetAudience")
	public List<Role> getTargetAudience() {
		return super.getTargetAudience();
	}
}
