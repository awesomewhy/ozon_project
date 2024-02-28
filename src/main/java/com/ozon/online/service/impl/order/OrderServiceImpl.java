package com.ozon.online.service.impl.order;

import com.ozon.online.dto.order.CreateOrderForChatShowDto;
import com.ozon.online.entity.Order;
import com.ozon.online.entity.User;
import com.ozon.online.exception.ErrorResponse;
import com.ozon.online.mapper.OrderMapper;
import com.ozon.online.repository.OrderRepository;
import com.ozon.online.repository.UserRepository;
import com.ozon.online.service.OrderService;
import com.ozon.online.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final OrderMapper orderMapper;

    @Override
    public ResponseEntity<?> createOrderForChat(@PathVariable String nickname, @RequestBody CreateOrderForChatShowDto createOrderForChatShowDto) {
        User user = userService.getAuthenticationPrincipalUserByNickname().orElseThrow();
        User user2 = userRepository.findByNickname(nickname).orElseThrow();

        Order order = orderMapper.mapCreateOrderForChatShowDtoToEntity(user2, createOrderForChatShowDto, user);

        orderRepository.save(order);

        return ResponseEntity.ok().body(new ErrorResponse(HttpStatus.OK.value(), "order added"));
    }

}
