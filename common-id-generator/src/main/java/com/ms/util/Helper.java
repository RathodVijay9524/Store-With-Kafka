package com.ms.util;

import com.ms.dto.PageableResponse;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Helper {

    public static <U, V> PageableResponse<V> getPageableResponse(Page<U> page, Function<U, V> mapper) {
        List<V> dtoList = page.getContent().stream()
                .map(mapper)
                .collect(Collectors.toList());

        PageableResponse<V> response = new PageableResponse<>();
        response.setContent(dtoList);
        response.setPageNumber(page.getNumber());
        response.setPageSize(page.getSize());
        response.setTotalElements(page.getTotalElements());
        response.setTotalPages(page.getTotalPages());
        response.setLastPage(page.isLast());
        return response;
    }
}




/*
@Component
@RequiredArgsConstructor
public class Helper {

    private final UserMapper userMapper; // inject the correct mapper

    public <U, V> PageableResponse<V> getPageableResponse(Page<U> page, Function<U, V> mapperFunction) {
        List<V> dtoList = page.getContent().stream()
                .map(mapperFunction)
                .collect(Collectors.toList());

        PageableResponse<V> response = new PageableResponse<>();
        response.setContent(dtoList);
        response.setPageNumber(page.getNumber());
        response.setPageSize(page.getSize());
        response.setTotalElements(page.getTotalElements());
        response.setTotalPages(page.getTotalPages());
        response.setLastPage(page.isLast());
        return response;
    }
}

Page<User> page = userRepository.findAll(pageable);
PageableResponse<UserResponseDto> response = Helper.getPageableResponse(page, userMapper::toResponseDto);

*/
