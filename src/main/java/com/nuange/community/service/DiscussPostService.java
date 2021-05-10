package com.nuange.community.service;

import com.nuange.community.dao.DiscussPostMapper;
import com.nuange.community.entity.DiscussPost;
import com.nuange.community.unity.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class DiscussPostService {

    @Autowired
    private SensitiveFilter sensitiveFilter;
    @Autowired
    private DiscussPostMapper discussPostMapper;
    public List<DiscussPost> findDiscussPost(int userId,int offset,int limit,int orderMode){
        return discussPostMapper.selectDiscussPost(userId,offset,limit,orderMode);
    }
    public int findDiscussPostRows(int userId){
        return discussPostMapper.selectDiscussPostRows(userId);
    }

    /**
     * 将帖子插入数据库并且是经过过滤的
     * @param discussPost
     * @return
     */
    public int insertDiscussPost(DiscussPost discussPost){
        if (discussPost==null){
            throw new IllegalArgumentException("参数不能为空");
        }
        //转义HTML标记
        discussPost.setTitle(HtmlUtils.htmlEscape(discussPost.getTitle()));
        discussPost.setContent(HtmlUtils.htmlEscape(discussPost.getContent()));
        //过滤敏感词
        discussPost.setTitle(sensitiveFilter.filter(discussPost.getTitle()));
        discussPost.setContent(sensitiveFilter.filter(discussPost.getContent()));
        return discussPostMapper.insertDiscussPost(discussPost);
    }
    /**
     * 查询帖子的方法
     */
    public DiscussPost findDiscussPostByid(int id){
        return discussPostMapper.selectDiscussPostById(id);
    }
    /**
     * 修改评论数的方法
     */
    public int updateCommentCount(int id,int commentCount){
        return discussPostMapper.updateComementCount(id,commentCount);
    }
    /**
     * 修改类型的方法
     */
    public int updateType(int id,int type){
        return discussPostMapper.updateType(id,type);
    }
    /**
     * 修改状态的方法
     */
    public int updateStatus(int id,int status){
        return discussPostMapper.updateStatus(id,status);
    }

    /**
     * 修改分数的方法
     */
    public int updateScore(int id,double score){
        return discussPostMapper.updateScore(id,score);
    }
}
