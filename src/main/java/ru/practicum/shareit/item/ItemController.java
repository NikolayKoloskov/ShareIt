package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.model.ItemDTO;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;

@RestController("itemController")
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @GetMapping("/{id}")
    public ItemDTO findById(@RequestHeader(value = "X-Sharer-User-Id") int userId,
                            @PathVariable("id") int id) {
        return itemService.findById(id);
    }

    @GetMapping
    public Collection<ItemDTO> findAllByOwner(@RequestHeader(value = "X-Sharer-User-Id") int userId) {
        return itemService.findByUserId(userId);
    }

    @PostMapping()
    public ResponseEntity<ItemDTO> createItem(@RequestHeader(value = "X-Sharer-User-Id") String userId,
                                              @Valid @RequestBody ItemDTO itemDTO) {
        itemDTO.setOwner(Integer.parseInt(userId));
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("X-Sharer-User-Id",
                String.valueOf(itemDTO.getOwner()));

        return ResponseEntity.ok()
                .headers(responseHeaders)
                .body(itemService.create(itemDTO));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ItemDTO> updateItem(@RequestHeader(value = "X-Sharer-User-Id") String userId,
                                              @PathVariable("id") int id,
                                              @RequestBody ItemDTO itemDTO) {
        itemDTO.setOwner(Integer.parseInt(userId));
        itemDTO.setId(id);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("X-Sharer-User-Id",
                String.valueOf(itemDTO.getOwner()));
        return ResponseEntity.ok().body(itemService.update(itemDTO));
    }

    @GetMapping("/search")
    public Collection<ItemDTO> search(@RequestParam String text) {
        return itemService.findByText(text);
    }

}
