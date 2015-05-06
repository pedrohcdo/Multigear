package multigear.mginterface.graphics.drawable.gui.widgets.textview;

import multigear.general.utils.Color;
import multigear.general.utils.Vector2;
import multigear.mginterface.engine.eventsmanager.GlobalClock;
import multigear.mginterface.graphics.drawable.gui.widgets.listview.ListView;
import multigear.mginterface.graphics.drawable.gui.widgets.listview.ListView.DrawingHolder;
import multigear.mginterface.graphics.drawable.gui.widgets.listview.ListView.Style;
import multigear.mginterface.graphics.drawable.gui.widgets.listview.ListViewAdapter;
import multigear.mginterface.graphics.drawable.gui.widgets.listview.ListViewAdapter.ItemHolder;
import multigear.mginterface.graphics.drawable.polygon.Polygon;
import multigear.mginterface.graphics.drawable.widget.Widget;
import multigear.mginterface.graphics.opengl.drawer.Drawer;
import multigear.mginterface.graphics.opengl.font.FontMap;
import multigear.mginterface.graphics.opengl.font.Letter;
import multigear.mginterface.graphics.opengl.font.LetterDrawer;
import multigear.mginterface.graphics.opengl.font.LetterWriter;
import multigear.mginterface.graphics.opengl.texture.Texture;
import multigear.mginterface.scene.Scene;
import multigear.mginterface.scene.components.receivers.Drawable;
import android.util.Log;
import android.view.MotionEvent;

/**
 * Text View
 * 
 * @author user
 *
 */
final public class TextView extends Widget {

	/**
	 * Text View Adapter to ListView
	 * @author user
	 *
	 */
	final private class TextAdapter implements ListViewAdapter {

		/**
		 * Create Holder
		 */
		@Override
		public ItemHolder createItem(int index, ItemHolder reUse) {
			return new TextHolder();
		}

		/**
		 * One item
		 */
		@Override
		public int getCount() {
			return 1;
		}
	}
	
	/**
	 * 
	 * @author user
	 *
	 */
	final private class TextHolder implements ItemHolder {

		/**
		 * Draw Text
		 * 
		 * @param drawer
		 * @param drawingHolder
		 * @param cellSize
		 */
		@Override
		public void draw(Drawer drawer, DrawingHolder drawingHolder, Vector2 cellSize) {
			// Draw Item
			if(drawingHolder == DrawingHolder.ITEM)
				drawer.drawLetter(mLetter);
		}

		/**
		 * Touch not consumed
		 * 
		 * @param motionEvent
		 * @return
		 */
		@Override
		public int touch(MotionEvent motionEvent) {
			return 0;
		}

		/**
		 * Get Text Height
		 * @return
		 */
		@Override
		public float getHeight() {
			return mHeight;
		}
		
	}
	
	/**
	 * Attributes
	 * 
	 * @author user
	 *
	 */
	final public static class Attributes {
		public float border;
	}
	
	// Final Private Variables
	final private ListView mListView;
	final private FontMap mFontMap;
	final Attributes mAttributes = new Attributes();
	
	// Private Variables
	private String mText;
	private Letter mLetter;
	private float mHeight;
	private boolean mAutomaticScroll;
	private float mAutomaticScrollSpeed;
	private int mAutomaticScrollPhase;
	private long mAutomaticScrollTime;
	
	/**
	 * Constructor
	 */
	public TextView(final Scene scene, final FontMap font) {
		mAttributes.border = scene.getDensityParser().smallerValue(30);
		final ListView.Attributes attributes = new ListView.Attributes();
		attributes.border = mAttributes.border;
		mListView = new ListView(scene, new Vector2(32, 32));
		mListView.setAdapter(new TextAdapter());
		mListView.setStyle(Style.UNSELECTABLE);
		mFontMap = font;
		mLetter = new Letter();
		mLetter.setFontMap(font);
		mAutomaticScrollSpeed = scene.getDensityParser().biggerValue(1);
		setSize(new Vector2(32, 32));
	}
	
	/**
	 * Constructor
	 */
	public TextView(final Scene scene, final FontMap font, final Attributes attributes ) {
		mAttributes.border = attributes.border;
		final ListView.Attributes listAttributes = new ListView.Attributes();
		listAttributes.border = attributes.border;
		mListView = new ListView(scene, listAttributes, new Vector2(32, 32));
		mListView.setAdapter(new TextAdapter());
		mListView.setStyle(Style.UNSELECTABLE);
		mFontMap = font;
		mLetter = new Letter();
		mLetter.setFontMap(font);
		mAutomaticScrollSpeed = scene.getDensityParser().biggerValue(1);
		setSize(new Vector2(32, 32));
	}
	
	/**
	 * Set Size
	 */
	@Override
	public void setSize(Vector2 size) {
		super.setSize(size);
		mListView.setSize(size);
		writeText();
	}
	
	/**
	 * Set Text
	 * @param text
	 */
	final public void setText(final String text) {
		mText = text;
		mAutomaticScrollPhase = 0;
		mAutomaticScrollTime = GlobalClock.currentTimeMillis();
		mListView.setScrollPosition(0);
		writeText();
		mListView.reset();
	}
	
	/**
	 * Calc text height
	 * @return
	 */
	final public float getTextHeight(final float width) {
		final String[] words = mText.split("[ ]");
		final Vector2 position = new Vector2();
		Vector2 space = mFontMap.getTextSize(" ");
		float max = width - mAttributes.border * 4;
		for(final String word : words) {
			final float textWidth = mFontMap.getTextSize(word).x;
			if(position.x + textWidth >= max) {
				position.x = 0;
				position.y += space.y;
			}
			position.x += space.x + textWidth;
		}
		return position.y + space.y;
	}
	
	/**
	 * Set Scroll Position
	 * @param scrollPosition
	 */
	final public void setScrollPosition(final float scrollPosition) {
		mListView.setScrollPosition(scrollPosition);
	}
	
	/**
	 * Get Scroll Position
	 * @param scrollPosition
	 */
	final public float getScrollPosition() {
		return mListView.getScrollPosition();
	}
	
	/** 
	 * Set Background
	 * 
	 * @param color
	 */
	final public void setBackground(final Color color) {
		mListView.setBackground(color);
	}
	
	/**
	 * Set Background
	 * @param color
	 */
	final public void setBackground(final Color color, float radius, float detail) {
		mListView.setBackground(color, radius, detail);
	}
	
	/** 
	 * Set Background
	 * 
	 * @param color
	 */
	final public void setBackground(final Texture color) {
		mListView.setBackground(color);
	}
	
	/** 
	 * Set Background
	 * 
	 * @param color
	 */
	final public void setBackground(final Drawable color) {
		mListView.setBackground(color);
	}
	
	/**
	 * Set Scrollable
	 * @param scrollable
	 */
	final public void setScrollable(final boolean scrollable) {
		mListView.setScrollable(scrollable);
	}
	
	/**
	 * Get Scrollable
	 * @return
	 */
	final public boolean getScrollable() {
		return mListView.getScrollable();
	}
	
	/**
	 * Set Automatic Scroll
	 * @param trigger
	 */
	final public void setAutoScroll(final boolean trigger) {
		mAutomaticScroll = trigger;
		mAutomaticScrollPhase = 0;
		mAutomaticScrollTime = GlobalClock.currentTimeMillis();
	}
	
	/**
	 * Get Auto Scroll
	 * @return
	 */
	final public boolean getAutoScroll() {
		return mAutomaticScroll;
	}
	
	/**
	 * Set Automatic Scroll Speed
	 * @param speed
	 */
	final public void setAutoScrollSpeed(final float speed) {
		mAutomaticScrollSpeed = Math.max(0.1f, speed);
	}
	
	/**
	 * Get Automatic Scroll Speed
	 * @return
	 */
	final public float getAutoScrollSpeed() {
		return mAutomaticScrollSpeed;
	}
	
	/**
	 * Write Text
	 */
	final private void writeText() {
		mLetter = new Letter();
		if(mText == null)
			return;
		mLetter = new Letter();
		mLetter.setFontMap(mFontMap);
		mLetter.write(new LetterWriter() {
			
			@Override
			public void onDraw(FontMap fontMap, LetterDrawer letterDrawer) {
				final String[] words = mText.split("[ ]");
				final Vector2 position = new Vector2();
				Vector2 space = fontMap.getTextSize(" ");
				
				float max = getSize().x - mAttributes.border * 4;
				
				for(final String word : words) {
					
					final float textWidth = fontMap.getTextSize(word).x;
					
					if(position.x + textWidth >= max) {
						position.x = 0;
						position.y += space.y;
					}
					letterDrawer.drawText(word, position);
					
					position.x += space.x + textWidth;
					
				}
				
				mHeight = position.y + space.y;
			}
		});
	}
	
	/**
	 * Update
	 */
	@Override
	protected void onUpdate() {
		if(mAutomaticScroll) {
			switch(mAutomaticScrollPhase) {
			case 0:
				if((GlobalClock.currentTimeMillis() - mAutomaticScrollTime) >= 1200) {
					mAutomaticScrollPhase++;
				}
				break;
			case 1:
				mListView.setScrollPosition(mListView.getScrollPosition() + mAutomaticScrollSpeed);
				if(mListView.getScrollPosition() >= mListView.getMaxScroll()) {
					mAutomaticScrollPhase++;
					mAutomaticScrollTime = GlobalClock.currentTimeMillis();
				}
				break;
			case 2:
				if((GlobalClock.currentTimeMillis() - mAutomaticScrollTime) >= 1200) {
					mAutomaticScrollPhase++;
				}
				break;
			case 3:
				mListView.setScrollPosition(mListView.getScrollPosition() - mAutomaticScrollSpeed*5);
				if(mListView.getScrollPosition() <= 0) {
					mAutomaticScrollPhase = 0;
					mAutomaticScrollTime = GlobalClock.currentTimeMillis();
				}
				break;
			}
			
		}
	}
	
	/**
	 * Draw List
	 */
	@Override
	protected void onDraw(Drawer drawer, DrawingLayer drawingLayer) {
		// Draw list in top
		if(drawingLayer == DrawingLayer.LAYER_TOP)
			mListView.draw(drawer);
	}
	
	
	/**
	 * Touch List
	 */
	@Override
	protected void onTouch(MotionEvent motionEvent) {
		mListView.touch(motionEvent);
	}
}