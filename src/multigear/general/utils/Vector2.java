package multigear.general.utils;


/**
 * @brief Classe para manipulação de um vetor 2D
 *
 * Essa classe define operações matemáticas básicas para um vetor
 * em 2 dimensões
 */
final public class Vector2 implements Cloneable
{
	
	// Variables
	public float x;
	public float y;
	
	/**
	 * @brief Constrói o vetor nulo (0, 0)
	 *
	 * Os valores de x e y serão 0
	 */
	public Vector2() {
		x = 0;
		y = 0;
	}
	
	/**
	 * @brief Constrói o vetor usando x e y
	 * @param [in] x O valor do eixo x
	 * @param [in] y O valor do eixo y
	 */
	public Vector2(float nx, float ny) {
		x = nx;
		y = ny;
	}	
	
	/**
	 * @brief Constrói o vetor usando outro vetor como referência
	 * @param [in] vec O outro vetor
	 *
	 * O vetor criado será uma cópia de vec
	 */
	public Vector2(Vector2 vec) {
		x = vec.x;
		y = vec.y;
	}
	
	/**
	 * @brief Seta os valores desta instancia
	 * @param [in] x O valor do eixo x
	 * @param [in] y O valor do eixo y
	 */
	final public void set(float nx, float ny) {
		x = nx;
		y = ny;
	}	
	
	/**
	 * @brief Seta os valores desta instancia
	 * @param [in] vec O outro vetor
	 *
	 */
	final public void set(Vector2 vec) {
		x = vec.x;
		y = vec.y;
	}
	
	/**
	 * @brief Adiciona membro a membro os valores ao vetor
	 * @param [in] x O valor incremental do eixo x
	 * @param [in] y O valor incremental do eixo y
	 */
	final public void sum(float nx, float ny) {
		x += nx;
		y += ny;
	}
	
	/**
	 * @brief Adiciona um valor do vetor
	 * @param [in] vec O vetor a ser adicionado a este
	 */
	final public void sum(Vector2 vec) { this.sum(vec.x, vec.y); }
	
	/**
	 * @brief Realiza a soma de dois vetores
	 * @param [in] vetor da esquerda
	 * @param [in] vetor da direita
	 */
	final public static Vector2 sum(Vector2 vec1, Vector2 vec2) { 
		return new Vector2(vec1.x + vec2.x, vec1.y + vec2.y);
	}
	
	/**
	 * @brief Subtrai membro a membro os valores ao vetor
	 * @param [in] x O valor a ser subtraido do eixo x
	 * @param [in] y O valor a ser subtraido do eixo y
	 */
	final public void sub(float nx, float ny) {
		x -= nx;
		y -= ny;
	}
	
	/**
	 * @brief Subtrai o valor do vetor
	 * @param [in] vec O vetor a ser adicionado a este
	 */
	final public void sub(Vector2 vec) { this.sub(vec.x, vec.y); }
	
	/**
	 * @brief Realiza a subtração de dois vetores
	 * @param [in] vetor da esquerda
	 * @param [in] vetor da direita
	 */
	final public static Vector2 sub(Vector2 vec1, Vector2 vec2) { 
		return new Vector2(vec1.x - vec2.x, vec1.y - vec2.y);
	}
	
	/**
	 * @brief Escala o vetor
	 * @param [in] factor O fator para escalar
	 */
	final public void scale(float factor) { this.scale( factor, factor ); }
	
	/**
	 * @brief Escala o vetor
	 * @param [in] factorx O fator da escala no eixo x
	 * @param [in] factory O fator da escala no eixo y
	 */
	final public void scale(float factorX, float factorY) {
		x *= factorX;
		y *= factorY;
	}
	
	/**
	 * @brief Escala o vetor
	 * @param [in] Fatores do eixo x e y
	 */
	final public void scale(Vector2 factor) {
		x *= factor.x;
		y *= factor.y;
	}
	
	/**
	 * @brief Realiza a escala de um vetor
	 * @param [in] vetor da esquerda
	 * @param [in] factor fator da escala dos dois eixos
	 */
	final public static Vector2 scale(Vector2 vec, float factor) { 
		return new Vector2(vec.x * factor, vec.y * factor);
	}
	
	/**
	 * @brief Realiza a escala de um vetor
	 * @param [in] vetor da esquerda
	 * @param [in] factorx O fator da escala no eixo x
	 * @param [in] factory O fator da escala no eixo y
	 * @return Retorna o resultado da operação
	 */
	final public static Vector2 scale(Vector2 vec, float factorx, float factory) { 
		return new Vector2(vec.x * factorx, vec.y * factory);
	}
	
	/**
	 * @brief Realiza a escala de um vetor
	 * @param [in] vetor da esquerda
	 * @param [in] fatores organizados em um vetor
	 * @return Retorna o resultado da operação
	 */
	final public static Vector2 scale(Vector2 vec1, Vector2 vec2) { 
		return new Vector2(vec1.x * vec2.x, vec1.y * vec2.y);
	}
	
	/**
	 * @brief Divide o vetor usando o fator
	 * @param [in] factor O fator da divisão
	 */
	final public void div(float factor) { 
		this.div( factor, factor );
	}
	
	/**
	 * @brief Divide o vetor
	 * @param [in] factorx O fator da escala no eixo x
	 * @param [in] factory O fator da escala no eixo y
	 */
	final public void div(float factorx, float factory) {
		x /= factorx;
		y /= factory;
	}
	
	/**
	 * @brief Realiza a divisão de um vetor
	 * @param [in] vetor da esquerda
	 * @param [in] factor O fator da divisão nos dois eixos
	 * @return Retorna o resultado da operação
	 */
	final public static Vector2 div(Vector2 vec, float factor) { 
		return new Vector2(vec.x / factor, vec.y / factor);
	}
	
	/**
	 * @brief Realiza a divisão de um fator por um vetor
	 * @param [in] factor O fator da divisão nos dois eixos
	 * @param [in] vetor
	 * @return Retorna o resultado da operação
	 */
	final public static Vector2 div(float factor, Vector2 vec) { 
		return new Vector2(factor / vec.x, factor / vec.y);
	}
	
	/**
	 * @brief Realiza a divisão de um vetor
	 * @param [in] vetor da esquerda
	 * @param [in] factorx O fator da divisão no eixo x
	 * @param [in] factory O fator da divisão no eixo y
	 * @return Retorna o resultado da operação
	 */
	final public static Vector2 div(Vector2 vec, float factorx, float factory) { 
		return new Vector2(vec.x / factorx, vec.y / factory);
	}
	
	/**
	 * @brief Realiza a divisão de um fator por um vetor
	 * @param [in] factorx O fator da divisão no eixo x
	 * @param [in] factory O fator da divisão no eixo y
	 * @param [in] vetor
	 * @return Retorna o resultado da operação
	 */
	final public static Vector2 div(float factorx, float factory, Vector2 vec) { 
		return new Vector2(factorx / vec.x, factory / vec.y);
	}
	
	/**
	 * @brief Realiza a divisão de um vetor
	 * @param [in] vetor da esquerda
	 * @param [in] fatores organizados em um vetor
	 * @return Retorna o resultado da operação
	 */
	final public static Vector2 div(Vector2 vec1, Vector2 vec2) { 
		return new Vector2(vec1.x / vec2.x, vec1.y / vec2.y);
	}
	
	/**
	 * @brief Produto escalar do vetor
	 * @param [in] vec O outro vetor
	 * @return O valor do produto escalar
	 */
	final public float dot(Vector2 vec) { 
		return x * vec.x + y * vec.y; 
	}
	
	/**
	 * @brief Realiza o produto escalar entre dois vetores
	 * @param [in] vetor da esquerda
	 * @param [in] vetor da direita
	 * @return Retorna o resultado da operação
	 */
	final public static float dot(Vector2 vec1, Vector2 vec2) { 
		return vec1.x * vec2.x + vec1.y * vec2.y;
	}
	
	/**
	 * @brief Faz o produto vetorial do vetor e retorna o eixo Z
	 * @param [in] vec O outro vetor
	 * @return O valor do eixo Z do produto vetorial
	 */
	final public float cross(Vector2 vec) { 
		return x * vec.y - y * vec.x; 
	}
	
	/**
	 * @brief Realiza o produto vetorial entre dois vetores e retorna o
	 * eixo Z
	 * @param [in] vetor da esquerda
	 * @param [in] vetor da direita
	 * @return Retorna o resultado da operação
	 */
	final public static float cross(Vector2 vec1, Vector2 vec2) { 
		return vec1.x * vec2.y - vec1.y * vec2.x;
	}
	
	/** 
	 * Get Vector length
	 * @return
	 */
	final public float length() {
		return (float)Math.hypot(x, y);
	}
	
	/** 
	 * Distance until the vector
	 * @return
	 */
	final public float distance(final Vector2 vec) {
		return (float)Math.hypot(x-vec.x, y-vec.y);
	}
	
	/** 
	 * Distance between the two vectors
	 * @return
	 */
	final static public float distance(final Vector2 vec1, final Vector2 vec2) {
		return (float)Math.hypot(vec1.x-vec2.x, vec1.y-vec2.y);
	}
	
	/** 
	 * Angle formed by this vector
	 * @return Angle in degree (-180...180)
	 */
	final public float angle() {
		return (float)GeneralUtils.radToDegree(Math.atan2(y, x));
	}
	
	/** 
	 * Angle formed by two vectors.
	 * @return Angle in degree (-180...180)
	 */
	final public float angle(final Vector2 vec) {
		return (float)GeneralUtils.radToDegree(Math.atan2(y-vec.y, x-vec.x));
	}
	
	/** 
	 * Angle formed by two vectors.
	 * @return Angle in degree (-180...180)
	 */
	final static public float angle(final Vector2 vec1, final Vector2 vec2) {
		return (float)GeneralUtils.radToDegree(Math.atan2(vec1.x-vec2.y, vec1.y-vec2.x));
	}
	
	/** 
	 * Position within the line formed by the two vectors.
	 * @return
	 */
	final public Vector2 position(final Vector2 vec, final float control) {
		return Vector2.sum(Vector2.scale(Vector2.sub(vec, this), control), this);
	}
	
	/** 
	 * Position within the line formed by the two vectors.
	 * @return
	 */
	final public static Vector2 position(final Vector2 vec1, final Vector2 vec2, final float control) {
		return Vector2.sum(Vector2.scale(Vector2.sub(vec2, vec1), control), vec2);
	}
	
	/** 
	 * Rotate Vector.
	 * @param vec Vector to rotate
	 * @param angle Angle to rotate
	 * @param Direction to rotate vector
	 * @return
	 */
	final public static Vector2 rotate(final Vector2 vec, final float angle) {
		final float length = vec.length();
		double newAng = GeneralUtils.degreeToRad(vec.angle() + angle);
		final float newX = (float)(Math.cos(newAng) * length);
		final float newY = (float)(Math.sin(newAng) * length);
		return new Vector2(newX, newY);
	}
	
	/**
	 * Return Aspect Ratio x/y.<br>
	 * Same as obj.x / obj.y.
	 * @return
	 */
	final public float aspectRatio() {
		return x / y;
	}
	
	/**
	 * To String
	 */
	final public String toString() {
		return "Vector2<" + x + ", " + y + ">";
	}
	
	/**
	 * Retorna o clone desta instancia
	 * @return Retorna uma nova instancia com os mesmos valores
	 */
	final public Vector2 clone() {
		return new Vector2(x, y);
	}
}
