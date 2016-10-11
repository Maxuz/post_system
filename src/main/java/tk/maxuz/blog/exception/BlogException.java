package tk.maxuz.blog.exception;

public class BlogException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public BlogException() {
		super();
	}

	public BlogException(String message) {
		super(message);
	}

	public BlogException(String message, Throwable reason) {
		super(message, reason);
	}
}
