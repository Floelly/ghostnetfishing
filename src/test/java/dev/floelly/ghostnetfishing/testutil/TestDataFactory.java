package dev.floelly.ghostnetfishing.testutil;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public final class TestDataFactory {
    public static final String NEW_NET_REQUEST = "newNetRequest";
    public static final String LOCATION_LAT = "locationLat";
    public static final String LOCATION_LONG = "locationLong";
    public static final String SIZE = "size";
    public static final List<String> NEW_NET_PARAMETERS = List.of(LOCATION_LAT, LOCATION_LONG, SIZE);
    public static final String EMPTY_STRING = "";
    public static final String WRONG_NET_LOCATION_LAT = "91.0";
    public static final String WRONG_NET_LOCATION_LONG = "-180.1";
    public static final String WRONG_NET_SIZE = "XXS";
    public static final String NETS_NEW_ENDPOINT = "/nets/new";
    public static final String NETS_ENDPOINT = "/nets";
    public static final String REQUEST_NET_RECOVERY_ENDPOINT = "/nets/%d/request-recovery";
    public static final String MARK_NET_RECOVERED_ENDPOINT = "/nets/%d/mark-recovered";
    public static final String LOGIN_ENDPOINT = "/login";
    public static final String NEW_GHOST_NET_HEADLINE = "Report new ghost net";
    public static final String SUCCESSFULLY_REPORTED_NET_MESSAGE = "New net added successfully";
    public static final String LATITUDE = "Latitude";
    public static final String LONGITUDE = "Longitude";
    public static final String LAYOUT_HTML_TAG = "footer";
    public static final String NEW_NET_FORM_QUERY_SELECTOR = "form[method=post][action='" + NETS_NEW_ENDPOINT + "']";
    public static final String INVALID_FEEDBACK_QUERY_SELECTOR = ".invalid-feedback";
    public static final Map<String, String> EXPECTED_SIZE_OPTIONS = Map.of(
            "", "- Choose size -",
            "S", "S - (Diameter up to 10 m)",
            "M", "M - (Diameter up to 30 m)",
            "L", "L - (Diameter up to 100 m)",
            "XL", "XL - (Diameter over 100 m)"
    );
    public static final String DB_COLUMN_LATITUDE = "location_lat";
    public static final String DB_COLUMN_LONGITUDE = "location_long";
    public static final String DB_COLUMN_SIZE = "size";
    public static final String DB_COLUMN_NETS = "nets";
    public static final String STANDARD_ROLE = "STANDARD";

    public static final String MARK_RECOVERED_FORM_QUERY = "form[method=post][action$=/mark-recovered]";
    public static final String REQUEST_RECOVERY_FORM_QUERY = "form[method=post][action$=/request-recovery]";
    public static final String SUBMIT_BUTTON_QUERY = "button[type=submit]";
    public static final String NET_ID_TR_QUERY = "tr[data-net-id=%s]";
    public static final String TABLE_ROWS_QUERY_SELECTOR = "main tbody tr";

    public static String formatDouble(double d) {
        return getDoubleFormat().format(d);
    }

    public static DecimalFormat getDoubleFormat() {
        DecimalFormat df = new DecimalFormat("#.####");
        df.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.US));
        df.setDecimalSeparatorAlwaysShown(false);
        return df;
    }

    public static double getRandomLongitude() {
        return ThreadLocalRandom.current().nextDouble(-180, 180);
    }

    public static double getRandomLatitude() {
        return ThreadLocalRandom.current().nextDouble(-90, 90);
    }
}
