package ru.practicum.shareit.item;

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
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemService itemService;
    private final ItemMapper mapper;

    @PostMapping
    public ItemDto createItem(
            @RequestHeader("X-Sharer-User-Id") final long userId,
            @RequestBody final NewItemDto newItemDto) {
        log.info("Received POST as /items: {} (X-Sharer-User-Id: {})", newItemDto, userId);
        final Item item = mapper.mapToItem(newItemDto);
        final ItemDto dto = mapper.mapToDto(itemService.createItem(item, userId));
        log.info("Responded to POST /items: {}", dto);
        return dto;
    }

    @GetMapping("/{id}")
    public ItemDto getUser(
            @RequestHeader("X-Sharer-User-Id") final long userId,
            @PathVariable final long id) {
        log.info("Received GET at /items/{} (X-Sharer-User-Id: {})", id, userId);
        final ItemDto dto = mapper.mapToDto(itemService.getItem(id, userId));
        log.info("Responded to GET /items/{}: {}", id, dto);
        return dto;
    }

    @GetMapping
    public List<ItemDto> getItems(@RequestHeader("X-Sharer-User-Id") final long userId) {
        log.info("Received GET at /items (X-Sharer-User-Id: {})", userId);
        final List<ItemDto> dtos = mapper.mapToDto(itemService.getItems(userId));
        log.info("Responded to GET /items: {}", dtos);
        return dtos;
    }

    @GetMapping("/search")
    public List<ItemDto> getItems(@RequestHeader("X-Sharer-User-Id") final long userId,
            @RequestParam final String text) {
        log.info("Received GET at /items/search?text={} (X-Sharer-User-Id: {})", text, userId);
        final List<ItemDto> dtos = mapper.mapToDto(itemService.getItems(text, userId));
        log.info("Responded to GET /items/search?text={} : {}", text, dtos);
        return dtos;
    }

    @PatchMapping("/{id}")
    public ItemDto updateItem(
            @RequestHeader("X-Sharer-User-Id") final long userId,
            @PathVariable final long id,
            @RequestBody final UpdateItemDto updateItemDto) {
        log.info("Received PATCH at /items/{}: {} (X-Sharer-User-Id: {})", id, updateItemDto, userId);
        final Item item = mapper.mapToItem(updateItemDto);
        final ItemDto dto = mapper.mapToDto(itemService.updateItem(id, item, userId));
        log.info("Responded to PATCH /items/{}: {}", id, dto);
        return dto;
    }

    @DeleteMapping("/{id}")
    public void deleteItem(@RequestHeader("X-Sharer-User-Id") final long userId,
            @PathVariable final long id) {
        log.info("Received DELETE at /items/{} (X-Sharer-User-Id: {})", id, userId);
        itemService.deleteItem(id, userId);
        log.info("Responded to DELETE /items/{} with no body", id);
    }
}
