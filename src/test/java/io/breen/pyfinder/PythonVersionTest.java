package io.breen.pyfinder;

import org.junit.Test;

import static org.junit.Assert.*;


public class PythonVersionTest {

    @org.junit.Test
    public void testEquals() throws Exception {
        assertTrue(new PythonVersion(3, 0, 0).equals(new PythonVersion(3, 0, 0)));
    }

    @org.junit.Test
    public void testCompareToEquals() throws Exception {
        assertTrue(new PythonVersion(3, 0, 0).compareTo(new PythonVersion(3, 0, 0)) == 0);
    }

    @org.junit.Test
    public void testLessThanMajor() throws Exception {
        assertTrue(new PythonVersion(2, 0, 0).compareTo(new PythonVersion(3, 0, 0)) < 0);
    }

    @org.junit.Test
    public void testGreaterThanMajor() throws Exception {
        assertTrue(new PythonVersion(4, 0, 0).compareTo(new PythonVersion(3, 0, 0)) > 0);
    }

    @org.junit.Test
    public void testLessThanMinor() throws Exception {
        assertTrue(new PythonVersion(3, 2, 0).compareTo(new PythonVersion(3, 5, 0)) < 0);
    }

    @org.junit.Test
    public void testGreaterThanMinor() throws Exception {
        assertTrue(new PythonVersion(3, 5, 0).compareTo(new PythonVersion(3, 1, 0)) > 0);
    }

    @org.junit.Test
    public void testLessThanMicro() throws Exception {
        assertTrue(new PythonVersion(3, 4, 2).compareTo(new PythonVersion(3, 4, 4)) < 0);
    }

    @org.junit.Test
    public void testGreaterThanMicro() throws Exception {
        assertTrue(new PythonVersion(3, 4, 6).compareTo(new PythonVersion(3, 4, 3)) > 0);
    }

    @Test
    public void testFromVersionString() throws Exception {
        String s = "Python 3.1.2";
        PythonVersion v = PythonVersion.fromVersionString(s);

        assertEquals("major version number", 3, v.major);
        assertEquals("minor version number", 1, v.minor);
        assertEquals("micro version number", 2, v.micro);
    }
}
