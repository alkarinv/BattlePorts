package mc.alk.battlePorts.objects;

public enum PortOption {
	CLEARINVENTORY ("clearInventory",false), NOBUFFS("noBuffs",false), 
	NOPOTIONS("noPotions",false),NOENCHANTS("noEnchants",false),
	HASGROUP("hasGroup",true),HASPERM("hasPerm",true),
	FIRSTUSE("firstUse", false);
	String name;
	boolean needsValue;
	PortOption(String name, boolean needsValue){this.name= name; this.needsValue = needsValue;}
	public String toString(){return needsValue ? name + "=<value>" : name;}
	public boolean needsValue(){return needsValue;}
	public String getName(){return name;}

}
