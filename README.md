# BorrowerService

- port 8090

### /getBranches

- GET

- - - -

### /getBooksByBranch?{branchId}

- GET

- - - -

### /checkoutBook

- POST

#### Request body:

```
{
  "branch": {
    "branchId": {branchId}
  },
  "book": {
    "bookId": {bookId}
  },
  "cardNo": {cardNo}
}
```

- - - -

### /returnBook

- POST

#### Request body:

```
{
  "branch": {
    "branchId": {branchId}
  },
  "book": {
    "bookId": {bookId}
  },
  "cardNo": {cardNo}
}
```