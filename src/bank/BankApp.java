package bank;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class BankApp extends JFrame {

    private Map<String, User> userDatabase = new HashMap<>();
    private User currentUser;

    private CardLayout cardLayout = new CardLayout();
    private JPanel mainContainer = new JPanel(cardLayout);

    // UI Components
    private JTextField loginUserField;
    private JPasswordField loginPinField;

    private JLabel welcomeLabel;
    private JLabel balanceLabel;

    public BankApp() {
        setTitle("Modern Banking Application");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Styling defaults
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        // Mock users
        userDatabase.put("admin", new User("admin", "1234", 1000.00));
        userDatabase.put("user1", new User("user1", "0000", 500.50));

        // Create Views
        JPanel loginPanel = createLoginView();
        JPanel dashboardPanel = createDashboardView();

        mainContainer.add(loginPanel, "LOGIN");
        mainContainer.add(dashboardPanel, "DASHBOARD");

        add(mainContainer);
        cardLayout.show(mainContainer, "LOGIN");
    }

    private JPanel createLoginView() {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setBackground(new Color(245, 245, 245));
        
        JPanel panel = new JPanel(new GridLayout(4, 1, 10, 10));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
            new EmptyBorder(30, 40, 30, 40)
        ));
        panel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Welcome to SecureBank", JLabel.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(40, 40, 40));

        JPanel userPanel = new JPanel(new BorderLayout(5, 5));
        userPanel.setBackground(Color.WHITE);
        userPanel.add(new JLabel("Username:"), BorderLayout.NORTH);
        loginUserField = new JTextField();
        loginUserField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        userPanel.add(loginUserField, BorderLayout.CENTER);

        JPanel pinPanel = new JPanel(new BorderLayout(5, 5));
        pinPanel.setBackground(Color.WHITE);
        pinPanel.add(new JLabel("PIN Code:"), BorderLayout.NORTH);
        loginPinField = new JPasswordField();
        loginPinField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        pinPanel.add(loginPinField, BorderLayout.CENTER);

        JButton loginButton = createStyledButton("LOGIN");
        loginButton.addActionListener(e -> attemptLogin());

        panel.add(titleLabel);
        panel.add(userPanel);
        panel.add(pinPanel);
        panel.add(loginButton);

        outer.add(panel);
        return outer;
    }

    private void attemptLogin() {
        String username = loginUserField.getText().trim();
        String pin = new String(loginPinField.getPassword()).trim();

        User user = userDatabase.get(username);
        if (user != null && user.getPin().equals(pin)) {
            currentUser = user;
            updateDashboard();
            loginUserField.setText("");
            loginPinField.setText("");
            cardLayout.show(mainContainer, "DASHBOARD");
        } else {
            JOptionPane.showMessageDialog(this, "Invalid credentials. Try admin/1234.", "Login Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel createDashboardView() {
        JPanel dashboard = new JPanel(new BorderLayout());
        dashboard.setBackground(Color.WHITE);

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(63, 81, 181)); // Material Indigo
        headerPanel.setBorder(new EmptyBorder(20, 30, 20, 30));

        welcomeLabel = new JLabel("Welcome back!");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        welcomeLabel.setForeground(Color.WHITE);
        
        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        logoutButton.setBackground(new Color(220, 53, 69)); // Danger red
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.addActionListener(e -> {
            currentUser = null;
            cardLayout.show(mainContainer, "LOGIN");
        });

        headerPanel.add(welcomeLabel, BorderLayout.WEST);
        headerPanel.add(logoutButton, BorderLayout.EAST);

        // Center Content
        JPanel contentPanel = new JPanel(new BorderLayout(20, 20));
        contentPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
        contentPanel.setBackground(new Color(245, 245, 245));

        // Balance Card
        JPanel cardPanel = new JPanel(new BorderLayout(10, 10));
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            new EmptyBorder(30, 30, 30, 30)
        ));

        JLabel staticBalanceText = new JLabel("Current Balance", JLabel.CENTER);
        staticBalanceText.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        staticBalanceText.setForeground(Color.DARK_GRAY);
        
        balanceLabel = new JLabel("$0.00", JLabel.CENTER);
        balanceLabel.setFont(new Font("Segoe UI", Font.BOLD, 48));
        balanceLabel.setForeground(new Color(40, 40, 40));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(Color.WHITE);

        JButton depositBtn = createActionBtn("Deposit", new Color(40, 167, 69));
        depositBtn.addActionListener(e -> depositAction());

        JButton withdrawBtn = createActionBtn("Withdraw", new Color(255, 193, 7));
        withdrawBtn.addActionListener(e -> withdrawAction());
        
        JButton transferBtn = createActionBtn("Transfer", new Color(23, 162, 184));
        transferBtn.addActionListener(e -> transferAction());

        JButton historyBtn = createActionBtn("History", new Color(108, 117, 125));
        historyBtn.addActionListener(e -> showHistoryAction());

        buttonPanel.add(depositBtn);
        buttonPanel.add(withdrawBtn);
        buttonPanel.add(transferBtn);
        buttonPanel.add(historyBtn);

        cardPanel.add(staticBalanceText, BorderLayout.NORTH);
        cardPanel.add(balanceLabel, BorderLayout.CENTER);
        cardPanel.add(buttonPanel, BorderLayout.SOUTH);

        contentPanel.add(cardPanel, BorderLayout.CENTER);

        dashboard.add(headerPanel, BorderLayout.NORTH);
        dashboard.add(contentPanel, BorderLayout.CENTER);

        return dashboard;
    }

    private void updateDashboard() {
        if (currentUser != null) {
            welcomeLabel.setText("Welcome back, " + currentUser.getUsername() + "!");
            balanceLabel.setText(String.format("$%.2f", currentUser.getBalance()));
        }
    }

    private void depositAction() {
        String input = JOptionPane.showInputDialog(this, "Enter amount to deposit:", "Deposit", JOptionPane.PLAIN_MESSAGE);
        if (input != null && !input.trim().isEmpty()) {
            try {
                double amount = Double.parseDouble(input);
                if (amount > 0) {
                    currentUser.deposit(amount);
                    updateDashboard();
                    JOptionPane.showMessageDialog(this, "Deposited $" + amount + " successfully.");
                } else {
                    JOptionPane.showMessageDialog(this, "Amount must be positive.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid amount entered.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void withdrawAction() {
        String input = JOptionPane.showInputDialog(this, "Enter amount to withdraw:", "Withdraw", JOptionPane.PLAIN_MESSAGE);
        if (input != null && !input.trim().isEmpty()) {
            try {
                double amount = Double.parseDouble(input);
                if (amount > 0) {
                    boolean success = currentUser.withdraw(amount);
                    if (success) {
                        updateDashboard();
                        JOptionPane.showMessageDialog(this, "Withdrew $" + amount + " successfully.");
                    } else {
                        JOptionPane.showMessageDialog(this, "Insufficient funds.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Amount must be positive.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid amount entered.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void transferAction() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        JTextField usernameField = new JTextField();
        JTextField amountField = new JTextField();
        
        panel.add(new JLabel("Recipient Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Amount:"));
        panel.add(amountField);
        
        int result = JOptionPane.showConfirmDialog(this, panel, "Transfer Money", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String recipientName = usernameField.getText().trim();
                double amount = Double.parseDouble(amountField.getText().trim());
                
                User recipient = userDatabase.get(recipientName);
                if (recipient == null) {
                    JOptionPane.showMessageDialog(this, "Recipient not found.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if (recipient.getUsername().equals(currentUser.getUsername())) {
                    JOptionPane.showMessageDialog(this, "Cannot transfer to yourself.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                boolean success = currentUser.transferTo(recipient, amount);
                if (success) {
                    updateDashboard();
                    JOptionPane.showMessageDialog(this, "Successfully transferred $" + amount + " to " + recipientName);
                } else {
                    JOptionPane.showMessageDialog(this, "Insufficient funds or invalid amount.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid amount entered.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showHistoryAction() {
        String[] columns = {"Date & Time", "Description", "Amount"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (Transaction t : currentUser.getTransactionHistory()) {
            String amountStr = (t.getAmount() > 0 ? "+" : "") + String.format("$%.2f", t.getAmount());
            model.addRow(new Object[]{t.getFormattedTimestamp(), t.getDescription(), amountStr});
        }

        JTable table = new JTable(model);
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(600, 300));
        
        JOptionPane.showMessageDialog(this, scrollPane, "Transaction History", JOptionPane.PLAIN_MESSAGE);
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setBackground(new Color(63, 81, 181));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(200, 45));
        return btn;
    }

    private JButton createActionBtn(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(100, 40));
        return btn;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new BankApp().setVisible(true);
        });
    }
}
