package com.ute.studentmvvm.ui

import android.app.Dialog
import android.os.Bundle
import android.util.Patterns
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.ute.studentmvvm.data.AppDatabase
import com.ute.studentmvvm.databinding.DialogAddStudentBinding
import com.ute.studentmvvm.model.Student
import com.ute.studentmvvm.repository.StudentRepository
import com.ute.studentmvvm.viewmodel.StudentViewModel
import com.ute.studentmvvm.viewmodel.StudentViewModelFactory
import java.util.Locale

class AddStudentDialogFragment : DialogFragment() {

    // Dùng chung ViewModel với màn hình danh sách.
    private val viewModel: StudentViewModel by activityViewModels {
        val database = AppDatabase.getDatabase(
            requireContext().applicationContext
        )
        StudentViewModelFactory(
            StudentRepository(database.studentDao())
        )
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding = DialogAddStudentBinding.inflate(layoutInflater)

        val alertDialog = AlertDialog.Builder(requireContext())
            .setTitle("Thêm sinh viên")
            .setView(binding.root)
            .setPositiveButton("Lưu", null)
            .setNegativeButton("Hủy", null)
            .create()

        // Tự xử lý nút Lưu để hộp thoại không đóng khi nhập sai.
        alertDialog.setOnShowListener {
            val saveButton = requireNotNull(
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
            )

            saveButton.setOnClickListener {
                val student = readStudent(binding)
                    ?: return@setOnClickListener

                saveButton.isEnabled = false
                viewModel.insert(student)
                dismiss()
            }
        }

        return alertDialog
    }

    private fun readStudent(
        binding: DialogAddStudentBinding
    ): Student? {
        val name = binding.etFullName.text.toString().trim()
        val code = binding.etStudentCode.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()

        val gpa = binding.etGpa.text.toString()
            .trim()
            .replace(',', '.')
            .toDoubleOrNull()

        // Xóa thông báo lỗi của lần kiểm tra trước.
        binding.etFullName.error = null
        binding.etStudentCode.error = null
        binding.etEmail.error = null
        binding.etGpa.error = null

        if (name.isBlank()) {
            binding.etFullName.error = "Vui lòng nhập họ tên"
            binding.etFullName.requestFocus()
            return null
        }

        if (code.isBlank()) {
            binding.etStudentCode.error = "Vui lòng nhập MSSV"
            binding.etStudentCode.requestFocus()
            return null
        }

        val finalEmail = email.ifBlank {
            "${code.lowercase(Locale.ROOT)}@ute.udn.vn"
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(finalEmail).matches()) {
            binding.etEmail.error = "Vui lòng nhập email hợp lệ"
            binding.etEmail.requestFocus()
            return null
        }

        if (gpa == null || !gpa.isFinite() || gpa !in 0.0..4.0) {
            binding.etGpa.error = "GPA phải là số từ 0 đến 4"
            binding.etGpa.requestFocus()
            return null
        }

        return Student(
            fullName = name,
            studentCode = code,
            email = finalEmail,
            gpa = gpa
        )
    }
}