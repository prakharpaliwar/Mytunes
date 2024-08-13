/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mytunes;
import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.BitstreamException;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.decoder.Header;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.AudioDevice;
import javazoom.jl.player.JavaSoundAudioDevice;

/**
 *
 * @author Robinhood
 */
public class CustomAudioDevice extends JavaSoundAudioDevice{
    /*private int position;
    private float volume = 1.0f; // Volume level (1.0 is max)


    @Override
    public void write(short[] samples, int offset, int length) throws JavaLayerException {
        super.write(samples, offset, length);
        position += length;
    }

    public int getPosition() {
        return position;
    }
    public void setVolume(float volume) {
        this.volume = volume;
    }*/
    private float volume = 1.0f; // Volume level (1.0 is max)

    @Override
    public void open(Decoder decoder) throws JavaLayerException {
        super.open(decoder);
    }

    @Override
    public void write(short[] samples, int offs, int len) throws JavaLayerException {
        for (int i = offs; i < len; i++) {
            samples[i] = (short) (samples[i] * volume);
        }
        super.write(samples, offs, len);
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }
    
}
