/**
 * 
 */
package pgdiff.parsers;

import pgdiff.Resources;
import pgdiff.schema.PgDatabase;
import pgdiff.schema.PgSchema;
import pgdiff.schema.PgSequence;
import java.text.MessageFormat;

/**
 * Parses ALTER SEQUENCE statements.
 *
 * @author Charles
 *
 */
public class AlterSequenceParser {

    /**
     * Parses ALTER SEQUENCE statement.
     *
     * @param database                database
     * @param statement               ALTER SEQUENCE statement
     * @param outputIgnoredStatements whether ignored statements should be
     *                                output in the diff
     */
    public static void parse(final PgDatabase database,
            final String statement, final boolean outputIgnoredStatements) {
        final Parser parser = new Parser(statement);

        parser.expect("ALTER", "SEQUENCE");

        final String sequenceName = parser.parseIdentifier();
        final String schemaName =
                ParserUtils.getSchemaName(sequenceName, database);
        final PgSchema schema = database.getSchema(schemaName);

        if (schema == null) {
            throw new RuntimeException(MessageFormat.format(
                    Resources.getString("CannotFindSchema"), schemaName,
                    statement));
        }

        final String objectName = ParserUtils.getObjectName(sequenceName);
        final PgSequence sequence = schema.getSequence(objectName);

        if (sequence == null) {
            throw new RuntimeException(MessageFormat.format(
                    Resources.getString("CannotFindSequence"), sequenceName,
                    statement));
        }

        while (!parser.expectOptional(";")) {

            if (parser.expectOptional("OWNED", "BY")) {
                if (parser.expectOptional("NONE")) {
                    sequence.setOwnedBy(null);
                } else {
                    sequence.setOwnedBy(parser.getExpression());
                }
            } else {
                parser.throwUnsupportedCommand();
            }
        }
    }

    /**
     * Creates new instance of AlterSequenceParser.
     */
    private AlterSequenceParser() {
    }
}
