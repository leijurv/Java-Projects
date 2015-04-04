/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package perlinnoise;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.Random;
import javax.swing.*;
/**
 *
 * @author leijurv
 */
public class PerlinNoise {
    static BufferedImage b;
    public static float[][] genWhiteNoise(int width, int height, long seed) {
        Random r = new Random(seed);
        float[][] noise = new float[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                noise[i][j] = r.nextFloat();
            }
        }
        return noise;
    }
    public static float[][] genSmoothNoise(float[][] baseNoise, int octave) {
        int width = baseNoise.length;
        int height = baseNoise[0].length;
        float[][] smoothNoise = new float[width][height];
        int samplePeriod = 1 << octave; // calculates 2 ^ k
        float sampleFrequency = 1.0f / samplePeriod;
        for (int i = 0; i < width; i++) {
            //calculate the horizontal sampling indices
            int sample_i0 = (i / samplePeriod) * samplePeriod;
            int sample_i1 = (sample_i0 + samplePeriod) % width; //wrap around
            float horizontal_blend = (i - sample_i0) * sampleFrequency;
            for (int j = 0; j < height; j++) {
                //calculate the vertical sampling indices
                int sample_j0 = (j / samplePeriod) * samplePeriod;
                int sample_j1 = (sample_j0 + samplePeriod) % height; //wrap around
                float vertical_blend = (j - sample_j0) * sampleFrequency;
                //blend the top two corners
                float top = Interpolate(baseNoise[sample_i0][sample_j0],
                        baseNoise[sample_i1][sample_j0], horizontal_blend);
                //blend the bottom two corners
                float bottom = Interpolate(baseNoise[sample_i0][sample_j1],
                        baseNoise[sample_i1][sample_j1], horizontal_blend);
                //final blend
                smoothNoise[i][j] = Interpolate(top, bottom, vertical_blend);
            }
        }
        return smoothNoise;
    }
    public static float Interpolate(float x0, float x1, float alpha) {
        return x0 * (1 - alpha) + alpha * x1;
    }
    public static float[][] GeneratePerlinNoise(float[][] baseNoise, int octaveCount, boolean returnLast, float persistance) {
        int width = baseNoise.length;
        int height = baseNoise[0].length;
        float[][][] smoothNoise = new float[octaveCount][][]; //an array of 2D arrays containing
        //generate smooth noise
        for (int i = 0; i < octaveCount; i++) {
            smoothNoise[i] = genSmoothNoise(baseNoise, i);
        }
        float[][] perlinNoise = new float[width][height];
        float amplitude = 1.0f;
        float totalAmplitude = 0.0f;
        //blend noise together
        for (int octave = octaveCount - 1; octave >= 0; octave--) {
            amplitude *= persistance;
            totalAmplitude += amplitude;
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    smoothNoise[octave][i][j] *= amplitude;
                    perlinNoise[i][j] += smoothNoise[octave][i][j];
                }
            }
        }
        if (returnLast) {
            perlinNoise = smoothNoise[octaveCount - 1];
        }
        //normalisation
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                perlinNoise[i][j] /= totalAmplitude;
            }
        }
        return perlinNoise;
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        JFrame frame = new JFrame("dank");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(100000, 10000);
        frame.setVisible(true);
        int width = frame.getWidth();
        int height = frame.getHeight();
        float[][] altitude = GeneratePerlinNoise(genWhiteNoise(width, height, 0), 7, false, 0.2F);
        float[][] roughness = GeneratePerlinNoise(genWhiteNoise(width, height, 1), 7, false, 0.2F);
        float[][] detail = GeneratePerlinNoise(genWhiteNoise(width, height, 2), 5, false, 1F);
        float[][] res = new float[width][height];
        float[][] rd = new float[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                rd[i][j] = roughness[i][j] * detail[i][j];
                res[i][j] = (altitude[i][j] + roughness[i][j] * detail[i][j]) / 2;
            }
        }
        b = gen(res);
        //float[][];
        frame.setContentPane(new JComponent() {
            public void paintComponent(Graphics g) {
                g.drawImage(b, 10, 10, null);
            }
        });
        frame.setLayout(new FlowLayout());
        JComboBox<String> show = new JComboBox(new String[] {"Altitude", "Roughness", "Roughness*Detail", "Detail", "Result"});
        show.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switch (show.getSelectedIndex()) {
                    case 0:
                        b = gen(altitude);
                        break;
                    case 1:
                        b = gen(roughness);
                        break;
                    case 2:
                        b = gen(rd);
                        break;
                    case 3:
                        b = gen(detail);
                        break;
                    case 4:
                        b = gen(res);
                        break;
                }
                frame.repaint();
            }
        });
        //frame.add(show);
        JSlider j = new JSlider(1, 10, 5);
        JSlider p = new JSlider(1, 100, 100);
        JCheckBox dank = new JCheckBox();
        ChangeListener dankk = new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                b = gen(GeneratePerlinNoise(genWhiteNoise(width, height, 0), j.getValue(), dank.isSelected(), ((float) p.getValue()) / 100));
                frame.repaint();
            }
        };
        j.addChangeListener(dankk);
        dank.addChangeListener(dankk);
        p.addChangeListener(dankk);
        frame.add(j);
        frame.add(dank);
        frame.add(p);
        frame.repaint();
    }
    public static BufferedImage gen(float[][] dank) {
        int width = dank.length;
        int height = dank[0].length;
        BufferedImage b = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                b.setRGB(i, j, new Color(dank[i][j], dank[i][j], dank[i][j]).getRGB());
            }
        }
        return b;
    }
}
