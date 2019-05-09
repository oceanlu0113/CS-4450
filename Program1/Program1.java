/** *************************************************************
 * file: Program 1.java
 * author: Ocean Lu
 * class: CS 4450 – Introduction to Computer Science
 *
 * assignment: Program 1
 * date last modified: 02/11/19
 *
 * purpose: Write a Java program which uses the LWJGL library to read coordinates from a file
 *
 *************************************************************** */
package program.pkg1;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import static org.lwjgl.opengl.GL11.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.*;

public class Program1 {

    public ArrayList<Line> lines = new ArrayList<>();
    public ArrayList<Circle> circles = new ArrayList<>();
    public ArrayList<Ellipse> ellipses = new ArrayList<>();

    public static void main(String[] args) {
        Program1 program1 = new Program1();
        program1.start();
    }

    public void start() {
        try {
            // address is specific to local files
            Scanner scanner = new Scanner(new File("D:\\User\\Documents\\2018-9 (Junior)\\2019 Spring\\CS 4450 - Computer Graphics\\Program1\\coordinates.txt"));
            int w, x, y, z;
            String input;
            while (scanner.hasNext()) {
                input = scanner.next();
                if (input.equals("l")) {
                    input = scanner.nextLine();
                    input = input.substring(1);
                    w = Integer.parseInt(input.substring(0, input.indexOf(",")));
                    input = input.substring(input.indexOf(",") + 1);
                    x = Integer.parseInt(input.substring(0, input.indexOf(" ")));
                    input = input.substring(input.indexOf(" ") + 1);
                    y = Integer.parseInt(input.substring(0, input.indexOf(",")));
                    input = input.substring(input.indexOf(",") + 1);
                    z = Integer.parseInt(input);
                    lines.add(new Line(w, x, y, z));
                } else if (input.equals("c")) {
                    input = scanner.nextLine();
                    input = input.substring(1);
                    w = Integer.valueOf(input.substring(0, input.indexOf(",")));
                    input = input.substring(input.indexOf(",") + 1);
                    x = Integer.valueOf(input.substring(0, input.indexOf(" ")));
                    input = input.substring(input.indexOf(" ") + 1);
                    y = Integer.valueOf(input);
                    circles.add(new Circle(w, x, y));
                } else {
                    input = scanner.nextLine();
                    input = input.substring(1);
                    w = Integer.valueOf(input.substring(0, input.indexOf(",")));
                    input = input.substring(input.indexOf(",") + 1);
                    x = Integer.valueOf(input.substring(0, input.indexOf(" ")));
                    input = input.substring(input.indexOf(" ") + 1);
                    y = Integer.valueOf(input.substring(0, input.indexOf(",")));
                    input = input.substring(input.indexOf(",") + 1);
                    z = Integer.valueOf(input);
                    ellipses.add(new Ellipse(w, x, y, z));
                }

            }
            scanner.close();
            createWindow();
            initGL();
            render();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createWindow() throws Exception {
        Display.setFullscreen(false);
        Display.setDisplayMode(new DisplayMode(640, 480));
        Display.setTitle("CS 4450 Homework 1");
        Display.create();
    }

    private void initGL() {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0, 640, 0, 480, 1, -1);
        glMatrixMode(GL_MODELVIEW);
        glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
    }

    private void render() {
        while (!Display.isCloseRequested()) {
            try {
                glClear(GL_COLOR_BUFFER_BIT
                        | GL_DEPTH_BUFFER_BIT);
                glLoadIdentity();
                glColor3f(1.0f, 1.0f, 0.0f);
                glPointSize(1);
                glBegin(GL_POINTS);
                for (Line line : lines) {
                    drawLine(line);
                }

                for (Circle circle : circles) {
                    drawCircle(circle);
                }

                for (Ellipse ellipse : ellipses) {
                    drawEllipse(ellipse);
                }
                glEnd();
                Display.update();
                Display.sync(60);
            } catch (Exception e) {
            }
        }
        Display.destroy();
    }

    public void drawLine(Line line) {
        float x = line.getX1();
        float y = line.getY1();
        float dx = Math.abs(line.getX2() - x);
        float dy = Math.abs(line.getY2() - y);
        float d = 2 * dy - dx;
        float incrementRight = (2 * dy);
        float incrementUpRight = 2 * (dy - dx);
        int c = -1;
        if (y - line.getY2() < 0) {
            c = 1;
        }

        while (x < line.getX2()) {
            if (d > 0) {
                d += incrementUpRight;
                y += c;
            } else {
                d += incrementRight;
            }
            x++;
            glColor3f(1, 0, 0);
            glVertex2f(x, y);
        }
    }

    public void drawCircle(Circle circle) {
        float x = circle.getX();
        float y = circle.getY();
        float radius = circle.getRadius();
        int theta = 0;
        glColor3f(0, 0, 1);
        while (theta < 359) {
            glVertex2f((radius * (float) Math.sin(theta)) + x, (radius * (float) Math.cos(theta)) + y);
            theta++;
        }
    }

    public void drawEllipse(Ellipse ellipse) {
        float x = ellipse.getX();
        float y = ellipse.getY();
        float xRadius = ellipse.getXRadius();
        float yRadius = ellipse.getYRadius();
        int theta = 0;
        glColor3f(0, 1, 0);
        while (theta < 359) {
            glVertex2f((xRadius * (float) Math.sin(theta)) + x, (yRadius * (float) Math.cos(theta)) + y);
            theta++;
        }
    }
    
    public class Line {

        int x1, y1, x2, y2;

        public Line(int x1, int y1, int x2, int y2) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
        }

        public float getX1() {
            return x1;
        }

        public float getX2() {
            return x2;
        }

        public float getY1() {
            return y1;
        }

        public float getY2() {
            return y2;
        }
    }

    public class Circle {

        float x, y, radius;

        public Circle(int x, int y, int radius) {
            this.x = x;
            this.y = y;
            this.radius = radius;
        }

        public float getX() {
            return x;
        }

        public float getY() {
            return y;
        }

        public float getRadius() {
            return radius;
        }
    }

    public class Ellipse {

        float x, y, xRadius, yRadius;

        public Ellipse(int x, int y, int xRadius, int yRadius) {
            this.x = x;
            this.y = y;
            this.xRadius = xRadius;
            this.yRadius = yRadius;
        }

        public float getX() {
            return x;
        }

        public float getY() {
            return y;
        }

        public float getXRadius() {
            return xRadius;
        }

        public float getYRadius() {
            return yRadius;
        }
    }
}
