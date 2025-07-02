package org.example.expert.domain.comment.repository;

import org.example.expert.domain.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("SELECT c FROM Comment c JOIN FETCH c.user WHERE c.todo.id = :todoId")
    List<Comment> findByTodoIdWithUser(@Param("todoId") Long todoId);
    // 댓글 목록 조회, 댓글 작성자 정보 조회가 같이 이루어지는데, 커멘트 수만큼 유저를 조회해서 n번의 쿼리가 나가는 문제가 발생. 댓글 작성자 정보가 댓글 목록의 댓글 수 만큼 쿼리 발생.
}   // 댓글 목록 조회가 = 1, 그로인해 발생한 댓글 수만큼의 유저조회가 n번의 쿼리.
