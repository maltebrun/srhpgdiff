/**
 * 
 */
package pgdiff.parsers;

import pgdiff.Resources;
import pgdiff.schema.PgDatabase;
import pgdiff.schema.PgSchema;
import pgdiff.schema.PgView;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Parses CREATE VIEW statements.
 *
 * @author Charles
 *
 */
public class CreateViewParser {

    /**
     * Parses CREATE VIEW statement.
     *
     * @param database  database
     * @param statement CREATE VIEW statement
     */
    public static void parse(final PgDatabase database,
            final String statement) {
        final Parser parser = new Parser(statement);

        parser.expect("CREATE");
        parser.expectOptional("OR", "REPLACE");
        final boolean materialized = parser.expectOptional("MATERIALIZED");        
        final boolean temporary = parser.expectOptional("TEMPORARY");
        final boolean recursive = parser.expectOptional("RECURSIVE");
        
        parser.expect("VIEW");

        final String viewName = parser.parseIdentifier();

        StringBuilder with = new StringBuilder();
        if (parser.expectOptional("WITH")) {
            parser.expect("(");
            while (!parser.expectOptional(")")) {
                with.append(parser.getExpression());

            }
        }

        final boolean columnsExist = parser.expectOptional("(");
        final List<String> columnNames = new ArrayList<String>(10);

        if (columnsExist) {
            while (!parser.expectOptional(")")) {
                columnNames.add(
                        ParserUtils.getObjectName(parser.parseIdentifier()));
                parser.expectOptional(",");
            }
        }

        parser.expect("AS");

        final String query = parser.getRest();

        final PgView view = new PgView(ParserUtils.getObjectName(viewName));
        view.setMaterialized(materialized);
        view.setTemporary(temporary);
        view.setRecursive(recursive);
        view.setWith(with.toString());
        view.setDeclaredColumnNames(columnNames);
        view.setQuery(query);

        final String schemaName = ParserUtils.getSchemaName(viewName, database);
        final PgSchema schema = database.getSchema(schemaName);

        if (schema == null) {
            throw new RuntimeException(MessageFormat.format(
                    Resources.getString("CannotFindSchema"), schemaName,
                    statement));
        }

        schema.addRelation(view);
    }

    /**
     * Creates a new instance of CreateViewParser.
     */
    private CreateViewParser() {
    }
}
