package com.tobe.healthy.workout.domain.dto.in;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class HistoryCommentAddCommand {

    @Schema(description = "부모 댓글 아이디", example = "1")
    private Long parentCommentId;

    @Schema(description = "댓글 내용", example = "댓글입니다.")
    @NotEmpty(message = "내용을 입력해 주세요.")
    private String content;

}
