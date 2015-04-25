package multigear.mginterface.graphics.opengl.font;

import java.nio.FloatBuffer;

import multigear.mginterface.graphics.opengl.programs.LetterRenderer;
import multigear.mginterface.graphics.opengl.vbo.VertexBufferObject;
import android.annotation.SuppressLint;

/**
 * Text Extender
 * 
 * @author user
 * 
 */
public class LetterWrapper {

	/**
	 * Process Writer to Draw
	 * 
	 * @param text
	 *            Text to draw
	 */
	@SuppressLint("WrongCall")
	final static public void processDrawer(final Letter letter, final LetterRenderer renderer) {
		if (renderer != null) {
			LetterDrawer drawer = letter.mLetterDrawer;
			FloatBuffer elements = drawer.mElements;
			FloatBuffer colors = drawer.mColors;
			FloatBuffer textures = drawer.mTextures;
			int count = drawer.mElementsCount;
			renderer.setBuffers(elements, colors, textures);
			renderer.render(count);
		}
	}
}
