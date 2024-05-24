package ru.practicum.shareit.request.service;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.JpaItemRepository;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.JpaRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.JpaUserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class RequestServiceImpl implements RequestService {

    private final JpaRequestRepository requestRepository;

    private final JpaUserRepository userRepository;

    private final JpaItemRepository itemRepository;

    private final ModelMapper modelMapper;


    @Override
    @Transactional
    public Request add(Integer userId, RequestDto requestDto) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(User.class, String.valueOf(userId),
                        "Пользователь с id " + userId + " не найден."));


        Request newRequest = modelMapper.map(requestDto, Request.class);

        newRequest.setCreated(LocalDateTime.now());
        newRequest.setRequestor(user);

        return requestRepository.save(newRequest);

    }

    @Override
    public List<RequestDto> getRequestsSent(Integer userId) {

        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(User.class, String.valueOf(userId),
                        "Пользователь с id " + userId + " не найден."));


        List<Request> requestList = requestRepository.findByRequestorIdOrderByCreatedDesc(userId);
        List<Integer> idList = requestList.stream()
                .map(Request::getId)
                .collect(Collectors.toList());


        Map<Integer, List<RequestDto.RequestItemDto>> items = itemRepository.findAllByRequestIdIn(idList)
                .stream()
                .map(ItemMapper::toRequestItemDto)
                .collect(groupingBy(RequestDto.RequestItemDto::getRequestId, toList()));

        return requestList.stream()
                .map(request -> {
                    RequestDto requestDto = modelMapper.map(request, RequestDto.class);
                    requestDto.setItems(items.getOrDefault(request.getId(), Collections.emptyList()));
                    return requestDto;
                })
                .collect(Collectors.toList());

    }

    @Override
    public RequestDto getRequest(Integer requestId, Integer userId) {


        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(User.class, String.valueOf(userId),
                        "Пользователь с id " + userId + " не найден."));

        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException(Request.class, String.valueOf(requestId),
                        "Запрос с id " + requestId + " не найден."));

        Map<Integer, List<RequestDto.RequestItemDto>> items = itemRepository.findAllByRequestIdIn(List.of(requestId))
                .stream()
                .map(ItemMapper::toRequestItemDto)
                .collect(groupingBy(RequestDto.RequestItemDto::getRequestId, toList()));

        RequestDto requestDto = modelMapper.map(request, RequestDto.class);
        requestDto.setItems(items.getOrDefault(request.getId(), Collections.emptyList()));

        return requestDto;

    }

//    @Override
//    public List<RequestDto> getRequests(int from, int size) {
//
//        validateSearchParameters(from, size);
//
//        List<Request> allRequests = requestRepository.findAll();
//        allRequests.sort(Comparator.comparing(Request::getCreated).reversed());
//
//
//        int startIndex = Math.min(from, allRequests.size());
//        int endIndex = Math.min(from + size, allRequests.size());
//
//        List<Request> paginatedRequests = allRequests.subList(startIndex, endIndex);
//
//
//        List<Integer> idList = paginatedRequests.stream()
//                .map(Request::getId)
//                .collect(Collectors.toList());
//
//        Map<Integer, List<RequestDto.RequestItemDto>> items = itemRepository.findAllByRequestIdIn(idList)
//                .stream()
//                .map(ItemMapper::toRequestItemDto)
//                .collect(Collectors.groupingBy(RequestDto.RequestItemDto::getRequest_id));
//
//        return paginatedRequests.stream()
//                .map(request -> {
//                    RequestDto requestDto = modelMapper.map(request, RequestDto.class);
//                    requestDto.setItems(items.getOrDefault(request.getId(), Collections.emptyList()));
//                    return requestDto;
//                })
//                .collect(Collectors.toList());
//    }


//    @Override
//    public List<RequestDto> getRequests(int from, int size) {
//
//        validateSearchParameters(from, size);
//
//        // Pageable sortedByCreated = PageRequest.of(from, size, Sort.by("created").descending());
//
////        Pageable sortedByCreated = FromSizeRequest.of(from, size, true);
//
////        int page = from / size; // Calculate page number from the 'from' parameter
////        Pageable sortedByCreated = PageRequest.of(page, size, Sort.by("created").descending());
//
////        boolean newestFirst = Sort.by("created").descending();
//
//        final PageRequest pageRequest = PageRequest.of(from / size, size, Sort.by("created").descending());
//
//        // Page<Request> requestPage = requestRepository.findAll(sortedByCreated);
//
//        Page<Request> requestPage = requestRepository.findAll(pageRequest);
//
//
//        List<Request> requestList = requestPage.getContent();
//
//
//        if (requestList.isEmpty()) {
//            return Collections.emptyList();
//        }
//
//
//        List<Integer> idList = requestList.stream()
//                .map(Request::getId)
//                .collect(Collectors.toList());
//
//        Map<Integer, List<RequestDto.RequestItemDto>> items = itemRepository.findAllByRequestIdIn(idList)
//                .stream()
//                .map(ItemMapper::toRequestItemDto)
//                .collect(Collectors.groupingBy(RequestDto.RequestItemDto::getRequest_id));
//
//        return requestList.stream()
//                .map(request -> {
//                    RequestDto requestDto = modelMapper.map(request, RequestDto.class);
//                    requestDto.setItems(items.getOrDefault(request.getId(), Collections.emptyList()));
//                    return requestDto;
//                })
//                .collect(Collectors.toList());
//
//    }


    @Override
    public List<RequestDto> getRequests(Integer from, Integer size, Integer userId) {

        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(User.class, String.valueOf(userId),
                        "Пользователь с id " + userId + " не найден."));

        validateSearchParameters(from, size);

        Pageable pageable = PageRequest.of(from / size, size, Sort.by("created").descending());

        // Page<Request> requestPage = requestRepository.findAll(pageable);

        Page<Request> requestPage = requestRepository.findAllByRequestorIdNot(userId, pageable);

        List<Request> requestList = requestPage.getContent();

        if (requestList.isEmpty()) {
            return Collections.emptyList();
        }

        List<Integer> idList = requestList.stream()
                .map(Request::getId)
                .collect(Collectors.toList());

        Map<Integer, List<RequestDto.RequestItemDto>> items = itemRepository.findAllByRequestIdIn(idList)
                .stream()
                .map(ItemMapper::toRequestItemDto)
                .collect(Collectors.groupingBy(RequestDto.RequestItemDto::getRequestId));

        return requestList.stream()
                .map(request -> {
                    RequestDto requestDto = modelMapper.map(request, RequestDto.class);
                    requestDto.setItems(items.getOrDefault(request.getId(), Collections.emptyList()));
                    return requestDto;
                })
                .collect(Collectors.toList());
    }






    private void validateSearchParameters(Integer from, Integer size) {

        if (from == 0 && size == 0 ) {
            throw new BadRequestException(Integer.class, from + " & " + size,
                    "Некорректные параметры поиска: from = " + from + " и " + " size = " + size);
        }

        if (from < 0 || size < 0) {
            throw new BadRequestException(Integer.class, from + " & " + size,
                    "Некорректные параметры поиска: from = " + from + " и " + " size = " + size);
        }

    }


}