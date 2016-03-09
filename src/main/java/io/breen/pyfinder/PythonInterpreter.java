package io.breen.pyfinder;


import java.nio.file.Path;

public final class PythonInterpreter implements Comparable<PythonInterpreter> {
    public final Path path;
    public final PythonVersion version;

    public PythonInterpreter(Path path, PythonVersion version) {
        this.path = path;
        this.version = version;
    }

    public int compareTo(PythonInterpreter other) {
        int versionDiff = version.compareTo(other.version);
        if (versionDiff == 0)
            return path.compareTo(other.path);
        else
            return versionDiff;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null)
            return false;

        if (!(other instanceof PythonInterpreter))
            return false;

        PythonInterpreter o = (PythonInterpreter)other;

        return path.equals(o.path) && version.equals(o.version);
    }

    @Override
    public String toString() {
        return version + " (" + path.toString() + ")";
    }
}
