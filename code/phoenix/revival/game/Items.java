public class Items {
	public static final int REPAIR_KIT_HP = 400;
	public static final int ENERGY_CELL_MP = 500;
	
	public static final int REPAIR_KIT_COST = 100;
	public static final int ENERGY_CELL_COST = 50;
	public static final int PHOENIX_REVIVAL_COST = 300;
	
	//NOTE: DO NOT CHANGE THESE NAMES!!! they're pretty stuck in. in different versions, you may have to convert diff names
	public static final String REPAIR_KIT = "Repair Kit";
	public static final String ENERGY_CELL = "Energy Cell";
	public static final String PHOENIX_REVIVAL = "Phoenix Revival";
    
	public static class HeroItemsViews {
		public Texture textureRepairKit;
		public Texture textureEnergyCell;
		public Texture texturePhoenixRevival;
		
		public TextureRegion regionRepairKit;
		public TextureRegion regionEnergyCell;
		public TextureRegion regionPhoenixRevival;
		
		public HeroItemsViews(OuyaGame game){
			textureRepairKit = new Texture(game, "items/basic/repair_kit.png");
			textureEnergyCell = new Texture(game, "items/basic/energy_cell.png");
			texturePhoenixRevival = new Texture(game, "items/basic/phoenix_revival.png");
			
			regionRepairKit = new TextureRegion(textureRepairKit, 0,0, 128, 128);
			regionEnergyCell = new TextureRegion(textureEnergyCell, 0,0, 128, 128);
			regionPhoenixRevival = new TextureRegion(texturePhoenixRevival, 0,0, 128, 128);
		}
	}
}
