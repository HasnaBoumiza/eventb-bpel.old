package za.vutshilalabs.bpelgen.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import za.vutshilalabs.bpelgen.Activator;

/**
 * 
 * @author mashern
 * 
 */

public class PreferencesPage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	public PreferencesPage() {
		super(GRID);
	}

	@Override
	protected void createFieldEditors() {
		Composite parent = getFieldEditorParent();

		StringFieldEditor prefixWsdl = new StringFieldEditor(
				PreferenceConstants.WSDL_NS_PREFIX, "Default &prefix", parent);

		StringFieldEditor tns = new StringFieldEditor(
				PreferenceConstants.TNS_WS, "&Target Namespace", parent);

		addField(prefixWsdl);
		addField(tns);

		// binding
		addField(new BooleanFieldEditor(PreferenceConstants.GENERATE_BINDING,
				"Generate WSDL &binding tag", parent));
		// service

		addField(new BooleanFieldEditor(PreferenceConstants.GENERATE_SERVICE,
				"Generate WSDL &service tag", parent));
		// options
		addField(new RadioGroupFieldEditor(PreferenceConstants.OPTIONS,
				"Translation options", 1, new String[][] {
						{ "&Replace existing files", "replace" },
						{ "&Apply changes", "apply" },
						{ "&Create new files", "generate" } }, parent));
	}

	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Generated WSDL files.");
	}

}
