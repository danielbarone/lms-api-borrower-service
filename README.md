# BorrowerService

- port 8090

## GET /getBranches

## GET /getBooksByBranch?{branchId}

## POST /checkoutBook

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

## POST /returnBook

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