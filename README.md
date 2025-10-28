# File server

A multithreaded file server and client.

## Features 

- PUT — uploading a file to the server.
- GET BY_NAME / BY_ID — download the file.
- DELETE BY_NAME / BY_ID — delete the file.
- EXIT is a neat shutdown of the server.
- Multithreading on the server (thread pool).

## Preview

```
Enter action (1 - get a file, 2 - save a file, 3 - delete a file): > 2
Enter name of the file: > my_cat.jpg
Enter name of the file to be saved on server: > 
The request was sent.
Response says that file is saved! ID = 0
```

```
Enter action (1 - get a file, 2 - save a file, 3 - delete a file): > 1
Do you want to get the file by name or by id (1 - name, 2 - id): > 2
Enter id: > 0
The request was sent.
The file was downloaded! Specify a name for it: > cat.jpg
File saved on the hard drive!
```

```
Enter action (1 - get a file, 2 - save a file, 3 - delete a file): > 3
Do you want to delete the file by name or by id (1 - name, 2 - id): > 2
Enter id: > 0
The request was sent.
The response says that this file was deleted successfully!
```
