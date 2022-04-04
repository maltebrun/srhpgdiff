/**
 * 
 */
package pgdiff;

import java.io.PrintWriter;

/**
 * Helps to output search path only if it was not output yet.
 *
 * @author Charles
 *
 */
public class SearchPathHelper {
    /**
     * Statement to output.
     */
    private final String searchPath;
    /**
     * Flag determining whether the statement was already output.
     */
    private boolean wasOutput;

    /**
     * Creates new instance of SearchPathHelper.
     *
     * @param searchPath {@link #searchPath}
     */
    public SearchPathHelper(final String searchPath) {
        this.searchPath = searchPath;
    }

    /**
     * Outputs search path if it was not output yet.
     *
     * @param writer writer
     */
    public void outputSearchPath(final PrintWriter writer) {
        if (!wasOutput && searchPath != null && !searchPath.isEmpty()) {
            writer.println();
            writer.println(searchPath);
            wasOutput = true;
        }
    }
}
