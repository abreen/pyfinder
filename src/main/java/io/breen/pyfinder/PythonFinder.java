package io.breen.pyfinder;


import org.apache.commons.lang3.SystemUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class PythonFinder {
    private enum OS {
        WINDOWS, NIX
    }

    private static OS os;

    static {
        os = SystemUtils.IS_OS_WINDOWS ? OS.WINDOWS : OS.NIX;
    }

    private abstract class VersionComparator {
        public abstract boolean shouldInclude(PythonVersion v);
    }

    private final Set<Path> likelyPaths = new HashSet<Path>(50);

    public PythonFinder() {
        Map<String, String> env = System.getenv();
        if (env.containsKey("PATH")) {
            String path = env.get("PATH");

            String pathSep = System.getProperty("path.separator");
            String[] dirs = path.split(pathSep);

            for (String dir : dirs) {
                likelyPaths.add(Paths.get(dir, "python"));
                likelyPaths.add(Paths.get(dir, "python3"));
            }
        }

        if (os == OS.WINDOWS) {
            String user = System.getProperty("user.name");

            for (String v : Arrays.asList("35", "34", "33", "32")) {
                if (user != null) {
                    String prefix = "C:\\Users\\" + user + "\\AppData\\Local\\Programs\\Python\\";
                    likelyPaths.add(Paths.get(prefix + "Python" + v + "\\python.exe"));
                }

                likelyPaths.add(Paths.get("C:\\Python" + v + "\\python.exe"));
            }

            likelyPaths.add(Paths.get("C:\\Program Files (x86)\\Anaconda\\python.exe"));
            likelyPaths.add(Paths.get("C:\\Program Files\\Anaconda\\python.exe"));

        } else {
            // UNIX, Linux, OS X...

            likelyPaths.add(Paths.get("/usr/local/bin/python"));
            likelyPaths.add(Paths.get("/usr/local/bin/python3"));
            likelyPaths.add(Paths.get("/usr/local/bin/python3.5"));
            likelyPaths.add(Paths.get("/usr/local/bin/python3.4"));
            likelyPaths.add(Paths.get("/usr/local/bin/python3.3"));
            likelyPaths.add(Paths.get("/usr/local/bin/python3.2"));
            likelyPaths.add(Paths.get("/usr/bin/python"));
            likelyPaths.add(Paths.get("/usr/bin/python3"));
        }
    }

    public void addLikelyPath(Path p) {
        likelyPaths.add(p);
    }

    private List<PythonInterpreter> find(VersionComparator comparator) throws IOException, InterruptedException {
        ArrayList<PythonInterpreter> all = new ArrayList<PythonInterpreter>(likelyPaths.size());

        for (Path p : likelyPaths) {
            PythonInterpreter i = PythonInterpreter.fromPath(p);
            if (i != null && comparator.shouldInclude(i.version))
                all.add(i);
        }

        return all;
    }

    public List<PythonInterpreter> findAll() throws IOException, InterruptedException {
        return find(new VersionComparator() {
            @Override
            public boolean shouldInclude(PythonVersion v) {
                return true;
            }
        });
    }

    public List<PythonInterpreter> findOrNewer(final PythonVersion version) throws IOException, InterruptedException {
        return find(new VersionComparator() {
            @Override
            public boolean shouldInclude(PythonVersion other) {
                return other.compareTo(version) >= 0;
            }
        });
    }

    public List<PythonInterpreter> findOrOlder(final PythonVersion version) throws IOException, InterruptedException {
        return find(new VersionComparator() {
            @Override
            public boolean shouldInclude(PythonVersion other) {
                return other.compareTo(version) <= 0;
            }
        });
    }

    public List<PythonInterpreter> findNewer(final PythonVersion version) throws IOException, InterruptedException {
        return find(new VersionComparator() {
            @Override
            public boolean shouldInclude(PythonVersion other) {
                return other.compareTo(version) > 0;
            }
        });
    }

    public List<PythonInterpreter> find(final PythonVersion version) throws IOException, InterruptedException {
        return find(new VersionComparator() {
            @Override
            public boolean shouldInclude(PythonVersion other) {
                return other.compareTo(version) == 0;
            }
        });
    }

    public List<PythonInterpreter> findBetween(PythonVersion v1, PythonVersion v2)
            throws IOException, InterruptedException
    {
        final PythonVersion lo, hi;

        if (v2.compareTo(v1) < 0) {
            lo = v2;
            hi = v1;
        } else {
            lo = v1;
            hi = v2;
        }

        return find(new VersionComparator() {
            @Override
            public boolean shouldInclude(PythonVersion other) {
                return other.compareTo(lo) >= 0 && other.compareTo(hi) < 0;
            }
        });
    }

    public List<PythonInterpreter> findOlder(final PythonVersion version) throws IOException, InterruptedException {
        return find(new VersionComparator() {
            @Override
            public boolean shouldInclude(PythonVersion other) {
                return other.compareTo(version) < 0;
            }
        });
    }

    public static void main(String[] args) throws Exception {
        PythonFinder finder = new PythonFinder();

        System.out.println("likely Python paths for this system:");

        for (Path p : finder.likelyPaths)
            System.out.println(p);

        System.out.println("actual interpreter locations:");

        for (PythonInterpreter i : finder.findAll())
            System.out.println(i);

        System.out.println("Python 3 or newer:");

        for (PythonInterpreter i : finder.findOrNewer(new PythonVersion(3)))
            System.out.println(i);

        System.out.println("only Python 2:");

        for (PythonInterpreter i : finder.findBetween(new PythonVersion(2), new PythonVersion(3)))
            System.out.println(i);
    }
}
