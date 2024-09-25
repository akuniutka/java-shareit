package ru.practicum.shareit.item;

import jakarta.servlet.http.HttpServletRequest;
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
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController extends BaseController {

    private final ItemService itemService;
    private final ItemMapper mapper;

    @PostMapping
    public ItemDto createItem(
            @RequestHeader("X-Sharer-User-Id") final long userId,
            @RequestBody final NewItemDto newItemDto,
            final HttpServletRequest request
    ) {
        logRequest(request, newItemDto);
        final Item item = mapper.mapToItem(newItemDto);
        final ItemDto dto = mapper.mapToDto(itemService.createItem(item, userId));
        logResponse(request, dto);
        return dto;
    }

    @GetMapping("/{id}")
    public ItemDto getUser(
            @RequestHeader("X-Sharer-User-Id") final long userId,
            @PathVariable final long id,
            final HttpServletRequest request
    ) {
        logRequest(request);
        final ItemDto dto = mapper.mapToDto(itemService.getItem(id, userId));
        logResponse(request, dto);
        return dto;
    }

    @GetMapping
    public List<ItemDto> getItems(
            @RequestHeader("X-Sharer-User-Id") final long userId,
            final HttpServletRequest request
    ) {
        logRequest(request);
        final List<ItemDto> dtos = mapper.mapToDto(itemService.getItems(userId));
        logResponse(request, dtos);
        return dtos;
    }

    @GetMapping("/search")
    public List<ItemDto> getItems(
            @RequestHeader("X-Sharer-User-Id") final long userId,
            @RequestParam final String text,
            final HttpServletRequest request
    ) {
        logRequest(request);
        final List<ItemDto> dtos = mapper.mapToDto(itemService.getItems(text, userId));
        logResponse(request, dtos);
        return dtos;
    }

    @PatchMapping("/{id}")
    public ItemDto updateItem(
            @RequestHeader("X-Sharer-User-Id") final long userId,
            @PathVariable final long id,
            @RequestBody final UpdateItemDto updateItemDto,
            final HttpServletRequest request
    ) {
        logRequest(request, updateItemDto);
        final Item item = mapper.mapToItem(updateItemDto);
        final ItemDto dto = mapper.mapToDto(itemService.updateItem(id, item, userId));
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
