# `pyfinder`

`pyfinder` is a Java library that helps find valid paths to installed Python
interpreters. For example, it contains OS-specific logic for finding `python.exe`
on Windows, where the default install location has not been historically
consistent.

Of course, it may not always be possible to locate a Python interpreter
if a user elected to install it to a completely non-standard location.

Part of the reason why this library exists is because, on Windows, the
path to `python.exe` is not added to the `PATH` environment variable by
default. This library will first attempt to find Python interpreters at
the directories listed in `PATH`, but will fall back to hard-coded interpreter
locations.


## Example

Use the `PythonFinder` class' `findAll()` method to get a list of all valid
interpreter locations. This method will attempt to run Python executables
and determine their versions.

    PythonFinder finder = new PythonFinder();
    for (PythonInterpreter i : finder.findAll())
        System.out.println(i);

### Limiting the search to specific Python verisons

Try using the other variants of the `findAll()` method. On my machine,

    PythonFinder finder = new PythonFinder();

    System.out.println("Python 3 or newer:");
    for (PythonInterpreter i : finder.findOrNewer(new PythonVersion(3)))
        System.out.println(i);

    System.out.println("only Python 2:");
    for (PythonInterpreter i : finder.findBetweenInclusive(new PythonVersion(2), new PythonVersion(3)))
        System.out.println(i);

produces the following output:

    Python 3 or newer:
    Python 3.5.0 (/usr/local/bin/python3.5)
    Python 3.5.0 (/usr/local/bin/python3)
    only Python 2:
    Python 2.7.10 (/usr/bin/python)

Note that this library is not symlink or "shortcut" aware: on my
system, `/usr/local/bin/python3` is a symlink to `/usr/local/bin/python3.5`.
