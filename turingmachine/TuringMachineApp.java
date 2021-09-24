/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package turingmachine;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 *
 * @author harry
 *
 * Counter program: +[[>[-]+>>+[-<+<[-]>>]<[->+<]<[->>>+<<<]>>>]+[-<<<<]>>>>+]
 */
public class TuringMachineApp extends JPanel implements Runnable {

    private static final int WIDTH = 1000, HEIGHT = 800;

    private JSplitPane pane1;
    private JSplitPane pane2;
    private JPanel programPanel;
    private JLabel programLabel;
    private JScrollPane programScroll;
    private JTextArea programArea;
    private JPanel ioPanel;
    private JLabel outputLabel;
    private JScrollPane outputScroll;
    private JTextArea outputArea;
    private JLabel textInputLabel;
    private JTextField textInputField;
    private JLabel byteInputLabel;
    private JTextField byteInputField;
    private String input;

    private JPanel buttonViewPanel;
    private JPanel buttonPanel;
    private JButton runButton;
    private JButton pauseButton;
    private JButton stopButton;
    private JLabel speedUpLabel;
    private DecimalFormat decimalFormat;
    private JSlider timeIntervalSlider;

    private TuringMachine turingMachine;

    public TuringMachineApp() {
        JFrame frame = new JFrame("Turing Machine App");

        Font font = new Font("Monospaced", Font.BOLD, 26);
        pane1 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true);
        pane1.setResizeWeight(1.0);
        pane2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true);
        pane2.setResizeWeight(1.0);
        pane1.setBottomComponent(pane2);

        programPanel = new JPanel();
        programPanel.setLayout(new BoxLayout(programPanel, BoxLayout.Y_AXIS));
        programLabel = new JLabel("Program:");
        programScroll = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        programArea = new JTextArea();
        programArea.setFont(font);
        programArea.setLineWrap(true);
        programScroll.setViewportView(programArea);

        ioPanel = new JPanel();
        ioPanel.setLayout(new BoxLayout(ioPanel, BoxLayout.Y_AXIS));
        outputLabel = new JLabel("Output:");
        outputScroll = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        outputArea = new JTextArea();
        outputArea.setFont(font);
        outputArea.setLineWrap(true);
        outputArea.setEditable(false);
        outputScroll.setViewportView(outputArea);
        textInputLabel = new JLabel("Text input:");
        textInputLabel.setAlignmentX(LEFT_ALIGNMENT);
        textInputField = new JTextField();
        textInputField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                input = textInputField.getText();
                textInputField.setText("");
                byteInputField.setText("");
            }
        });
        textInputField.setFont(font);
        textInputField.setAlignmentX(LEFT_ALIGNMENT);
        textInputField.setMaximumSize(new Dimension(10000, 50));
        byteInputLabel = new JLabel("Byte input:");
        byteInputLabel.setAlignmentX(LEFT_ALIGNMENT);
        byteInputField = new JTextField();
        byteInputField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String[] byteStrs = byteInputField.getText().split("\\D");
                byte[] bytes = new byte[byteStrs.length];
                for (int i = 0; i < byteStrs.length; i++) {
                    bytes[i] = (byte) Integer.parseInt(byteStrs[i]);
                }
                input = new String(bytes, StandardCharsets.UTF_8);
                textInputField.setText("");
                byteInputField.setText("");
            }
        });
        byteInputField.setFont(font);
        byteInputField.setAlignmentX(LEFT_ALIGNMENT);
        byteInputField.setMaximumSize(new Dimension(10000, 50));

        buttonViewPanel = new JPanel();
        buttonViewPanel.setLayout(new BoxLayout(buttonViewPanel, BoxLayout.Y_AXIS));
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

        runButton = new JButton("Run");
        runButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String program = programArea.getText();
                if (TuringMachine.isValidProgram(program)) {
                    turingMachine.run(program);
                    outputArea.setText("");
                } else {
                    outputArea.append("\nError: incorrect or incomplete bracketing");
                }
            }
        });
        buttonPanel.add(runButton);
        buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(Box.createHorizontalGlue());
        speedUpLabel = new JLabel();
        speedUpLabel.setAlignmentX(LEFT_ALIGNMENT);
        speedUpLabel.setFont(new Font("Monospaced", Font.PLAIN, 16));
        buttonPanel.add(speedUpLabel);
        decimalFormat = new DecimalFormat();
        timeIntervalSlider = new JSlider(0, 26);
        timeIntervalSlider.setSnapToTicks(true);
        timeIntervalSlider.setPaintTicks(true);
        timeIntervalSlider.setMajorTickSpacing(2);
        timeIntervalSlider.setMinorTickSpacing(1);
        timeIntervalSlider.setValue(1);
        timeIntervalSlider.setAlignmentX(RIGHT_ALIGNMENT);
        buttonPanel.add(timeIntervalSlider);

        buttonViewPanel.add(buttonPanel);
        buttonViewPanel.add(this);

        programPanel.add(programLabel);
        programPanel.add(programScroll);
        programScroll.setAlignmentX(LEFT_ALIGNMENT);
        ioPanel.add(outputLabel);
        ioPanel.add(outputScroll);
        ioPanel.add(textInputLabel);
        ioPanel.add(textInputField);
        ioPanel.add(byteInputLabel);
        ioPanel.add(byteInputField);
        outputScroll.setAlignmentX(LEFT_ALIGNMENT);
        pane1.setTopComponent(programPanel);
        pane2.setBottomComponent(ioPanel);
        pane2.setTopComponent(buttonViewPanel);
        pane1.setDividerLocation(150);
        pane2.setDividerLocation(400);

        pane1.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        frame.add(pane1);
        frame.setResizable(true);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    @Override
    protected synchronized void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke(new BasicStroke(2));
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        
        double w = getWidth();
        double h = getHeight();
        double cx = w / 2.0;
        double cy = h / 2.0;
        double cellSize = h / 8.0;
        double hCellSize = cellSize / 2.0;
        int numCells = (int) Math.ceil(w / cellSize) + 4;
        if (numCells % 2 == 1)
            numCells++;
        double cellsOffsetX = numCells * cellSize / 2.0;
        
        Polygon tri = new Polygon();
        tri.addPoint((int) -cellSize / 2, (int) -cellSize / 2);
        tri.addPoint((int) cellSize / 2, (int) -cellSize / 2);
        tri.addPoint(0, (int) cellSize / 2);
        
        
        AffineTransform at = new AffineTransform();
        
        AffineTransform original = g2d.getTransform();
        at.translate(cx, cy + cellSize * 0.75);
        g2d.transform(at);
        g2d.setColor(Color.WHITE);
        g2d.fillPolygon(tri);
        g2d.setTransform(original);
        
        original = g2d.getTransform();
        at.setToIdentity();
        at.translate(cx, cy - cellSize * 0.75);
        at.rotate(Math.toRadians(180.0));
        g2d.transform(at);
        g2d.setColor(Color.WHITE);
        g2d.fillPolygon(tri);
        g2d.setTransform(original);
        
        double x0 = cx - cellsOffsetX - hCellSize - 
                cellSize * (turingMachine.getNextStepTime() * turingMachine.getInstructionPtrDir() % 1.0);
        double x1 = cx - cellsOffsetX - hCellSize - 
                cellSize * (turingMachine.getNextStepTime() * turingMachine.getDataPtrDir() % 1.0);
        double y0 = cy - 2.5 * cellSize - hCellSize;
        double y1 = cy + 2.5 * cellSize - hCellSize;
        int instructionAddress = turingMachine.getInstructionPtrExact() - numCells / 2;
        int dataAddress = turingMachine.getDataPtr() - numCells / 2;
        
        Font tapeFont = new Font("Monospaced", Font.BOLD, (int) (cellSize * 0.48));
        for (int i = 0; i < numCells; i++) {
            g2d.setColor(Color.WHITE);
            g2d.fillRect((int) x0, (int) y0, (int) cellSize, (int) cellSize);
            g2d.fillRect((int) x1, (int) y1, (int) cellSize, (int) cellSize);
            g2d.setColor(Color.BLACK);
            g2d.drawRect((int) x0, (int) y0, (int) cellSize, (int) cellSize);
            g2d.drawRect((int) x1, (int) y1, (int) cellSize, (int) cellSize);
            
            g2d.setColor(Color.BLACK);
            g2d.setFont(tapeFont);
            String instructionStr = String.valueOf(turingMachine.getInstruction(instructionAddress));
            Rectangle2D r2d = tapeFont.getStringBounds(instructionStr, g2d.getFontRenderContext());
            g2d.drawString(instructionStr, (int) (x0 + hCellSize - r2d.getWidth() / 2.0), (int) (y0 + hCellSize + 0.25 * r2d.getHeight()));
            
            g2d.setColor(Color.BLACK);
            g2d.setFont(tapeFont);
            String dataStr = String.valueOf(turingMachine.getData(dataAddress) + 128);
            r2d = tapeFont.getStringBounds(dataStr, g2d.getFontRenderContext());
            g2d.drawString(dataStr, (int) (x1 + hCellSize - r2d.getWidth() / 2.0), (int) (y1 + hCellSize + 0.25 * r2d.getHeight()));
            
            x0 += cellSize;
            x1 += cellSize;
            instructionAddress++;
            dataAddress++;
        }
    }

    @Override
    public void run() {
        turingMachine = new TuringMachine();
        turingMachine.run("");

        long lastTime = System.nanoTime();
        long ticks = 0;
        double delta = 0.0;
        double step = 1.0 / 60;
        while (true) {
            long now = System.nanoTime();
            delta += 1e-9 * (now - lastTime);
            lastTime = now;

            while (delta > step) {
                turingMachine.setInput(input);
                
                long speedUp = timeIntervalSlider.getValue() == 0 ? 0 : (long) Math.pow(2, timeIntervalSlider.getValue() - 1);
                turingMachine.update(2 * speedUp * step);
                
                outputArea.append(turingMachine.getOutput());
                JScrollBar outputScrollBar = outputScroll.getVerticalScrollBar();
                if (timeIntervalSlider.getValue() > 0) {
                    outputScrollBar.setValue(outputScrollBar.getMaximum());
                    outputScroll.repaint();
                }
                
                input = "";
                
                speedUpLabel.setText(String.format("%13s", decimalFormat.format(speedUp) + " x "));
                repaint();
                delta -= step;
            }
            ticks++;
            try {
                Thread.sleep(1);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TuringMachineApp tma = new TuringMachineApp();
            new Thread(tma).start();
        });
    }
}
