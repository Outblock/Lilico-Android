package io.outblock.lilico.database

import androidx.room.*

@Dao
interface BookmarkDao {
    @Insert
    fun save(bookmark: Bookmark): Long

    @Query("select * from Bookmark where url=:url limit 1")
    fun findByUrl(url: String): Bookmark?

    @Query("select count(*) from Bookmark")
    fun count(): Int

    @Query("select * from Bookmark where 1=1 order by createTime desc")
    fun findAll(): List<Bookmark>

    @Query("delete from Bookmark where url=:url")
    fun deleteByUrl(url: String)

    @Delete
    fun delete(bookmark: Bookmark)

    @Update
    fun update(bookmark: Bookmark)
}