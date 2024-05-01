package com.tobe.healthy.lessonHistory.domain.entity

import com.tobe.healthy.common.BaseTimeEntity
import com.tobe.healthy.member.domain.entity.Member
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType.LAZY
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne

@Entity
class LessonHistoryFiles(
    val fileUrl: String,

    val originalFileName: String,

    val fileOrder: Int,

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    val member: Member? = null,

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "lesson_history_id")
    val lessonHistory: LessonHistory? = null,

    @ManyToOne(fetch = LAZY, cascade = [CascadeType.ALL])
    @JoinColumn(name = "lesson_history_comment_id")
    val lessonHistoryComment: LessonHistoryComment? = null,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lesson_history_files_id")
    val id: Long = 0
) : BaseTimeEntity<LessonHistoryFiles, Long>() {

    companion object {
        fun create(originalFileName: String?, member: Member?, fileUrl: String?, fileOrder: Int): LessonHistoryFiles {
            return LessonHistoryFiles(
                originalFileName = originalFileName!!,
                member = member,
                fileUrl = fileUrl!!,
                fileOrder = fileOrder
            )
        }
    }
}

