package ru.practicum.shareit.item;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.common.BaseController;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
class ItemController extends BaseController {

    private final ItemService itemService;
    private final ItemMapper mapper;

    @PostMapping
    public ItemRetrieveDto createItem(
            @RequestHeader("X-Sharer-User-Id") final long userId,
            @Valid @RequestBody final ItemCreateDto itemCreateDto,
            final HttpServletRequest request
    ) {
        logRequest(request, itemCreateDto);
        final Item item = mapper.mapToItem(itemCreateDto);
        final ItemRetrieveDto dto = mapper.mapToDto(itemService.createItem(item, userId));
        logResponse(request, dto);
        return dto;
    }

    @GetMapping("/{id}")
    public ItemRetrieveDto getItem(
            @PathVariable final long id,
            final HttpServletRequest request
    ) {
        logRequest(request);
        final ItemRetrieveDto dto = mapper.mapToDto(itemService.getItem(id));
        logResponse(request, dto);
        return dto;
    }

    @GetMapping
    public List<ItemRetrieveDto> getItems(
            @RequestHeader("X-Sharer-User-Id") final long userId,
            final HttpServletRequest request
    ) {
        logRequest(request);
        final List<ItemRetrieveDto> dtos = mapper.mapToDto(itemService.getItems(userId));
        logResponse(request, dtos);
        return dtos;
    }

    @GetMapping("/search")
    public List<ItemRetrieveDto> getItems(
            @RequestHeader("X-Sharer-User-Id") final long userId,
            @RequestParam final String text,
            final HttpServletRequest request
    ) {
        logRequest(request);
        final List<ItemRetrieveDto> dtos = mapper.mapToDto(itemService.getItems(text, userId));
        logResponse(request, dtos);
        return dtos;
    }

    @PatchMapping("/{id}")
    public ItemRetrieveDto updateItem(
            @RequestHeader("X-Sharer-User-Id") final long userId,
            @PathVariable final long id,
            @RequestBody final ItemUpdateDto itemUpdateDto,
            final HttpServletRequest request
    ) {
        logRequest(request, itemUpdateDto);
        final Item item = mapper.mapToItem(itemUpdateDto);
        final ItemRetrieveDto dto = mapper.mapToDto(itemService.updateItem(id, item, userId));
        logResponse(request, dto);
        return dto;
    }

    @DeleteMapping("/{id}")
    public void deleteItem(
            @RequestHeader("X-Sharer-User-Id") final long userId,
            @PathVariable final long id,
            final HttpServletRequest request
    ) {
        logRequest(request);
        itemService.deleteItem(id, userId);
        logResponse(request);
    }
}
