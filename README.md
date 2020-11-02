# BorrowerService

port 8090

- - - -

### /getBranches

- GET

- - - -

### /getBooksByBranch?branchId={branchId}

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