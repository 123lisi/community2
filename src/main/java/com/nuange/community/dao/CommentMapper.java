package com.nuange.community.dao;

import com.nuange.community.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper {
    List<Comment> selectCommentsEntity(int entityType,int entityId,int offset,int limit);
    int selectCountByEntity(int entityType,int entityId);

    int insertComment(Comment comment);

    /**
     * 根据id查询一个Comment
     */
    Comment selectCommentById(int Id);
}
