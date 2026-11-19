package com.ute.studentmvvm.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.ute.studentmvvm.data.AppDatabase
import com.ute.studentmvvm.databinding.FragmentStudentListBinding
import com.ute.studentmvvm.model.Student
import com.ute.studentmvvm.repository.StudentRepository
import com.ute.studentmvvm.ui.adapter.StudentAdapter
import com.ute.studentmvvm.viewmodel.StudentViewModel
import com.ute.studentmvvm.viewmodel.StudentViewModelFactory
import java.util.Locale

class StudentListFragment : Fragment() {

    private var _binding: FragmentStudentListBinding? = null
    private val binding get() = checkNotNull(_binding)

    // Dùng Factory đã tạo để cung cấp Repository cho ViewModel.
    private val viewModel: StudentViewModel by activityViewModels {
        val database = AppDatabase.getDatabase(
            requireContext().applicationContext
        )
        val repository = StudentRepository(database.studentDao())
        StudentViewModelFactory(repository)
    }

    private lateinit var studentAdapter: StudentAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStudentListBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        binding.fabAdd.setOnClickListener {
            val tag = "ADD_STUDENT_DIALOG"

            if (childFragmentManager.findFragmentByTag(tag) == null) {
                AddStudentDialogFragment().show(
                    childFragmentManager,
                    tag
                )
            }
        }

        studentAdapter = StudentAdapter(
            onStudentClick = { student ->
                showStudentDetail(student)
            },
            onDeleteClick = { student ->
                confirmDelete(student)
            }
        )

        binding.rvStudents.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = studentAdapter
            setHasFixedSize(true)
        }

        viewModel.allStudents.observe(viewLifecycleOwner) { students ->
            studentAdapter.submitList(students)
            binding.tvStudentCountBadge.text =
                "${students.size} sinh viên"
            binding.layoutEmpty.isVisible = students.isEmpty()
        }

        viewModel.statusMessage.observe(viewLifecycleOwner) { message ->
            if (!message.isNullOrBlank()) {
                Toast.makeText(
                    requireContext(),
                    message,
                    Toast.LENGTH_SHORT
                ).show()

                viewModel.clearStatusMessage()
            }
        }
    }

    private fun showStudentDetail(student: Student) {
        val formattedGpa = String.format(
            Locale.getDefault(),
            "%.2f",
            student.gpa
        )

        val detail = """
            Họ tên: ${student.fullName}
            MSSV: ${student.studentCode}
            Email: ${student.email}
            Ngành học: ${student.major}
            GPA: $formattedGpa
        """.trimIndent()

        AlertDialog.Builder(requireContext())
            .setTitle("Thông tin sinh viên")
            .setMessage(detail)
            .setPositiveButton("Đóng", null)
            .show()
    }

    private fun confirmDelete(student: Student) {
        AlertDialog.Builder(requireContext())
            .setTitle("Xóa sinh viên")
            .setMessage(
                "Bạn có muốn xóa sinh viên ${student.fullName}?"
            )
            .setNegativeButton("Hủy", null)
            .setPositiveButton("Xóa") { _, _ ->
                viewModel.delete(student)
            }
            .show()
    }

    override fun onDestroyView() {
        binding.rvStudents.adapter = null
        super.onDestroyView()
        _binding = null
    }
}