package ru.practicum.shareit.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.common.exception.NotFoundException;
import ru.practicum.shareit.user.UserService;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository repository;
    private final UserService userService;
    private final Validator validator;

    @Override
    @Transactional
    public ItemRequest createItemRequest(final ItemRequest itemRequest) {
        Objects.requireNonNull(itemRequest, "Cannot create item request: is null");
        Set<ConstraintViolation<ItemRequest>> violations = validator.validate(itemRequest);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

        // TODO: Remove the following code after sprint #16
        // Hardcoded Postman test does not expect that one item request id may be skipped while creating an item request
        // by unknown user
        Objects.requireNonNull(itemRequest.getRequester().getId(), "Cannot create item request: requester id is null");
        userService.getUser(itemRequest.getRequester().getId());

        final ItemRequest createdItemRequest = repository.save(itemRequest);
        log.info(("Created item request with id = {}: {}"), createdItemRequest.getId(), createdItemRequest);
        return createdItemRequest;
    }

    @Override
    public ItemRequest getItemRequest(final long id) {
        return repository.findById(id).orElseThrow(
                () -> new NotFoundException(ItemRequest.class, id)
        );
    }

    @Override
    public ItemRequest getItemRequestWithRelations(final long id, long userId) {
        userService.getUser(userId);
        return repository.findByIdWithRelations(id).orElseThrow(
                () -> new NotFoundException(ItemRequest.class, id)
        );
    }

    @Override
    public List<ItemRequest> getOwnRequests(final long userId) {
        userService.getUser(userId);
        Sort sort = Sort.by(Sort.Direction.DESC, "created");
        return repository.findAllByRequesterId(userId, sort);
    }

    @Override
    public List<ItemRequest> getOthersRequests(long userId) {
        userService.getUser(userId);
        Sort sort = Sort.by(Sort.Direction.DESC, "created");
        return repository.findAllOtherByRequesterId(userId, sort);
    }
}
