/**
 * 
 */
package pgdiff.parsers;

import pgdiff.Resources;
import pgdiff.schema.PgDatabase;
import pgdiff.schema.PgProcedure;
import pgdiff.schema.PgSchema;

import java.text.MessageFormat;

/**
 * Parses CREATE PROCEDURE and CREATE OR REPLACE PROCEDURE statements.
 *
 * @author Charles
 *
 */
public class CreateProcedureParser {

    /**
     * Parses CREATE PROCEDURE and CREATE OR REPLACE PROCEDURE statement.
     *
     * @param database  database
     * @param statement CREATE PROCEDURE statement
     */
    public static void parse(final PgDatabase database,
            final String statement) {
        final Parser parser = new Parser(statement);
        parser.expect("CREATE");
        parser.expectOptional("OR", "REPLACE");
        parser.expect("PROCEDURE");

        final String procedureName = parser.parseIdentifier();
        final String schemaName =
                ParserUtils.getSchemaName(procedureName, database);
        final PgSchema schema = database.getSchema(schemaName);

        if (schema == null) {
            throw new RuntimeException(MessageFormat.format(
                    Resources.getString("CannotFindSchema"), schemaName,
                    statement));
        }

        final PgProcedure procedure = new PgProcedure();
        procedure.setName(ParserUtils.getObjectName(procedureName));
        schema.addProcedure(procedure);

        parser.expect("(");

        while (!parser.expectOptional(")")) {
            final String mode;

            if (parser.expectOptional("IN")) {
                mode = "IN";
            } else if (parser.expectOptional("OUT")) {
                mode = "OUT";
            } else if (parser.expectOptional("INOUT")) {
                mode = "INOUT";
            } else if (parser.expectOptional("VARIADIC")) {
                mode = "VARIADIC";
            } else {
                mode = null;
            }

            final int position = parser.getPosition();
            String argumentName = null;
            String dataType = parser.parseDataType();

            final int position2 = parser.getPosition();

            if (!parser.expectOptional(")") && !parser.expectOptional(",")
                    && !parser.expectOptional("=")
                    && !parser.expectOptional("DEFAULT")) {
                parser.setPosition(position);
                argumentName =
                        ParserUtils.getObjectName(parser.parseIdentifier());
                dataType = parser.parseDataType();
            } else {
                parser.setPosition(position2);
            }

            final String defaultExpression;

            if (parser.expectOptional("=")
                    || parser.expectOptional("DEFAULT")) {
                defaultExpression = parser.getExpression();
            } else {
                defaultExpression = null;
            }

            final PgProcedure.Argument argument = new PgProcedure.Argument();
            argument.setDataType(dataType);
            argument.setDefaultExpression(defaultExpression);
            argument.setMode(mode);
            argument.setName(argumentName);
            procedure.addArgument(argument);

            if (parser.expectOptional(")")) {
                break;
            } else {
                parser.expect(",");
            }
        }

        procedure.setBody(parser.getRest());
    }

    /**
     * Creates a new instance of CreateProcedureParser.
     */
    private CreateProcedureParser() {
    }
}
