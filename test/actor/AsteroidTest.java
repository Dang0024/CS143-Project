package actor;

import static org.junit.Assert.*;
import graphics.Model;

import math.Quaternion;
import math.Vector3;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class AsteroidTest {
    private static final float EPSILON = 1.00E-6f;

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public final void testGetFarthestPointInDirection() {
        Actor a = new Asteroid();
        a.model= Model.findOrCreateByName("cube_centered.obj"); // cube -1..1 on all three axies
        a.angularVelocity = Quaternion.IDENTITY; // Don't spin for this test
        
        // Test basic stationary object case in all primary directions
        assertEquals(1.0f, a.getFarthestPointInDirection(Vector3.UNIT_X).x, EPSILON);
        assertEquals(1.0f, a.getFarthestPointInDirection(Vector3.UNIT_Y).y, EPSILON);
        assertEquals(1.0f, a.getFarthestPointInDirection(Vector3.UNIT_Z).z, EPSILON);
        assertEquals(-1.0f, a.getFarthestPointInDirection(Vector3.UNIT_X.negate()).x, EPSILON);
        assertEquals(-1.0f, a.getFarthestPointInDirection(Vector3.UNIT_Y.negate()).y, EPSILON);
        assertEquals(-1.0f, a.getFarthestPointInDirection(Vector3.UNIT_Z.negate()).z, EPSILON);
        assertEquals(3.0f, a.getFarthestPointInDirection(new Vector3(1, 1, 1)).magnitude2(), EPSILON);
               
        a.setVelocity(new Vector3(-1.7f, 0.4f, -3.2f));
        assertEquals(1.0f, a.getFarthestPointInDirection(Vector3.UNIT_X).x, EPSILON);
        assertEquals(1.4f, a.getFarthestPointInDirection(Vector3.UNIT_Y).y, EPSILON);
        assertEquals(1.0f, a.getFarthestPointInDirection(Vector3.UNIT_Z).z, EPSILON);
        assertEquals(-2.7f, a.getFarthestPointInDirection(Vector3.UNIT_X.negate()).x, EPSILON);
        assertEquals(-1.0f, a.getFarthestPointInDirection(Vector3.UNIT_Y.negate()).y, EPSILON);
        assertEquals(-4.2f, a.getFarthestPointInDirection(Vector3.UNIT_Z.negate()).z, EPSILON);

        a.setVelocity(new Vector3(0.2f, 0.2f, 0.2f));
        for (int i = 0; i < 10; i++) {
            assertEquals(1.2f + i * 0.2f, a.getFarthestPointInDirection(Vector3.UNIT_X).x, EPSILON);
            assertEquals(1.2f + i * 0.2f, a.getFarthestPointInDirection(Vector3.UNIT_Y).y, EPSILON);
            assertEquals(1.2f + i * 0.2f, a.getFarthestPointInDirection(Vector3.UNIT_Z).z, EPSILON);
            assertEquals(-1.0f + i * 0.2f, a.getFarthestPointInDirection(Vector3.UNIT_X.negate()).x, EPSILON);
            assertEquals(-1.0f + i * 0.2f, a.getFarthestPointInDirection(Vector3.UNIT_Y.negate()).y, EPSILON);
            assertEquals(-1.0f + i * 0.2f, a.getFarthestPointInDirection(Vector3.UNIT_Z.negate()).z, EPSILON);
            a.update();
        }

        // Test rotation
        a.position = new Vector3();
        a.velocity = new Vector3();
        a.rotation = new Quaternion(Vector3.UNIT_Z, 30);
        assertEquals(1.0f, a.getFarthestPointInDirection(Vector3.UNIT_Z).z, EPSILON);
        assertEquals(Math.cos(Math.PI / 6.0) + Math.sin(Math.PI / 6.0), a.getFarthestPointInDirection(Vector3.UNIT_X).x, EPSILON);
        assertEquals(Math.cos(Math.PI / 6.0) + Math.sin(Math.PI / 6.0), a.getFarthestPointInDirection(Vector3.UNIT_Y).y, EPSILON);

        // Test angular velocity
        a.position = new Vector3();
        a.velocity = new Vector3();
        a.rotation = new Quaternion();
        a.angularVelocity = new Quaternion(Vector3.UNIT_Z, 30);
        assertEquals(1.0f, a.getFarthestPointInDirection(Vector3.UNIT_Z).z, EPSILON);
        assertEquals(Math.cos(Math.PI / 6.0) + Math.sin(Math.PI / 6.0), a.getFarthestPointInDirection(Vector3.UNIT_X).x, EPSILON);
        assertEquals(Math.cos(Math.PI / 6.0) + Math.sin(Math.PI / 6.0), a.getFarthestPointInDirection(Vector3.UNIT_Y).y, EPSILON);
    }

}
