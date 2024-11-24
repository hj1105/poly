package com.example.poly.repository

import com.example.poly.repository.entity.Workbook
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface WorkbookRepository : JpaRepository<Workbook, Long>