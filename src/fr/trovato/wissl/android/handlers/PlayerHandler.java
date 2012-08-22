package fr.trovato.wissl.android.handlers;

import android.os.Handler;
import android.os.Message;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import fr.trovato.wissl.android.R;
import fr.trovato.wissl.commons.utils.Player;

public class PlayerHandler extends Handler {

	private ProgressBar progressBar;
	private ImageButton playButton;
	private ImageButton stopButton;
	private ImageButton nextButton;
	private ImageButton previousButton;
	private ImageButton playingButton;

	public PlayerHandler(ProgressBar progressBar, ImageButton playButton,
			ImageButton stopButton, ImageButton nextButton,
			ImageButton previousButton, ImageButton playingButton) {
		this.progressBar = progressBar;
		this.playButton = playButton;
		this.stopButton = stopButton;
		this.nextButton = nextButton;
		this.previousButton = previousButton;
		this.playingButton = playingButton;
	}

	@Override
	public void handleMessage(Message msg) {
		Player[] values = Player.values();

		if (msg.what < values.length) {
			switch (values[msg.what]) {
			case SEEK:
				if (this.progressBar != null) {
					this.progressBar.setEnabled(true);
					this.progressBar.setProgress(msg.arg1);
					this.progressBar.setMax(msg.arg2);
				}
				break;
			case PLAY:
				if (this.playButton != null) {
					this.playButton.setImageResource(R.drawable.pause);
				}
				break;
			case PAUSE:
				if (this.playButton != null) {
					this.playButton.setImageResource(R.drawable.play);
				}
				break;
			case STOP:
				if (this.progressBar != null) {
					this.progressBar.setEnabled(false);
					this.progressBar.setProgress(0);
					this.progressBar.setMax(0);
				}

				if (this.playButton != null) {
					this.playButton.setImageResource(R.drawable.play);
				}
				break;
			case QUEUE:
				boolean hasSong = msg.arg1 != 0;
				boolean hasNext = msg.arg1 > msg.arg2;
				boolean hasPrevious = msg.arg1 > 0;

				if (this.playButton != null) {
					this.playButton.setEnabled(hasSong);
				}

				if (this.playingButton != null) {
					this.playingButton.setEnabled(hasSong);
				}

				if (this.stopButton != null) {
					this.stopButton.setEnabled(hasSong);
				}

				if (this.nextButton != null) {
					this.nextButton.setEnabled(hasNext);
				}

				if (this.previousButton != null) {
					this.previousButton.setEnabled(hasPrevious);
				}
				break;
			default:
				break;
			}
		}
	}
}
