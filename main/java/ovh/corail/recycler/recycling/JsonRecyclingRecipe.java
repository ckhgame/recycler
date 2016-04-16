package ovh.corail.recycler.recycling;

public class JsonRecyclingRecipe {
	String inputItem;
	String[] outputItems;
	boolean canBeRepaired;
	public JsonRecyclingRecipe(String inputItem, String[] outputItems, boolean canBeRepaired) {
		this.inputItem=inputItem;
		this.outputItems=outputItems;
		this.canBeRepaired=canBeRepaired;
	}
	public JsonRecyclingRecipe(String inputItem, String[] outputItems) {
		this(inputItem, outputItems, false);
	}
}
