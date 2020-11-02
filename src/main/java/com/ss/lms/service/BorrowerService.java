package com.ss.lms.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ss.lms.entity.Book;
import com.ss.lms.entity.BookCopies;
import com.ss.lms.entity.Branch;
import com.ss.lms.entity.Loan;
import com.ss.lms.repo.AuthorRepo;
import com.ss.lms.repo.BookRepo;
import com.ss.lms.repo.BorrowerRepo;
import com.ss.lms.repo.BranchRepo;
import com.ss.lms.repo.GenreRepo;
import com.ss.lms.repo.LoanRepo;
import com.ss.lms.repo.PublisherRepo;

class BorrowBookRequest {
  private Branch branch;
  private Book book;
	private Integer cardNo;

  public BorrowBookRequest(Branch branch, Book book, Integer cardNo) {
    this.branch = branch;
    this.book = book;
		this.cardNo = cardNo;
  }

  public Branch getBranch() {
    return branch;
  }

  public Book getBook() {
    return book;
  }

  public Integer getCardNo() {
    return cardNo;
	}
	
}

@RestController
public class BorrowerService {
  @Autowired
	AuthorRepo arepo;
	
	@Autowired
	BookRepo brepo;
	
	@Autowired
	BorrowerRepo borrowerRepo;

	@Autowired
	BranchRepo branchRepo;

	@Autowired
  GenreRepo grepo;
  
  @Autowired
	LoanRepo lrepo;

	@Autowired
  PublisherRepo prepo;

  @RequestMapping(value = "/getBranches", method = RequestMethod.GET, produces = "application/json")
	public List<Branch> getBranches() {
		List<Branch> branches = new ArrayList<>();
		branches = branchRepo.findAll();
		return branches;
  }
  
  @RequestMapping(value = "/getBooksByBranch", method = RequestMethod.GET, produces = "application/json")
	public List<Book> getBooksByBranch(@RequestParam Integer branchId) {
		List<Book> books = new ArrayList<>();
    Branch branch = branchRepo.getOne(branchId);
    
    // only display books with > 0 copies
    BookCopies currBookCopies;
    for (Book b : branch.getBooks()) {
      currBookCopies = branchRepo.getNoOfCopies(branchId, b.getBookId()).get(0);
      if (currBookCopies.getNoOfCopies() > 0) {
        books.add(b);
      }
    }
		return books;
  }
  
  private boolean adjustBookCopies(Branch branch, Book book, Integer adjustment) {
    try {
      List<BookCopies> bookCopies = branchRepo.getNoOfCopies(branch.getBranchId(), book.getBookId());
      Integer currNoCopies = bookCopies.get(0).getNoOfCopies();
      branchRepo.updateNoOfCopies(branch.getBranchId(), book.getBookId(), currNoCopies + adjustment);
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }

  }

  @RequestMapping(value = "/checkoutBook", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	public ResponseEntity<?> checkoutBook(@RequestBody BorrowBookRequest borrowBookRequest) {
    Branch branch = borrowBookRequest.getBranch();
		Book book = borrowBookRequest.getBook();
    Integer cardNo = borrowBookRequest.getCardNo();
    
    Date date = new Date();
    Calendar c = Calendar.getInstance();
    c.setTime(date);
    c.add(Calendar.DATE, 7);
    Date due = c.getTime();

    Timestamp dateOut = new Timestamp(date.getTime());
    Timestamp dueDate = new Timestamp(due.getTime());

    Loan loan = new Loan();
    loan.setBranchId(branch.getBranchId());
    loan.setBookId(book.getBookId());
    loan.setCardNo(cardNo);
    loan.setDateOut(dateOut);
    loan.setDueDate(dueDate);
    loan.setDateIn(null);

    try {
      BookCopies bookCopies = branchRepo.getNoOfCopies(branch.getBranchId(), book.getBookId()).get(0);
      if (bookCopies.getNoOfCopies() > 0) {
        lrepo.save(loan);
        adjustBookCopies(branch, book, -1);
        return new ResponseEntity<>(loan, HttpStatus.OK);
      } else {
        return new ResponseEntity<>("Error: No more copies of this book.", HttpStatus.BAD_REQUEST);
      }
    } catch (Exception e) {
      e.printStackTrace();
      return new ResponseEntity<>("Checkout failed", HttpStatus.BAD_REQUEST);
    }
  }
  
  @RequestMapping(value = "/returnBook", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	public ResponseEntity<?> returnBook(@RequestBody BorrowBookRequest borrowBookRequest) {
		Branch branch = borrowBookRequest.getBranch();
		Book book = borrowBookRequest.getBook();
		Integer cardNo = borrowBookRequest.getCardNo();

    Date date = new Date();
    Timestamp dateIn = new Timestamp(date.getTime());

    try {
      List<Loan> loans = branchRepo.getLoan(branch.getBranchId(), book.getBookId(), cardNo);
      Loan loan = loans.get(0);
      if (loan.getDateIn() != null) {
        return new ResponseEntity<>("Error: Book already returned.", HttpStatus.BAD_REQUEST);
      }
      branchRepo.returnBook(branch.getBranchId(), book.getBookId(), cardNo, dateIn);
      adjustBookCopies(branch, book, 1);
      loan.setDateIn(dateIn);
      return new ResponseEntity<>(loan, HttpStatus.OK);
      
    } catch (Exception e) {
      e.printStackTrace();
      return new ResponseEntity<>("Return book failed.", HttpStatus.BAD_REQUEST);
    }
	}
}
