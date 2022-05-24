package lib.engine;

import lib.data.units.Castle;
import lib.data.units.Farmer;
import lib.data.units.Soldier;
import lib.data.units.Tractor;

import java.util.ArrayList;
import java.util.List;

/** A shop of units that can be purchased. **/
public class Shop {
    /** The player that can access and use this shop. Bought items will belong to this player. **/
    private Player player;
    /** All items that can be bought here. **/
    private List<Buyable> items;

    public Shop(Player player){
        this.player = player;
        this.items = new ArrayList<>();
    }

    public void addItem(Buyable item){
        items.add(item);
    }

    public static Shop defaultShop(Player player){
        Shop shop = new Shop(player);

        shop.addItem(new Soldier());
        shop.addItem(new Farmer());
        shop.addItem(new Castle());
        shop.addItem(new Soldier());
        shop.addItem(new Farmer());
        shop.addItem(new Castle());
        shop.addItem(new Soldier());
        shop.addItem(new Farmer());
        shop.addItem(new Castle());

        return shop;
    }

    public List<Buyable> getItems() {
        return items;
    }

    public Buyable getItem(int index){
        return items.get(index);
    }

    public Player getPlayer() {
        return player;
    }
}
