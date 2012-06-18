package ro.gdg.android.domain;

import java.util.Arrays;

public class MenuResponse extends RestaurantResponse {
	private Category[] categories;

	public Category[] getCategories() {
		return categories;
	}

	public void setCategories(Category[] categories) {
		this.categories = categories;
	}

	@Override
	public String toString() {
		return "MenuResponse [categories=" + Arrays.toString(categories) + "]";
	}

}
