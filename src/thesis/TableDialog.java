package thesis;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.*;

public class TableDialog {
	private JTable list;

	private JLabel label;
	private JOptionPane optionPane;
	private JButton okButton, cancelButton;
	private ActionListener okEvent, cancelEvent;
	private JDialog dialog;

	public TableDialog(String message, JTable listToDisplay){
		list = listToDisplay;
		label = new JLabel(message);
		createAndDisplayOptionPane();
	}

	public TableDialog(String title, String message, JTable listToDisplay){
		this(message, listToDisplay);
		dialog.setTitle(title);

	}

	private void createAndDisplayOptionPane(){
		setupButtons();
		JPanel pane = layoutComponents();

		optionPane = new JOptionPane(pane);
		optionPane.setOptions(new Object[]{okButton});
		dialog = optionPane.createDialog("Select option");
		dialog.pack();
		dialog.setResizable(true);
		dialog.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				Main.exit(true);
			}
		});
	}

	private void setupButtons(){
		okButton = new JButton("OK");
		okButton.addActionListener(e -> handleOkButtonClick(e));

		cancelButton = new JButton("Close");
		cancelButton.addActionListener(e -> handleCancelButtonClick(e));
	}

	private JPanel layoutComponents(){

		JScrollPane scroller = new JScrollPane(list);
		JPanel panel = new JPanel(new BorderLayout());

		panel.add(label, BorderLayout.NORTH);
		panel.add(scroller, BorderLayout.CENTER);

		return panel;
	}

	public void setOnOk(ActionListener event){ okEvent = event; }

	public void setOnClose(ActionListener event){
		cancelEvent  = event;
	}


	public void windowClosing(WindowEvent e) {
		System.out.println("Window");
		Main.exit(true);
		hide();

	}

	public void windowClosed(WindowEvent e) {
		System.out.println("Window");
		Main.exit(true);
		hide();

	}

	private void handleOkButtonClick(ActionEvent e){
		if(okEvent != null){ okEvent.actionPerformed(e); }
		hide();
	}



	private void handleCancelButtonClick(ActionEvent e){
		if(cancelEvent != null){ cancelEvent.actionPerformed(e);}
		hide();
		Main.exit(true);
	}



	public void show(){ dialog.setVisible(true); }

	private void hide(){ dialog.setVisible(false); }

	public int[] getSelectedItem(){ return list.getSelectedRows(); }
}


