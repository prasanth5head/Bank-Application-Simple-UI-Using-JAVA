package bank;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

public class AtmApp extends JFrame implements ActionListener {

    private Map<String, User> userDatabase = new HashMap<>();
    private User currentUser = null;

    // UI Components
    private JTextArea screenDisplay;
    private JLabel cardLabel;
    private JTextField cardInput;

    // ATM State enum
    private enum AtmState {
        WAITING_FOR_CARD,
        WAITING_FOR_PIN,
        MAIN_MENU,
        WITHDRAWAL_MENU,
        CUSTOM_WITHDRAWAL,
        DEPOSIT_MENU,
        BALANCE_INQUIRY
    }

    private AtmState currentState = AtmState.WAITING_FOR_CARD;
    private String currentInput = "";

    public AtmApp() {
        setTitle("ATM Machine Simulator");
        setSize(500, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(192, 192, 192)); // Silver ATM casing

        // Mock users
        userDatabase.put("123456", new User("John Doe", "1234", 1000.00));
        userDatabase.put("987654", new User("Jane Smith", "0000", 500.50));

        // --- TOP PANEL (Card Insertion) ---
        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.setBackground(new Color(192, 192, 192));
        cardLabel = new JLabel("Insert Card Number:");
        cardLabel.setFont(new Font("Monospaced", Font.BOLD, 14));
        cardInput = new JTextField(15);
        JButton insertCardBtn = new JButton("Insert Card");
        insertCardBtn.addActionListener(e -> processCard());
        topPanel.add(cardLabel);
        topPanel.add(cardInput);
        topPanel.add(insertCardBtn);
        add(topPanel, BorderLayout.NORTH);

        // --- CENTER PANEL (ATM Screen) ---
        JPanel screenOuter = new JPanel(new BorderLayout());
        screenOuter.setBorder(new EmptyBorder(20, 30, 20, 30));
        screenOuter.setBackground(new Color(192, 192, 192));

        screenDisplay = new JTextArea(10, 30);
        screenDisplay.setBackground(new Color(0, 51, 102)); // Deep blue ATM screen
        screenDisplay.setForeground(Color.WHITE);
        screenDisplay.setFont(new Font("Monospaced", Font.BOLD, 16));
        screenDisplay.setEditable(false);
        screenDisplay.setMargin(new Insets(15, 15, 15, 15));
        screenDisplay.setBorder(new LineBorder(Color.DARK_GRAY, 5));
        
        screenOuter.add(screenDisplay, BorderLayout.CENTER);
        add(screenOuter, BorderLayout.CENTER);

        // --- BOTTOM PANEL (Keypad & Action Buttons) ---
        JPanel keypadOuter = new JPanel(new BorderLayout());
        keypadOuter.setBorder(new EmptyBorder(10, 40, 30, 40));
        keypadOuter.setBackground(new Color(192, 192, 192));

        JPanel keypadPanel = new JPanel(new GridLayout(4, 3, 10, 10));
        keypadPanel.setBackground(new Color(192, 192, 192));
        
        String[] keys = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "", "0", ""};
        for (String key : keys) {
            if (key.isEmpty()) {
                keypadPanel.add(new JLabel("")); 
            } else {
                JButton btn = createKeypadButton(key);
                btn.addActionListener(this);
                keypadPanel.add(btn);
            }
        }

        JPanel actionPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        actionPanel.setBackground(new Color(192, 192, 192));
        actionPanel.setBorder(new EmptyBorder(0, 20, 0, 0));

        JButton cancelBtn = createActionButton("CANCEL", new Color(220, 53, 69));
        JButton clearBtn = createActionButton("CLEAR", new Color(255, 193, 7));
        JButton enterBtn = createActionButton("ENTER", new Color(40, 167, 69));

        cancelBtn.addActionListener(this);
        clearBtn.addActionListener(this);
        enterBtn.addActionListener(this);

        actionPanel.add(cancelBtn);
        actionPanel.add(clearBtn);
        actionPanel.add(enterBtn);

        keypadOuter.add(keypadPanel, BorderLayout.CENTER);
        keypadOuter.add(actionPanel, BorderLayout.EAST);

        add(keypadOuter, BorderLayout.SOUTH);

        updateScreen();
    }

    private JButton createKeypadButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 20));
        btn.setBackground(Color.LIGHT_GRAY);
        btn.setFocusPainted(false);
        return btn;
    }

    private JButton createActionButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        return btn;
    }

    private void processCard() {
        if (currentState == AtmState.WAITING_FOR_CARD) {
            String cardNumber = cardInput.getText().trim();
            if (userDatabase.containsKey(cardNumber)) {
                currentUser = userDatabase.get(cardNumber);
                currentState = AtmState.WAITING_FOR_PIN;
                currentInput = "";
                cardInput.setText("");
                cardInput.setEnabled(false);
                updateScreen();
            } else {
                screenDisplay.setText("\n\n   INVALID CARD NUMBER.\n   PLEASE TRY AGAIN.");
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();

        if (cmd.equals("CANCEL")) {
            resetAtm();
            return;
        }

        if (cmd.equals("CLEAR")) {
            currentInput = "";
            updateScreen();
            return;
        }

        if (cmd.equals("ENTER")) {
            processEnter();
            return;
        }

        // Numeric keys
        if (cmd.length() == 1 && Character.isDigit(cmd.charAt(0))) {
            currentInput += cmd;
            updateScreen();
        }
    }

    private void processEnter() {
        switch (currentState) {
            case WAITING_FOR_PIN:
                if (currentUser.getPin().equals(currentInput)) {
                    currentState = AtmState.MAIN_MENU;
                    currentInput = "";
                } else {
                    currentInput = "";
                    screenDisplay.setText("\n\n   INCORRECT PIN.\n   PLEASE TRY AGAIN.");
                    return; // Don't call updateScreen immediately
                }
                break;
            case MAIN_MENU:
                if (currentInput.equals("1")) currentState = AtmState.WITHDRAWAL_MENU;
                else if (currentInput.equals("2")) currentState = AtmState.DEPOSIT_MENU;
                else if (currentInput.equals("3")) currentState = AtmState.BALANCE_INQUIRY;
                else if (currentInput.equals("4")) resetAtm();
                currentInput = "";
                break;
            case WITHDRAWAL_MENU:
                try {
                    int amount = Integer.parseInt(currentInput);
                    if (amount > 0 && amount % 10 == 0) { // Must be multiple of $10
                        if (currentUser.withdraw(amount)) {
                            screenDisplay.setText("\n\n   PLEASE TAKE YOUR CASH.\n   $" + amount);
                            currentState = AtmState.MAIN_MENU;
                            currentInput = "";
                            return;
                        } else {
                            screenDisplay.setText("\n\n   INSUFFICIENT FUNDS.");
                            currentInput = "";
                            return;
                        }
                    } else {
                        screenDisplay.setText("\n\n   INVALID AMOUNT.\n   MUST BE MULTIPLE OF $10.");
                        currentInput = "";
                        return;
                    }
                } catch (NumberFormatException ex) {
                    currentInput = "";
                }
                break;
            case DEPOSIT_MENU:
                try {
                    double amount = Double.parseDouble(currentInput);
                    if (amount > 0) {
                        currentUser.deposit(amount);
                        screenDisplay.setText("\n\n   DEPOSIT SUCCESSFUL!\n   $" + String.format("%.2f", amount));
                        currentState = AtmState.MAIN_MENU;
                        currentInput = "";
                        return;
                    }
                } catch (NumberFormatException ex) {}
                currentInput = "";
                break;
            case BALANCE_INQUIRY:
                currentState = AtmState.MAIN_MENU;
                currentInput = "";
                break;
        }
        updateScreen();
    }

    private void resetAtm() {
        currentState = AtmState.WAITING_FOR_CARD;
        currentUser = null;
        currentInput = "";
        cardInput.setEnabled(true);
        updateScreen();
    }

    private void updateScreen() {
        StringBuilder sb = new StringBuilder();
        
        switch (currentState) {
            case WAITING_FOR_CARD:
                sb.append("\n\n    WELCOME TO SECUREBANK ATM\n\n");
                sb.append("      PLEASE INSERT CARD\n");
                sb.append("      IN THE SLOT ABOVE\n\n");
                sb.append("   (Test cards: 123456, 987654)");
                break;

            case WAITING_FOR_PIN:
                sb.append("\n\n    ENTER YOUR 4-DIGIT PIN:\n\n");
                sb.append("             ");
                for (int i = 0; i < currentInput.length(); i++) {
                    sb.append("*");
                }
                sb.append("\n\n    PRESS ENTER TO CONFIRM");
                break;

            case MAIN_MENU:
                sb.append("    MAIN MENU\n");
                sb.append("  -------------------------\n");
                sb.append("    [1] WITHDRAW CASH\n");
                sb.append("    [2] MAKE A DEPOSIT\n");
                sb.append("    [3] CHECK BALANCE\n");
                sb.append("    [4] RETURN CARD\n\n");
                sb.append("  ENTER OPTION: " + currentInput);
                break;

            case WITHDRAWAL_MENU:
                sb.append("    WITHDRAW CASH\n");
                sb.append("  -------------------------\n");
                sb.append("  ENTER AMOUNT IN MULTIPLES\n");
                sb.append("  OF $10.\n\n");
                sb.append("  AMOUNT: $" + currentInput);
                break;

            case DEPOSIT_MENU:
                sb.append("    MAKE A DEPOSIT\n");
                sb.append("  -------------------------\n");
                sb.append("  ENTER AMOUNT PREPARED\n");
                sb.append("  FOR DEPOSIT.\n\n");
                sb.append("  AMOUNT: $" + currentInput);
                break;

            case BALANCE_INQUIRY:
                sb.append("\n\n    YOUR CURRENT BALANCE IS:\n\n");
                sb.append(String.format("          $%.2f\n\n", currentUser.getBalance()));
                sb.append("    PRESS ENTER TO RETURN.");
                break;
        }
        
        screenDisplay.setText(sb.toString());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new AtmApp().setVisible(true);
        });
    }
}
