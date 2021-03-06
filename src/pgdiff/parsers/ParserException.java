/**
 * 
 */
package pgdiff.parsers;

/**
 * Thrown if parsing problem occurred.
 * 
 * @author Charles
 *
 */
public class ParserException extends RuntimeException {

    /**
     * Serial version uid.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new instance of {@code ParserException} without detail message.
     */
    public ParserException() {
    }

    /**
     * Constructs an instance of {@code ParserException} with the specified
     * detail message.
     *
     * @param msg the detail message
     */
    public ParserException(final String msg) {
        super(msg);
    }

    /**
     * Constructs an instance of {@code ParserException} with the specified
     * detail message.
     *
     * @param msg   the detail message
     * @param cause cause of the exception
     */
    public ParserException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}
