package ru.practicum.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.client.BaseClient;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.item.dto.ItemDto;

@Service
public class ItemClient extends BaseClient {

    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> create(ItemDto itemDto, long ownerId) {

        return post("", ownerId, itemDto);
    }

    public ResponseEntity<Object> update(long itemId, ItemDto itemDto, long ownerId) {

        String url = String.format("/%d", itemId);

        return patch(url, ownerId, itemDto);
    }

    public ResponseEntity<Object> findById(long userId, long itemId) {

        String url = String.format("/%d", itemId);

        return get(url, userId);
    }

    public ResponseEntity<Object> findAllByOwnerId(long ownerId) {

        String url = String.format("?from=%d&size=%d");

        return get(url, ownerId);
    }

    public ResponseEntity<Object> search(String text) {

        String url = String.format("/search?text=%s&from=%d&size=%d", text);

        return get(url);
    }

    public ResponseEntity<Object> addComment(CommentDto itemDto, long itemId, long userId) {

        String url = String.format("/%d/comment", itemId);

        return post(url, userId, itemDto);
    }
}
