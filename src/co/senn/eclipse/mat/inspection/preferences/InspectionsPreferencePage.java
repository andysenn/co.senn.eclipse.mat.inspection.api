package co.senn.eclipse.mat.inspection.preferences;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.jface.preference.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

import co.senn.eclipse.mat.inspection.internal.spec.InspectionSpec;
import co.senn.eclipse.mat.inspection.internal.spec.TechnologySpec;
import co.senn.eclipse.mat.inspection.internal.util.InspectionUtil;

import org.eclipse.ui.IWorkbench;

public class InspectionsPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	private final Preferences preferences = ConfigurationScope.INSTANCE
			.getNode(InspectionsPreferenceHelper.PREFERENCE_NODE);

	private boolean success = true;
	private Tree tree = null;

	public InspectionsPreferencePage() {
		super();
		setDescription("Configure how inspections are performed");
	}

	@Override
	public void init(IWorkbench workbench) {

	}

	@Override
	protected Control createContents(Composite parent) {
		Composite top = new Composite(parent, SWT.LEFT);

		top.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		top.setLayout(new GridLayout());

		try {
			Label listLabel = new Label(top, SWT.NONE);
			listLabel.setText("Inspections:");

			tree = new Tree(top, SWT.CHECK | SWT.BORDER);
			tree.setLayoutData(new GridData(GridData.FILL_BOTH));
			tree.setHeaderVisible(true);

			TreeColumn colInspectionName = new TreeColumn(tree, SWT.LEFT);
			colInspectionName.setText("Inspection");
			colInspectionName.setWidth(200);

			TreeColumn colInspectionDesc = new TreeColumn(tree, SWT.LEFT);
			colInspectionDesc.setText("Description");
			colInspectionDesc.setWidth(400);

			Map<TechnologySpec, List<InspectionSpec>> inspections = InspectionUtil.getInspections();
			for (Entry<TechnologySpec, List<InspectionSpec>> entry : inspections.entrySet()) {
				TechnologySpec technologySpec = entry.getKey();

				TreeItem technologyItem = new TreeItem(tree, SWT.NONE);
				technologyItem.setData(technologySpec);
				technologyItem.setText(new String[] { technologySpec.getName(), technologySpec.getDescription() });

				for (InspectionSpec inspectionSpec : entry.getValue()) {
					TreeItem inspectionItem = new TreeItem(technologyItem, SWT.NONE);
					inspectionItem.setData(inspectionSpec);
					inspectionItem.setText(new String[] { inspectionSpec.getName(), inspectionSpec.getDescription() });
					inspectionItem.setChecked(!preferences.getBoolean(
							InspectionsPreferenceHelper.INSPECTION_DISABLE_PREFIX + inspectionSpec.getId(), false));

					if (inspectionItem.getChecked()) {
						technologyItem.setChecked(true);
					}
				}
			}

			tree.addListener(SWT.Selection, e -> {
				if (e.detail == SWT.CHECK) {
					if (e.item instanceof TreeItem) {
						// Update the checked state of the child items
						TreeItem item = (TreeItem) e.item;
						for (TreeItem subItem : item.getItems()) {
							subItem.setChecked(item.getChecked());
						}

						// If this item has a parent, it should update its state based on its children
						TreeItem parentItem = item.getParentItem();
						if (parentItem != null) {
							boolean check = false;
							for (TreeItem subItem : parentItem.getItems()) {
								if (subItem.getChecked()) {
									check = true;
									break;
								}
							}
							parentItem.setChecked(check);
						}
					}
				}
			});
		} catch (Exception e) {
			Label error = new Label(top, SWT.NONE);
			error.setText("An error occurred while loading the inspection preferences");

			// TODO: Print the stack trace to the screen
			success = false;
		}

		return top;
	}

	@Override
	public boolean performOk() {
		// If an exception occurred, prevent overwriting preferences
		if (!success) {
			return false;
		}

		try {
			// Clear any disabled inspections
			for (String key : preferences.keys()) {
				if (key.startsWith(InspectionsPreferenceHelper.INSPECTION_DISABLE_PREFIX)) {
					preferences.remove(key);
				}
			}

			// Add any disabled inspections
			Collection<InspectionSpec> disabledInspections = getDisabledInspections();
			for (InspectionSpec disabledInspection : disabledInspections) {
				preferences.putBoolean(
						InspectionsPreferenceHelper.INSPECTION_DISABLE_PREFIX + disabledInspection.getId(), true);
				System.out.println("Adding disabled inspection: " + disabledInspection.getId());
			}

			// Flush the preferences
			preferences.flush();

			return super.performOk();
		} catch (BackingStoreException e) {
			return false;
		}
	}

	private Collection<InspectionSpec> getDisabledInspections() {
		Collection<InspectionSpec> disabledInspections = new ArrayList<>();
		for (TreeItem item : tree.getItems()) {
			getDisabledInspections(item, disabledInspections);
		}
		return disabledInspections;
	}

	private void getDisabledInspections(TreeItem parent, Collection<InspectionSpec> collection) {
		for (TreeItem item : parent.getItems()) {
			if (!item.getChecked() && item.getData() instanceof InspectionSpec) {
				collection.add(((InspectionSpec) item.getData()));
			}

			if (item.getItemCount() > 0) {
				getDisabledInspections(item, collection);
			}
		}
	}

}