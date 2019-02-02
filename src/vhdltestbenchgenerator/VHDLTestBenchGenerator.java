package vhdltestbenchgenerator;

import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

/**
 *
 * @author Daniele
 */
public class VHDLTestBenchGenerator {

    private static final int MAX_TEST_VALUE = 1000;
    private static final int MAX_SCHEDULED_THREAD_POOL = 8;
    private static MultithreadedTestBenchGenerationComputation multithreadedTestBenchCreator;
    
    private static JProgressBar progressBar;
    private static JLabel label;
    
    
    public static void main(String[] args) {

        JFrame frame = new JFrame("Test benches generated");
        frame.setPreferredSize(new Dimension(300, 100));
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        JPanel panel = new JPanel(new FlowLayout());
        progressBar = new JProgressBar(0, MAX_TEST_VALUE);
        label = new JLabel(progressBar.getPercentComplete() * 100 + " %  (" + progressBar.getValue() + " / " + progressBar.getMaximum() + ")");
        panel.add(progressBar);
        panel.add(label);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(panel);
        frame.pack();
        frame.setVisible(true);
        multithreadedTestBenchCreator = new MultithreadedTestBenchGenerationComputation(MAX_TEST_VALUE, label, progressBar);
        for(int i = 0; i < MAX_SCHEDULED_THREAD_POOL; i++)
            multithreadedTestBenchCreator.addThread(i / MAX_SCHEDULED_THREAD_POOL * MAX_TEST_VALUE, (i + 1) / MAX_SCHEDULED_THREAD_POOL * MAX_TEST_VALUE - 1, label, progressBar);
    }
    
}
