package ovh.corail.recycler.common;

public class jsonRecipe {
	String inputItem;
	String[] outputItems;
	boolean canBeRepaired;
	public jsonRecipe(String inputItem, String[] outputItems, boolean canBeRepaired) {
		this.inputItem=inputItem;
		this.outputItems=outputItems;
		this.canBeRepaired=canBeRepaired;
	}
	public jsonRecipe(String inputItem, String[] outputItems) {
		this(inputItem, outputItems, false);
	}
}
