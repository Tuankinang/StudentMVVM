package com.ute.studentmvvm.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ute.studentmvvm.model.Student
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentDao {

    // Thêm một sinh viên, trả về ID của dòng vừa thêm.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: Student): Long

    // Thêm danh sách sinh viên, dùng khi đồng bộ API.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(students: List<Student>)

    // Cập nhật sinh viên dựa trên khóa chính id.
    @Update
    suspend fun updateStudent(student: Student)

    // Xóa một sinh viên.
    @Delete
    suspend fun deleteStudent(student: Student)

    // Xóa sinh viên theo ID.
    @Query("DELETE FROM students_table WHERE id = :id")
    suspend fun deleteStudentById(id: Int)

    // Tự phát danh sách mới khi dữ liệu trong bảng thay đổi.
    @Query("SELECT * FROM students_table ORDER BY id DESC")
    fun getAllStudents(): Flow<List<Student>>

    // Tìm một sinh viên theo mã số sinh viên.
    @Query(
        "SELECT * FROM students_table WHERE student_code = :code LIMIT 1"
    )
    suspend fun getStudentByCode(code: String): Student?
}