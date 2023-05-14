package ru.practicum.shareit.features.booking;

import org.apache.commons.lang3.EnumUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.exceptions.WrongStateArgumentException;
import ru.practicum.shareit.features.booking.model.BookingShortDto;
import ru.practicum.shareit.features.booking.model.State;
import ru.practicum.shareit.features.client.BaseClient;

import java.util.Map;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> getBookingById(long userId, Long bookingId) {
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> getBookingsOfBooker(long userId, String state, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "state", getState(state),
                "from", from,
                "size", size
        );
        return get("?state={state}&from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> getBookingsOfOwner(long userId, String state, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "state", getState(state),
                "from", from,
                "size", size
        );
        return get("/owner?state={state}&from={from}&size={size}", userId, parameters);
    }


    public ResponseEntity<Object> postBooking(long userId, BookingShortDto bookingShortDto) {
        return post("", userId, bookingShortDto);
    }

    public ResponseEntity<Object> patchBooking(long userId, Long bookingId, Boolean approved) {
        return patch("/" + bookingId + "?approved=" + approved, userId, approved);
    }

    private State getState(String state) {
        if (!EnumUtils.isValidEnum(State.class, state)) {
            throw new WrongStateArgumentException("Unknown state: " + state);
        } else {
            return State.valueOf(state);
        }
    }
}