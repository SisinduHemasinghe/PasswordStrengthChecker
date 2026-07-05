import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;

public class PasswordCheckerGUI extends JFrame {

    private JPasswordField passwordField;
    private JButton toggleVisibilityButton;
    private JButton checkButton;
    private JButton generateButton;
    private JButton copyButton;

    private JSpinner lengthSpinner;
    private JCheckBox upperCheck;
    private JCheckBox lowerCheck;
    private JCheckBox digitsCheck;
    private JCheckBox symbolsCheck;

    private JProgressBar scoreBar;
    private JLabel strengthLabel;
    private JTextArea suggestionsArea;
    private JLabel crackTimeLabel;

    private boolean passwordVisible = false;

    public PasswordCheckerGUI() {
        setTitle("Password Strength Checker");
        setSize(480, 580);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        mainPanel.add(buildPasswordInputPanel());
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(buildActionButtonsPanel());
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(buildGeneratorOptionsPanel());
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(buildResultPanel());

        add(mainPanel);

        // live update: recalculate strength on every keystroke, not just on button click
        passwordField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateStrengthDisplay();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateStrengthDisplay();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateStrengthDisplay();
            }
        });
    }

    private JPanel buildPasswordInputPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 0));
        panel.setBorder(BorderFactory.createTitledBorder("Password"));

        passwordField = new JPasswordField();
        passwordField.setEchoChar('\u2022');

        toggleVisibilityButton = new JButton("Show");
        toggleVisibilityButton.addActionListener(this::onToggleVisibility);

        panel.add(passwordField, BorderLayout.CENTER);
        panel.add(toggleVisibilityButton, BorderLayout.EAST);

        return panel;
    }

    private JPanel buildActionButtonsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 8, 0));

        checkButton = new JButton("Check Strength");
        checkButton.addActionListener(this::onCheckStrength);

        generateButton = new JButton("Generate Password");
        generateButton.addActionListener(this::onGeneratePassword);

        copyButton = new JButton("Copy");
        copyButton.addActionListener(this::onCopy);

        panel.add(checkButton);
        panel.add(generateButton);
        panel.add(copyButton);

        return panel;
    }

    private JPanel buildGeneratorOptionsPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 3, 8, 8));
        panel.setBorder(BorderFactory.createTitledBorder("Generator Options"));

        panel.add(new JLabel("Length:"));
        lengthSpinner = new JSpinner(new SpinnerNumberModel(12, 4, 64, 1));
        panel.add(lengthSpinner);
        panel.add(new JLabel(""));

        upperCheck = new JCheckBox("Uppercase", true);
        lowerCheck = new JCheckBox("Lowercase", true);
        digitsCheck = new JCheckBox("Digits", true);
        symbolsCheck = new JCheckBox("Symbols", true);

        panel.add(upperCheck);
        panel.add(lowerCheck);
        panel.add(digitsCheck);
        panel.add(symbolsCheck);

        return panel;
    }

    private JPanel buildResultPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Result"));

        scoreBar = new JProgressBar(0, 100);
        scoreBar.setStringPainted(true);
        scoreBar.setAlignmentX(Component.LEFT_ALIGNMENT);

        strengthLabel = new JLabel("Strength: -");
        strengthLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        strengthLabel.setFont(strengthLabel.getFont().deriveFont(Font.BOLD, 14f));

        suggestionsArea = new JTextArea(8, 20);
        suggestionsArea.setEditable(false);
        suggestionsArea.setLineWrap(true);
        suggestionsArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(suggestionsArea);
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);

        crackTimeLabel = new JLabel("Estimated crack time: -");
        crackTimeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(scoreBar);
        panel.add(Box.createVerticalStrut(6));
        panel.add(strengthLabel);
        panel.add(Box.createVerticalStrut(6));
        panel.add(scrollPane);
        panel.add(Box.createVerticalStrut(6));
        panel.add(crackTimeLabel);

        return panel;
    }

    private void onToggleVisibility(ActionEvent e) {
        passwordVisible = !passwordVisible;
        if (passwordVisible) {
            passwordField.setEchoChar((char) 0);
            toggleVisibilityButton.setText("Hide");
        } else {
            passwordField.setEchoChar('\u2022');
            toggleVisibilityButton.setText("Show");
        }
    }

    // Button click: an explicit "check" action, so it's fine to warn on empty input
    private void onCheckStrength(ActionEvent e) {
        String password = new String(passwordField.getPassword());

        if (password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a password first.",
                    "No Password", JOptionPane.WARNING_MESSAGE);
            return;
        }

        updateStrengthDisplay();
    }

    // Shared by the button AND the live document listener.
    // No dialogs here - this can fire dozens of times a second while typing.
    private void updateStrengthDisplay() {
        String password = new String(passwordField.getPassword());

        if (password.isEmpty()) {
            scoreBar.setValue(0);
            scoreBar.setString("0/100");
            scoreBar.setForeground(colorForScore(0));
            strengthLabel.setText("Strength: -");
            suggestionsArea.setText("");
            crackTimeLabel.setText("Estimated crack time: -");
            return;
        }

        PasswordChecker checker = new PasswordChecker(password);
        checker.checkStrength();

        int score = checker.getScore();
        scoreBar.setValue(score);
        scoreBar.setString(score + "/100");
        scoreBar.setForeground(colorForScore(score));

        strengthLabel.setText("Strength: " + checker.getStrengthLevel());

        StringBuilder sb = new StringBuilder();
        for (String suggestion : checker.getSuggestions()) {
            sb.append("- ").append(suggestion).append("\n");
        }
        suggestionsArea.setText(sb.toString());

        crackTimeLabel.setText("Estimated crack time: " + checker.estimateCrackTime());
    }

    private Color colorForScore(int score) {
        if (score <= 30) return new Color(200, 40, 40);
        if (score <= 60) return new Color(210, 150, 20);
        if (score <= 80) return new Color(60, 140, 60);
        return new Color(20, 120, 60);
    }

    private void onGeneratePassword(ActionEvent e) {
        int length = (Integer) lengthSpinner.getValue();
        boolean upper = upperCheck.isSelected();
        boolean lower = lowerCheck.isSelected();
        boolean digits = digitsCheck.isSelected();
        boolean symbols = symbolsCheck.isSelected();

        try {
            String generated = PasswordGenerator.generate(length, upper, lower, digits, symbols);
            passwordField.setText(generated); // this alone triggers updateStrengthDisplay() via the document listener
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Invalid Options", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onCopy(ActionEvent e) {
        String password = new String(passwordField.getPassword());
        if (password.isEmpty()) return;

        StringSelection selection = new StringSelection(password);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
        JOptionPane.showMessageDialog(this, "Password copied to clipboard.",
                "Copied", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PasswordCheckerGUI().setVisible(true));
    }
}