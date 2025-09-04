package dev.floelly.ghostnetfishing.testutil;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public final class TestDataFactory {

    //Request parameter
    public static final String NEW_NET_REQUEST = "newNetRequest";
    public static final String LOCATION_LAT = "locationLat";
    public static final String LOCATION_LONG = "locationLong";
    public static final String SIZE = "size";
    public static final List<String> NEW_NET_PARAMETERS = List.of(LOCATION_LAT, LOCATION_LONG, SIZE);
    public static final Map<String, String> EXPECTED_SIZE_OPTIONS = Map.of(
            "", "- Choose size -",
            "S", "S - (Diameter up to 10 m)",
            "M", "M - (Diameter up to 30 m)",
            "L", "L - (Diameter up to 100 m)",
            "XL", "XL - (Diameter over 100 m)"
    );

    //Request parameter values
    public static final String EMPTY_STRING = "";
    public static final String INVALID_NET_LOCATION_LAT = "91.0";
    public static final String INVALID_NET_LOCATION_LONG = "-180.1";
    public static final String INVALID_NET_SIZE = "XXS";
    public static final String LOST = "LOST";
    public static final String RECOVERED = "RECOVERED";
    public static final String RECOVERY_PENDING = "RECOVERY_PENDING";
    public static final String REPORTED = "REPORTED";
    public static final String INVALID_NET_ID = "invalidNetId";

    //controller endpoints
    public static final String NETS_NEW_ENDPOINT = "/nets/new";
    public static final String NETS_ENDPOINT = "/nets";
    public static final String LOGIN_ENDPOINT = "/login";
    public static final String REQUEST_NET_RECOVERY_ENDPOINT = "/nets/%d/request-recovery";
    public static final String MARK_NET_RECOVERED_ENDPOINT = "/nets/%d/mark-recovered";
    public static final String MARK_NET_LOST_ENDPOINT = "/nets/%d/mark-lost";

    //Thymeleaf templates & attributes
    public static final String NEW_NET_CONTENT_TEMPLATE = NETS_NEW_ENDPOINT;
    public static final String NETS_CONTENT_TEMPLATE = NETS_ENDPOINT;
    public static final String POST_NEW_NET_REDIRECT_TEMPLATE = NETS_CONTENT_TEMPLATE;
    public static final String REQUEST_RECOVERY_REDIRECT_TEMPLATE = NETS_CONTENT_TEMPLATE;
    public static final String MARK_RECOVERED_REDIRECT_TEMPLATE = NETS_CONTENT_TEMPLATE;
    public static final String MARK_LOST_REDIRECT_TEMPLATE = NETS_CONTENT_TEMPLATE;
    public static final String NETS_THYMELEAF_ATTRIBUTE = "nets";


    //db implementation info
    public static final String DB_COLUMN_LATITUDE = "location_lat";
    public static final String DB_COLUMN_LONGITUDE = "location_long";
    public static final String DB_COLUMN_SIZE = "size";
    public static final String DB_TABLE_NETS = "nets";
    public static final String SPRING_SECURITY_STANDARD_ROLE = "STANDARD";
    public static final String SPRING_SECURITY_RECOVERER_ROLE = "RECOVERER";

    //sql data
    public static final String REPORTED_NET_ID = "1001";
    public static final String LOST_NET_ID = "1002";
    public static final String RECOVERED_NET_ID = "1003";
    public static final String RECOVERY_PENDING_NET_ID = "1004";
    public static final String NOT_EXISTING_NET_ID = "0";
    public static final String USERNAME = "user";
    public static final String USERNAME_WITH_NUMBER = "userwithnumber";
    public static final String USERNAME_WITH_NUMBER_AND_NET = "userwithnumberandnet";
    public static final String ADMIN_USERNAME = "admin";

    //front end messages
    public static final String NEW_GHOST_NET_HEADLINE = "Report new ghost net";
    public static final String LATITUDE = "Latitude";
    public static final String LONGITUDE = "Longitude";
    public static final String LAYOUT_HTML_TAG = "footer";
    public static final String SUCCESSFULLY_REPORTED_NET_MESSAGE = "New net added successfully";
    public static final String[] INVALID_ID_TOAST_MESSAGE = {INVALID_NET_ID, "parameter"};
    public static final String[] NO_PERMISSION_TOAST_MESSAGE = {"no", "permission"};
    public static final String[] ID_NOT_FOUND_TOAST_MESSAGE = {NOT_EXISTING_NET_ID, "id", "not found"};
    public static final String[] ILLEGAL_STATE_CHANGE_TOAST_MESSAGE = {"state"};

    //generate random values
    public static String getRandomLongitude() {
        double d = ThreadLocalRandom.current().nextDouble(-180, 180);
        return formatDouble(d);
    }
    public static String getRandomLatitude() {
        double d = ThreadLocalRandom.current().nextDouble(-90, 90);
        return formatDouble(d);
    }
    public static String getRandomNetSize() {
        return EXPECTED_SIZE_OPTIONS.keySet().stream().filter(s -> !s.isEmpty()).iterator().next();
    }
    public static long getRandomNetId() {
        return ThreadLocalRandom.current().nextLong();
    }
    private static String formatDouble(double d) {
        return getDoubleFormat().format(d);
    }
    private static DecimalFormat getDoubleFormat() {
        DecimalFormat df = new DecimalFormat("#.####");
        df.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.US));
        df.setDecimalSeparatorAlwaysShown(false);
        return df;
    }
}
