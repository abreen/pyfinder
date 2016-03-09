package io.breen.pyfinder;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class PythonVersion implements Comparable<PythonVersion> {
    private static final Pattern VERSION_PATTERN = Pattern.compile("Python (\\d+)\\.(\\d+)\\.(\\d+)");

    public final int major;
    public final int minor;
    public final int micro;

    public static PythonVersion fromVersionString(String s) {
        Matcher m = VERSION_PATTERN.matcher(s);

        if (m.matches()) {
            int major = Integer.parseInt(m.group(1));
            int minor = Integer.parseInt(m.group(2));
            int micro = Integer.parseInt(m.group(3));

            return new PythonVersion(major, minor, micro);
        } else {
            throw new IllegalArgumentException("bad version string");
        }
    }

    public PythonVersion(int major, int minor, int micro) {
        this.major = major;
        this.minor = minor;
        this.micro = micro;
    }

    public PythonVersion(int major, int minor) {
        this(major, minor, 0);
    }

    public PythonVersion(int major) {
        this(major, 0, 0);
    }

    public int compareTo(PythonVersion other) {
        if (major != other.major)
            return major - other.major;
        else if (minor != other.minor)
            return minor - other.minor;
        else
            return micro - other.micro;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null)
            return false;

        if (!(other instanceof PythonVersion))
            return false;

        PythonVersion o = (PythonVersion)other;

        return major == o.major && minor == o.minor && micro == o.micro;
    }

    @Override
    public String toString() {
        return "Python " + major + "." + minor + "." + micro;
    }
}
