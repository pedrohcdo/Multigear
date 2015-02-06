package multigear.mginterface.graphics.opengl.font;

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
			if(drawer.mPrepared) {
				VertexBufferObject elementsVBO = drawer.mElementsVBO;
				VertexBufferObject colorsVBO = drawer.mColorsVBO;
				VertexBufferObject texturesVBO = drawer.mTexturesVBO;
				VertexBufferObject indicesVBO = drawer.mIndicesIBO;
				int count = drawer.mElementsCount;
				renderer.setVBO(elementsVBO, colorsVBO, texturesVBO);
				renderer.render(indicesVBO, count);
			}
		}
	}
}
