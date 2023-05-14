package ru.practicum.shareit.features.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.features.client.BaseClient;
import ru.practicum.shareit.features.user.model.UserDto;

@Service
public class UserClient extends BaseClient {
    private static final String API_PREFIX = "/users";

    @Autowired
    public UserClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> getAll() {
        return get("");
    }

    public ResponseEntity<Object> getUserDtoById(long id) {
        return get("/" + id, id);
    }

    public ResponseEntity<Object> create(UserDto userDto) {
        return post("", userDto);
    }

    public ResponseEntity<Object> patch(long id, String json) {
        return patch("/" + id, json);
    }

    public void deleteById(long id) {
        delete("/" + id, id);
    }
}
