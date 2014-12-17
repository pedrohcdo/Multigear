package multigear.mginterface.scene;

/**
 * Delayed Install/Uninstall
 * 
 * @author PedroH, RaphaelB
 *
 * Property Createlier.
 */
public class DelayIU {
	
	// Final Private Variables
	final private long mStartedTime;
	final private long mDelay;
	final private multigear.mginterface.scene.Installation mInstallation;
	
	/*
	 * Constutor
	 */
	protected DelayIU(final long startedTime, final long delay, final multigear.mginterface.scene.Installation installation) {
		mStartedTime = startedTime;
		mDelay = delay;
		mInstallation = installation;
	}
	
	/*
	 * Se finalizou o tempo
	 */
	final protected boolean isTimesUp(final long timeNow) {
		return (timeNow - mStartedTime >= mDelay);
	}
	
	/*
	 * Retorna instala��o
	 */
	final protected multigear.mginterface.scene.Installation getInstallation() {
		return mInstallation;
	}
	
}
