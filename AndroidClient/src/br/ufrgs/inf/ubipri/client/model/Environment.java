package br.ufrgs.inf.ubipri.client.model;

import java.util.ArrayList;

import android.location.Location;

public class Environment {

    private int id;
    private String name;
    private Point basePoint;
    private ArrayList<Point> points;
    private Environment parentEnvironment;
    private Environment next;
    private Environment firstChild;
    private boolean isFinal;

    public Environment() {
        basePoint = null;
        parentEnvironment = null;
        next = null;
        firstChild = null;
        points = new ArrayList<Point>();
    }

    public Environment(int id) {
        super();
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Point> getPoints() {
        return points;
    }

    public void setPoints(ArrayList<Point> points) {
        this.points = points;
    }

    public Environment getParentEnvironment() {
        return parentEnvironment;
    }

    public void setParentEnvironment(Environment parentEnvironment) {
        this.parentEnvironment = parentEnvironment;
    }

    public Environment getNext() {
        return next;
    }

    public Point getBasePoint() {
        return basePoint;
    }

    public void setBasePoint(Point basePoint) {
        this.basePoint = basePoint;
    }

    public Environment getFirstChild() {
        return firstChild;
    }

    public boolean isContainedLocation(Location location) {
        if (this.isFinal()) { // se for final busca no ponto base
            EnvironmentMap map = new EnvironmentMap();
            Double distance = map.geoDistanceInM(this.basePoint.getLatitude(), this.basePoint.getLongitude(), location.getLatitude(), location.getLongitude());
            if (distance <= this.basePoint.getOperatingRange()) {
                return true;
            }
            return false;
        }
        // Falta fazer o cálculo de precisão do ambiente (para diferenciar ambientes adjacentes)
        for (Point p : points) { // 
        }
        return false;
    }

    public boolean isFinal() {
        return isFinal;
    }

    public void setIsFinal(boolean isFinal) {
        this.isFinal = isFinal;
    }

    public boolean addChild(Environment e) {
        if (this.firstChild == null) {
            this.firstChild = e;
        } else {
            this.firstChild.addNext(e);
        }
        return true;
    }

    public boolean addNext(Environment e) {
        if (this.next == null) {
            this.next = e;
        } else {
            return this.next.addNext(e);
        }
        return true;
    }

    public boolean hasChild() {
        return (this.firstChild != null) ? true : false;
    }

    public boolean hasNext() {
        return (this.next != null) ? true : false;
    }
}