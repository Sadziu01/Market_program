package pwr.bsadowski.shopApp;


import model.SubmittedOrder;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.rmi.RemoteException;

public class ShopGUI extends JFrame {

	private JPanel contentPane, panelShopTable;
	private DefaultTableModel tableModelShop;
	/**
	 * Create the frame.
	 */
	public ShopGUI() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 630, 385);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JButton btnStart = new JButton("Start");
		btnStart.addActionListener(e -> {
			ShopApp shopController = new ShopApp();
			try {
				shopController.ini();
				btnStart.setVisible(false);
				panelShopTable.setVisible(true);
			} catch (RemoteException ex) {
				throw new RuntimeException(ex);
			}

		});

		btnStart.setBounds(260, 200, 85, 21);
		contentPane.add(btnStart);
		
		panelShopTable = new JPanel();
		panelShopTable.setBounds(0, 0, 614, 346);
		contentPane.add(panelShopTable);

		panelShopTable.setVisible(false);

		String[] columnsShop = {"Order Id","Client Id", "Name", "Advert", "Quantity", "Price", "Status"};
		tableModelShop = new DefaultTableModel(columnsShop, 0);
		panelShopTable.setLayout(null);
		JTable tableShop = new JTable(tableModelShop);
		JScrollPane planeShop = new JScrollPane(tableShop);
		planeShop.setBounds(10, 10, 594, 325);
		panelShopTable.add(planeShop);
	}


	public void updateShopTable(){
		tableModelShop.setRowCount(0);
		for(SubmittedOrder so : ShopImp.so){
			for (int i = 0; i < so.getOrder().getOll().size(); i++) {
				tableModelShop.addRow(new Object[]{so.getId(), so.getOrder().getClientID(), so.getOrder().getOll().get(i).getIt().getName(),
						so.getOrder().getOll().get(i).getAdvert(), so.getOrder().getOll().get(i).getQuantity(),
						so.getOrder().getOll().get(i).getCost(), so.getStatus()});
			}
		}
	}
}
