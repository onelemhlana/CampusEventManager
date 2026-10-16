package gui;

import model.DataManager;
import model.Organizer;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class LoginDialog extends JDialog {
    private JTextField txtId;
    private JPasswordField txtPassword;
    private boolean authenticated = false;
    private Organizer loggedInOrganizer;
    private DataManager dataManager;

    public LoginDialog(Frame parent, DataManager dm) {
        super(parent, "Campus Event Manager - Login", true);
        this.dataManager = dm;
        setLayout(new BorderLayout(10, 10));
        setSize(350, 180);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel form = new JPanel(new GridLayout(3, 2, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));

        form.add(new JLabel("Organizer ID:"));
        txtId = new JTextField();
        form.add(txtId);

        form.add(new JLabel("Password:"));
        txtPassword = new JPasswordField();
        form.add(txtPassword);

        JButton btnLogin = new JButton("Login");
        JButton btnCancel = new JButton("Cancel");

        form.add(btnLogin);
        form.add(btnCancel);

        add(form, BorderLayout.CENTER);

        JLabel hint = new JLabel("Default: admin / admin123", SwingConstants.CENTER);
        hint.setFont(hint.getFont().deriveFont(Font.ITALIC, 11f));
        add(hint, BorderLayout.SOUTH);

        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String id = txtId.getText().trim();
                String pwd = new String(txtPassword.getPassword());
                Organizer o = dataManager.authenticate(id, pwd);
                if (o != null) {
                    authenticated = true;
                    loggedInOrganizer = o;
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(LoginDialog.this,
                            "Invalid credentials. Please try again.",
                            "Login Failed", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnCancel.addActionListener(e -> {
            authenticated = false;
            dispose();
        });

        // Enter key triggers login
        getRootPane().setDefaultButton(btnLogin);
    }

    public boolean isAuthenticated() { return authenticated; }
    public Organizer getLoggedInOrganizer() { return loggedInOrganizer; }
}
