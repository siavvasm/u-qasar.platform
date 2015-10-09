package eu.uqasar.web.components.tree.util;

import java.io.Serializable;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.extensions.markup.html.repeater.tree.AbstractTree;
import org.apache.wicket.extensions.markup.html.repeater.tree.AbstractTree.State;
import org.apache.wicket.extensions.markup.html.repeater.tree.ITreeProvider;
import org.apache.wicket.extensions.markup.html.repeater.tree.content.Folder;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;

import eu.uqasar.model.tree.Metric;
import eu.uqasar.model.tree.Project;
import eu.uqasar.model.tree.QualityIndicator;
import eu.uqasar.model.tree.QualityObjective;
import eu.uqasar.model.tree.TreeNode;
import eu.uqasar.util.UQasarUtil.SuggestionType;

public class TreeFolder extends Folder<TreeNode> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 396855834247379445L;
	
	private final AbstractTree<TreeNode> tree;

	public TreeFolder(String id, AbstractTree<TreeNode> tree,
			IModel<TreeNode> model) {
		super(id, tree, model);
		this.tree = tree;
	}

	@Override
	protected Component newLabelComponent(String id, IModel<TreeNode> model) {
		WebMarkupContainer labelContainer = new WebMarkupContainer(id);
		Label label = new Label("labelText", newLabelModel(model));
		Label icon = (Label) new Label("icon").add(new AttributeAppender(
				"class", model.getObject().getIconType().cssClassName()));

		// If there are suggestion(s) attached to the tree item, add a label to the entry
		// TODO: Move CSS changes to CSS files
		String suggestionStr = "";
		if (model.getObject().getSuggestionType() != null) {
			if (model.getObject().getSuggestionType().equals(SuggestionType.QO_REMOVE) || 
					model.getObject().getSuggestionType().equals(SuggestionType.QO_REPLACE) || 
					model.getObject().getSuggestionType().equals(SuggestionType.METRIC_REMOVE)) {
				suggestionStr = " (*Change Suggestion)";
			}			
		}
		Label suggestionLabel = (Label) new Label("suggestionLabelText", suggestionStr);
		labelContainer.add(label);
		labelContainer.add(icon);
		labelContainer.add(suggestionLabel);
		return labelContainer;
	}

	@Override
	protected boolean isClickable() {
		return true;
	}

	/**
	 * Delegates to others methods depending wether the given model is a folder,
	 * expanded, collapsed or selected.
	 * 
	 * @see ITreeProvider#hasChildren(Object)
	 * @see AbstractTree#getState(Object)
	 * @see #isSelected()
	 * @see #getOpenStyleClass()
	 * @see #getClosedStyleClass()
	 * @see #getOtherStyleClass(Object)
	 * @see #getSelectedStyleClass()
	 */
	@Override
	protected String getStyleClass() {
		TreeNode t = getModelObject();

		String styleClass = "";

		if (t instanceof Project) {
			styleClass = getProjectStyle((Project) t);
		} else if (t instanceof QualityObjective) {
			styleClass = getQualityObjectiveStyle((QualityObjective) t);
		} else if (t instanceof QualityIndicator) {
			styleClass = getQualityIndicatorStyle((QualityIndicator) t);
		} else if (t instanceof Metric) {
			styleClass = getMetricStyle((Metric) t);
		}

		if (isSelected()) {
			styleClass += " " + getSelectedStyleClass();
		}
		styleClass += " " + getRunningStyle(t);
		styleClass += " " + t.getClass().getSimpleName().toLowerCase();
		styleClass += " " + t.getQualityStatus().toString().toLowerCase();
		return styleClass;
	}

	protected String getStyle(TreeNode node) {
		if (tree.getProvider().hasChildren(node)) {
			if (tree.getState(node) == State.EXPANDED) {
				return getOpenStyleClass();
			} else {
				return getClosedStyleClass();
			}
		} else {
			return getOtherStyleClass(node);
		}
	}

	protected String getMetricStyle(Metric node) {
		return getOtherStyleClass(node);
	}

	protected String getQualityIndicatorStyle(QualityIndicator node) {
		if (tree.getState(node) == State.EXPANDED) {
			return getOpenStyleClass();
		} else {
			return getClosedStyleClass();
		}
	}

	protected String getQualityObjectiveStyle(QualityObjective node) {
		if (tree.getState(node) == State.EXPANDED) {
			return getOpenStyleClass();
		} else {
			return getClosedStyleClass();
		}
	}

	protected String getProjectStyle(Project node) {
		if (!node.isRunning()) {
			return "over";
		}
		return "running";
	}

	protected String getRunningStyle(TreeNode node) {
		Project p = node.getProject();
		if (p != null && !p.isRunning()) {
			return "over";
		} else {
			return "running";
		}
	}

}