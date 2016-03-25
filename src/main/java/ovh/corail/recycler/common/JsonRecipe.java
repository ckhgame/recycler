package ovh.corail.recycler.common;

public class JsonRecipe {
	String inputItem;
	String[] outputItems;
	boolean canBeRepaired;
	public JsonRecipe(String inputItem, String[] outputItems, boolean canBeRepaired) {
		this.inputItem=inputItem;
		this.outputItems=outputItems;
		this.canBeRepaired=canBeRepaired;
	}
	public JsonRecipe(String inputItem, String[] outputItems) {
		this(inputItem, outputItems, false);
	}
}
