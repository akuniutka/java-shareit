package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.common.exception.NotFoundException;
import ru.practicum.shareit.user.UserService;

import java.util.List;
import java.util.Objects;

@Service
@Validated
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
class RequestServiceImpl implements RequestService {

    private final RequestRepository repository;
    private final UserService userService;

    @Override
    @Transactional
    public Request createRequest(final Request request) {
        Objects.requireNonNull(request, "Cannot create request: is null");

        // TODO: Remove the following code after sprint #16
        // Hardcoded Postman test does not expect that one item request id may be skipped while creating an item request
        // by unknown user
        Objects.requireNonNull(request.getRequester().getId(), "Cannot create request: requester id is null");
        userService.getUser(request.getRequester().getId());

        final Request createdRequest = repository.save(request);
        log.info(("Created request with id = {}: {}"), createdRequest.getId(), createdRequest);
        return createdRequest;
    }

    @Override
    public Request getRequest(final long id) {
        return repository.findById(id).orElseThrow(
                () -> new NotFoundException(Request.class, id)
        );
    }

    @Override
    public Request getRequestWithRelations(final long id, long userId) {
        userService.getUser(userId);
        return repository.findByIdWithRelations(id).orElseThrow(
                () -> new NotFoundException(Request.class, id)
        );
    }

    @Override
    public List<Request> getOwnRequests(final long userId, final int from, final int size) {
        userService.getUser(userId);
        final Sort sort = Sort.by(Sort.Direction.DESC, "created");
        final Pageable page = PageRequest.of(from / size, size, sort);
        return repository.findAllByRequesterId(userId, page);
    }

    @Override
    public List<Request> getOthersRequests(final long userId, final int from, final int size) {
        userService.getUser(userId);
        final Sort sort = Sort.by(Sort.Direction.DESC, "created");
        final Pageable page = PageRequest.of(from / size, size, sort);
        return repository.findAllOtherByRequesterId(userId, page);
    }
}
