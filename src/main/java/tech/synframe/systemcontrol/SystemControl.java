package tech.synframe.systemcontrol;

import com.synload.framework.Log;
import com.synload.framework.modules.ModuleClass;
import com.synload.framework.modules.annotations.Module;
import com.synload.framework.modules.annotations.Module.LogLevel;
import tech.synframe.systemcontrol.models.PendingAction;
import tech.synframe.systemcontrol.models.Project;
import tech.synframe.systemcontrol.settings.SettingsLoader;
import tech.synframe.systemcontrol.utils.NewVersionCheck;
import tech.synframe.systemcontrol.utils.Queue;

import java.util.List;

/*
 * Module class (requires both the annotation and the extending of the class)
 */
@Module(author = "Nathaniel Davidson", depend = { "" }, log = LogLevel.INFO, name = "System Control", version = "1.0")
public class SystemControl extends ModuleClass {
	@Override
	public void initialize() {
		// Sent when the module is run (after module elements loaded)
		Log.info("Loaded System Control", SystemControl.class);
		new Thread(new Queue()).start();
		new Thread(new NewVersionCheck()).start();
		try {
			List<PendingAction> pending = PendingAction._find(PendingAction.class, "").exec(PendingAction.class);
			for (PendingAction pa : pending) {
				Queue.add(pa);
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		try {
			List<Project> projects = Project._find(Project.class, "autostart=?", 1).exec(Project.class);
			for (Project p : projects) {
				p.start();
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		// READ ALL SETTINGS GETTERS AND SETTERS
		SettingsLoader.readSettings();
	}
	@Override
	public void crossTalk(Object... obj) {
		
	}

}
