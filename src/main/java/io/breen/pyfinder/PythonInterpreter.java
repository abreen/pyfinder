package io.breen.pyfinder;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;

public final class PythonInterpreter implements Comparable<PythonInterpreter> {
    public final Path path;
    public final PythonVersion version;

    public static PythonInterpreter fromPath(Path pathToInterpreter) throws IOException, InterruptedException {
        if (!Files.exists(pathToInterpreter) || !Files.isExecutable(pathToInterpreter))
            return null;

        String pathStr = pathToInterpreter.toString();

        ProcessBuilder pb1 = new ProcessBuilder(pathStr, "-c", "def f(): pass");

        if (pb1.start().waitFor() == 0) {
            /*
             * This path is probably valid for a Python interpreter; it exited OK for the command line
             * arguments above and parsed a basic Python function. (?)
             */

            // get version string
            ProcessBuilder pb2 = new ProcessBuilder(pathStr, "--version");

            Process p2 = pb2.start();

            String nextLine = null;

            /*
             * Need readers for both stdout and stderr: Python 2 prints its version to stderr,
             * Python 3 prints it to stdout.
             */
            BufferedReader outReader = new BufferedReader(new InputStreamReader(p2.getInputStream()));
            BufferedReader errReader = new BufferedReader(new InputStreamReader(p2.getErrorStream()));

            String outLastLine = null;
            while ((nextLine = outReader.readLine()) != null)
                outLastLine = nextLine;

            String errLastLine = null;
            while ((nextLine = errReader.readLine()) != null)
                errLastLine = nextLine;

            if (p2.waitFor() != 0 || (outLastLine == null && errLastLine == null)) {
                // error getting version string
                return null;
            } else {
                try {
                    PythonVersion version = PythonVersion.fromVersionString(
                            outLastLine != null ? outLastLine : errLastLine
                    );

                    return new PythonInterpreter(pathToInterpreter, version);

                } catch (IllegalArgumentException ignored) {}
            }
        }

        // path might be an executable, but exited with an error with command line args
        return null;
    }

    private PythonInterpreter(Path path, PythonVersion version) {
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
