package com.sparta.petplace.post.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.petplace.post.entity.Post;
import com.sparta.petplace.post.entity.Sort;
import com.sparta.petplace.review.entity.QReview;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.sparta.petplace.post.entity.QPost.post;

@Repository
@Transactional(readOnly = true)
@Slf4j
public class PostRepositoryCustomImpl extends QuerydslRepositorySupport implements PostRepositoryCustom{


    private final JPAQueryFactory queryFactory;

    public PostRepositoryCustomImpl(JPAQueryFactory queryFactory){
        super(Post.class);
        this.queryFactory = queryFactory;
    }


    @Override
    public List<Post> search(String category, String keyword, Double lat, Double lng, Pageable pageable, Sort sort) {
        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();
        QReview review = QReview.review1;

        switch (sort) {
            case DISTANCE:
                orderSpecifiers.add(Expressions.asNumber(Expressions.template(Double.class, distanceQuery(lat, lng))).asc());
                break;
            case STAR:
                NumberExpression<Double> avgStar = review.star.avg().coalesce(0.0);
                orderSpecifiers.add(avgStar.desc());
                break;
            case REVIEW:
                orderSpecifiers.add(post.reviews.size().desc());
                break;
            default:
                throw new IllegalArgumentException("Invalid sort value: " + sort);
        }

        return queryFactory.selectFrom(post)
                .leftJoin(post.reviews, review)
                .where(eqCategory(category))
                .where(containTitle(keyword)
                        .or(containContents(keyword))
                        .or(containFeature(keyword))
                        .or(containAddress(keyword)))
                .groupBy(post)
                .orderBy(orderSpecifiers.toArray(new OrderSpecifier[0]))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }


    @Override
    public List<Post> find(String category, Pageable pageable, Double lat, Double lng, Sort sort) {
        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();
        QReview review = QReview.review1;

        switch (sort) {
            case DISTANCE:
                orderSpecifiers.add(Expressions.asNumber(Expressions.template(Double.class, distanceQuery(lat, lng))).asc());
                break;
            case STAR:
                NumberExpression<Double> avgStar = review.star.avg().coalesce(0.0);
                orderSpecifiers.add(avgStar.desc());
                break;
            case REVIEW:
                orderSpecifiers.add(post.reviews.size().desc());
                break;
            default:
                throw new IllegalArgumentException("Invalid sort value: " + sort);
        }

        return queryFactory
                .select(post)
                .from(post)
                .leftJoin(post.reviews, review)
                .where(post.category.eq(category))
                .groupBy(post)
                .orderBy(orderSpecifiers.toArray(new OrderSpecifier[0]))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }


    private String distanceQuery(Double lat1, Double lng1, Double lat2, Double lng2) {
        return "6371 * acos(cos(radians(" + lat1 + ")) * cos(radians(" + lat2 + ")) * cos(radians(" + lng2 + ") - radians(" + lng1 + ")) + sin(radians(" + lat1 + ")) * sin(radians(" + lat2 + ")))";
    }

    private String distanceQuery(Double lat, Double lon) {
        return "6371 * acos(cos(radians(:lat)) * cos(radians(post.lat)) * cos(radians(post.lng) - radians(:lon)) + sin(radians(:lat)) * sin(radians(post.lat)))"
                .replace(":lat", String.valueOf(lat))
                .replace(":lon", String.valueOf(lon));
    }

    @Override
    public long countByCategory(String category) {
        Long count = queryFactory.select(post.id.countDistinct())
                .from(post)
                .where(post.category.eq(category))
                .fetchOne();
        return Optional.ofNullable(count).orElse(0L);
    }

    @Override
    public long countByCategoryAndKeyword(String category, String keyword) {
        Long count = queryFactory.select(post.id.countDistinct())
                .from(post)
                .where(eqCategory(category))
                .where(containTitle(keyword)
                        .or(containContents(keyword))
                        .or(containFeature(keyword))
                        .or(containAddress(keyword)))
                .fetchOne();
        return Optional.ofNullable(count).orElse(0L);
    }


    private BooleanExpression eqCategory(String category) {
        if(category == null || category.isEmpty()) {
            return Expressions.asBoolean(true).isTrue();
        }
        return post.category.eq(category);
    }

    private BooleanExpression containTitle(String keyword) {
        if(keyword == null || keyword.isEmpty()) {
            return Expressions.asBoolean(true).isTrue();
        }
        return post.title.containsIgnoreCase(keyword);
    }

    private BooleanExpression containContents(String keyword) {
        if(keyword == null || keyword.isEmpty()) {
            return Expressions.asBoolean(true).isTrue();
        }
        return post.contents.containsIgnoreCase(keyword);
    }

    private BooleanExpression containFeature(String keyword) {
        if(keyword == null || keyword.isEmpty()) {
            return Expressions.asBoolean(true).isTrue();
        }
        return post.feature1.containsIgnoreCase(keyword);
    }

    private BooleanExpression containAddress(String keyword) {
        if(keyword == null || keyword.isEmpty()) {
            return Expressions.asBoolean(true).isTrue();
        }
        return post.address.containsIgnoreCase(keyword);
    }

}