package compiler;
import compiler.Compiler;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.text.DefaultCaret;
/**
 *
 * @author leijurv
 */
public class Gooey {
    static JTextArea debugText;
    static JTextArea printText;
    public static void setup() {
        JFrame frame = new JFrame("Kitteh");
        frame.setLayout(new GridBagLayout());
        JPanel codePanel = new JPanel();
        codePanel.setLayout(new BorderLayout());
        final JTextArea codeText = new JTextArea();
        JScrollPane codeScroll = new JScrollPane(codeText);
        codePanel.add(codeScroll, BorderLayout.CENTER);
        JPanel debugPanel = new JPanel();
        debugPanel.setLayout(new BorderLayout());
        debugText = new JTextArea();
        debugText.setEditable(false);
        JScrollPane debugScroll = new JScrollPane(debugText);
        debugPanel.add(debugScroll, BorderLayout.CENTER);
        DefaultCaret caret = (DefaultCaret) debugText.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        JPanel printPanel = new JPanel();
        printPanel.setLayout(new BorderLayout());
        printText = new JTextArea();
        printText.setEditable(false);
        DefaultCaret printCaret = (DefaultCaret) printText.getCaret();
        printCaret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        JScrollPane printScroll = new JScrollPane(printText);
        printPanel.add(printScroll, BorderLayout.CENTER);
        JLabel resultLabel = new JLabel("Debug");
        JLabel codeLabel = new JLabel("Code");
        JLabel printLabel = new JLabel("Result");
        JButton run = new JButton("Run");
        run.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread() {
                    public void run() {
                        try {
                            output = "";
                            debug = "";
                            update();
                            String program = codeText.getText();
                            println("STARTING TO PARSE: " + program);
                            println();
                            long time = System.currentTimeMillis();
                            ArrayList<Command> prograaa = Compiler.toCommandList(Compiler.parse(program));
                            println();
                            println("Parsed program: " + prograaa);
                            println("Took " + (System.currentTimeMillis() - time) + "ms");
                            println();
                            println();
                            println("Compiling...");
                            println();
                            println();
                            byte[] compiled = Compiler.compile(prograaa);
                            Compiler.runProgram(compiled);
                            //run(prograaa);
                        } catch (Exception ex) {
                            StringWriter sw = new StringWriter();
                            PrintWriter pw = new PrintWriter(sw);
                            ex.printStackTrace(pw);
                            println(sw.toString()); // stack trace as a string
                        }
                    }
                }.start();
            }
        });
        final JComboBox<String> programs = new JComboBox<>(Compiler.exampleNames);
        JButton load = new JButton("Load example");
        load.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                codeText.setText(Compiler.examples[programs.getSelectedIndex()]);
            }
        });
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 3;
        c.anchor = GridBagConstraints.CENTER;
        c.gridwidth = 2;
        frame.add(run, c);
        c.gridx = 1;
        c.gridy = 1;
        frame.add(resultLabel, c);
        c.gridwidth = 1;
        c.gridx = 0;
        frame.add(codeLabel, c);
        c.gridx = 3;
        frame.add(printLabel, c);
        c.gridx = 1;
        c.gridy = 0;
        frame.add(programs, c);
        c.gridx = 2;
        frame.add(load, c);
        c.insets = new Insets(10, 10, 0, 5);
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 1.0;
        c.weightx = 0.5;
        c.gridx = 1;
        c.gridy = 2;
        c.gridwidth = 2;
        frame.add(debugPanel, c);
        c.insets = new Insets(10, 5, 0, 0);
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 1.0;
        c.weightx = 1;
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 1;
        frame.add(codePanel, c);
        c.insets = new Insets(10, 5, 0, 5);
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 1.0;
        c.weightx = 1;
        c.gridx = 3;
        c.gridy = 2;
        frame.add(printPanel, c);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(2000, 2000);
        frame.setVisible(true);
    }
    static String debug = "";
    static String output = "";
    public static void println() {
        System.out.println();
        debug = debug + "\n";
        update();
        //debugText.append("\n");
    }
    public static void println(String s) {
        System.out.println(s);
        debug = debug + s + "\n";
        update();
        //debugText.append(s + "\n");
    }
    public static void print(String s) {
        System.out.print(s);
        debug = debug + s;
        update();
        //debugText.append(s);
    }
    public static void printlnP(final String s) {
        output = output + s + "\n";
        //printText.append(s + "\n");
        update();
    }
    public static void update() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                printText.setText(output);
                debugText.setText(debug);
            }
        });
    }
}
