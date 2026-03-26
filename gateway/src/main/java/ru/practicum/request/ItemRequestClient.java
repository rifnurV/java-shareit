package ru.practicum.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.client.BaseClient;

@Service
public class ItemRequestClient extends BaseClient {

    private static final String API_PREFIX = "/requests";

    @Autowired
    public ItemRequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }


    public ResponseEntity<Object> createItemRequest(long requesterId, String text) {

        return post("", requesterId, text);
    }

    public ResponseEntity<Object> getByRequestId(long userId, long requestId) {

        String url = String.format("/%d", requestId);

        return get(url, userId);
    }

    public ResponseEntity<Object> getAll() {

        String url = String.format("/all?from=%d&size=%d", null, null);

        return get(url);
    }

    public ResponseEntity<Object> getAllByRequesterId(long requesterId) {

        return get("", requesterId);
    }
}
