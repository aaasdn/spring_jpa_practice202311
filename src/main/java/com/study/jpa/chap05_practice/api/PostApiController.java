package com.study.jpa.chap05_practice.api;

import com.study.jpa.chap05_practice.dto.*;
import com.study.jpa.chap05_practice.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Tag(name = "post API", description = "게시물 조회, 등록 및 수정, 삭제 api 입니다.")
@RestController // 화면단이 리액트기 때문에 RestController로 사용해야 함
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
public class PostApiController {

    // 리소스: 게시물 (Post)
    /*
        게시물 목록 조회: /posts            - GET, param: (page, size)
        게시물 개별 조회: /posts/{id}       - GET
        게시물 등록:     /posts            - POST, payload(전송되는 순수한데이터): (writer, title, content, hashTags)
        게시물 수정:     /posts            - PUT, PATCH,  payload: (title, content, postNo)
        게시물 삭제:     /posts/{id}       - DELETE
     */

    private final PostService postService;

    @GetMapping
    public ResponseEntity<?> list(PageDTO pageDTO) {
        log.info("/api/v1/posts?page={}&size={}", pageDTO.getPage(),pageDTO.getSize());

        PostListResponseDTO dto = postService.getPosts(pageDTO);

        return ResponseEntity.ok().body(dto);
    }


    // 게시물 개별 조회
    @GetMapping("/{id}")
    public ResponseEntity<?> detail(@PathVariable Long id) {
        log.info("/api/v1/posts/{} GET", id);

        try { // id가 잘못 되면 예외를 발생 시킴
            PostDetailResponseDTO dto = postService.getDetail(id);
            return ResponseEntity.ok().body(dto);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "게시물 작성", description = "게시물 작성을 담당하는 메서드 입니다.")
    @Parameters({
            @Parameter(name = "writer",
                    description = "게시물의 작성자 이름을 쓰세요!",
                    example = "김뽀삐",
                    required = true), // required = true NOT NULL
            @Parameter(name = "title",
                    description = "게시물의 제목을 쓰세요!",
                    example = "제목제목",
                    required = true),
            @Parameter(name = "content",
                    description = "게시물의 내용을 쓰세요!",
                    example = "내용내용"),
            @Parameter(name = "hashTags",
                    description = "게시물의 해시태그를 작성하세요!",
                    example = "['하하', '호호']")
    })
    // 게시물 등록
    @PostMapping
    public ResponseEntity<?> create(
            @Validated @RequestBody PostCreateDTO dto,
            BindingResult result // 검증 에러 정보를 가진 객체
    ) { // requestBody를 붙여야 값이 받아짐. javascript객체를 json으로 변환?
        log.info("/api/v1/posts POST!! -  payload: {}", dto);

        if (dto == null) {
            return ResponseEntity.badRequest().body("등록 게시물 정보를 전달해 주세요!");
        }
        ResponseEntity<List<FieldError>> fieldErrors = getValidatedResult(result);
        if (fieldErrors != null) return fieldErrors;

        try {
            // 위에 존재하는 if문을 모두 건너뜀 -> dto가 null도 아니고, 입력값 검증도 모두 통과함. -> service에게 명령.
            PostDetailResponseDTO responseDTO = postService.insert(dto);
            return ResponseEntity
                    .ok()
                    .body(responseDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .internalServerError()
                    .body("미안 서버 터짐 원인: " + e.getMessage());
        }
    }

    // 게시물 수정
    @RequestMapping(method = {RequestMethod.PATCH, RequestMethod.PUT}) // 두 가지 요청방식을 한번에 처리하는 법
    public ResponseEntity<?> update(
            @Validated @RequestBody PostModifyDTO dto,
            BindingResult result,
            HttpServletRequest request // 요청방법 확인 방법
    ) {
        log.info("/api/v1/posts {} - payload: {}"
                , request.getMethod(), dto);
        ResponseEntity<List<FieldError>> fieldErrors = getValidatedResult(result);
        if (fieldErrors != null) return fieldErrors;

        PostDetailResponseDTO responseDTO
                = postService.modify(dto);

        return ResponseEntity.ok().body(responseDTO);

    }

    // 입력값 검증(Validation)의 결과를 처리해 주는 전역 메서드
    private static ResponseEntity<List<FieldError>> getValidatedResult(BindingResult result) {
        if (result.hasErrors()) { // 입력값 검증 단계에서 문제가 있었다면 true
            List<FieldError> fieldErrors = result.getFieldErrors();
            fieldErrors.forEach(err -> {
                log.warn("invalid client data - {}", err.toString());
            });
            return ResponseEntity.badRequest().body(fieldErrors);
        }
        return null;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        log.info("/api/v1/posts/{} DELETE!!", id);

        try {
            postService.delete(id);
            return ResponseEntity.ok("DEL SUCCESS!!");
        }
//        catch (SQLIntegrityConstraintViolationException e) {
//            return ResponseEntity.internalServerError()
//                    .body("해시태그가 달린 게시물은 삭제할 수 없습니다.");
//        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(e.getMessage());
        }
    }
}


















