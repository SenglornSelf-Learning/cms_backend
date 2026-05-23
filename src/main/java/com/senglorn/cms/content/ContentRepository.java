package com.senglorn.cms.content;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import com.senglorn.cms.model.CmsContent;

@Mapper
public interface ContentRepository {

    @Select("""
            SELECT *
            FROM tb_contents
            ORDER BY id
            """)
    @Results({
            @Result(property = "isDeleted", column = "is_deleted"),
            @Result(property = "createAt", column = "create_at"),
            @Result(property = "categoryId", column = "category_id")
    })
    List<CmsContent> selectAllContents();

    @Select("""
            SELECT *
            FROM tb_contents
            WHERE id = #{id}
            """)
    @Results({
            @Result(property = "isDeleted", column = "is_deleted"),
            @Result(property = "createAt", column = "create_at"),
            @Result(property = "categoryId", column = "category_id")
    })
    CmsContent selectContentById(Integer id);

    @Insert("""
            INSERT INTO tb_contents (
                uuid,
                slug,
                keyword,
                title,
                description,
                thumbnail,
                editor,
                is_deleted,
                create_at,
                category_id
            )
            VALUES (
                #{uuid},
                #{slug},
                #{keyword},
                #{title},
                #{description},
                #{thumbnail},
                #{editor},
                #{isDeleted},
                #{createAt},
                #{categoryId}
            )
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insertContent(CmsContent content);
}
