package in.tech_camp.protospace_b.repository;

import java.util.List;

import org.apache.ibatis.annotations.Many;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.One;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import in.tech_camp.protospace_b.entity.PrototypeEntity;

@Mapper
public interface PrototypeShowRepository {

    @Select("""
            SELECT
                p.id p_id,
                p.prototypeName,
                p.catchCopy,
                p.concept,
                p.img,
                p.created_at,
                p.updated_at,
                p.published,
                u.id u_id,
                u.nickname nickname,
                COALESCE(n.niceCount, 0) niceCount,
                n.isNice,
                MAX(CASE WHEN r.user_id = #{currentUserId} OR p.user_id = #{currentUserId} THEN 1 ELSE 0 END) read,
                t.id t_id,
                t.tag_name
            FROM
                prototype p
            LEFT JOIN users u ON p.user_id = u.id
            LEFT JOIN (
                SELECT
                    nice.prototype_id,
                    MAX(CASE WHEN nice.user_id = #{currentUserId} THEN 1 ELSE 0 END) isNice,
                    count(*) niceCount
                FROM
                    nice
                GROUP BY
                    nice.prototype_id
            ) n ON p.id = n.prototype_id
            LEFT JOIN prototype_read_status r ON r.prototype_id = p.id AND r.user_id = #{currentUserId}
            LEFT JOIN prototype_tags pt ON pt.prototype_id = p.id
            LEFT JOIN tags t ON t.id = pt.tags_id
            WHERE p.published = true
            GROUP BY
                p.id,
                p.prototypeName,
                p.catchCopy,
                p.concept,
                p.img,
                p.created_at,
                p.updated_at,
                p.published,
                u.id,
                u.nickname,
                n.isNice,
                n.niceCount,
                t.id
            ORDER BY p.created_at DESC, p.id ASC
            """)
    @Results(value = {
            @Result(property = "id", column = "p_id"),
            @Result(property = "user.id", column = "u_id"),
            @Result(property = "user.nickname", column = "nickname"),
            @Result(property = "imgPath", column = "img"),
            @Result(property = "tag.id", column = "t_id"),
            @Result(property = "tag.tagName", column = "tag_name"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at"),
            @Result(property = "published", column = "published")

    })
    List<PrototypeEntity> showAll(Integer currentUserId);

    @Select("""
            SELECT
                p.id p_id,
                p.prototypeName,
                p.catchCopy,
                p.concept,
                p.img,
                p.created_at,
                p.updated_at,
                p.published,
                u.id u_id,
                u.nickname nickname,
                COALESCE(n.niceCount, 0) niceCount,
                n.isNice,
                MAX(CASE WHEN r.user_id = #{currentUserId} OR p.user_id = #{currentUserId} THEN 1 ELSE 0 END) read,
                MAX(CASE WHEN pin.user_id = #{userId} THEN 1 ELSE 0 END) pinned,
                MAX(CASE WHEN b.user_id = #{userId} THEN 1 ELSE 0 END) is_bookmark,
                t.id t_id,
                t.tag_name
            FROM
                prototype p
            LEFT JOIN users u ON p.user_id = u.id
            LEFT JOIN (
                SELECT
                    nice.prototype_id,
                    MAX(CASE WHEN nice.user_id = #{currentUserId} THEN 1 ELSE 0 END) isNice,
                    COUNT(*) niceCount
                FROM nice
                GROUP BY
                    nice.prototype_id
            ) n ON p.id = n.prototype_id
            LEFT JOIN prototype_read_status r ON r.prototype_id = p.id AND r.user_id = #{currentUserId}
            LEFT JOIN prototype_tags pt ON p.id = pt.prototype_id
            LEFT JOIN bookmark b ON p.id = b.prototype_id
            LEFT JOIN pin ON p.id = pin.prototype_id
            LEFT JOIN tags t ON t.id = pt.tags_id
            WHERE p.published = true AND u.id = #{userId}
            GROUP BY
                p.id,
                p.prototypeName,
                p.catchCopy,
                p.concept,
                p.img,
                p.created_at,
                p.updated_at,
                p.published,
                u.id,
                u.nickname,
                n.isNice,
                n.niceCount,
                t.id
            ORDER BY p.created_at DESC, p.id ASC
            """)
    @Results(value = {
            @Result(property = "id", column = "p_id"),
            @Result(property = "user.id", column = "u_id"),
            @Result(property = "user.nickname", column = "nickname"),
            @Result(property = "imgPath", column = "img"),
            @Result(property = "tag.id", column = "t_id"),
            @Result(property = "tag.tagName", column = "tag_name"),
            @Result(property = "pin", column = "pinned"),
            @Result(property = "bookmark", column = "is_bookmark"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at"),
            @Result(property = "published", column = "published")
    })
    List<PrototypeEntity> showByUserId(Integer currentUserId, Integer userId);

    @Select("""
            SELECT
                p.id p_id,
                p.prototypeName,
                p.catchCopy,
                p.concept,
                p.img,
                p.created_at,
                p.updated_at,
                p.published,
                p.user_id u_id,
                COALESCE(n.niceCount, 0) niceCount,
                n.isNice,
                MAX(CASE WHEN r.user_id = #{currentUserId} OR p.user_id = #{currentUserId} THEN 1 ELSE 0 END) read,
                MAX(CASE WHEN pin.user_id = #{currentUserId} THEN 1 ELSE 0 END) pinned,
                MAX(CASE WHEN b.user_id = #{currentUserId} THEN 1 ELSE 0 END) bookmark
            FROM (
                SELECT * FROM prototype WHERE id = #{id}
            ) p
            LEFT JOIN (
                select
                    nice.prototype_id,
                    max(case when nice.user_id = #{currentUserId} then 1 else 0 end) isNice,
                    count(*) niceCount
                from
                    nice
                group by
                    nice.prototype_id
            ) n ON p.id = n.prototype_id AND p.id = #{id}
            LEFT JOIN prototype_read_status r ON r.prototype_id = p.id AND r.user_id = #{currentUserId}
            LEFT JOIN bookmark b ON p.id = b.prototype_id AND b.user_id = #{currentUserId}
            LEFT JOIN pin ON p.id = pin.prototype_id
            GROUP BY
                p.id,
                p.prototypeName,
                p.catchCopy,
                p.concept,
                p.img,
                p.created_at,
                p.updated_at,
                p.published,
                p.user_id,
                n.isNice,
                n.niceCount
            """)
    @Results(value = {
            @Result(property = "id", column = "p_id"),
            @Result(property = "user", column = "u_id", one = @One(select = "in.tech_camp.protospace_b.repository.UserDetailRepository.findById")),
            @Result(property = "imgPath", column = "img"),
            @Result(property = "tags", column = "p_id", many = @Many(select = "in.tech_camp.protospace_b.repository.TagRepository.prototypeTags")),
            @Result(property = "pin", column = "pinned"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at"),
            @Result(property = "published", column = "published")
    })
    PrototypeEntity findByPrototypeId(Integer currentUserId, Integer id);

    @Select("""
            SELECT
                p.id p_id,
                p.prototypeName,
                p.catchCopy,
                p.concept,
                p.img,
                p.created_at,
                p.updated_at,
                p.published,
                u.id u_id,
                u.nickname nickname,
                COALESCE(n.niceCount, 0) niceCount,
                n.isNice,
                MAX(CASE WHEN r.user_id = #{currentUserId} OR p.user_id = #{currentUserId} THEN 1 ELSE 0 END) read,
                t.id t_id,
                t.tag_name
            FROM 
                prototype p
            LEFT JOIN users u ON p.user_id = u.id
            LEFT JOIN (
                SELECT
                    nice.prototype_id,
                    MAX(CASE WHEN nice.user_id = #{currentUserId} THEN 1 ELSE 0 END) isNice,
                    count(*) niceCount
                FROM
                    nice
                GROUP BY
                    nice.prototype_id
            ) n ON p.id = n.prototype_id
            LEFT JOIN prototype_read_status r ON r.prototype_id = p.id AND r.user_id = #{currentUserId}
            LEFT JOIN prototype_tags pt ON pt.prototype_id = p.id
            LEFT JOIN tags t ON t.id = pt.tags_id
            WHERE p.published = true AND p.prototypeName LIKE CONCAT('%', #{prototypeName}, '%') AND p.id IN (
                SELECT prototype_id
                FROM prototype_tags
                WHERE tags_id = #{tagId}
            )
            GROUP BY
                p.id,
                p.prototypeName,
                p.catchCopy,
                p.concept,
                p.img,
                p.created_at,
                p.updated_at,
                p.published,
                u.id,
                u.nickname,
                n.isNice,
                n.niceCount,
                t.id
            ORDER BY p.created_at DESC, p.id ASC
            """)
    @Results(value = {
            @Result(property = "id", column = "p_id"),
            @Result(property = "user.id", column = "u_id"),
            @Result(property = "user.nickname", column = "nickname"),
            @Result(property = "imgPath", column = "img"),
            @Result(property = "tag.id", column = "t_id"),
            @Result(property = "tag.tagName", column = "tag_name"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at"),
            @Result(property = "published", column = "published")
    })
    List<PrototypeEntity> findByPrototypeNameWithTag(@Param("currentUserId") Integer currentUserId,
            @Param("prototypeName") String prototypeName, @Param("tagId") Integer tagId);

    @Select("""
            SELECT
                p.id p_id,
                p.prototypeName,
                p.catchCopy,
                p.concept,
                p.img,
                p.created_at,
                p.updated_at,
                p.published,
                u.id u_id,
                u.nickname nickname,
                COALESCE(n.niceCount, 0) niceCount,
                n.isNice,
                MAX(CASE WHEN r.user_id = #{currentUserId} OR p.user_id = #{currentUserId} THEN 1 ELSE 0 END) read,
                t.id t_id,
                t.tag_name
            FROM
                prototype p
            LEFT JOIN users u ON p.user_id = u.id
            LEFT JOIN (
                SELECT
                    nice.prototype_id,
                    MAX(CASE WHEN nice.user_id = #{currentUserId} THEN 1 ELSE 0 END) isNice,
                    count(*) niceCount
                FROM
                    nice
                GROUP BY
                    nice.prototype_id
            ) n ON p.id = n.prototype_id
            LEFT JOIN prototype_read_status r ON r.prototype_id = p.id AND r.user_id = #{currentUserId}
            LEFT JOIN prototype_tags pt ON pt.prototype_id = p.id
            LEFT JOIN tags t ON t.id = pt.tags_id
            WHERE p.published = true AND p.prototypeName LIKE CONCAT('%', #{prototypeName}, '%')
            GROUP BY
                p.id,
                p.prototypeName,
                p.catchCopy,
                p.concept,
                p.img,
                p.created_at,
                p.updated_at,
                p.published,
                u.id,
                u.nickname,
                n.isNice,
                n.niceCount,
                t.id
            ORDER BY p.created_at DESC, p.id ASC
            """)
    @Results(value = {
            @Result(property = "id", column = "p_id"),
            @Result(property = "user.id", column = "u_id"),
            @Result(property = "user.nickname", column = "nickname"),
            @Result(property = "imgPath", column = "img"),
            @Result(property = "tag.id", column = "t_id"),
            @Result(property = "tag.tagName", column = "tag_name"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at"),
            @Result(property = "published", column = "published")
    })
    List<PrototypeEntity> findByPrototypeName(@Param("currentUserId") Integer currentUserId,
            @Param("prototypeName") String prototypeName);

    // OPTIMIZE: N+1
    @Select("""
            SELECT
                p.id p_id,
                p.prototypeName,
                p.catchCopy,
                p.concept,
                p.img,
                p.created_at,
                p.updated_at,
                p.published,
                u.id u_id,
                u.nickname nickname,
                COALESCE(n.niceCount, 0) niceCount,
                n.isNice,
                MAX(CASE WHEN r.user_id = #{currentUserId} OR p.user_id = #{currentUserId} THEN 1 ELSE 0 END) read
            FROM
                prototype p
            LEFT JOIN users u ON p.user_id = u.id
            LEFT JOIN (
                SELECT
                    nice.prototype_id,
                    MAX(CASE WHEN nice.user_id = #{currentUserId} THEN 1 ELSE 0 END) isNice,
                    count(*) niceCount
                FROM nice
                GROUP BY
                    nice.prototype_id
            ) n ON p.id = n.prototype_id
            LEFT JOIN prototype_read_status r ON r.prototype_id = p.id AND r.user_id = #{currentUserId}
            WHERE p.published = true
            GROUP BY
                p.id,
                p.prototypeName,
                p.catchCopy,
                p.concept,
                p.img,
                p.created_at,
                p.updated_at,
                p.published,
                u.id,
                u.nickname,
                n.isNice,
                n.niceCount
            ORDER BY niceCount DESC
            """)
    @Results(value = {
            @Result(property = "id", column = "p_id"),
            @Result(property = "user.id", column = "u_id"),
            @Result(property = "user.nickname", column = "nickname"),
            @Result(property = "imgPath", column = "img"),
            @Result(property = "tags", column = "p_id", many = @Many(select = "in.tech_camp.protospace_b.repository.TagRepository.prototypeTags")),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at"),
            @Result(property = "published", column = "published")
    })
    List<PrototypeEntity> findPrototypesOrderByCountDesc(Integer currentUserId);

    // 下書きのみ取得
    @Select("""
                    SELECT
                        p.id p_id,
                        p.prototypeName,
                        p.catchCopy,
                        p.concept,
                        p.img,
                        p.created_at,
                        p.updated_at,
                        p.published,
                        u.id u_id,
                        u.nickname nickname,
                        COALESCE(n.niceCount, 0) niceCount,
                        n.isNice,
                        MAX(CASE WHEN r.user_id = #{userId} THEN 1 ELSE 0 END) read
                    FROM
                        prototype p
                    LEFT JOIN users u ON p.user_id = u.id
                    LEFT JOIN (
                    SELECT
                        nice.prototype_id,
                        MAX(CASE WHEN nice.user_id = #{userId} THEN 1 ELSE 0 END) isNice,
                        COUNT(*) niceCount
                    FROM
                        nice
                    GROUP BY
                        nice.prototype_id
                ) n ON p.id = n.prototype_id
                LEFT JOIN prototype_read_status r ON r.prototype_id = p.id AND r.user_id = #{userId}
                    WHERE
                        p.published = false
                        AND p.user_id = #{userId}
                    GROUP BY
                        p.id,
                        p.prototypeName,
                        p.catchCopy,
                        p.concept,
                        p.img,
                        p.created_at,
                        p.updated_at,
                        p.published,
                        u.id,
                        u.nickname,
                        n.niceCount,
                        n.isNice
                    ORDER BY p.updated_at DESC
                    """)
    @Results(value = {
            @Result(property = "id", column = "p_id"),
            @Result(property = "user.id", column = "u_id"),
            @Result(property = "user.nickname", column = "nickname"),
            @Result(property = "imgPath", column = "img"),
            @Result(property = "tags", column = "p_id", many = @Many(select = "in.tech_camp.protospace_b.repository.TagRepository.prototypeTags")),
            @Result(property = "niceCount", column = "niceCount"),
            @Result(property = "isNice", column = "isNice"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at"),
            @Result(property = "published", column = "published")
    })
    List<PrototypeEntity> findDraftsByUserId(
            @Param("userId") Integer userId);

    @Select("""
            SELECT
                p.id p_id,
                p.prototypeName,
                p.catchCopy,
                p.concept,
                p.img,
                p.created_at,
                p.updated_at,
                p.published,
                u.id u_id,
                u.nickname nickname,
                COALESCE(n.niceCount, 0) niceCount,
                n.isNice,
                MAX(CASE WHEN r.user_id = #{currentUserId} OR p.user_id = #{currentUserId} THEN 1 ELSE 0 END) read
            FROM
                prototype p
            LEFT JOIN users u ON p.user_id = u.id
            LEFT JOIN (
                SELECT
                    nice.prototype_id,
                    MAX(CASE WHEN nice.user_id = #{currentUserId} THEN 1 ELSE 0 END) isNice,
                    count(*) niceCount
                FROM
                    nice
                GROUP BY
                    nice.prototype_id
            ) n ON p.id = n.prototype_id
            LEFT JOIN prototype_read_status r ON r.prototype_id = p.id AND r.user_id = #{currentUserId}
            GROUP BY
                p.id,
                p.prototypeName,
                p.catchCopy,
                p.concept,
                p.img,
                p.created_at,
                p.updated_at,
                u.id,
                u.nickname,
                n.isNice,
                n.niceCount
            ORDER BY p.created_at ASC
            """)
    @Results(value = {
            @Result(property = "id", column = "p_id"),
            @Result(property = "user.id", column = "u_id"),
            @Result(property = "user.nickname", column = "nickname"),
            @Result(property = "imgPath", column = "img"),
            @Result(property = "tags", column = "p_id", many = @Many(select = "in.tech_camp.protospace_b.repository.TagRepository.prototypeTags")),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at")
    })
    List<PrototypeEntity> showAllOrderByCreatedAtAsc(@Param("currentUserId") Integer currentUserId);

    @Select("""
            SELECT
                p.id p_id,
                p.prototypeName,
                p.catchCopy,
                p.concept,
                p.img,
                p.created_at,
                p.updated_at,
                p.published,
                u.id u_id,
                u.nickname nickname,
                COALESCE(n.niceCount, 0) niceCount,
                n.isNice,
                MAX(CASE WHEN r.user_id = #{currentUserId} OR p.user_id = #{currentUserId} THEN 1 ELSE 0 END) read,
                MAX(CASE WHEN pin.user_id = #{userId} THEN 1 ELSE 0 END) pinned,
                t.id t_id,
                t.tag_name
            FROM
                prototype p
            LEFT JOIN users u ON p.user_id = u.id
            LEFT JOIN (
                SELECT
                    nice.prototype_id,
                    MAX(CASE WHEN nice.user_id = #{currentUserId} THEN 1 ELSE 0 END) isNice,
                    COUNT(*) niceCount
                FROM nice
                GROUP BY
                    nice.prototype_id
            ) n ON p.id = n.prototype_id
            LEFT JOIN prototype_read_status r ON r.prototype_id = p.id AND r.user_id = #{currentUserId}
            LEFT JOIN prototype_tags pt ON p.id = pt.prototype_id
            LEFT JOIN bookmark b ON p.id = b.prototype_id
            LEFT JOIN pin ON p.id = pin.prototype_id
            LEFT JOIN tags t ON t.id = pt.tags_id
            WHERE p.published = true AND b.user_id = #{userId}
            GROUP BY
                p.id,
                p.prototypeName,
                p.catchCopy,
                p.concept,
                p.img,
                p.created_at,
                p.updated_at,
                p.published,
                u.id,
                u.nickname,
                n.isNice,
                n.niceCount,
                t.id
            ORDER BY p.created_at DESC, p.id ASC
            """)
    @Results(value = {
            @Result(property = "id", column = "p_id"),
            @Result(property = "user.id", column = "u_id"),
            @Result(property = "user.nickname", column = "nickname"),
            @Result(property = "imgPath", column = "img"),
            @Result(property = "tag.id", column = "t_id"),
            @Result(property = "tag.tagName", column = "tag_name"),
            @Result(property = "pin", column = "pinned"),
            @Result(property = "bookmark", column = "is_bookmark"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at"),
            @Result(property = "published", column = "published")
    })
    List<PrototypeEntity> showBookmarkedByUserId(Integer currentUserId, Integer userId);
}
