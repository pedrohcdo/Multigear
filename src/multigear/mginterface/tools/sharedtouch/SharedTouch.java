package multigear.mginterface.tools.sharedtouch;

import multigear.communication.tcp.support.ComManager;
import multigear.communication.tcp.support.ConnectionInfo;
import multigear.communication.tcp.support.objectmessage.ObjectMessage;
import multigear.communication.tcp.support.objectmessage.ObjectMessageBuilder;
import multigear.general.utils.Vector2;
import multigear.mginterface.scene.Scene;
import multigear.mginterface.scene.components.TouchableListener;
import android.util.Log;
import android.view.MotionEvent;
import android.view.MotionEvent.PointerCoords;
import android.view.MotionEvent.PointerProperties;

/**
 * Shared Touch
 * 
 * @author user
 *
 */
final public class SharedTouch {
	
	/**
	 * Touchable Listener
	 */
	TouchableListener mTouchableListener = new TouchableListener() {
		
		/**
		 * Touch
		 */
		@Override
		public void onTouch(Scene scene, MotionEvent motionEvent) {
			touch(motionEvent);
		}
	};
	
	// Final Private Constants
	final private static int CONNECTION_REQUEST = 0;
	final private static int CONNECTION_REQUEST_ACCEPTED = 1;
	final private static int TOUCH_DATA = 2;
	
	// Final Private Variables
	final private Scene mScene;
	final private ComManager mComManager;
	final private int mComCode;
	
	// Private Variables
	private boolean mConnected = false;
	private boolean mConnecting = false;
	private SharedTouchOffset mOffset = new SharedTouchOffset();
	
	/**
	 * Constructor
	 * @param scene Scene to attach
	 * @param communicationCode Communication code used to create the
	 *  message, enter a code that you had used for other purposes, 
	 *  because it can be confused later. 
	 *  Read more: ObjectMessage.create(code)
	 */
	public SharedTouch(final Scene scene, final int communicationCode) {
		mScene = scene;
		mScene.addTouchableListener(mTouchableListener);
		mComManager = scene.getComManager();
		mComCode = communicationCode;
	}
	
	/**
	 * Set offset
	 * @param offset Offset, null is SharedTouchOffset( (0,0), (0,0) )
	 */
	final public void setOffset(final SharedTouchOffset offset) {
		mOffset = offset.clone();
		if(mOffset == null)
			mOffset = new SharedTouchOffset();
	}
	
	/**
	 * Starts connect. If no map the wait, the same had been waiting.
	 * If connected it already is ignored.
	 */
	final public void connect() {
		mConnecting = true;
		while(mConnecting) {
			mComManager.sendForAll(ObjectMessage.create(mComCode).add(CONNECTION_REQUEST).build());
			mComManager.update();
		}
		mConnected = true;
	}
	
	/**
	 * Return True if connected
	 * @return True/False
	 */
	final public boolean isConnected() {
		return mConnected;
	}
	
	/**
	 * Touch
	 * 
	 * @param motionEvent
	 */
	final private void touch(final MotionEvent motionEvent) {
		// Not read if deviceId() == 0 but event is created for this class
		if(mConnected && motionEvent.getDeviceId() != 0) {
			final int action = motionEvent.getActionMasked();
			int index = 0;
			int id = 0;
			
			Vector2 position = new Vector2();
			Vector2 precision = new Vector2();
			ObjectMessageBuilder builder = ObjectMessage.create(mComCode).add(TOUCH_DATA);
			
			switch(action) {
			case MotionEvent.ACTION_DOWN:
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_POINTER_DOWN:
			case MotionEvent.ACTION_POINTER_UP:
			case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_HOVER_ENTER:
			case MotionEvent.ACTION_HOVER_EXIT:
			case MotionEvent.ACTION_OUTSIDE:
			case MotionEvent.ACTION_SCROLL:
				index = motionEvent.getActionIndex();
				id = motionEvent.getPointerId(index);
				position.x = (motionEvent.getX() + mOffset.sourceOffset.x) * mOffset.sourceAdjust;
				position.y = (motionEvent.getY() + mOffset.sourceOffset.y) * mOffset.sourceAdjust;
				precision.x = motionEvent.getXPrecision();
				precision.y = motionEvent.getYPrecision();
				
				builder.
				add(action).
				add(index).
				add(id).
				add(motionEvent.getDownTime()).
				add(motionEvent.getEventTime()).
				add(position).
				add(motionEvent.getPressure()).
				add(motionEvent.getSize()).
				add(motionEvent.getMetaState()).
				add(precision).
				add(motionEvent.getEdgeFlags());
				
				break;
			case MotionEvent.ACTION_MOVE:
			case MotionEvent.ACTION_HOVER_MOVE:
				
				position.x = (motionEvent.getX() + mOffset.sourceOffset.x) * mOffset.sourceAdjust;
				position.y = (motionEvent.getY() + mOffset.sourceOffset.y) * mOffset.sourceAdjust;
				precision.x = motionEvent.getXPrecision();
				precision.y = motionEvent.getYPrecision();
				
				builder.
				add(action).
				add(motionEvent.getDownTime()).
				add(motionEvent.getEventTime()).
				add(motionEvent.getPressure()).
				add(motionEvent.getSize()).
				add(motionEvent.getMetaState()).
				add(precision).
				add(position).
				add(motionEvent.getEdgeFlags()).
				add(motionEvent.getPointerCount());
				
				for(index=0; index<motionEvent.getPointerCount(); index++) {
					id = motionEvent.getPointerId(index);
					position.x = (motionEvent.getX(index) + mOffset.sourceOffset.x) * mOffset.sourceAdjust;
					position.y = (motionEvent.getY(index) + mOffset.sourceOffset.y) * mOffset.sourceAdjust;
					builder.add(id).add(motionEvent.getPressure(index)).add(motionEvent.getSize(index)).add(position);
				}
				
				break;
			}
			
			// Send event
			mComManager.sendForAll(builder.build());
		}
	}
	
	/**
	 * Touch Data
	 * @param message
	 */
	final private void touchData(final ObjectMessage message) {
		final int action = (Integer)message.getValue(1);
		MotionEvent event = null;
		int index = 0;
		int id = 0;
		long downTime = 0;
		long eventTime = 0;
		Vector2 position = null;
		Vector2 pointerPosition = null;
		float pressure = 0;
		float size = 0;
		int metaState = 0;
		Vector2 precision = null;
		int edgeFlags = 0;
		int pointerCount = 0;
		
		
		switch(action) {
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_POINTER_DOWN:
		case MotionEvent.ACTION_POINTER_UP:
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_HOVER_ENTER:
		case MotionEvent.ACTION_HOVER_EXIT:
		case MotionEvent.ACTION_OUTSIDE:
		case MotionEvent.ACTION_SCROLL:
			index = (Integer)message.getValue(2);
			id = (Integer)message.getValue(3);
			downTime = (Long)message.getValue(4);
			eventTime = (Long)message.getValue(5);
			position = (Vector2)message.getValue(6);
			pressure = (Float)message.getValue(7);
			size = (Float)message.getValue(8);
			metaState = (Integer)message.getValue(9);
			precision = (Vector2)message.getValue(10);
			edgeFlags = (Integer)message.getValue(11);
			event = MotionEvent.obtain(downTime, eventTime, action, position.x * mOffset.receivedAdjust + mOffset.receiveOffset.x, position.y * mOffset.receivedAdjust + mOffset.receiveOffset.y, pressure, size, metaState, precision.x, precision.y, 0, edgeFlags);
			
			mScene.getEngine().safe().touch(event).unsafe();
			event.recycle();
			break;
		case MotionEvent.ACTION_MOVE:
		case MotionEvent.ACTION_HOVER_MOVE:
			
			downTime = (Long)message.getValue(2);
			eventTime = (Long)message.getValue(3);
			pressure = (Float)message.getValue(4);
			size = (Float)message.getValue(5);
			metaState = (Integer)message.getValue(6);
			precision = (Vector2)message.getValue(7);
			position = (Vector2)message.getValue(8);
			edgeFlags = (Integer)message.getValue(9);
			pointerCount = (Integer)message.getValue(10);
			
			
			for(index=0; index<pointerCount; index++) {
				id = (Integer)message.getValue(11 + index * 4);
				pressure = (Float)message.getValue(12 + index * 4);
				size = (Float)message.getValue(13 + index * 4);
				pointerPosition = (Vector2)message.getValue(14 + index * 4);
				
				event = MotionEvent.obtain(downTime, eventTime | (index << MotionEvent.ACTION_POINTER_INDEX_SHIFT), action, pointerPosition.x * mOffset.receivedAdjust + mOffset.receiveOffset.x, pointerPosition.y * mOffset.receivedAdjust + mOffset.receiveOffset.y, pressure, size, metaState, precision.x, precision.y, 0, edgeFlags);
				
				mScene.getEngine().safe().touch(event).unsafe();
				event.recycle();
			}
			
		}
	}
	
	/**
	 * Object Message
	 */
	final public void onMessage(final ConnectionInfo connectionInfo, final ObjectMessage objectMessage) {
		if(objectMessage.getCode() == mComCode) {
			// Message
			switch((Integer)objectMessage.getValue(0)) {
			// Request Received
			case CONNECTION_REQUEST:
				// For prevention, consider connection
				if(mConnecting || mConnected) {
					// Accept connection if connecting in this side
					mComManager.sendForAll(ObjectMessage.create(mComCode).add(CONNECTION_REQUEST_ACCEPTED).build());
					// Prevent
					if(!mConnected)
						mComManager.sendForAll(ObjectMessage.create(mComCode).add(CONNECTION_REQUEST).build());
				}
				break;
			// Request Received in other side
			// It is not necessary to continue with the request
			case CONNECTION_REQUEST_ACCEPTED:
				// Connection accepted
				mConnecting = false;
				break;
			case TOUCH_DATA:
				touchData(objectMessage);
				break;
			}
		}
	}
	
	/**
	 * Destroy object
	 */
	final public void destroy() {
		mScene.removeTouchableListener(mTouchableListener);
	}
}
