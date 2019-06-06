package com.my.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;

/**
 * <一句话功能简述><br>
 * ()
 *
 * @author M.Y
 * @date 2019/5/30
 * @since 1.0.0
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(indexName = "contents",type = "content")
//indexName索引名称 可以理解为数据库名 必须为小写 不然会报org.elasticsearch.indices.InvalidIndexNameException异常
//type类型 可以理解为表名
public class GoodsInfo {
    private Long id;
    private String name;
    private String des;

}