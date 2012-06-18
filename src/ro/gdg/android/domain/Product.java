package ro.gdg.android.domain;

public class Product {
	private String name;
	private int category_id;
	private int price;

	public Product(String name, int category_id, int price) {
		this.name = name;
		this.category_id = category_id;
		this.price = price;
	}

	public int getCategory_id() {
		return category_id;
	}

	public void setCategory_id(int category_id) {
		this.category_id = category_id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}
}
