/**
 * 
 */
package pgdiff.parsers;

import pgdiff.Resources;
import pgdiff.schema.PgDatabase;
import pgdiff.schema.PgRule;
import pgdiff.schema.PgSchema;

import java.text.MessageFormat;

/**
 * Parses CREATE RULE statements.
 *
 * @author Charles
 *
 */
public class CreateRuleParser {

    /**
     * Parses CREATE VIEW statement.
     *
     * @param database database
     * @param statement CREATE VIEW statement
     */
    public static void parse(final PgDatabase database,
            final String statement) {
        final Parser parser = new Parser(statement);

        parser.expect("CREATE");
        parser.expectOptional("OR", "REPLACE");
        parser.expect("RULE");

        final String ruleName = parser.parseIdentifier();

        final PgRule rule = new PgRule(ParserUtils.getObjectName(ruleName));

        parser.expect("AS", "ON");

        if (parser.expectOptional("INSERT")) {
            rule.setEvent("INSERT");
        } else if (parser.expectOptional("UPDATE")) {
           rule.setEvent("UPDATE");
        } else if (parser.expectOptional("DELETE")) {
            rule.setEvent("DELETE");
        } else if (parser.expectOptional("SELECT")) {
            rule.setEvent("SELECT");
        } else {
            parser.throwUnsupportedCommand();
        }

        parser.expect("TO");

        final String relationName = parser.parseIdentifier();

        final String query = parser.getRest();

        rule.setRelationName(ParserUtils.getObjectName(relationName));

        rule.setQuery(query);

        final String schemaName = ParserUtils.getSchemaName(relationName, database);
        final PgSchema schema = database.getSchema(schemaName);

        if (schema == null) {
            throw new RuntimeException(MessageFormat.format(
                    Resources.getString("CannotFindSchema"), schemaName,
                    statement));
        }

        schema.addRelation(rule);
        
         schema.getRelation(rule.getRelationName()).addRule(rule);
    }

    /**
     * Creates a new instance of CreateRuleParser.
     */
    private CreateRuleParser() {
    }
}
