package com.tobe.healthy.course.presentation;

import com.tobe.healthy.common.ResponseHandler;
import com.tobe.healthy.config.security.CustomMemberDetails;
import com.tobe.healthy.course.application.CourseService;
import com.tobe.healthy.course.domain.dto.in.CourseAddCommand;
import com.tobe.healthy.workout.application.ExerciseService;
import com.tobe.healthy.workout.domain.dto.ExerciseDto;
import com.tobe.healthy.workout.domain.dto.in.HistoryCommentAddCommand;
import com.tobe.healthy.workout.domain.entity.ExerciseCategory;
import com.tobe.healthy.workout.domain.entity.PrimaryMuscle;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/course/v1")
@Tag(name = "수강권 API", description = "수강권 API")
@Slf4j
public class CourseController {

    private final CourseService courseService;

    @Operation(summary = "수강권 등록", responses = {
            @ApiResponse(responseCode = "404", description = "존재하지 않는 트레이너"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 회원"),
            @ApiResponse(responseCode = "400", description = "이미 등록된 수강권이 존재"),
            @ApiResponse(responseCode = "400", description = "내 학생이 아닙니다."),
            @ApiResponse(responseCode = "200", description = "수강권을 등록한다.")
    })
    @PostMapping
    public ResponseHandler<Void> addCourse(@AuthenticationPrincipal CustomMemberDetails customMemberDetails,
                                           @RequestBody @Valid CourseAddCommand command) {
        courseService.addCourse(customMemberDetails.getMember().getId(), command);
        return ResponseHandler.<Void>builder()
                .message("수강권이 등록되었습니다.")
                .build();
    }

    @Operation(summary = "수강권 삭제", responses = {
            @ApiResponse(responseCode = "404", description = "존재하지 않는 트레이너"),
            @ApiResponse(responseCode = "200", description = "수강권을 삭제한다.")
    })
    @DeleteMapping("/{courseId}")
    public ResponseHandler<Void> deleteCourse(@AuthenticationPrincipal CustomMemberDetails customMemberDetails,
                                              @Parameter(description = "수강권 ID") @PathVariable("courseId") Long courseId) {
        courseService.deleteCourse(customMemberDetails.getMember().getId(), courseId);
        return ResponseHandler.<Void>builder()
                .message("수강권이 삭제되었습니다.")
                .build();
    }

}
