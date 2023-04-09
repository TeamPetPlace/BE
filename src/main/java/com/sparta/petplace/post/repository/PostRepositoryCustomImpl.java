package com.sparta.petplace.post.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.petplace.post.entity.Post;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

import static com.sparta.petplace.post.entity.QPost.post;
import static com.sparta.petplace.review.entity.QReview.review1;

public class PostRepositoryCustomImpl extends QuerydslRepositorySupport implements PostRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    public PostRepositoryCustomImpl(JPAQueryFactory queryFactory){
        super(Post.class);
        this.queryFactory = queryFactory;
    }


    @Override
    public List<Post> search(String category, String keyword){
        return queryFactory.selectFrom(post)
                .where(eqCategory(category))
                .where(containTitle(keyword)
                        .or(containContents(keyword))
                        .or(containFeature(keyword))
                        .or(containAddress(keyword)))
                .fetch();
    }

    @Override
    public List<Post> find(String category){
        return queryFactory.selectFrom(post)
                .leftJoin(post.reviews, review1).fetchJoin()
                .where(post.category.eq(category))
                .distinct()
                .fetch();
    }


    private BooleanExpression eqCategory(String category) {
        if(category == null || category.isEmpty()) {
            return null;
        }
        return post.category.eq(category);
    }

    private BooleanExpression containTitle(String keyword) {
        if(keyword == null || keyword.isEmpty()) {
            return null;
        }
        return post.title.containsIgnoreCase(keyword);
    }

    private BooleanExpression containContents(String keyword) {
        if(keyword == null || keyword.isEmpty()) {
            return null;
        }
        return post.contents.containsIgnoreCase(keyword);
    }

    private BooleanExpression containFeature(String keyword) {
        if(keyword == null || keyword.isEmpty()) {
            return null;
        }
        return post.feature1.containsIgnoreCase(keyword);
    }

    private BooleanExpression containAddress(String keyword) {
        if(keyword == null || keyword.isEmpty()) {
            return null;
        }
        return post.address.containsIgnoreCase(keyword);
    }

}
