package multigear.general.utils;

import java.lang.Cloneable;

/**
 * @brief Classe para manipulação de um vetor 2D
 *
 * Essa classe define operações matemáticas básicas para um vetor
 * em 2 dimensões
 */
public class Vector2D
	implements Cloneable
{
	
	// Variables
	public float X;
	public float Y;
	
	/**
	 * @brief Constrói o vetor nulo (0, 0)
	 *
	 * Os valores de X e Y serão 0
	 */
	public Vector2D() {
		X = 0;
		Y = 0;
	}
	/**
	 * @brief Constrói o vetor usando X e Y
	 * @param [in] x O valor do eixo X
	 * @param [in] y O valor do eixo Y
	 */
	public Vector2D(float x, float y) {
		X = x;
		Y = y;
	}	
	/**
	 * @brief Constrói o vetor usando outro vetor como referência
	 * @param [in] vec O outro vetor
	 *
	 * O vetor criado será uma cópia de vec
	 */
	public Vector2D(Vector2D vec) {
		X = vec.X;
		Y = vec.Y;
	}
	/**
	 * @brief Adiciona membro a membro os valores ao vetor
	 * @param [in] x O valor incremental do eixo X
	 * @param [in] y O valor incremental do eixo Y
	 */
	public void add(float x, float y) {
		X += x;
		Y += y;
	}
	/**
	 * @brief Adiciona um valor do vetor
	 * @param [in] vec O vetor a ser adicionado a este
	 */
	public void add(Vector2 vec) { this.add(vec.X, vec.Y); }
	/**
	 * @brief Subtrai membro a membro os valores ao vetor
	 * @param [in] x O valor a ser subtraido do eixo X
	 * @param [in] y O valor a ser subtraido do eixo Y
	 */
	public void sub(float x, float y) {
		X -= x;
		Y -= y;
	}
	/**
	 * @brief Subtrai o valor do vetor
	 * @param [in] vec O vetor a ser adicionado a este
	 */
	public void sub(Vector2 vec) { this.sub(vec.X, vec.Y); }
	/**
	 * @brief Escala o vetor
	 * @param [in] factor O fator para escalar
	 */
	public void scale(float factor) { this.scale( factor, factor ); }
	/**
	 * @brief Escala o vetor
	 * @param [in] factorX O fator da escala no eixo X
	 * @param [in] factorY O fator da escala no eixo Y
	 */
	public void scale(float factorX, float factorY) {
		X *= factorX;
		Y *= factorY;
	}
	/**
	 * @brief Divide o vetor usando o fator
	 * @param [in] factor O fator da divisão
	 */
	public void div(float factor) { this.div( factor, factor ); }
	/**
	 * @brief Divide o vetor
	 * @param [in] factorX O fator da escala no eixo X
	 * @param [in] factorY O fator da escala no eixo Y
	 */
	public void div(float factorX, float factorY) {
		X /= factorX;
		Y /= factorY;
	}
	/**
	 * @brief Produto escalar do vetor
	 * @param [in] vec O outro vetor
	 * @return O valor do produto escalar
	 */
	public float dot(Vector2 vec) { return X * vec.X + Y * vec.Y; }
	/**
	 * @brief Faz o produto vetorial do vetor e retorna o eixo Z
	 * @param [in] vec O outro vetor
	 * @return O valor do eixo Z do produto vetorial
	 */
	public float cross(Vector2 vec) { return X * vec.Y - Y * vec.X; }
	
	/// Comprimento do vetor
	public float length() {	return Math.sqrt( X * X + Y * Y ); }	
}
