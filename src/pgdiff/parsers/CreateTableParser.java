/**
 * 
 */
package pgdiff.parsers;

import pgdiff.Resources;
import pgdiff.schema.PgColumn;
import pgdiff.schema.PgConstraint;
import pgdiff.schema.PgDatabase;
import pgdiff.schema.PgSchema;
import pgdiff.schema.PgTable;
import java.text.MessageFormat;

/**
 * Parses CREATE TABLE statements.
 *
 * @author Charles
 *
 */
public class CreateTableParser {

    /**
     * Parses CREATE TABLE statement.
     *
     * @param database  database
     * @param statement CREATE TABLE statement
     * @param ignoreSchemaCreation whether schema creation should be ignored
     */
    public static void parse(final PgDatabase database,
            final String statement, final boolean ignoreSchemaCreation) {
        final Parser parser = new Parser(statement);
        parser.expect("CREATE");
        final boolean unlogged = parser.expectOptional("UNLOGGED");
        final boolean foreign = parser.expectOptional("FOREIGN");
        parser.expect("TABLE");

        // Optional IF NOT EXISTS, irrelevant for our purposes
        parser.expectOptional("IF", "NOT", "EXISTS");

        final String tableName = parser.parseIdentifier();
        final String schemaName =
                ParserUtils.getSchemaName(tableName, database);
        PgSchema schema = database.getSchema(schemaName);

        if (schema == null) {
            if (ignoreSchemaCreation) {
                schema = new PgSchema(schemaName);
                database.addSchema(schema);
            } else {
                throw new RuntimeException(MessageFormat.format(
                    Resources.getString("CannotFindSchema"), schemaName,
                    statement));
            }
        }

        final PgTable table = new PgTable(ParserUtils.getObjectName(tableName), database, schema);
        table.setUnlogged(unlogged);
        table.setForeign(foreign);
        schema.addRelation(table);

        parser.expect("(");

        while (!parser.expectOptional(")")) {
            if (parser.expectOptional("CONSTRAINT")) {
                parseConstraint(parser, table);
            } else if (parser.expectOptional("PRIMARY", "KEY")) {
                throw new ParserException(Resources.getString(
                        "CreateTablePrimaryKeyNotSupported"));
            } else if (parser.expectOptional("UNIQUE")) {
                throw new ParserException(
                        Resources.getString("CreateTableUniqueNotSupported"));
            } else {
                parseColumn(parser, table);
            }

            if (parser.expectOptional(")")) {
                break;
            } else {
                parser.expect(",");
            }
        }

        while (!parser.expectOptional(";")) {
            if (parser.expectOptional("PARTITION","BY")) {
                table.setPartionBy("PARTITION BY "+parser.getExpression());
            } else if (parser.expectOptional("INHERITS")) {
                parseInherits(database, parser, table);
            } else if (parser.expectOptional("WITHOUT")) {
                table.setWith("OIDS=false");            
            } else if (parser.expectOptional("WITH")) {
                if (parser.expectOptional("OIDS")
                        || parser.expectOptional("OIDS=true")) {
                    table.setWith("OIDS=true");
                } else if (parser.expectOptional("OIDS=false")) {
                    table.setWith("OIDS=false");
                } else {
                    table.setWith(parser.getExpression());
                }
            } else if (parser.expectOptional("TABLESPACE")) {
                table.setTablespace(parser.parseString());
            } else if (parser.expectOptional("SERVER")) {
            	table.setForeignServer(parser.getExpression());
            } else {
            	parser.throwUnsupportedCommand();
            }
        }
    }

    /**
     * Parses INHERITS.
     *
     * @param database database
     * @param parser parser
     * @param table  pg table
     */
    private static void parseInherits(final PgDatabase database,final Parser parser,
            final PgTable table) {
        parser.expect("(");

        while (!parser.expectOptional(")")) {
         final String parsedString = parser.parseIdentifier();
         final String tableName = ParserUtils.getObjectName(parsedString);
         final String schemaName = parsedString.contains(".") ?
             ParserUtils.getSecondObjectName(parsedString) :
             database.getDefaultSchema().getName();
            table.addInherits(schemaName, tableName);
            if (parser.expectOptional(")")) {
                break;
            } else {
                parser.expect(",");
            }
        }
    }

    /**
     * Parses CONSTRAINT definition.
     *
     * @param parser parser
     * @param table  table
     */
    private static void parseConstraint(final Parser parser,
            final PgTable table) {
        final PgConstraint constraint = new PgConstraint(
                ParserUtils.getObjectName(parser.parseIdentifier()));
        table.addConstraint(constraint);
        constraint.setDefinition(parser.getExpression());
        constraint.setTableName(table.getName());
    }

    /**
     * Parses column definition.
     *
     * @param parser parser
     * @param table  table
     */
    private static void parseColumn(final Parser parser, final PgTable table) {
        final PgColumn column = new PgColumn(
                ParserUtils.getObjectName(parser.parseIdentifier()));
        table.addColumn(column);
        column.parseDefinition(parser.getExpression());
    }

    /**
     * Creates a new instance of CreateTableParser.
     */
    private CreateTableParser() {
    }
}
