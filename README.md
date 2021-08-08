# DetectDuplicates

This little helper program collects the hashsums of all files (optionally recursive) in a directory. It also reports back if there are files with identical
hashsums. If wanted, duplicate files can be deleted.

## Building
Maven and JDK 8 are needed. ```mvn install```

## Running
JRE 8 is needed. Run the jar with `-h` to get these instructions.

```
This program calculates the SHA-1 checksums of all files in a directory
and checks if there are identical files.
If there are duplicates, the file sha-1-duplicates.txt will be created with details.

Usage: java -jar DetectDuplicates.jar [(optional) Path to a Directory] [OPTION]

The first optional parameter is a directory path to check for identical files.
If omitted, the directory of the jar-file will be checked.
Possible Modes:
[default]             - checks for duplicates and lists them in sha-1-duplicates.txt
--delete | -d         - deletes duplicates
--list | -l           - lists the SHA-1 hashes in sha-1-hashes.txt
Options:
--recursive | -r      - also processes sub-folders
--bench | -b          - prints some benchmark data after completion
Help:
--help                - prints this message and exits


```
