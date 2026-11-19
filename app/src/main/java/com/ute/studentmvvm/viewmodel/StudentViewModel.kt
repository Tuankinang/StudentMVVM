package com.ute.studentmvvm.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.ute.studentmvvm.model.Student
import com.ute.studentmvvm.repository.StudentRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StudentViewModel(
    private val repository: StudentRepository
) : ViewModel() {

    // Giao diện sẽ quan sát danh sách này.
    val allStudents: LiveData<List<Student>> =
        repository.allStudents.asLiveData(Dispatchers.IO)

    // Thông báo kết quả thêm hoặc xóa.
    private val _statusMessage = MutableLiveData<String?>()
    val statusMessage: LiveData<String?> = _statusMessage

    fun insert(student: Student) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.insert(student)
                _statusMessage.postValue(
                    "Đã thêm sinh viên: ${student.fullName}"
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _statusMessage.postValue(
                    "Lỗi khi thêm sinh viên: ${e.message}"
                )
            }
        }
    }

    fun delete(student: Student) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.delete(student)
                _statusMessage.postValue(
                    "Đã xóa sinh viên: ${student.fullName}"
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _statusMessage.postValue(
                    "Lỗi khi xóa sinh viên: ${e.message}"
                )
            }
        }
    }

    // Giao diện gọi sau khi đã hiển thị thông báo.
    fun clearStatusMessage() {
        _statusMessage.value = null
    }
}