package in.tech_camp.protospace_b.repository;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import in.tech_camp.protospace_b.entity.CommentEntity;

@Mapper
public interface CommentRepository {
    @Select("""
            SELECT
              c.id c_id,
              c.text,
              u.id u_id,
              u.nickname,
              p.id p_id,
              c.created_at
            FROM comments c
            LEFT JOIN users u ON c.user_id = u.id
            LEFT JOIN prototype p ON c.prototype_id = p.id
            WHERE c.prototype_id = #{prototypeId}
            """)
    @Results(value = {
            @Result(property = "id", column = "c_id"),
            @Result(property = "user.id", column = "u_id"),
            @Result(property = "user.nickname", column = "nickname"),
            @Result(property = "createdAt", column = "created_at"),
    })
    List<CommentEntity> findByPrototypeId(Integer prototypeId);

    // コメント保存
    @Insert("INSERT INTO comments (text, user_id, prototype_id) VALUES (#{text}, #{user.id}, #{prototype.id})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(CommentEntity comment);

    // コメント削除
    @Delete("DELETE FROM comments WHERE id = #{id}")
    void deleteById(Integer id);

    // コメント取得
    @Select("SELECT user_id FROM comments WHERE id = #{commentId}")
    Integer findByCommentId(Integer commentId);
}