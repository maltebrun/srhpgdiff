/**
 * 
 */
package pgdiff;

import java.util.ResourceBundle;

/**
 * Utility class for accessing localized resources.
 *
 * @author Charles
 *
 */

public class Resources {
	 /**
     * Resource bundle.
     */
    private static final ResourceBundle RESOURCE_BUNDLE =
//            ResourceBundle.getBundle("cz/startnet/utils/pgdiff/Resources");
    		ResourceBundle.getBundle("pgdiff/Resources");

    /**
     * Returns string from resource bundle based on the key.
     *
     * @param key key
     *
     * @return string
     */
    public static String getString(final String key) {
        return RESOURCE_BUNDLE.getString(key);
    }

    /**
     * Creates new instance of Resources.
     */
    private Resources() {
    }
}
