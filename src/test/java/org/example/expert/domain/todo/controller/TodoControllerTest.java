package org.example.expert.domain.todo.controller;

import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.service.TodoService;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TodoController.class) // 컨트롤러 계층만 로딩해서 테스트
class TodoControllerTest {

    // Service, Repository는 직접 mock으로 등록
    @Autowired
    private MockMvc mockMvc; // 웹 요청을 가짜로 만들어서 컨트롤러에 보낸 뒤 응답을 테스트 할 수 있는 도구.
    // mockMvc : 실제 웹 요청처럼 동작하게 해줍니다. (예: .perform(get("/todos/1")))
    @MockBean
    private TodoService todoService; // @MockBean: 테스트 대상인 컨트롤러에서 의존하는 서비스(TodoService)를 가짜로 만들어 컨트롤러에 주입합니다.

    // 테스트 메서드 1: 정상적으로 todo를 조회하는 경우
    @Test
    void todo_단건_조회에_성공한다() throws Exception {
        // 1. 준비(Given) : 가짜 TodoResponse 객체를 생성해둠
        long todoId = 1L;
        String title = "title";
        AuthUser authUser = new AuthUser(1L, "email", UserRole.USER);
        User user = User.fromAuthUser(authUser);
        UserResponse userResponse = new UserResponse(user.getId(), user.getEmail());
        TodoResponse response = new TodoResponse(
                todoId,
                title,
                "contents",
                "Sunny",
                userResponse,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        // 2. 행동(When) : todoService.getTodo(todoId)가 호출되면 response를 반환하도록 설정
        when(todoService.getTodo(todoId)).thenReturn(response);

        // 3. 검증(Then) : GET /todos/1 요청을 보내고, 응답 상태와 내용(id, title)이 일치하는지 확인
        mockMvc.perform(get("/todos/{todoId}", todoId))
                .andExpect(status().isOk()) // status().isOk(): 응답 코드가 200인지 확인
                .andExpect(jsonPath("$.id").value(todoId)) // jsonPath: JSON 응답의 특정 값을 검사하는 도구
                .andExpect(jsonPath("$.title").value(title));
    }

    // 테스트 메서드 2: Todo가 존재하지 않을 때 예외 발생
    @Test
    void todo_단건_조회_시_todo가_존재하지_않아_예외가_발생한다() throws Exception {
        // [전체 흐름]
        // 1. todoService.getTodo(todoId)가 호출되면 InvalidRequestException을 던지게 설정
        // 2. 컨트롤러는 이 예외를 잡아서 응답 객체를 반환 (아마 예외를 잡아서 message, code, status를 포함한 JSON 응답을 주는 구조일 겁니다)
        // 3. mockMvc로 요청을 보내고, 그 응답 JSON의 속성들을 검증

        // given
        long todoId = 1L;

        // when
        when(todoService.getTodo(todoId))
                .thenThrow(new InvalidRequestException("Todo not found"));

        // then
        mockMvc.perform(get("/todos/{todoId}", todoId))
                .andExpect(status().isBadRequest()) // .andExpect() : 반환된 값이 내가 기대한 값과 같은지 다른지만 볼 수 있다.
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.name()))
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").value("Todo not found"))
                .andDo(print()); // .andDo() : 객체에서 반환된 값을 콘솔에 프린트해주는 기능 -> 반환값을 좀 더 자세히 확인 가능.
        // 여기서 status().isOk()가 조금 의아할 수 있는데, 실제로 에러지만 **응답 코드 200(OK)**로 내려주는 구조인 것 같습니다.
        // 이것은 예외 응답을 JSON으로 포장해서 보내는 패턴입니다.
        // (API 디자인에 따라 400이나 404로 내려주는 것이 일반적이지만, 이렇게 설계하는 경우도 있습니다.)
    }
}

// 1. 위 테스트를 기준으로 컨트롤러 하나를 직접 만들어보고 테스트도 따라 만들어보기
// 2. @SpringBootTest, @DataJpaTest, @WebMvcTest의 차이점 익히기
// 3. 예외처리 응답 구조(JSON)에 대해 공부해보기 (@ControllerAdvice 등)
