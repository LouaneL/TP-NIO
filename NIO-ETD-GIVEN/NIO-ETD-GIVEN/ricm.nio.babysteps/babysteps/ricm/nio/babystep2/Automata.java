package ricm.nio.babystep2;

public class Automata {
	ReaderAutomata read;
	WriterAutomata write;
	
	public Automata(ReaderAutomata read,WriterAutomata write) {
		this.read = read;
		this.write = write;
	}
	
	public ReaderAutomata getRead() {
		return read;
	}
	
	public WriterAutomata getWrite() {
		return write;
	}
}
