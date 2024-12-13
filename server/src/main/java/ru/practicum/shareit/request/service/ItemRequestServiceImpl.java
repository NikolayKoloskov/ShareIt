package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestSaveDto;
import ru.practicum.shareit.request.dto.ItemResponseToRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final UserRepository userRepo;
    private final ItemRepository itemRepo;
    private final ItemMapper itemMapper;
    private final ItemRequestRepository itemRequestRepo;
    private final ItemRequestMapper itemRequestMapper;


    @Override
    public ItemRequestDto createItemRequest(int userId, ItemRequestSaveDto itemRequestSaveDto) {
        log.info("Создание заявки на предмет пользователем с id - {}, {}", userId, itemRequestSaveDto);
        User user = userRepo.findById(userId).orElseThrow(
                () -> new UserNotFoundException("Пользователь с id " + userId + " не найден."));
        ItemRequest itemRequest = itemRequestMapper.map(itemRequestSaveDto);
        itemRequest.setRequester(user);
        ItemRequest savedItemRequest = itemRequestRepo.save(itemRequest);
        log.info("Создана заявка на предмет пользователем с id - {}, {}", userId, savedItemRequest);
        return itemRequestMapper.map(savedItemRequest);
    }

    @Override
    public Collection<ItemRequestDto> getAllUserItemRequest(int userId) {
        log.info("Получение всех заявок пользователя с id - {}", userId);
        Collection<ItemRequestDto> result = itemRequestMapper.map(itemRequestRepo.findAllByRequesterIdOrderByCreatedDesc(userId));
        return getRequestsWithItems(userId, result);

    }

    @Override
    public Collection<ItemRequestDto> getAllItemRequests(int userId) {
        log.info("Получение всех заявок кроме пользователя с id - {}", userId);
        Collection<ItemRequestDto> result = itemRequestMapper
                .map(itemRequestRepo.findAllByRequester_IdNotInOrderByCreatedDesc(List.of(userId)));
        return getRequestsWithItems(userId, result);
    }

    @Override
    public ItemRequestDto getItemRequest(int requestId) {
        log.info("Получение заявки с id - {}", requestId);
        ItemRequest itemRequest = itemRequestRepo.findById(requestId)
                .orElseThrow(
                        () -> new ItemNotFoundException("Заявка с id " + requestId + " не найдена."));
        Collection<Item> items = itemRepo.findAllByRequestId(requestId);
        items.forEach(item -> log.debug("item id - {}, owner id - {}", item.getId(), item.getOwner().getId()));
        ItemRequestDto itemRequestDto = itemRequestMapper.map(itemRequest);
        itemRequestDto.setItems(itemMapper.mapToResponseToRequest(items));
        log.info("Найдена заявка {}", itemRequestDto);
        return itemRequestDto;
    }

    private Collection<ItemRequestDto> getRequestsWithItems(int userId, Collection<ItemRequestDto> result) {
        Collection<Integer> itemsIds = itemRequestRepo.findAllByRequesterIdOrderByCreatedDesc(userId)
                .stream()
                .map(ItemRequest::getId)
                .toList();
        Collection<ItemResponseToRequestDto> items = itemRequestMapper.mapItems(itemRepo.findAllByIdIn(itemsIds));
        Map<Integer, List<ItemResponseToRequestDto>> itemsMap = items.stream()
                .collect(Collectors.groupingBy(ItemResponseToRequestDto::getId));
        return result.stream()
                .peek(itemRequest -> itemRequest.setItems(itemsMap.get(itemRequest.getId())))
                .collect(Collectors.toList());
    }
}
