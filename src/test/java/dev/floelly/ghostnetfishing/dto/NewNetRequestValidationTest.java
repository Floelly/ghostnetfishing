package dev.floelly.ghostnetfishing.dto;

import dev.floelly.ghostnetfishing.model.NetSize;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Set;
import java.util.stream.Stream;

import static dev.floelly.ghostnetfishing.testutil.TestDataFactory.*;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class NewNetRequestValidationTest {
    private static final Stream<Double> INVALID_LATITUDES = Stream.of(-90.0001, 90.0001, null);
    private static final Stream<Double> VALID_LATITUDES = Stream.of(-90.0, 5.000000000001, 90.0);
    private static final Stream<Double> INVALID_LONGITUDES = Stream.of(-180.1, 180.0000001, null);
    private static final Stream<Double> VALID_LONGITUDES = Stream.of(-180.0, -32.123456, 180.0);
    public static final double VALID_LATITUDE = 0.0;
    public static final double VALID_LONGITUDE = 0.0;
    public static final NetSize VALID_SIZE = NetSize.L;

    private static Validator validator;

    private NewNetRequest defaultValidRequest;

    @BeforeAll
    static void beforeAll() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    public static Stream<Double> provideInvalidLongitudes() {
        return INVALID_LONGITUDES;
    }

    public static Stream<Double> provideValidLongitudes() {
        return VALID_LONGITUDES;
    }

    @BeforeEach
    void setUp() {
        defaultValidRequest = new NewNetRequest(VALID_LATITUDE, VALID_LONGITUDE, VALID_SIZE);
    }

    @ParameterizedTest
    @MethodSource("provideInvalidLatitudes")
    void shouldFailForLatitudeOutOfRange(Double lat) {
        defaultValidRequest.setLocationLat(lat);

        Set<ConstraintViolation<NewNetRequest>> violations = validator.validate(defaultValidRequest);

        assertThat(violations).isNotEmpty();
        assertThat(violations.stream().map(v -> v.getPropertyPath().toString()))
                .contains(LOCATION_LAT);
    }

    @ParameterizedTest
    @MethodSource("provideValidLatitudes")
    void shouldBeValidLatitude(Double lat) {
        defaultValidRequest.setLocationLat(lat);

        Set<ConstraintViolation<NewNetRequest>> violations = validator.validate(defaultValidRequest);

        assertThat(violations).isEmpty();
    }

    @ParameterizedTest
    @MethodSource("provideInvalidLongitudes")
    void shouldFailForLongitudeOutOfRange(Double lon) {
        defaultValidRequest.setLocationLong(lon);

        Set<ConstraintViolation<NewNetRequest>> violations = validator.validate(defaultValidRequest);

        assertThat(violations).isNotEmpty();
        assertThat(violations.stream().map(v -> v.getPropertyPath().toString()))
                .contains(LOCATION_LONG);
    }

    @ParameterizedTest
    @MethodSource("provideValidLongitudes")
    void shouldBeValidLongitude(Double lon) {
        defaultValidRequest.setLocationLong(lon);

        Set<ConstraintViolation<NewNetRequest>> violations = validator.validate(defaultValidRequest);

        assertThat(violations).isEmpty();
    }

    private static Stream<Double> provideInvalidLatitudes() {
        return INVALID_LATITUDES;
    }

    public static Stream<Double> provideValidLatitudes() {
        return VALID_LATITUDES;
    }
}