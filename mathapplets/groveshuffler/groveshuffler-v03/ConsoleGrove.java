class ConsoleGrove {
    public static void main(String[] args) {
	Grove g = new Grove();
	g.print();
	for (int i = 0;i<10;i++) {
	    g.iterate(1);
	    g.print();
	}
    }
}
