package com.sparta.petplace.post.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.petplace.post.entity.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.sparta.petplace.post.entity.QPost.post;
import static com.sparta.petplace.review.entity.QReview.review1;

@Repository
@Transactional(readOnly = true)
public class PostRepositoryCustomImpl extends QuerydslRepositorySupport implements PostRepositoryCustom{

    @Autowired
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

//    @Override
//    public List<Post> find(String category, Pageable pageable){
//        return queryFactory.selectFrom(post)
//                .leftJoin(post.reviews, review1).fetchJoin()
//                .where(post.category.eq(category))
//                .distinct()
//                .offset(pageable.getOffset())
//                .limit(pageable.getPageSize())
//                .fetch();
//    }

    @Override
    public List<Post> find(String category, Pageable pageable) {
        return queryFactory.selectFrom(post)
                .leftJoin(post.reviews, review1).fetchJoin()
                .where(post.category.eq(category))
                .distinct()
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    @Override
    public long countByCategory(String category) {
        Long count = queryFactory.select(post.id.countDistinct())
                .from(post)
                .where(post.category.eq(category))
                .fetchOne();
        return Optional.ofNullable(count).orElse(0L);
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
