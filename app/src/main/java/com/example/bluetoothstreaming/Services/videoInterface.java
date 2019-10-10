package com.example.bluetoothstreaming.Services;

public interface videoInterface {
    public abstract void handleTextReception(String textReceived);
    public abstract void playVideo(String textReceived);
    public abstract void updateProgressBar(int progress);

}
