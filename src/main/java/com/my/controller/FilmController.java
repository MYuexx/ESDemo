package com.my.controller;

import com.alibaba.fastjson.JSON;
import com.my.dao.FilmRepository;
import com.my.entity.FilmEntity;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.DisMaxQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <一句话功能简述><br>
 * ()
 *
 * @author M.Y
 * @date 2019/6/4
 * @since 1.0.0
 */
@RestController
@RequestMapping("/film")
public class FilmController {
    @Autowired
    FilmRepository filmRepository;
    @Autowired
    TransportClient client;
    @GetMapping("save")
    public String save(String des,String name){

        LocalDateTime localDateTime = LocalDateTime.now();
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDateTime.atZone(zone).toInstant();
        Date date = Date.from(instant);
        FilmEntity filmEntity = new FilmEntity(System.currentTimeMillis(),name,des,date);
        filmRepository.save(filmEntity);
        return "success";
    }

    /**
     * 拼接搜索条件
     *
     * @param name     the name
     * @return list
     */
    @GetMapping("search")
    public List<FilmEntity> search(String name) {
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(structureQuery(name))
                .build();
        List<FilmEntity> list = filmRepository.search(searchQuery).getContent();
        return list;
    }

    /**
     * 中文、拼音混合搜索
     *
     * @param content the content
     * @return dis max query builder
     */
    public DisMaxQueryBuilder structureQuery(String content) {
        //使用dis_max直接取多个query中，分数最高的那一个query的分数即可
        DisMaxQueryBuilder disMaxQueryBuilder = QueryBuilders.disMaxQuery();
        //boost 设置权重,只搜索匹配name和disrector字段
        QueryBuilder ikNameQuery = QueryBuilders.matchQuery("name", content).boost(2f);
        QueryBuilder pinyinNameQuery = QueryBuilders.matchQuery("name.pinyin", content);
        QueryBuilder ikDirectorQuery = QueryBuilders.matchQuery("director", content).boost(2f);
        disMaxQueryBuilder.add(ikNameQuery);
        disMaxQueryBuilder.add(pinyinNameQuery);
        disMaxQueryBuilder.add(ikDirectorQuery);
        return disMaxQueryBuilder;
    }

    /**
     * 构建高亮查询
     * @param des
     * @return
     */
    @GetMapping("query")
    public List<FilmEntity> query(String des) {
        QueryBuilder query = structureQuery(des);
        // 加入查询中
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.preTags("<span>");//设置前缀
        highlightBuilder.postTags("</span>");//设置后缀
        highlightBuilder.field("name");//设置高亮字段
        highlightBuilder.field("director");//设置高亮字段
//        highlightBuilder.field("name.pinyin");//设置高亮字段
        SearchResponse response = client.prepareSearch("new_film")
                .setTypes("new")
                .setQuery(query).highlighter(highlightBuilder).execute().actionGet();

        // 遍历结果, 获取高亮片段
        SearchHits searchHits = response.getHits();
        FilmEntity filmEntity = null;
        List<FilmEntity> result = new ArrayList<>();
        for (SearchHit hit : searchHits) {
            Map<String, Object> entityMap = hit.getSourceAsMap();
            filmEntity = JSON.parseObject(JSON.toJSONString(entityMap), FilmEntity.class);
            if (!StringUtils.isEmpty(hit.getHighlightFields().get("name"))) {
                Text[] text = hit.getHighlightFields().get("name").getFragments();
                filmEntity.setName(text[0].toString());
            }
            if (!StringUtils.isEmpty(hit.getHighlightFields().get("director"))) {
                Text[] text = hit.getHighlightFields().get("director").getFragments();
                filmEntity.setDirector(text[0].toString());
            }
            result.add(filmEntity);
        }
        return result;
    }
}
