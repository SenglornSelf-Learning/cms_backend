package com.senglorn.cms.category;

import com.senglorn.cms.model.Category;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CategoryRepository {

    @Select("""
           SELECT *
           FROM tb_categories
           ORDER BY id
           """)
    @Results({
            @Result(property = "isDeleted", column = "is_deleted")
    })
    List<Category> selectAllCategorise();

    @Select("""
            SELECT *
            FROM tb_categories
            WHERE id = #{id}
            """)
    @Results({
            @Result(property = "isDeleted", column = "is_deleted")
    })
    Category selectCategoryById(Integer id);

    @Insert("""
            INSERT INTO tb_categories (name, is_deleted)
            VALUES (#{name}, #{isDeleted})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insertCategory(Category category);
}
