package com.ssafy.nanumi.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.nanumi.common.ChatMessageDTO;
import com.ssafy.nanumi.common.ChatRoomInfoDTO;
import com.ssafy.nanumi.config.response.CustomResponse;
import com.ssafy.nanumi.config.response.ResponseService;
import com.ssafy.nanumi.config.response.exception.CustomException;
import com.ssafy.nanumi.config.response.exception.CustomExceptionStatus;
import com.ssafy.nanumi.db.entity.ChatMessageEntity;
import com.ssafy.nanumi.db.entity.ChatRoomEntity;
import com.ssafy.nanumi.db.entity.Product;
import com.ssafy.nanumi.db.entity.User;
import com.ssafy.nanumi.db.repository.ChatRepository;
import com.ssafy.nanumi.db.repository.ChatRoomRepository;
import com.ssafy.nanumi.db.repository.ProductRepository;
import com.ssafy.nanumi.db.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ChatService {
    // 웹 소켓 메시지를 전송하는데 사용되는 인터페이스
    // 웹 소켓 메시지를 전송하는데 사용되는 인터페이스
    private final SimpMessageSendingOperations messageTemplate;
    private final ChatRepository chatRepository;
    private final ProductRepository productRepository;
    private final ResponseService responseService;
    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;

    public ChatService(SimpMessageSendingOperations messageTemplate, ChatRepository chatRepository, ProductRepository productRepository, ResponseService responseService, UserRepository userRepository, ChatRoomRepository chatRoomRepository) {
        this.messageTemplate = messageTemplate;
        this.chatRepository = chatRepository;
        this.productRepository = productRepository;
        this.responseService = responseService;
        this.userRepository = userRepository;
        this.chatRoomRepository = chatRoomRepository;
    }


    // TODO DTO 객체를 입력으로 받아서, 채팅 메시지를 저장하고 해당 채팅방에 전송한다.
    @Transactional
    public void CreateChat(ChatMessageDTO DTO) {
        long user = DTO.getSender();

        // 해당 사용자가 참여 중인 채팅방 목록을 가져옵니다.
        List<ChatRoomEntity> chatRoomEntities = chatRoomRepository.findAllByUserListContaining(user);

        // DTO의 roomId에 해당하는 채팅방을 찾습니다.
        ChatRoomEntity chatRoomEntity = chatRoomEntities.stream()
                .filter(chatRoom -> chatRoom.getChatroomSeq() == DTO.getRoomId()) // 'getRoomId()' 대신 'getChatroomSeq()'를 사용합니다.
                .findFirst()
                .orElseThrow(() -> new CustomException(CustomExceptionStatus.NOT_FOUND_CHAT_ROOM));

        long opponentId = Arrays.stream(chatRoomEntity.getUserList())
                .filter(id -> id != user)
                .findFirst()
                .orElse(0L);

        // 상대방 정보를 UserRepository에서 가져오기 (MySQL)
        User opponent = userRepository.findById(opponentId).orElse(null);

        // sender의 프로필 URL을 가져옵니다.
        User senderUser = userRepository.findById(DTO.getSender())
                .orElseThrow(() -> new CustomException(CustomExceptionStatus.NOT_FOUND_USER));
        String profileUrl = senderUser.getProfileUrl();

        // 날짜 형식 및 포멧터를 직접 설정
        String pattern = "yyyy-MM-dd a KK:mm ss:SSS";
        DateFormat df = new SimpleDateFormat(pattern);

        // chatEntiy 객체를 데이터 베이스에 저장
        ChatMessageEntity chatEntity = ChatMessageEntity.builder()
                .type(DTO.getType())
                .roomId(DTO.getRoomId())
                .sender(DTO.getSender())
                .message(DTO.getMessage())
                //.profileUrl(profileUrl)
                .sendTime(df.format(new Date()))
                .opponentID(opponent.getId())
                .build();
        chatRepository.save(chatEntity);

        // 채팅방의 구독자들에게 채팅 메시지를 전송한다.
        ObjectMapper objectMapper = new ObjectMapper();
        String chatEntityJson = null;
        try {
            chatEntityJson = objectMapper.writeValueAsString(chatEntity);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return;
        }

        messageTemplate.convertAndSend("/sub/chat/room/" + DTO.getRoomId(), chatEntityJson);
    }




    // TODO roomSeq를 입력으로 받아서 해당 채팅방의 최근 20개의 채팅 로그를 반환한다.
    @Transactional
    public List<ChatMessageEntity> GetChatLogLimit20(long roomSeq) {
        // 데이터베이스에서 최근 20개의 채팅 로그를 가져온다.
        return chatRepository.findTop20ByRoomIdOrderBySendTimeDesc(roomSeq);
    }

    @Transactional
    public CustomResponse chatEndMatch(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(()-> new CustomException(CustomExceptionStatus.NOT_FOUND_PRODUCT));
        product.matchedEnd();
        return responseService.getSuccessResponse();
    }
}