package multigear.mginterface.tools.mgmap;

import multigear.communication.tcp.support.ComManager;
import multigear.communication.tcp.support.ConnectionInfo;
import multigear.communication.tcp.support.ParentAttributes;
import multigear.communication.tcp.support.SupportMessage;
import multigear.communication.tcp.support.objectmessage.ObjectMessage;
import multigear.mginterface.scene.Scene;
import multigear.mginterface.scene.components.UpdatableListener;

/**
 * Multigear Map
 * @author user
 *
 */
public class DuoMap {

	/**
	 * Updater
	 * @author user
	 *
	 */
	final private UpdatableListener mUpdaterListener = new UpdatableListener() {

		/**
		 * Update
		 */
		@Override
		public void onUpdate(Scene scene) {
			update();
		}
	};
	
	// Final Private Constants
	/** Connections Conts*/
	final private static int CONNECTION_REQUEST = 0;
	final private static int CONNECTION_REQUEST_ACCEPTED = 1;
	
	// Final Private Variables
	final private Scene mScene;
	final private ComManager mComManager;
	final private int mComCode;
	
	// Private Variables
	private boolean mConnecting = false;
	private boolean mConnected = false;
	private DuoMapAdjust mAdjust = DuoMapAdjust.NOT_SET;
	
	/**
	 * Constructor
	 * @param scene Scene to attach
	 * @param communicationCode Communication code used to create the
	 *  message, enter a code that you had used for other purposes, 
	 *  because it can be confused later. 
	 *  Read more: ObjectMessage.create(code)
	 */
	public DuoMap(final Scene scene, final int communicationCode) {
		mScene = scene;
		mComManager = scene.getComManager();
		mComCode = communicationCode;
		mScene.addUpdatableListener(mUpdaterListener);
	}
	
	/**
	 * Starts connect. If no map the wait, the same had been waiting.
	 * If connected it already is ignored.
	 */
	final public void connect() {
		if(mConnected)
			return;
		mConnecting = true;
		mComManager.sendForAll(ObjectMessage.create(mComCode).add(CONNECTION_REQUEST).build());
	}
	
	/**
	 * Update
	 */
	final private void update() {
		// Wait for connection accepted
		if(mConnecting)
			mComManager.sendForAll(ObjectMessage.create(mComCode).add(CONNECTION_REQUEST).build());
	}
	
	/**
	 * Communication Message
	 */
	final public void onComMessage(final SupportMessage message) {
		switch (message.Message) {
		case SupportMessage.PARENT_PREPAREDATTRIBUTES:
			final ParentAttributes attributes = (ParentAttributes) message.Object;
			final float thisScreenY = mScene.getPhysicalScreenSize().y;
			final float parentScreenY = attributes.getPhysicalScreenSize().y;
			if(thisScreenY > parentScreenY) {
				mScene.setBaseDpi(attributes.Dpi);
				mAdjust = DuoMapAdjust.ADJUST_MAJOR;
			} else if(thisScreenY < parentScreenY)
				mAdjust = DuoMapAdjust.ADJUST_MINOR;
			else {
				final float thisScreenDpi = mScene.getDPI();
				final float parentDpi = attributes.getDpi();
				if(thisScreenDpi > parentDpi) {
					mScene.setBaseDpi(attributes.Dpi);
					mAdjust = DuoMapAdjust.ADJUST_MAJOR;
				} else if(thisScreenDpi < parentDpi)
					mAdjust = DuoMapAdjust.ADJUST_MINOR;
				else
					mAdjust = DuoMapAdjust.ADJUST_EQUAL;
			}
			break;
		}
	}
	
	/**
	 * Get Device Adjust
	 * @return Device Adjust
	 */
	final public DuoMapAdjust getAdjust() {
		return mAdjust;
	}
	
	/**
	 * Object Message
	 */
	final public void onMessage(final ConnectionInfo connectionInfo, final ObjectMessage objectMessage) {
		if(objectMessage.getCode() == mComCode) {
			switch((Integer)objectMessage.getValue(0)) {
			// Request Received
			case CONNECTION_REQUEST:
				if(!mConnected) {
					mConnected = true;
					mComManager.sendForAll(ObjectMessage.create(mComCode).add(CONNECTION_REQUEST_ACCEPTED).build());
				} // else: message out (ok)
				break;
			// Request Received in other side
			// It is not necessary to continue with the request
			case CONNECTION_REQUEST_ACCEPTED:
				mConnecting = false;
				mComManager.prepareParentsAttributes();
				break;
			}
		}
	}
	
	/**
	 * Destroy object
	 */
	final public void destroy() {
		mScene.removeUpdatableListener(mUpdaterListener);
	}
}
