package fr.trovato.wissl.android.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import fr.trovato.wissl.android.R;
import fr.trovato.wissl.android.data.HomeItem;

public class HomeAdapter extends ArrayAdapter<HomeItem> {

	public HomeAdapter(Context context, List<HomeItem> objects) {
		super(context, R.layout.home_item, objects);
	}
	
	@Override
	public final View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) this.getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View rowView = inflater.inflate(R.layout.home_item, parent, false);

		HomeItem currentItem = this.getItem(position);

		TextView textView = (TextView) rowView.findViewById(R.id.text);
		textView.setText(currentItem.getText());
		
		return rowView;
	}

}
