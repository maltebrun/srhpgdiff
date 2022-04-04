/**
 * 
 */
package pgdiff;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

/**
 * Compares two PostgreSQL dumps and outputs information about differences in
 * the database schemas.
 *
 * @author Charles
 *
 */
public class Main {

	/**
	 * @param args
	 */
	public static void main(final String[] args) throws UnsupportedEncodingException {

		// TODO Auto-generated method stub
		System.out.println("The CEGEDIM-SRH schema diff");
				
        @SuppressWarnings("UseOfSystemOutOrSystemErr")
        final PrintWriter writer = new PrintWriter(System.out, true);
        final PgDiffArguments arguments = new PgDiffArguments();

        if (arguments.parse(writer, args)) {
            @SuppressWarnings("UseOfSystemOutOrSystemErr")
            final PrintWriter encodedWriter = new PrintWriter(
                    new OutputStreamWriter(
                    System.out, arguments.getOutCharsetName()));
            PgDiff.createDiff(encodedWriter, arguments);
            encodedWriter.close();
        }

        writer.close();		

	}
	
	/**
	 * Creates a new Main object.
	 */
	private Main() {
	
	}

}

