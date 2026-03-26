package ru.practicum.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.booking.dto.BookingDto;
import ru.practicum.client.BaseClient;

import java.util.Map;

@Service
public class BookingClient extends BaseClient {

    private static final String API_PREFIX = "/bookings";
    private static final String GET_ALL_OWNER_PATCH = "/owner?state=%s&from=%d&size=%d";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> add(BookingDto bookingDto, Long userId) {
        return post("", userId, bookingDto);
    }

    public ResponseEntity<Object> updateStatus(Long bookingId, Long userId, Boolean approved) {
        String url = String.format("/%d?approved=%s", bookingId, approved);
        return patch(url, userId, null);
    }

    public ResponseEntity<Object> getBookingById(long userId, long bookingId) {
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> getAllUsersBookings(Long userId, BookingStatus status) {
        Map<String, Object> parameters = Map.of(
                "state", status.name());
        return get("?state={state}", userId, parameters);
    }

    public ResponseEntity<Object> getAllItemOwnerBookings(Long userId, BookingStatus status) {
        String url = String.format(GET_ALL_OWNER_PATCH, status.name());
        return get(url, userId);
    }
}
