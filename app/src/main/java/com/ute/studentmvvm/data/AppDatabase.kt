package com.ute.studentmvvm.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.ute.studentmvvm.model.Student

@Database(
    entities = [Student::class],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun studentDao(): StudentDao

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {

                // Kiểm tra lại để tránh tạo nhiều instance
                // khi có nhiều luồng cùng gọi getDatabase().
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ute_student_room.db"
                )
                    .addCallback(DatabaseCallback())
                    .build()
                    .also { database ->
                        INSTANCE = database
                    }
            }
        }
    }

    private class DatabaseCallback : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            val initialStudents = listOf(
                Student(
                    fullName = "Nguyễn Văn An",
                    studentCode = "20110001",
                    email = "an.nv@ute.udn.vn",
                    gpa = 3.85,
                    major = "Công Nghệ Thông Tin"
                ),
                Student(
                    fullName = "Trần Thị Mai",
                    studentCode = "20110002",
                    email = "mai.tt@ute.udn.vn",
                    gpa = 3.92,
                    major = "Kỹ Thuật Phần Mềm"
                ),
                Student(
                    fullName = "Lê Hoàng Phúc",
                    studentCode = "20110003",
                    email = "phuc.lh@ute.udn.vn",
                    gpa = 3.45,
                    major = "An Toàn Thông Tin"
                ),
                Student(
                    fullName = "Phạm Quốc Dũng",
                    studentCode = "20110004",
                    email = "dung.pq@ute.udn.vn",
                    gpa = 3.60,
                    major = "Mạng Máy Tính"
                )
            )

            // Chèn dữ liệu mẫu ngay trong lần tạo database đầu tiên.
            initialStudents.forEach { student ->
                db.execSQL(
                    """
                    INSERT INTO students_table
                        (full_name, student_code, email, gpa, major)
                    VALUES (?, ?, ?, ?, ?)
                    """.trimIndent(),
                    arrayOf<Any>(
                        student.fullName,
                        student.studentCode,
                        student.email,
                        student.gpa,
                        student.major
                    )
                )
            }
        }
    }
}