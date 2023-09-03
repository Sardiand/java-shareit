package ru.practicum.shareit.item.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentItemDto;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query(nativeQuery = true, name = "CommentDtos")
    List<CommentDto> findAllCommentDtoByItemId(long itemId);

    @Query(nativeQuery = true, name = "CommentItemDtos")
    List<CommentItemDto> findAllCommentDtoByItemIds(@Param("id") List<Long> itemIds);
}
