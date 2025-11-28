package com.piyush.bmusurat.data.models

import com.google.gson.annotations.SerializedName

data class ProgramResponse(
    @SerializedName("institute")
    val institute: InstituteData?,
    @SerializedName("program")
    val programInfo: ProgramBasicInfo?,
    @SerializedName("success")
    val success: Boolean
)

data class ProgramBasicInfo(
    @SerializedName("_id")
    val id: String?,
    @SerializedName("bmu_id")
    val bmuId: Int?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("short_name")
    val shortName: String?
)

data class InstituteData(
    @SerializedName("director")
    val director: Director?,
    @SerializedName("faculty")
    val faculty: List<FacultyMember>?,
    @SerializedName("gallery")
    val gallery: List<GalleryAlbum>?,
    @SerializedName("infrastructure")
    val infrastructure: List<GalleryAlbum>?,
    @SerializedName("placement")
    val placement: List<PlacementMember>?,
    @SerializedName("programs")
    val programs: Map<String, ProgramDetails>?,
    @SerializedName("students_recruited")
    val studentsRecruited: List<RecruitedStudent>?
)

data class Director(
    @SerializedName("email")
    val email: String?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("photo")
    val photo: String?,
    @SerializedName("qualification")
    val qualification: String?,
    @SerializedName("teaching_experience")
    val teachingExperience: String?
)

data class FacultyMember(
    @SerializedName("designation")
    val designation: String?,
    @SerializedName("email")
    val email: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("photo")
    val photo: String?,
    @SerializedName("qualification")
    val qualification: String?,
    @SerializedName("specialization")
    val specialization: String?
)

data class GalleryAlbum(
    @SerializedName("title")
    val title: String?,
    @SerializedName("images")
    val images: List<String>?
)

data class PlacementMember(
    @SerializedName("designation")
    val designation: String?,
    @SerializedName("email")
    val email: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("phone")
    val phone: String?,
    @SerializedName("photo")
    val photo: String?,
    @SerializedName("qualification")
    val qualification: String?
)

data class ProgramDetails(
    @SerializedName("description")
    val description: List<String>?,
    @SerializedName("semesters")
    val semesters: List<Semester>?
)

data class Semester(
    @SerializedName("semester")
    val semesterName: String?,
    @SerializedName("subjects")
    val subjects: List<Subject>?
)

data class Subject(
    @SerializedName("subject_code")
    val code: String?,
    @SerializedName("subject_name")
    val name: String?
)

data class RecruitedStudent(
    @SerializedName("company_name")
    val companyName: String?,
    @SerializedName("department_name")
    val departmentName: String?,
    @SerializedName("sr_no")
    val srNo: String?,
    @SerializedName("student_name")
    val studentName: String?
)