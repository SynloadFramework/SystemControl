package tech.synframe.systemcontrol;

import com.synload.framework.Log;
import com.synload.framework.modules.ModuleClass;
import com.synload.framework.modules.annotations.Module;
import com.synload.framework.modules.annotations.Module.LogLevel;
import tech.synframe.systemcontrol.models.PendingAction;
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
		try {
			List<PendingAction> pending = PendingAction._find(PendingAction.class, "").exec(PendingAction.class);
			for (PendingAction pa : pending) {
				Queue.add(pa);
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	@Override
	public void crossTalk(Object... obj) {
		
	}

}
