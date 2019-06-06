package com.my.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Mapping;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(indexName = "new_film", type = "new")
public class FilmEntity {

    private Long id;
    private String name;
    private String director;
    private Date created ;

    @Override
    public String toString() {
        return "FilmEntity [id=" + id + ", name=" + name + ", director=" + director + "]";
    }
}