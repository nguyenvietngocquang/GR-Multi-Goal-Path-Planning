package util;

public class Neuron extends Point {

	public Neuron next;
	public Neuron prev;

	public Neuron(double x, double y) {
		super(x, y);
		next = this;
		prev = this;
	}

}
