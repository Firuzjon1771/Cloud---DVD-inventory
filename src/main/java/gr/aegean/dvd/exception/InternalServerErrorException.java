package gr.aegean.dvd.exception;

public class InternalServerErrorException extends Exception{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InternalServerErrorException() {
		super();
	}
	
	public InternalServerErrorException(String message) {
		super(message);
	}
}
