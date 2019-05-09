/**
 * *************************************************************
 * file: Program 1.java author: Ocean Lu class: CS 4450 – Introduction to
 * Computer Science
 *
 * assignment: Program 2 date last modified: 03/03/19
 *
 * purpose: Write a Java program which uses the LWJGL library to read
 * coordinates from a file
 *
 ***************************************************************
 */
package program2;

import java.util.Arrays;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import static org.lwjgl.opengl.GL11.*;
import java.io.File;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import org.lwjgl.input.Keyboard;

public class Program2 {

    public static void main(String[] args) {
        Program2 p2 = new Program2();
        p2.start();
    }

    public class Vertex {

        private float x;
        private float y;

        public Vertex(float x, float y) {
            this.x = x;
            this.y = y;
        }

        public float getX() {
            return x;
        }

        public float getY() {
            return y;
        }

        public void setX(float x) {
            this.x = x;
        }
    }

    public class Edge {

        private Vertex v1;
        private Vertex v2;
        private Vertex maxYVertex;
        private Vertex minYVertex;
        private float dy;
        private float dx;

        public Edge(Vertex v1, Vertex v2) {
            this.v1 = v1;
            this.v2 = v2;
            if (v1.getY() > v2.getY()) {
                maxYVertex = v1;
                minYVertex = v2;
            } else {
                maxYVertex = v2;
                minYVertex = v1;
            }
            dy = v2.getY() - v1.getY();
            dx = v2.getX() - v1.getX();
        }

        public Vertex getMaxYVertex() {
            return maxYVertex;
        }

        public Vertex getMinYVertex() {
            return minYVertex;
        }

        public float getSlope() {
            return dy / dx;
        }

        public float getSlopeRecipical() {
            //except dy = 0
            if (dy == 0) {
                return Float.MAX_VALUE;
            }
            return dx / dy;
        }

        public void setXValue(float x) {
            getMinYVertex().setX(x);
        }

        @Override
        public String toString() {
            return "\nY-min: " + getMinYVertex().getY() + "\nY-max: " + getMaxYVertex().getY() + "\nX-Val: " + getMinYVertex().getX() + "\n1/m: " + getSlopeRecipical() + " ";
        }
    }

    public class Matrix {

        float[][] matrix;
        int row;
        int col;

        public Matrix() {
            matrix = null;
            row = 0;
            col = 0;
        }

        public Matrix(float[][] m) {
            row = m.length;
            col = m[0].length;
            matrix = new float[row][col];
            for (int i = 0; i < row; i++) {
                for (int j = 0; j < col; j++) {
                    matrix[i][j] = m[i][j];
                }
            }
        }

        public float[][] getVertexMatrix(float x, float y) {
            float[][] tm = {{x}, {y}, {1}};
            row = tm.length;
            col = tm[0].length;
            matrix = tm;
            return tm;
        }

        public float[][] getTranslateMatrix(float x, float y) {
            float[][] tm = {{1, 0, x}, {0, 1, y}, {0, 0, 1}};
            row = tm.length;
            col = tm[0].length;
            matrix = tm;
            return tm;
        }

        public float[][] getRotationMatrix(float degree) {
            double radian = degree * Math.PI / 180;
            float[][] tm = {{(float) (Math.cos(radian)), -(float) Math.sin(radian), 0}, {(float) Math.sin(radian), (float) Math.cos(radian), 0}, {0, 0, 1}};
            row = tm.length;
            col = tm[0].length;
            matrix = tm;
            return tm;
        }

        public float[][] getScaleMatrix(float x, float y) {

            float[][] tm = {{x, 0, 0}, {0, y, 0}, {0, 0, 1}};
            row = tm.length;
            col = tm[0].length;
            matrix = tm;
            return tm;
        }

        public float[][] getMatrix() {
            return matrix;
        }

        public int getRowSize() {
            return row;
        }

        public int getColSize() {
            return col;
        }

        public float[] getRow(int row) {
            return matrix[row];
        }

        public float[] getCol(int col) {
            float[] colArray = new float[this.row];
            for (int i = 0; i < colArray.length; i++) {
                colArray[i] = matrix[i][col];
            }
            return colArray;
        }

        public Matrix multiplication(Matrix rhs) {
            float[][] answer = new float[row][rhs.getColSize()];
            for (int i = 0; i < answer.length; i++) {
                for (int j = 0; j < answer[0].length; j++) {
                    answer[i][j] = getRowTimesCol(this.getRow(i), rhs.getCol(j));
                }
            }
            return new Matrix(answer);
        }

        public int getRowTimesCol(float[] row, float[] col) {
            int answer = 0;
            for (int i = 0; i < row.length; i++) {
                answer += row[i] * col[i];
            }
            return answer;
        }

        public String toString() {
            StringBuilder str = new StringBuilder();
            for (int i = 0; i < row; i++) {
                str.append(Arrays.toString(matrix[i]) + "\n");
            }
            return str.toString();
        }
    }

    private final int Y_MIN = 0;
    private final int Y_MAX = 1;
    private final int X_VAL = 2;
    private final int SR = 3;

    public void start() {
        try {
            createWindow();
            initGL();
            render();
        } catch (LWJGLException ex) {
            ex.printStackTrace();
        }
    }

    public void createWindow() throws LWJGLException {
        Display.setFullscreen(false);
        Display.setDisplayMode(new DisplayMode(640, 480));
        Display.setTitle("Program 2");
        Display.create();

    }

    public void initGL() {
        glClearColor(0f, 0f, 0f, 0.0f);

        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        //origin in center
        glOrtho(-320, 320, -240, 240, 1, -1);

        glMatrixMode(GL_MODELVIEW);
        glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
    }

    public void render() {
        HashMap<ArrayList<Vertex>, ArrayList<String>> polygon = null;
        try {
            File file = new File("D:/User/Desktop/coordinates.txt");
            polygon = readPolygon(file);
        } catch (Exception e) {

        }
        while (!Display.isCloseRequested() && !Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
            Iterator<ArrayList<Vertex>> polygonIterator = polygon.keySet().iterator();
            try {
                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
                glLoadIdentity();
                while (polygonIterator.hasNext()) {
                    ArrayList<Vertex> vertexList = polygonIterator.next();
                    ArrayList<String> atr = polygon.get(vertexList);
                    String[] color = atr.get(0).split(" ");
                    glColor3f(Float.parseFloat(color[1]), Float.parseFloat(color[2]), Float.parseFloat(color[3]));
                    vertexList = transformation(vertexList, atr);
                    fillPolygon(getAllEdgeTable(vertexList));
                }
                Display.update();
                Display.sync(60);
            } catch (Exception ex) {

            }
        }
        Display.destroy();
    }

    public ArrayList<Vertex> transformation(ArrayList<Vertex> vertexList, ArrayList<String> transforms) {
        ArrayList<Vertex> transformed = new ArrayList<Vertex>();
        for (int i = 1; i < transforms.size(); i++) {
            String[] curTransform = transforms.get(i).split(" ");
            char type = curTransform[0].charAt(0);
            switch (type) {
                case 'r':
                    for (int j = 0; j < vertexList.size(); j++) {
                        transformed.add(rotate(vertexList.get(j), Float.parseFloat(curTransform[1]), Float.parseFloat(curTransform[2]), Float.parseFloat(curTransform[3])));
                    }
                    break;
                case 't':
                    for (int j = 0; j < vertexList.size(); j++) {
                        transformed.add(translate(vertexList.get(j), Float.parseFloat(curTransform[1]), Float.parseFloat(curTransform[2])));
                    }
                    break;
                case 's':
                    for (int j = 0; j < vertexList.size(); j++) {
                        transformed.add(scale(vertexList.get(j), Float.parseFloat(curTransform[1]), Float.parseFloat(curTransform[2]), Float.parseFloat(curTransform[3]), Float.parseFloat(curTransform[4])));
                    }
                    break;
                default:
                    break;
            }
            vertexList = transformed;
            transformed = new ArrayList<>();
        }
        return vertexList;
    }

    public HashMap<ArrayList<Vertex>, ArrayList<String>> readPolygon(File file) {
        HashMap<ArrayList<Vertex>, ArrayList<String>> polygon = new HashMap<>();
        ArrayList<Vertex> vertexList = new ArrayList<>();
        ArrayList<String> atr = null;
        try {
            Scanner readFile = new Scanner(file);
            char state = 'p';
            while (readFile.hasNextLine()) {
                String line = readFile.nextLine();
                String readLine[] = line.split(" ");
                if (state == 'p') {
                    if (readLine[0].equals("P")) {
                        atr = new ArrayList<>();
                        atr.add(line);
                        continue;
                    } else if (readLine[0].equals("T")) {
                        state = 't';
                    } else {
                        vertexList.add(new Vertex(Float.parseFloat(readLine[0]), Float.parseFloat(readLine[1])));
                        continue;
                    }
                }
                if (state == 't') {
                    if (readLine[0].equals("T")) {
                        continue;
                    } else if (readLine[0].equals("P")) {
                        state = 'p';
                        polygon.put(vertexList, atr);
                        atr = new ArrayList<String>();
                        vertexList = new ArrayList<Vertex>();
                        atr.add(line);
                    } else {
                        atr.add(line);
                    }
                }
            }
            polygon.put(vertexList, atr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return polygon;
    }

    public void fillPolygon(LinkedList<ArrayList<Float>> allEdges) {
        LinkedList<ArrayList<Float>> global_edges = getGlobalEdges(allEdges);
        LinkedList<ArrayList<Float>> active_edges = new LinkedList<>();
        int parity = 0;
        float scanLine = global_edges.getFirst().get(Y_MIN);
        while (!global_edges.isEmpty() && global_edges.getFirst().get(Y_MIN) == scanLine) {
            active_edges.add(global_edges.removeFirst());
        }

        while (!active_edges.isEmpty()) {
            ListIterator<ArrayList<Float>> activeEdgesList;
            activeEdgesList = active_edges.listIterator();
            float curX = -320;
            float nextX;
            while (activeEdgesList.hasNext()) {
                nextX = activeEdgesList.next().get(X_VAL);
                while (curX < nextX) {
                    if (parity == 1) {
                        draw(curX, scanLine);
                    }
                    curX += 1;
                }
                if (parity == 1) {
                    parity = 0;
                } else {
                    parity = 1;
                }
                draw(curX, scanLine);
            }
            scanLine += 1;
            activeEdgesList = active_edges.listIterator();
            while (activeEdgesList.hasNext()) {
                ArrayList<Float> edge = activeEdgesList.next();
                edge.set(X_VAL, edge.get(X_VAL) + edge.get(SR));
            }
            while (!global_edges.isEmpty() && global_edges.getFirst().get(Y_MIN) == scanLine) {
                active_edges.add(global_edges.removeFirst());
            }
            activeEdgesList = active_edges.listIterator();
            while (activeEdgesList.hasNext()) {
                if (activeEdgesList.next().get(Y_MAX) == scanLine) {
                    activeEdgesList.remove();
                }
            }
            active_edges = (sortEdgesByX(active_edges));
        }
    }

    public LinkedList<ArrayList<Float>> getGlobalEdges(LinkedList<ArrayList<Float>> allEdges) {
        LinkedList<ArrayList<Float>> globalEdges = new LinkedList<>();
        ListIterator<ArrayList<Float>> allEdgeList = allEdges.listIterator();
        while (allEdgeList.hasNext()) {
            ArrayList<Float> curEdge = allEdgeList.next();
            if (curEdge.get(SR) != Float.MAX_VALUE) {
                globalEdges.add(curEdge);
            }
        }
        return sortGlobalEdge(globalEdges);
    }

    public LinkedList<ArrayList<Float>> getAllEdgeTable(ArrayList<Vertex> vertexList) {
        LinkedList<ArrayList<Float>> all_edge = new LinkedList<>();
        Vertex first = vertexList.get(0);
        for (int i = 0; i < vertexList.size(); i++) {
            ArrayList<Float> values = new ArrayList<>();
            Edge curEdge;
            if (i == vertexList.size() - 1) {
                curEdge = new Edge(vertexList.get(i), first);
            } else {
                curEdge = new Edge(vertexList.get(i), vertexList.get(i + 1));
            }
            values.add(curEdge.getMinYVertex().getY());
            values.add(curEdge.getMaxYVertex().getY());
            values.add(curEdge.getMinYVertex().getX());
            values.add(curEdge.getSlopeRecipical());
            all_edge.add(values);
        }
        return all_edge;
    }

    public void draw(float x, float y) {
        glBegin(GL_POINTS);
        glVertex2f(x, y);
        glEnd();

    }

    public Vertex translate(Vertex v, float x, float y) {
        Matrix translateMatrix = new Matrix();
        translateMatrix.getTranslateMatrix(x, y);
        Matrix vertexMatrix = new Matrix();
        vertexMatrix.getVertexMatrix(v.getX(), v.getY());
        Matrix product = translateMatrix.multiplication(vertexMatrix);
        return new Vertex(product.getMatrix()[0][0], product.getMatrix()[1][0]);
    }

    public Vertex rotate(Vertex v, float degree, float pivotX, float pivotY) {
        Matrix rotationMatrix = new Matrix();
        Matrix vertexMatrix = new Matrix();
        rotationMatrix.getRotationMatrix(degree);
        vertexMatrix.getVertexMatrix(v.getX(), v.getY());
        Matrix after = rotationMatrix.multiplication(vertexMatrix);
        return new Vertex(after.getMatrix()[0][0], after.getMatrix()[1][0]);
    }

    public Vertex scale(Vertex v, float x, float y, float pivotX, float pivotY) {
        Matrix scaleMatrix = new Matrix();
        Matrix vertexMatrix = new Matrix();
        scaleMatrix.getScaleMatrix(x, y);
        vertexMatrix.getVertexMatrix(v.getX(), v.getY());
        Matrix after = scaleMatrix.multiplication(vertexMatrix);
        return new Vertex(after.getMatrix()[0][0], after.getMatrix()[1][0]);
    }

    public LinkedList<ArrayList<Float>> sortEdgesByX(LinkedList<ArrayList<Float>> edges) {
        LinkedList<ArrayList<Float>> sortedEdge = new LinkedList<>();
        ListIterator<ArrayList<Float>> iterator = edges.listIterator();
        while (iterator.hasNext()) {
            ArrayList<Float> edge = iterator.next();
            if (sortedEdge.isEmpty()) {
                sortedEdge.add(edge);
                continue;
            } else {
                ListIterator<ArrayList<Float>> second_iterator = sortedEdge.listIterator();
                boolean inserted = false;
                while (second_iterator.hasNext()) {
                    ArrayList<Float> curEdge = second_iterator.next();
                    if (edge.get(X_VAL) < curEdge.get(X_VAL)) {
                        second_iterator.previous();
                        second_iterator.add(edge);
                        inserted = true;
                        break;
                    }
                }
                if (inserted == false) {
                    second_iterator.add(edge);
                }
            }
        }
        return sortedEdge;
    }

    public LinkedList<ArrayList<Float>> sortGlobalEdge(LinkedList<ArrayList<Float>> globalEdge) {
        LinkedList<ArrayList<Float>> sortedEdge = new LinkedList<>();
        ListIterator<ArrayList<Float>> iterator = globalEdge.listIterator();
        while (iterator.hasNext()) {
            ArrayList<Float> edge = iterator.next();
            if (sortedEdge.isEmpty()) {
                sortedEdge.add(edge);
                continue;
            } else {
                ListIterator<ArrayList<Float>> sortedEdgeList = sortedEdge.listIterator();
                boolean inserted = false;
                while (sortedEdgeList.hasNext()) {
                    ArrayList<Float> curEdge = sortedEdgeList.next();
                    if (edge.get(Y_MIN) < curEdge.get(Y_MIN)) {
                        sortedEdgeList.previous();
                        sortedEdgeList.add(edge);
                        inserted = true;
                        break;
                    } else if (edge.get(Y_MIN) == curEdge.get(Y_MIN)) {
                        if (edge.get(X_VAL) < curEdge.get(X_VAL)) {
                            sortedEdgeList.previous();
                            sortedEdgeList.add(edge);
                            inserted = true;
                            break;
                        } else if (edge.get(X_VAL) == curEdge.get(X_VAL)) {
                            if (edge.get(Y_MAX) < curEdge.get(Y_MAX)) {
                                sortedEdgeList.previous();
                                sortedEdgeList.add(edge);
                                inserted = true;
                                break;
                            }
                        }
                    }
                }
                if (inserted == false) {
                    sortedEdgeList.add(edge);
                }
            }
        }
        return sortedEdge;
    }
}
