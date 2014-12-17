package multigear.general.utils;

/**
 * 
 * Classe utilisada como Linha.
 * 
 * @author PedroH, RaphaelB
 *
 * Property SpringBall.
 */
final public class Line2D {
	
	// Variables
	public multigear.general.utils.Ref2F Start;
	public multigear.general.utils.Ref2F End;
	
	/*
	 * Comnstrutor
	 */
	public Line2D(multigear.general.utils.Ref2F start, multigear.general.utils.Ref2F end) {
		Start = start;
		End = end;
	}
}