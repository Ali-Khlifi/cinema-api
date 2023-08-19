package fr.uga.gestioncinema.utils;

import lombok.experimental.UtilityClass;


/**
 * defines common constants and there classification
 * @author ali khlifi
 */
@UtilityClass
public class StringUtils {

    /**
     * defines all runtime profiles of the application
     */
    @UtilityClass
    public class Profiles {
        public static final String DEFAULT = "default";
        public static final String PROD = "prod";
        public static final String DEV = "dev";
    }

    /**
     * defines all exceptions messages
     */
    @UtilityClass
    public class Exceptions {
        public static final String INVALID_QUERY = "provided query parameters are incorrectly formed";
        public static final String NO_DATA = "nothing was found matching provided parameters";
        public static final String EXISTS = "cannot create already stored data";
        public static final String NOT_EXISTS = "cannot update values of none existing data";
        public static final String UNKOWN = "an unexcpected error occured";
    }

    /**
     * defines all assertions messages
     */
    @UtilityClass
    public class Assertions {
        public static final String MUST_BE_RANGEABLE = "must be a rangeable type";
        public static final String MUST_BE_MIN_MAX_PAIR = "must be a min-max pair";
        public static final String MUST_BE_STRING = "must be a string";
        public static final String MUST_BE_SINGULAR = "must be a singular value";
        public static final String MUST_CONTAIN_VALUES = "must contain at least one value";
    }

    /**
     * defines all security related constants
     */
    @UtilityClass
    public class Securities {
        public static final String ANGULAR_ORIGIN = "http://localhost:4200";
    }
}
