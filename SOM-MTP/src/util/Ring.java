package util;

import java.util.LinkedList;

public class Ring {

	public Neuron lastNeuron;
	public LinkedList<Neuron> neurons;

	public Ring() {
		lastNeuron = null;
		neurons = new LinkedList<Neuron>();
	}

	// Returns if the list is empty or not
	public boolean isEmpty() {
		return (lastNeuron == null);
	}

	// Returns the number of neurons in the list
	public int size() {
		return neurons.size();
	}

	// Returns the index of neurons in the list
	public int indexOf(Neuron neuron) {
		return neurons.indexOf(neuron);
	}

	// Returns the neuron of index i
	public Neuron get(int i) {
		return neurons.get(i);
	}

	// Returns the last selected neuron
	public Neuron getLast() {
		return lastNeuron;
	}

	// Insert a neuron to the right of neuron p
	public void rightInsert(Neuron n, Neuron p) {
		if (isEmpty()) {
			lastNeuron = n;
		} else {
			p.next.prev = n;
			n.next = p.next;
			p.next = n;
			n.prev = p;
		}
		neurons.add(n);
	}

	// Insert a neuron to the left of neuron p
	public void leftInsert(Neuron n, Neuron p) {
		if (isEmpty()) {
			lastNeuron = n;
		} else {
			p.prev.next = n;
			n.prev = p.prev;
			p.prev = n;
			n.next = p;
		}
		neurons.add(n);
	}

	// Delete a neuron from the list
	public void remove(Neuron n) {
		if (n == lastNeuron)
			lastNeuron = n.next;

		neurons.remove(n);
		n.prev.next = n.next;
		n.next.prev = n.prev;
		n = null;
	}

}
