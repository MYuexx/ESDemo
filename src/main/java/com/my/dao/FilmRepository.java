package com.my.dao;

import com.my.entity.FilmEntity;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Component;

/**
 * <一句话功能简述><br>
 * ()
 *
 * @author M.Y
 * @date 2019/5/30
 * @since 1.0.0
 */
@Component
public interface FilmRepository extends ElasticsearchRepository<FilmEntity,Long> {
}
