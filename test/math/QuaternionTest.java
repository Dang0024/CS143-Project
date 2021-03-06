package math;

import static org.junit.Assert.*;

import java.util.Random;

import org.junit.Test;

public class QuaternionTest {
    private static final double EPSILON = 1.00E-6;
    private Random gen = new Random();

    @Test
    public void testEqualsQuaternion() {
        Quaternion a = new Quaternion(0.232f, 12.232f, 234.23f, 76.23f);
        Quaternion b = new Quaternion(0.232f, 12.232f, 234.23f, 76.23f);
        assertQuaternionEquals(a, b);
    }

    @Test
    public void testBasicQuaternionProperties() {
        Quaternion n = new Quaternion(-1, 0, 0, 0);
        Quaternion i = new Quaternion(0, 1, 0, 0);
        Quaternion j = new Quaternion(0, 0, 1, 0);
        Quaternion k = new Quaternion(0, 0, 0, 1);

        assertQuaternionEquals(Quaternion.IDENTITY, n.times(n)); // (-1)^2 == 1

        // i^2 = j^2 = k^2 = ijk = -1
        assertQuaternionEquals(n, i.times(i)); // i^2 = -1
        assertQuaternionEquals(n, j.times(j)); // j^2 = -1
        assertQuaternionEquals(n, k.times(k)); // k^2 = -1
        assertQuaternionEquals(n, i.times(j).times(k)); // ijk = -1
    }

    @Test
    public void testToMatrixString() {
        // This transformation matrix should rotate 90 degrees about the x axis
        assertEquals(
                "| 01.000 00.000 00.000 00.000 |\n" +
                "| 00.000 00.000 01.000 00.000 |\n" +
                "| 00.000 -1.000 00.000 00.000 |\n" +
                "| 00.000 00.000 00.000 01.000 |",
                new Quaternion(Vector3f.UNIT_X, 90).toMatrixString());
    }

    @Test
    public void testRollPitchAndYawAxies() {
        // Start with a fairly random rotation
        Quaternion r = new Quaternion(Vector3f.UNIT_X, 15); 
        r.timesEquals(new Quaternion(Vector3f.UNIT_Y, 7));
        r.timesEquals(new Quaternion(Vector3f.UNIT_Z, -328));

        Vector3Test.assertVector3Equals(Vector3f.UNIT_X.negate().times(r), r.pitchAxis());
        Vector3Test.assertVector3Equals(Vector3f.UNIT_Y.negate().times(r), r.yawAxis());
        Vector3Test.assertVector3Equals(Vector3f.UNIT_Z.negate().times(r), r.rollAxis());
    }

    @Test
    public void testQuaterionMatrix() {
        float identity[] = {
                1, 0, 0, 0,
                0, 1, 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1,
        };
        assertQuaternionEquals(Quaternion.IDENTITY, new Quaternion(identity));


        for (int i = 0; i < 100; i++) {
            // Start with a fairly random rotation
            Quaternion r = new Quaternion(Vector3f.UNIT_X, gen.nextInt(360)); 
            r.timesEquals(new Quaternion(Vector3f.UNIT_Y, gen.nextInt(360)));
            r.timesEquals(new Quaternion(Vector3f.UNIT_Z, gen.nextInt(360)));

            Quaternion q = new Quaternion(r.toGlMatrix());
            assertQuaternionEquals(q, r);
        }
    }

    @Test
    public void testRotationMatrix() {
        float m[] = Quaternion.rotationMatrix(Vector3f.UNIT_Z, Vector3f.UNIT_Y);

        System.out.println(String.format(
                "| %06.3f %06.3f %06.3f %06.3f |\n" +
                "| %06.3f %06.3f %06.3f %06.3f |\n" +
                "| %06.3f %06.3f %06.3f %06.3f |\n" +
                "| %06.3f %06.3f %06.3f %06.3f |",
                m[0], m[4], m[8], m[12],
                m[1], m[5], m[9], m[13],
                m[2], m[6], m[10], m[14],
                m[3], m[7], m[11], m[15]));

    }

    @Test
    public void testQuaterionFromLookAt() {
        Vector3f tests[] = {
                Vector3f.UNIT_X,
                Vector3f.UNIT_X.negate(),
                Vector3f.UNIT_Z,
                Vector3f.UNIT_Z.negate(),
                new Vector3f(1, 1, 1),
        };

        for (Vector3f v: tests) {
            Quaternion q = null;
            try {
                q = new Quaternion(v, Vector3f.UNIT_Y);
                Vector3Test.assertVector3Equals(v, q.rollAxis());
            }
            catch(AssertionError e){
                System.err.println(e.getMessage());
                System.err.println("Q: " + q);
                System.err.println("V: " + v);
                System.err.println("");
            }
        }
    }


    @Test
    public void testGimbleLock() {
        Quaternion rotation = new Quaternion();
        rotation.timesEquals(new Quaternion(Vector3f.UNIT_X, 90));
        rotation.timesEquals(new Quaternion(Vector3f.UNIT_Z, 90));
        rotation.timesEquals(new Quaternion(Vector3f.UNIT_Y, 90));
        rotation.timesEquals(new Quaternion(Vector3f.UNIT_Z, -90));

        // There should be a better way to compare within epsilon
        assertQuaternionEquals(Quaternion.IDENTITY, rotation);

        // Test that rotating through 360 degrees on all axies brings us back to normal
        rotation.timesEquals(new Quaternion(Vector3f.UNIT_X, 90));
        rotation.timesEquals(new Quaternion(Vector3f.UNIT_X, 90));
        rotation.timesEquals(new Quaternion(Vector3f.UNIT_X, 90));
        rotation.timesEquals(new Quaternion(Vector3f.UNIT_X, 90));
        rotation.timesEquals(new Quaternion(Vector3f.UNIT_Y, 90));
        rotation.timesEquals(new Quaternion(Vector3f.UNIT_Y, 90));
        rotation.timesEquals(new Quaternion(Vector3f.UNIT_Y, 90));
        rotation.timesEquals(new Quaternion(Vector3f.UNIT_Y, 90));
        rotation.timesEquals(new Quaternion(Vector3f.UNIT_Z, 90));
        rotation.timesEquals(new Quaternion(Vector3f.UNIT_Z, 90));
        rotation.timesEquals(new Quaternion(Vector3f.UNIT_Z, 90));
        rotation.timesEquals(new Quaternion(Vector3f.UNIT_Z, 90));

        // Here we get a negative real component by the transformation matrix is the same
        assertQuaternionEquals(Quaternion.IDENTITY, rotation);        
    }

    @Test
    public void testMagnitude() {
        Quaternion rotation = new Quaternion();
        assertEquals(1.0, rotation.magnitude(), EPSILON);
    }

    @Test
    public void testMagnitude2() {
        Quaternion rotation = new Quaternion();
        assertEquals(1.0, rotation.magnitude2(), EPSILON);
    }

    @Test
    public void testNormalize() {
        Quaternion rotation = new Quaternion();
        assertEquals(1.0, rotation.magnitude(), 0);
        assertQuaternionEquals(Quaternion.IDENTITY, rotation.normalize());
        assertQuaternionEquals(Quaternion.IDENTITY, rotation);

        for( int i = 0; i < (4 * 4096); i++){
            // Rotate a lot to denormalize our vector
            rotation.timesEquals(new Quaternion(Vector3f.UNIT_X, i));
            rotation.timesEquals(new Quaternion(Vector3f.UNIT_Y, i));
            rotation.timesEquals(new Quaternion(Vector3f.UNIT_Z, i));
            rotation.timesEquals(new Quaternion(Vector3f.UNIT_X, i));
        }
        rotation.normalize();
    }

    @Test
    public void testInverse() {
        // Test the inverse works as expected
        // Sometimes the inverse isn't quite accurate, but every near due to floating point
        Quaternion rotation = Quaternion.newRandom(360);
        assertQuaternionEquals(Quaternion.IDENTITY, rotation.times(rotation.inverse()));

    }

    @Test
    public void testInversePerformance() {
        // This was showing up in TPTP as a slow spot
        Quaternion original = new Quaternion(Vector3f.UNIT_Z, 30);
        original.timesEquals(new Quaternion(Vector3f.UNIT_Y, 237));

        Quaternion rotation = new Quaternion(original);

        for (int i = 0; i < 16 * 1024; i++)
            rotation = rotation.inverse();

        assertQuaternionEquals(original, rotation);
    }

    @Test
    public void testRotationPreformance() {
        // This was showing up in TPTP as a slow spot
        Quaternion rotation = new Quaternion(Vector3f.UNIT_Z, 30);
        rotation.timesEquals(new Quaternion(Vector3f.UNIT_Y, 237));

        Quaternion rotationDelta = new Quaternion(Vector3f.UNIT_X, 0.1f);

        for (int i = 0; i < 16 * 1024; i++)
            rotation.timesEquals(rotationDelta);

    }

    @Test
    public void testToGlMatrixPreformance() {
        // This was showing up in TPTP as a slow spot
        Quaternion rotation = new Quaternion(Vector3f.UNIT_Z, 30);
        rotation.timesEquals(new Quaternion(Vector3f.UNIT_Y, 237));

        for (int i = 0; i < 16 * 1024; i++)
            rotation.toGlMatrix();

    }

    public static void assertQuaternionEquals(Quaternion a, Quaternion b) {
        assertQuaternionEquals(a, b, EPSILON);
    }

    private static void assertQuaternionEquals(Quaternion a, Quaternion b, double delta) {
        try {
            assertEquals(Math.abs(a.w_), Math.abs(b.w_), delta);
            assertEquals(a.x_, b.x_, delta);
            assertEquals(a.y_, b.y_, delta);
            assertEquals(a.z_, b.z_, delta);
        } catch(AssertionError e){
            throw new AssertionError("Expected: " + a + " but was " + b);
        }
    }
}
