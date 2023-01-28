package pwr.bsadowski.sellerApp;

import model.Status;
import model.SubmittedOrder;
import pwr.bsadowski.exceptions.OrderNotFoundException;

import java.rmi.RemoteException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.util.Optional;

public class SellerGUI extends JFrame {

	private JPanel contentPane;
	private DefaultTableModel tableModelOrders;
	private JTextField textOrderIdToChange;
	private Status status;
	SellerApp sellerController;

	/**
	 * Create the frame.
	 */
	public SellerGUI() throws RemoteException {
		sellerController = new SellerApp();
		sellerController.registerSeller();

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 630, 385);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);

		String[] columnsOders = {"Order Id","Client Id", "Name", "Advert", "Quantity", "Price", "Status"};
		tableModelOrders = new DefaultTableModel(columnsOders, 0);
		contentPane.setLayout(null);
		JTable tableOrders = new JTable(tableModelOrders);
		JScrollPane planeSeller = new JScrollPane(tableOrders);
		planeSeller.setBounds(10, 10, 594, 220);
		contentPane.add(planeSeller);

		JPanel formSeller = new JPanel();
		formSeller.setBounds(10, 228, 594, 110);
		contentPane.add(formSeller);
		formSeller.setLayout(null);
		
		JLabel lblOrderId = new JLabel("Order ID");
		lblOrderId.setBounds(227, 36, 55, 13);
		formSeller.add(lblOrderId);
		
		textOrderIdToChange = new JTextField();
		textOrderIdToChange.setBounds(292, 32, 96, 21);
		formSeller.add(textOrderIdToChange);
		textOrderIdToChange.setColumns(10);


		JButton btnChangeStatus = new JButton("Change Status");
		btnChangeStatus.addActionListener(e -> {
			Optional<SubmittedOrder> soOptional = sellerController.getSellerSOList().stream()
					.filter(o -> o.getId() == Integer.parseInt(textOrderIdToChange.getText())).findFirst();
			if(soOptional.isPresent()){
				if(soOptional.get().getStatus().equals(Status.NEW)){
					status = Status.PROCESSING;
				} else if (soOptional.get().getStatus().equals(Status.PROCESSING)) {
					status = Status.READY;
				}else if (soOptional.get().getStatus().equals(Status.READY)) {
					status = Status.DELIVERED;
				} else if (soOptional.get().getStatus().equals(Status.READY)) {
					status = Status.DELIVERED;
				}else if (soOptional.get().getStatus().equals(Status.DELIVERED)) {
					status = Status.DELIVERED;
					System.out.println("Package is DELIVERED.");
				}

				try {
					sellerController.getShop().setStatus(Integer.parseInt(textOrderIdToChange.getText()), status);
				}catch (IndexOutOfBoundsException exception){
					System.out.println("Illegal argument");
				} catch (RemoteException ex) {
					throw new RuntimeException(ex);
				}
			}
			else{
				throw new OrderNotFoundException("Error 404. Order with id " + textOrderIdToChange.getText() + " not found." );
			}

		});
		btnChangeStatus.setBounds(232, 64, 131, 21);
		formSeller.add(btnChangeStatus);


		sellerController.refreshContent();
	}


	public void updateSellerTable(){
		tableModelOrders.setRowCount(0);
		for(SubmittedOrder so : sellerController.getSellerSOList()){
			for (int i = 0; i < so.getOrder().getOll().size(); i++) {
				tableModelOrders.addRow(new Object[]{so.getId(), so.getOrder().getClientID(), so.getOrder().getOll().get(i).getIt().getName(),
						so.getOrder().getOll().get(i).getAdvert(), so.getOrder().getOll().get(i).getQuantity(),
						so.getOrder().getOll().get(i).getCost(), so.getStatus()});
			}
		}
	}
}
