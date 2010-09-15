package vutshila.labs.bpelgen.core.translation;

public abstract class DocumentCreator {
	protected String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public abstract boolean createDocument();

	public abstract boolean createFile();

}
