package com.example.poly.repository

import com.example.poly.repository.entity.StudentWorkbook
import org.springframework.data.jpa.repository.JpaRepository

interface StudentWorkbookRepository : JpaRepository<StudentWorkbook, Long>