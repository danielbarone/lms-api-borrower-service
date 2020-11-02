package com.ss.lms.repo;

import javax.persistence.Id;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ss.lms.entity.Loan;

@Repository
public interface LoanRepo extends JpaRepository<Loan, Id>{

}