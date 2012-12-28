package fr.trovato.wissl.android.data;

public class HomeItem {

	private int icon;
	private int text;
	private Class<?> intentClass;

	public HomeItem(int icon, int text, Class<?> intentClass) {
		this.icon = icon;
		this.text = text;
		this.intentClass = intentClass;
	}

	public int getText() {
		return this.text;
	}

	public int getIcon() {
		return this.icon;
	}

	public Class<?> getIntentClass() {
		return this.intentClass;
	}

}
